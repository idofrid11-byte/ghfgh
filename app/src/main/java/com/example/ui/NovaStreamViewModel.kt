package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AddonBehaviorHints
import com.example.data.model.AddonCatalog
import com.example.data.model.AddonManifest
import com.example.data.model.Bookmark
import com.example.data.model.CommunityRepository
import com.example.data.model.InstalledAddon
import com.example.data.model.MetaDetail
import com.example.data.model.MetaPreview
import com.example.data.model.StreamItem
import com.example.data.model.WatchProgress
import com.example.data.repository.AddonRepository
import com.example.data.repository.MediaRepository
import com.example.data.repository.WatchProgressRepository
import com.example.player.PlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class Screen {
    object Discover : Screen()
    object Search : Screen()
    object Library : Screen()
    object CommunityAddons : Screen()
    object AddonCreator : Screen()
    data class MediaDetail(val mediaId: String, val type: String) : Screen()
    data class Player(val mediaId: String, val title: String, val type: String) : Screen()
}

class NovaStreamViewModel(application: Application) : AndroidViewModel(application) {

    private val addonRepository = AddonRepository()
    private val watchProgressRepository = WatchProgressRepository()
    private val mediaRepository = MediaRepository(addonRepository)

    // Current navigation stack / screen
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Discover)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screen backstack for nested views
    private val navigationBackstack = mutableListOf<Screen>()

    // Catalogs state
    val installedAddons: StateFlow<List<InstalledAddon>> = addonRepository.installedAddons
    val communityRepos: StateFlow<List<CommunityRepository>> = addonRepository.communityRepos
    val watchHistory: StateFlow<List<WatchProgress>> = watchProgressRepository.watchHistory
    val bookmarks: StateFlow<List<Bookmark>> = watchProgressRepository.bookmarks

    private val _catalogs = MutableStateFlow<Map<String, List<MetaPreview>>>(emptyMap())
    val catalogs: StateFlow<Map<String, List<MetaPreview>>> = _catalogs.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow<String?>("All")
    val selectedTypeFilter: StateFlow<String?> = _selectedTypeFilter.asStateFlow()

    private val _featuredMedia = MutableStateFlow<List<MetaDetail>>(emptyList())
    val featuredMedia: StateFlow<List<MetaDetail>> = _featuredMedia.asStateFlow()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MetaPreview>>(emptyList())
    val searchResults: StateFlow<List<MetaPreview>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // Detail & Stream Resolving state
    private val _currentDetail = MutableStateFlow<MetaDetail?>(null)
    val currentDetail: StateFlow<MetaDetail?> = _currentDetail.asStateFlow()

    private val _resolvedStreams = MutableStateFlow<List<StreamItem>>(emptyList())
    val resolvedStreams: StateFlow<List<StreamItem>> = _resolvedStreams.asStateFlow()

    private val _isResolvingStreams = MutableStateFlow(false)
    val isResolvingStreams: StateFlow<Boolean> = _isResolvingStreams.asStateFlow()

    // Add Custom Addon Dialog & Tester state
    private val _addonUrlInput = MutableStateFlow("")
    val addonUrlInput: StateFlow<String> = _addonUrlInput.asStateFlow()

    private val _addonInstallState = MutableStateFlow<String?>(null)
    val addonInstallState: StateFlow<String?> = _addonInstallState.asStateFlow()

    private val _isInstallingAddon = MutableStateFlow(false)
    val isInstallingAddon: StateFlow<Boolean> = _isInstallingAddon.asStateFlow()

    // Player manager
    val playerManager = PlayerManager(application) { pos, dur ->
        val detail = _currentDetail.value
        if (detail != null) {
            watchProgressRepository.updateProgress(
                mediaId = detail.id,
                title = detail.name,
                poster = detail.poster,
                type = detail.type,
                positionMs = pos,
                durationMs = dur
            )
        }
    }

    init {
        loadCatalogs()
        _featuredMedia.value = mediaRepository.getFeaturedMedia()
    }

    fun navigateTo(screen: Screen) {
        navigationBackstack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (navigationBackstack.isNotEmpty()) {
            val prev = navigationBackstack.removeAt(navigationBackstack.lastIndex)
            _currentScreen.value = prev
            return true
        }
        if (_currentScreen.value !is Screen.Discover) {
            _currentScreen.value = Screen.Discover
            return true
        }
        return false
    }

    fun setTypeFilter(filter: String?) {
        _selectedTypeFilter.value = filter
        loadCatalogs()
    }

    fun loadCatalogs() {
        viewModelScope.launch {
            val typeParam = when (_selectedTypeFilter.value) {
                "Movies" -> "movie"
                "Series" -> "series"
                "Live TV" -> "channel"
                else -> null
            }
            _catalogs.value = mediaRepository.getCatalogsForType(typeParam)
        }
    }

    fun onSearchQueryChanged(q: String) {
        _searchQuery.value = q
        if (q.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isSearching.value = true
            _searchResults.value = mediaRepository.search(q)
            _isSearching.value = false
        }
    }

    fun openMediaDetail(mediaId: String, type: String = "movie") {
        viewModelScope.launch {
            _isResolvingStreams.value = true
            _resolvedStreams.value = emptyList()
            val detail = mediaRepository.getMediaDetail(mediaId, type)
            _currentDetail.value = detail
            navigateTo(Screen.MediaDetail(mediaId, type))

            // Resolve streams
            val streams = mediaRepository.resolveStreams(mediaId, type)
            _resolvedStreams.value = streams
            _isResolvingStreams.value = false
        }
    }

    fun playStream(stream: StreamItem, startPosMs: Long = 0L) {
        val detail = _currentDetail.value
        val title = detail?.name ?: stream.title ?: "Decentralized Stream"
        val type = detail?.type ?: "movie"

        if (stream.isTorrent && stream.url.isNullOrBlank()) {
            // It's a torrent/magnet link! Open external or show dialog
            openExternalPlayer(stream.streamPlayUrl)
            return
        }

        playerManager.playStream(stream, title, type, startPosMs)
        navigateTo(Screen.Player(detail?.id ?: "stream", title, type))
    }

    fun toggleBookmark(detail: MetaDetail) {
        watchProgressRepository.toggleBookmark(
            mediaId = detail.id,
            title = detail.name,
            poster = detail.poster,
            type = detail.type,
            releaseYear = detail.year ?: detail.releaseInfo,
            rating = detail.imdbRating
        )
    }

    fun isBookmarked(mediaId: String): Boolean {
        return watchProgressRepository.isBookmarked(mediaId)
    }

    fun toggleAddon(addonId: String) {
        addonRepository.toggleAddon(addonId)
        loadCatalogs()
    }

    fun uninstallAddon(addonId: String) {
        addonRepository.uninstallAddon(addonId)
        loadCatalogs()
    }

    fun setAddonUrlInput(url: String) {
        _addonUrlInput.value = url
        _addonInstallState.value = null
    }

    fun installAddonFromInput() {
        val url = _addonUrlInput.value.trim()
        if (url.isEmpty()) return
        viewModelScope.launch {
            _isInstallingAddon.value = true
            _addonInstallState.value = "Fetching and validating decentralized manifest..."
            val result = addonRepository.installAddonFromUrl(url)
            result.fold(
                onSuccess = { installed ->
                    _addonInstallState.value = "Successfully installed ${installed.manifest.name} (${installed.latencyMs}ms)"
                    _addonUrlInput.value = ""
                    loadCatalogs()
                },
                onFailure = { error ->
                    _addonInstallState.value = "Failed to install: ${error.message}"
                }
            )
            _isInstallingAddon.value = false
        }
    }

    fun testAddon(url: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val res = addonRepository.testAddonUrl(url)
            res.fold(
                onSuccess = { (manifest, latency) ->
                    onResult("✓ Online: '${manifest.name}' v${manifest.version} • Latency: ${latency}ms • Resources: ${manifest.resources.joinToString()}")
                },
                onFailure = { err ->
                    onResult("✗ Offline / Invalid: ${err.message}")
                }
            )
        }
    }

    fun createCustomAddon(
        id: String,
        name: String,
        version: String,
        description: String,
        types: List<String>,
        resources: List<String>,
        catalogName: String
    ) {
        val manifest = AddonManifest(
            id = id.ifBlank { "custom.addon.${System.currentTimeMillis()}" },
            name = name.ifBlank { "My Community Stream Addon" },
            version = version.ifBlank { "1.0.0" },
            description = description.ifBlank { "Decentralized custom media stream source" },
            types = types,
            resources = resources,
            catalogs = listOf(AddonCatalog(type = types.firstOrNull() ?: "movie", id = "custom_cat", name = catalogName.ifBlank { "Custom Catalog" })),
            behaviorHints = AddonBehaviorHints(p2p = false)
        )
        addonRepository.addManualAddon(manifest, "https://local-custom-addon.stream/manifest.json")
        loadCatalogs()
        Toast.makeText(getApplication(), "Custom Addon '${manifest.name}' Created!", Toast.LENGTH_SHORT).show()
        navigateTo(Screen.CommunityAddons)
    }

    fun openExternalPlayer(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse(url), if (url.startsWith("magnet:")) null else "video/*")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(intent)
        } catch (e: Exception) {
            copyToClipboard("Stream Link", url)
            Toast.makeText(getApplication(), "No external video player found. Link copied to clipboard!", Toast.LENGTH_LONG).show()
        }
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(getApplication(), "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}

package com.example.data.repository

import com.example.data.model.AddonManifest
import com.example.data.model.CommunityRepository
import com.example.data.model.InstalledAddon
import com.example.data.network.AddonApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddonRepository(
    private val apiClient: AddonApiClient = AddonApiClient()
) {
    private val _installedAddons = MutableStateFlow<List<InstalledAddon>>(BuiltInAddons.BUILT_IN_ADDONS)
    val installedAddons: StateFlow<List<InstalledAddon>> = _installedAddons.asStateFlow()

    private val _communityRepos = MutableStateFlow<List<CommunityRepository>>(BuiltInAddons.COMMUNITY_REPOSITORIES)
    val communityRepos: StateFlow<List<CommunityRepository>> = _communityRepos.asStateFlow()

    fun toggleAddon(addonId: String) {
        _installedAddons.update { list ->
            list.map { addon ->
                if (addon.manifest.id == addonId) {
                    addon.copy(isEnabled = !addon.isEnabled)
                } else {
                    addon
                }
            }
        }
    }

    fun uninstallAddon(addonId: String) {
        _installedAddons.update { list ->
            list.filterNot { it.manifest.id == addonId }
        }
    }

    suspend fun installAddonFromUrl(rawUrl: String): Result<InstalledAddon> {
        val result = apiClient.fetchManifest(rawUrl)
        return result.map { (manifest, latency) ->
            val normalized = apiClient.normalizeAddonUrl(rawUrl)
            val newAddon = InstalledAddon(
                manifest = manifest,
                transportUrl = normalized,
                isEnabled = true,
                isBuiltIn = false,
                latencyMs = latency,
                repositoryName = "Custom / Community URL"
            )
            _installedAddons.update { current ->
                // Remove if already exists with same ID, then add to top
                val filtered = current.filterNot { it.manifest.id == manifest.id }
                listOf(newAddon) + filtered
            }
            newAddon
        }
    }

    suspend fun testAddonUrl(rawUrl: String): Result<Pair<AddonManifest, Long>> {
        return apiClient.fetchManifest(rawUrl)
    }

    fun addManualAddon(manifest: AddonManifest, transportUrl: String): InstalledAddon {
        val addon = InstalledAddon(
            manifest = manifest,
            transportUrl = transportUrl,
            isEnabled = true,
            isBuiltIn = false,
            latencyMs = 24,
            repositoryName = "Decentralized Creator"
        )
        _installedAddons.update { current ->
            listOf(addon) + current.filterNot { it.manifest.id == manifest.id }
        }
        return addon
    }

    fun addCommunityRepo(name: String, url: String, description: String) {
        val repo = CommunityRepository(
            id = "custom_repo_${System.currentTimeMillis()}",
            name = name,
            description = description,
            url = url,
            isOfficial = false,
            addonsCount = 0
        )
        _communityRepos.update { it + repo }
    }

    fun getEnabledStreamAddons(): List<InstalledAddon> {
        return _installedAddons.value.filter { it.isEnabled && it.manifest.resources.contains("stream") }
    }

    fun getEnabledCatalogAddons(): List<InstalledAddon> {
        return _installedAddons.value.filter { it.isEnabled && it.manifest.resources.contains("catalog") }
    }
}

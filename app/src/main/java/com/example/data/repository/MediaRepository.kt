package com.example.data.repository

import com.example.data.model.MetaDetail
import com.example.data.model.MetaPreview
import com.example.data.model.StreamItem
import com.example.data.network.AddonApiClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class MediaRepository(
    private val addonRepository: AddonRepository,
    private val apiClient: AddonApiClient = AddonApiClient()
) {

    // Returns a list of featured/trending items for the hero banner and carousel
    fun getFeaturedMedia(): List<MetaDetail> {
        return listOf(
            BuiltInAddons.SAMPLE_MEDIA_DETAILS["bbb_movie"]!!,
            BuiltInAddons.SAMPLE_MEDIA_DETAILS["live_nasa_tv"]!!,
            BuiltInAddons.SAMPLE_MEDIA_DETAILS["tos_movie"]!!,
            BuiltInAddons.SAMPLE_MEDIA_DETAILS["charge_movie"]!!,
            BuiltInAddons.SAMPLE_MEDIA_DETAILS["notld_movie"]!!
        )
    }

    // Aggregates catalogs from all enabled catalog addons
    suspend fun getCatalogsForType(type: String? = null): Map<String, List<MetaPreview>> = coroutineScope {
        val enabledAddons = addonRepository.getEnabledCatalogAddons()
        val result = mutableMapOf<String, MutableList<MetaPreview>>()

        // Populate base catalog from built-in sample data
        val baseItems = BuiltInAddons.SAMPLE_MEDIA_DETAILS.values
            .filter { type == null || it.type == type }
            .map { detail ->
                MetaPreview(
                    id = detail.id,
                    type = detail.type,
                    name = detail.name,
                    poster = detail.poster,
                    banner = detail.background,
                    description = detail.description,
                    releaseInfo = detail.releaseInfo,
                    imdbRating = detail.imdbRating,
                    genres = detail.genres,
                    addonSourceId = detail.addonSourceId,
                    addonSourceName = detail.addonSourceName
                )
            }

        // Group into rows
        val movies = baseItems.filter { it.type == "movie" }
        val series = baseItems.filter { it.type == "series" }
        val channels = baseItems.filter { it.type == "channel" }

        if (movies.isNotEmpty()) result["Popular Open Movies"] = movies.toMutableList()
        if (channels.isNotEmpty()) result["Decentralized Live Channels"] = channels.toMutableList()
        if (series.isNotEmpty()) result["Trending Community Series"] = series.toMutableList()

        // Also query remote installed addons if any custom ones exist
        val remoteTasks = enabledAddons.filterNot { it.isBuiltIn }.flatMap { addon ->
            addon.manifest.catalogs
                .filter { type == null || it.type == type }
                .map { catalog ->
                    async {
                        val res = apiClient.fetchCatalog(addon.transportUrl, catalog.type, catalog.id)
                        Pair("${addon.manifest.name} • ${catalog.name}", res.getOrNull().orEmpty())
                    }
                }
        }

        val remoteResults = remoteTasks.awaitAll()
        for ((title, items) in remoteResults) {
            if (items.isNotEmpty()) {
                result[title] = items.toMutableList()
            }
        }

        result
    }

    // Get detail for a specific media item
    suspend fun getMediaDetail(mediaId: String, type: String = "movie"): MetaDetail? {
        val local = BuiltInAddons.SAMPLE_MEDIA_DETAILS[mediaId]
        if (local != null) return local

        // Otherwise search enabled addons with 'meta' resource
        val metaAddons = addonRepository.installedAddons.value
            .filter { it.isEnabled && it.manifest.resources.contains("meta") && !it.isBuiltIn }

        for (addon in metaAddons) {
            // Could query /meta/$type/$mediaId.json
        }

        return local
    }

    // Resolves streams across all enabled stream addons
    suspend fun resolveStreams(mediaId: String, type: String = "movie"): List<StreamItem> = coroutineScope {
        val enabledStreamAddons = addonRepository.getEnabledStreamAddons()
        val allStreams = mutableListOf<StreamItem>()

        // 1. Get built-in streams
        val builtInStreams = BuiltInAddons.resolveStreamsForMedia(mediaId)
        allStreams.addAll(builtInStreams)

        // 2. Query any custom installed remote addons that support streams
        val remoteAddons = enabledStreamAddons.filterNot { it.isBuiltIn }
        val tasks = remoteAddons.map { addon ->
            async {
                val res = apiClient.fetchStreams(addon.transportUrl, type, mediaId)
                res.getOrNull().orEmpty()
            }
        }

        val remoteStreams = tasks.awaitAll().flatten()
        allStreams.addAll(remoteStreams)

        allStreams
    }

    // Global search across all media and installed addons
    suspend fun search(query: String, filterType: String? = null): List<MetaPreview> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()

        val allItems = BuiltInAddons.SAMPLE_MEDIA_DETAILS.values
            .map { detail ->
                MetaPreview(
                    id = detail.id,
                    type = detail.type,
                    name = detail.name,
                    poster = detail.poster,
                    banner = detail.background,
                    description = detail.description,
                    releaseInfo = detail.releaseInfo,
                    imdbRating = detail.imdbRating,
                    genres = detail.genres,
                    addonSourceId = detail.addonSourceId,
                    addonSourceName = detail.addonSourceName
                )
            }

        return allItems.filter { item ->
            val matchesType = filterType == null || item.type == filterType
            val matchesText = item.name.lowercase().contains(q) ||
                    (item.description?.lowercase()?.contains(q) == true) ||
                    (item.genres?.any { it.lowercase().contains(q) } == true)
            matchesType && matchesText
        }
    }
}

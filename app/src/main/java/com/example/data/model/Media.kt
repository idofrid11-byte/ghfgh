package com.example.data.model

import com.squareup.moshi.JsonClass

enum class MediaType(val key: String, val label: String) {
    MOVIE("movie", "Movies"),
    SERIES("series", "Series"),
    CHANNEL("channel", "Live TV"),
    ANIME("anime", "Anime"),
    OTHER("other", "Open Media")
}

@JsonClass(generateAdapter = true)
data class MetaPreview(
    val id: String,
    val type: String,
    val name: String,
    val poster: String? = null,
    val posterShape: String? = "regular",
    val banner: String? = null,
    val logo: String? = null,
    val description: String? = null,
    val releaseInfo: String? = null,
    val imdbRating: String? = null,
    val genres: List<String>? = null,
    val addonSourceId: String? = null,
    val addonSourceName: String? = null
)

@JsonClass(generateAdapter = true)
data class MetaDetail(
    val id: String,
    val type: String,
    val name: String,
    val genres: List<String> = emptyList(),
    val poster: String? = null,
    val background: String? = null,
    val logo: String? = null,
    val description: String? = null,
    val releaseInfo: String? = null,
    val year: String? = null,
    val runtime: String? = null,
    val director: List<String> = emptyList(),
    val cast: List<String> = emptyList(),
    val imdbRating: String? = null,
    val videos: List<VideoEpisode> = emptyList(),
    val trailerStreams: List<StreamItem> = emptyList(),
    val addonSourceId: String? = null,
    val addonSourceName: String? = null
)

@JsonClass(generateAdapter = true)
data class VideoEpisode(
    val id: String,
    val title: String,
    val released: String? = null,
    val season: Int = 1,
    val episode: Int = 1,
    val thumbnail: String? = null,
    val overview: String? = null,
    val streams: List<StreamItem>? = null
)

@JsonClass(generateAdapter = true)
data class StreamItem(
    val title: String? = null,
    val name: String? = null,
    val url: String? = null,
    val infoHash: String? = null,
    val fileIdx: Int? = null,
    val externalUrl: String? = null,
    val behaviorHints: StreamBehaviorHints? = null,
    // Enriched metadata for decentralized resolvers
    val quality: String = "1080p",
    val seeders: Int? = null,
    val size: String? = null,
    val audio: String? = null,
    val sourceAddon: String = "Community Addon",
    val format: String = "MP4"
) {
    val isTorrent: Boolean
        get() = !infoHash.isNullOrBlank()

    val streamPlayUrl: String
        get() = url ?: externalUrl ?: if (isTorrent) "magnet:?xt=urn:btih:$infoHash" else ""
}

@JsonClass(generateAdapter = true)
data class StreamBehaviorHints(
    val notWebReady: Boolean = false,
    val proxyHeaders: Map<String, String>? = null,
    val videoHash: String? = null,
    val videoSize: Long? = null,
    val filename: String? = null
)

data class WatchProgress(
    val mediaId: String,
    val title: String,
    val poster: String?,
    val type: String,
    val positionMs: Long,
    val durationMs: Long,
    val lastWatchedAt: Long = System.currentTimeMillis(),
    val season: Int? = null,
    val episode: Int? = null,
    val episodeTitle: String? = null
) {
    val progressFraction: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}

data class Bookmark(
    val mediaId: String,
    val title: String,
    val poster: String?,
    val type: String,
    val releaseYear: String?,
    val rating: String?,
    val bookmarkedAt: Long = System.currentTimeMillis()
)

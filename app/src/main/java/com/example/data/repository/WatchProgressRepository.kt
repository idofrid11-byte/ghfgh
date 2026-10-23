package com.example.data.repository

import com.example.data.model.Bookmark
import com.example.data.model.WatchProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WatchProgressRepository {

    // Initial pre-populated sample progress so user sees Continue Watching right away
    private val _watchHistory = MutableStateFlow<List<WatchProgress>>(
        listOf(
            WatchProgress(
                mediaId = "bbb_movie",
                title = "Big Buck Bunny",
                poster = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Big_buck_bunny_poster_big.jpg/640px-Big_buck_bunny_poster_big.jpg",
                type = "movie",
                positionMs = 380000,
                durationMs = 600000
            ),
            WatchProgress(
                mediaId = "tos_movie",
                title = "Tears of Steel",
                poster = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Tears_of_Steel_poster.jpg/640px-Tears_of_Steel_poster.jpg",
                type = "movie",
                positionMs = 240000,
                durationMs = 720000
            )
        )
    )
    val watchHistory: StateFlow<List<WatchProgress>> = _watchHistory.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(
        listOf(
            Bookmark(
                mediaId = "live_nasa_tv",
                title = "NASA TV HD (Live)",
                poster = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=640",
                type = "channel",
                releaseYear = "Live",
                rating = "9.2"
            ),
            Bookmark(
                mediaId = "notld_movie",
                title = "Night of the Living Dead",
                poster = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/Night_of_the_Living_Dead_%281968%29_theatrical_poster.jpg/640px-Night_of_the_Living_Dead_%281968%29_theatrical_poster.jpg",
                type = "movie",
                releaseYear = "1968",
                rating = "7.9"
            )
        )
    )
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    fun updateProgress(
        mediaId: String,
        title: String,
        poster: String?,
        type: String,
        positionMs: Long,
        durationMs: Long,
        season: Int? = null,
        episode: Int? = null,
        episodeTitle: String? = null
    ) {
        if (durationMs <= 0) return
        val progress = WatchProgress(
            mediaId = mediaId,
            title = title,
            poster = poster,
            type = type,
            positionMs = positionMs,
            durationMs = durationMs,
            lastWatchedAt = System.currentTimeMillis(),
            season = season,
            episode = episode,
            episodeTitle = episodeTitle
        )
        _watchHistory.update { list ->
            listOf(progress) + list.filterNot { it.mediaId == mediaId }
        }
    }

    fun isBookmarked(mediaId: String): Boolean {
        return _bookmarks.value.any { it.mediaId == mediaId }
    }

    fun toggleBookmark(
        mediaId: String,
        title: String,
        poster: String?,
        type: String,
        releaseYear: String?,
        rating: String?
    ) {
        _bookmarks.update { list ->
            if (list.any { it.mediaId == mediaId }) {
                list.filterNot { it.mediaId == mediaId }
            } else {
                listOf(
                    Bookmark(
                        mediaId = mediaId,
                        title = title,
                        poster = poster,
                        type = type,
                        releaseYear = releaseYear,
                        rating = rating
                    )
                ) + list
            }
        }
    }

    fun removeHistoryItem(mediaId: String) {
        _watchHistory.update { list -> list.filterNot { it.mediaId == mediaId } }
    }
}

package com.example.player

import com.example.data.model.StreamItem

enum class PlayerResizeMode {
    FIT,
    ZOOM,
    FILL
}

data class StreamPlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = true,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val resizeMode: PlayerResizeMode = PlayerResizeMode.FIT,
    val errorMessage: String? = null,
    val activeStream: StreamItem? = null,
    val mediaTitle: String = "",
    val mediaType: String = "movie"
) {
    val progressFraction: Float
        get() = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}

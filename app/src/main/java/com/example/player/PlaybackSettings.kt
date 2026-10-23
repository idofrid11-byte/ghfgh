package com.example.player

import androidx.compose.ui.graphics.Color
import androidx.media3.ui.AspectRatioFrameLayout

enum class SubtitleSize(val label: String, val spSize: Float) {
    SMALL("Small", 14f),
    MEDIUM("Medium", 18f),
    LARGE("Large", 24f),
    EXTRA_LARGE("Extra Large", 30f)
}

enum class SubtitleColor(val label: String, val color: Color) {
    WHITE("White", Color.White),
    YELLOW("Yellow", Color(0xFFFFEB3B)),
    CYAN("Cyan", Color(0xFF00E5FF)),
    GREEN("Green", Color(0xFF00E676))
}

enum class SubtitleBackground(val label: String, val color: Color) {
    TRANSPARENT("None", Color.Transparent),
    SEMI_TRANSPARENT("Dark Glass", Color(0xB3000000)),
    SOLID_BLACK("Solid Black", Color.Black)
}

enum class AspectRatioMode(val label: String, val resizeMode: Int) {
    FIT("Fit (16:9)", AspectRatioFrameLayout.RESIZE_MODE_FIT),
    ZOOM("Zoom (Fill Screen)", AspectRatioFrameLayout.RESIZE_MODE_ZOOM),
    FILL("Stretch", AspectRatioFrameLayout.RESIZE_MODE_FILL)
}

data class PlaybackSettingsState(
    // Audio Settings
    val selectedAudioTrackIndex: Int = 0,
    val audioTracks: List<String> = listOf(
        "English [Original] (5.1 Dolby Digital)",
        "English Stereo (AAC 320kbps)",
        "Spanish Dub (Español 5.1)",
        "French Dub (Français)",
        "Japanese Audio (日本語)",
        "Director's Commentary"
    ),
    val audioDelayMs: Int = 0, // -500 to +500 ms
    val audioBoostEnabled: Boolean = false,
    val dialogueEnhanceEnabled: Boolean = true,

    // Subtitle Settings
    val subtitlesEnabled: Boolean = true,
    val selectedSubtitleTrackIndex: Int = 1, // 0 = Off
    val subtitleTracks: List<String> = listOf(
        "Off",
        "English [Full SDH]",
        "English [Dialogue Only]",
        "Spanish (Español)",
        "French (Français)",
        "German (Deutsch)",
        "Japanese (日本語)",
        "Arabic (العربية)"
    ),
    val subtitleDelayMs: Int = 0, // -2000 to +2000 ms
    val subtitleSize: SubtitleSize = SubtitleSize.MEDIUM,
    val subtitleColor: SubtitleColor = SubtitleColor.YELLOW,
    val subtitleBackground: SubtitleBackground = SubtitleBackground.SEMI_TRANSPARENT,

    // Playback Settings
    val playbackSpeed: Float = 1.0f,
    val loopMode: Boolean = false,
    val sleepTimerMinutes: Int = 0, // 0 = Off, 15, 30, 45, 60
    val aspectRatioMode: AspectRatioMode = AspectRatioMode.FIT,
    val hardwareAcceleration: Boolean = true,
    val streamQuality: String = "1080p FHD (Auto)",
    val availableQualities: List<String> = listOf(
        "4K UHD (High Bitrate)",
        "1080p FHD (Recommended)",
        "720p HD (Data Saver)",
        "Auto (Adaptive Bitrate)"
    )
)

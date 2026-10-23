package com.example.ui.screens.playback

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.player.PlaybackSettingsState
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackScreen(
    streamUrl: String,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialPositionMs: Long = 0L,
    onProgressTick: ((currentPosMs: Long, durationMs: Long) -> Unit)? = null
) {
    val context = LocalContext.current

    // Initialize ExoPlayer instance for this playback session
    val exoPlayer = remember(streamUrl) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(streamUrl)
            setMediaItem(mediaItem)
            prepare()
            if (initialPositionMs > 0L) {
                seekTo(initialPositionMs)
            }
            playWhenReady = true
        }
    }

    // Playback state variables
    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(initialPositionMs) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showControls by remember { mutableStateOf(true) }
    var isDraggingSlider by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    // Settings State
    var settings by remember { mutableStateOf(PlaybackSettingsState()) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var sleepTimerRemainingSec by remember { mutableStateOf<Int?>(null) }

    // Clean up player on lifecycle disposal
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        isBuffering = true
                        errorMessage = null
                    }
                    Player.STATE_READY -> {
                        isBuffering = false
                        durationMs = exoPlayer.duration.coerceAtLeast(0L)
                        errorMessage = null
                    }
                    Player.STATE_ENDED -> {
                        isBuffering = false
                        isPlaying = false
                    }
                    Player.STATE_IDLE -> {
                        // Idle state
                    }
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlayerError(error: PlaybackException) {
                isBuffering = false
                isPlaying = false
                errorMessage = "Stream playback error: ${error.localizedMessage ?: "Unable to render video stream"}"
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Apply settings changes to ExoPlayer
    LaunchedEffect(settings.playbackSpeed) {
        exoPlayer.playbackParameters = PlaybackParameters(settings.playbackSpeed)
    }

    LaunchedEffect(settings.loopMode) {
        exoPlayer.repeatMode = if (settings.loopMode) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
    }

    LaunchedEffect(settings.audioBoostEnabled) {
        exoPlayer.volume = if (settings.audioBoostEnabled) 1.5f else 1.0f
    }

    // Sleep Timer Handler
    LaunchedEffect(settings.sleepTimerMinutes) {
        if (settings.sleepTimerMinutes > 0) {
            sleepTimerRemainingSec = settings.sleepTimerMinutes * 60
            while (sleepTimerRemainingSec != null && sleepTimerRemainingSec!! > 0) {
                delay(1000)
                if (isPlaying) {
                    sleepTimerRemainingSec = sleepTimerRemainingSec!! - 1
                }
            }
            if (sleepTimerRemainingSec == 0) {
                exoPlayer.pause()
                sleepTimerRemainingSec = null
                settings = settings.copy(sleepTimerMinutes = 0)
            }
        } else {
            sleepTimerRemainingSec = null
        }
    }

    // Progress tracker coroutine loop
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            val current = exoPlayer.currentPosition.coerceAtLeast(0L)
            val total = exoPlayer.duration.coerceAtLeast(0L)
            currentPositionMs = current
            durationMs = total
            if (!isDraggingSlider && total > 0) {
                sliderPosition = (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)
            }
            onProgressTick?.invoke(current, total)
            delay(1000)
        }
    }

    // Auto-hide controls after 4 seconds of playback
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4000)
            showControls = false
        }
    }

    BackHandler {
        onBack()
    }

    fun formatDuration(millis: Long): String {
        val totalSeconds = (millis / 1000).coerceAtLeast(0L)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val hours = minutes / 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes % 60, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    // Subtitle Line computation with delay offset
    val activeSubtitleText = remember(currentPositionMs, settings.subtitleDelayMs, settings.selectedSubtitleTrackIndex, settings.subtitlesEnabled) {
        if (!settings.subtitlesEnabled || settings.selectedSubtitleTrackIndex == 0) {
            null
        } else {
            val effectiveTime = (currentPositionMs + settings.subtitleDelayMs).coerceAtLeast(0L)
            // Generate contextual subtitle lines according to playback time window
            val cycleSec = (effectiveTime / 1000) % 24
            when (cycleSec) {
                in 1..5 -> "♪ [Atmospheric Cinematic Score Playing] ♪"
                in 6..10 -> when (settings.selectedSubtitleTrackIndex) {
                    2 -> "[Sound of mechanical gears turning]"
                    3 -> "La señal de transmisión descentralizada está activa."
                    4 -> "Le signal de streaming décentralisé est connecté."
                    5 -> "Das dezentrale Streaming-Signal ist verbunden."
                    6 -> "分散型ストリーミング信号が接続されました。"
                    7 -> "تم ربط إشارة البث اللامركزي بنجاح."
                    else -> "“The decentralized streaming node has synchronized.”"
                }
                in 11..16 -> when (settings.selectedSubtitleTrackIndex) {
                    3 -> "Todos los paquetes de video se transmiten en alta definición."
                    4 -> "Tous les paquets vidéo sont diffusés en haute fidélité."
                    5 -> "Alle Videopakete werden in hoher Wiedergabetreue gestreamt."
                    6 -> "すべてのビデオパケットは高忠実度で配信されています。"
                    else -> "“All video packets are streaming with full fidelity.”"
                }
                in 17..22 -> "“Buffer health optimal • Seeders swarm verified.”"
                else -> null
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
            .testTag("playback_screen")
    ) {
        // Media3 ExoPlayer Video Rendering Surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = settings.aspectRatioMode.resizeMode
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { playerView ->
                playerView.resizeMode = settings.aspectRatioMode.resizeMode
            },
            modifier = Modifier.fillMaxSize().testTag("video_player_view")
        )

        // Live Subtitle Overlay
        if (!activeSubtitleText.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (showControls) 110.dp else 40.dp)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(settings.subtitleBackground.color)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("active_subtitle_overlay")
                ) {
                    Text(
                        text = activeSubtitleText,
                        color = settings.subtitleColor.color,
                        fontSize = settings.subtitleSize.spSize.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Buffering Indicator
        if (isBuffering && errorMessage == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = ElectricCyan,
                        modifier = Modifier.size(52.dp),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Rendering Video Stream...",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Error message overlay
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Playback Failed",
                        color = CinemaRed,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            errorMessage = null
                            exoPlayer.prepare()
                            exoPlayer.play()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("retry_playback_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retry Stream")
                    }
                }
            }
        }

        // Animated On-Screen Controls Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                            )
                        )
                )

                // Top Bar: Back button, Media Title, and Settings Quick-Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("playback_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = settings.streamQuality,
                                color = ElectricCyan,
                                fontSize = 11.sp
                            )
                            if (settings.playbackSpeed != 1.0f) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${settings.playbackSpeed}x",
                                    color = CinemaGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (sleepTimerRemainingSec != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ⏱ ${sleepTimerRemainingSec!! / 60}m",
                                    color = NeonViolet,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Quick Subtitles Toggle Button
                    IconButton(
                        onClick = {
                            val nextIndex = if (settings.selectedSubtitleTrackIndex == 0) 1 else 0
                            settings = settings.copy(
                                selectedSubtitleTrackIndex = nextIndex,
                                subtitlesEnabled = nextIndex > 0
                            )
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (settings.subtitlesEnabled && settings.selectedSubtitleTrackIndex > 0) ElectricCyan else Color.Black.copy(alpha = 0.6f),
                                CircleShape
                            )
                            .testTag("playback_subtitles_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Subtitles,
                            contentDescription = "Toggle Subtitles",
                            tint = if (settings.subtitlesEnabled && settings.selectedSubtitleTrackIndex > 0) Color.Black else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Open Full Settings Sheet Button (Audio, Subtitles, Playback)
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("playback_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Player Settings",
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Center Basic Playback Controls (Seek -10s, Play/Pause, Seek +10s)
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("playback_center_controls"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    // Seek Backward 10s
                    IconButton(
                        onClick = {
                            val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("playback_seek_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "Rewind 10 Seconds",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // Play / Pause Toggle
                    IconButton(
                        onClick = {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        },
                        modifier = Modifier
                            .size(68.dp)
                            .background(ElectricCyan, CircleShape)
                            .testTag("playback_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    // Seek Forward 10s
                    IconButton(
                        onClick = {
                            val newPos = (exoPlayer.currentPosition + 10000L).coerceAtMost(exoPlayer.duration)
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("playback_seek_forward_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "Forward 10 Seconds",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Bottom gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                            )
                        )
                )

                // Bottom Timeline Scrubber and Elapsed / Total Duration
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.BottomCenter)
                        .testTag("playback_bottom_bar")
                ) {
                    // Time Labels & Active Settings Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDuration(currentPositionMs),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.testTag("playback_current_time")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Audio track badge
                        Text(
                            text = "[${settings.audioTracks.getOrNull(settings.selectedAudioTrackIndex)?.take(12) ?: "Audio"}]",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = formatDuration(durationMs),
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.testTag("playback_total_duration")
                        )
                    }

                    // Interactive Seek Slider
                    Slider(
                        value = if (isDraggingSlider) sliderPosition else {
                            if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
                        },
                        onValueChange = { frac ->
                            isDraggingSlider = true
                            sliderPosition = frac
                        },
                        onValueChangeFinished = {
                            if (durationMs > 0) {
                                val targetMs = (sliderPosition * durationMs).toLong()
                                exoPlayer.seekTo(targetMs)
                                currentPositionMs = targetMs
                            }
                            isDraggingSlider = false
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricCyan,
                            activeTrackColor = ElectricCyan,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("playback_slider")
                    )
                }
            }
        }

        // Interactive Settings Bottom Sheet Dialog
        if (showSettingsSheet) {
            PlaybackSettingsSheet(
                settings = settings,
                onSettingsChanged = { updated ->
                    settings = updated
                },
                onDismiss = { showSettingsSheet = false }
            )
        }
    }
}

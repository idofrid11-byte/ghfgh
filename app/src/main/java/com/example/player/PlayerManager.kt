package com.example.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.model.StreamItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class PlayerManager(
    private val context: Context,
    private val onProgressTick: ((positionMs: Long, durationMs: Long) -> Unit)? = null
) {
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    private val _playerState = MutableStateFlow(StreamPlayerState())
    val playerState: StateFlow<StreamPlayerState> = _playerState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _playerState.update { it.copy(isBuffering = true, errorMessage = null) }
                    }
                    Player.STATE_READY -> {
                        _playerState.update {
                            it.copy(
                                isBuffering = false,
                                durationMs = exoPlayer.duration.coerceAtLeast(0L),
                                errorMessage = null
                            )
                        }
                    }
                    Player.STATE_ENDED -> {
                        _playerState.update { it.copy(isPlaying = false, isBuffering = false) }
                    }
                    Player.STATE_IDLE -> {
                        // idle
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _playerState.update {
                    it.copy(
                        isBuffering = false,
                        isPlaying = false,
                        errorMessage = "Stream playback error: ${error.localizedMessage ?: "Unable to stream source"}"
                    )
                }
            }
        })
    }

    fun playStream(
        stream: StreamItem,
        title: String,
        type: String,
        startPositionMs: Long = 0L
    ) {
        val streamUrl = stream.url ?: stream.externalUrl ?: ""
        if (streamUrl.isBlank()) {
            _playerState.update {
                it.copy(errorMessage = "Selected stream does not have a direct playable URL. (Try opening in external player for magnets).")
            }
            return
        }

        _playerState.update {
            it.copy(
                activeStream = stream,
                mediaTitle = title,
                mediaType = type,
                isBuffering = true,
                errorMessage = null
            )
        }

        val mediaItem = MediaItem.fromUri(Uri.parse(streamUrl))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        if (startPositionMs > 0L) {
            exoPlayer.seekTo(startPositionMs)
        }
        exoPlayer.play()
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun seekForward(millis: Long = 10000L) {
        val newPos = (exoPlayer.currentPosition + millis).coerceAtMost(exoPlayer.duration)
        exoPlayer.seekTo(newPos)
        _playerState.update { it.copy(currentPositionMs = newPos) }
    }

    fun seekBackward(millis: Long = 10000L) {
        val newPos = (exoPlayer.currentPosition - millis).coerceAtLeast(0L)
        exoPlayer.seekTo(newPos)
        _playerState.update { it.copy(currentPositionMs = newPos) }
    }

    fun seekToFraction(fraction: Float) {
        val duration = exoPlayer.duration
        if (duration > 0L) {
            val targetPos = (fraction * duration).toLong()
            exoPlayer.seekTo(targetPos)
            _playerState.update { it.copy(currentPositionMs = targetPos) }
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer.playbackParameters = PlaybackParameters(speed)
        _playerState.update { it.copy(playbackSpeed = speed) }
    }

    fun cycleResizeMode() {
        val next = when (_playerState.value.resizeMode) {
            PlayerResizeMode.FIT -> PlayerResizeMode.ZOOM
            PlayerResizeMode.ZOOM -> PlayerResizeMode.FILL
            PlayerResizeMode.FILL -> PlayerResizeMode.FIT
        }
        _playerState.update { it.copy(resizeMode = next) }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val current = exoPlayer.currentPosition.coerceAtLeast(0L)
                val total = exoPlayer.duration.coerceAtLeast(0L)
                _playerState.update {
                    it.copy(currentPositionMs = current, durationMs = total)
                }
                onProgressTick?.invoke(current, total)
                delay(1000)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracker()
        exoPlayer.release()
    }
}

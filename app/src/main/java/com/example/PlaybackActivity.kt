package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.ui.screens.playback.PlaybackScreen
import com.example.ui.theme.NovaStreamTheme

class PlaybackActivity : ComponentActivity() {

    companion object {
        const val EXTRA_STREAM_URL = "extra_stream_url"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_POSITION_MS = "extra_position_ms"
        const val DEFAULT_STREAM = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

        fun createIntent(
            context: Context,
            streamUrl: String,
            title: String = "Decentralized Stream",
            initialPositionMs: Long = 0L
        ): Intent {
            return Intent(context, PlaybackActivity::class.java).apply {
                putExtra(EXTRA_STREAM_URL, streamUrl)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_POSITION_MS, initialPositionMs)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val streamUrl = intent?.getStringExtra(EXTRA_STREAM_URL)
            ?: intent?.data?.toString()
            ?: DEFAULT_STREAM

        val title = intent?.getStringExtra(EXTRA_TITLE) ?: "Stream Playback"
        val initialPosMs = intent?.getLongExtra(EXTRA_POSITION_MS, 0L) ?: 0L

        setContent {
            NovaStreamTheme {
                PlaybackScreen(
                    streamUrl = streamUrl,
                    title = title,
                    initialPositionMs = initialPosMs,
                    onBack = { finish() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

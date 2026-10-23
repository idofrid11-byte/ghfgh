package com.example

import com.example.data.model.StreamItem
import com.example.data.model.WatchProgress
import com.example.data.network.AddonApiClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testNormalizeAddonUrl() {
        val client = AddonApiClient()
        val stremioUrl = "stremio://v3-cinemeta.strem.io/manifest.json"
        val normalized = client.normalizeAddonUrl(stremioUrl)
        assertEquals("https://v3-cinemeta.strem.io/manifest.json", normalized)

        val rawDomain = "https://community-addon.org"
        val normalizedDomain = client.normalizeAddonUrl(rawDomain)
        assertEquals("https://community-addon.org/manifest.json", normalizedDomain)
    }

    @Test
    fun testStreamTorrentDetection() {
        val torrentStream = StreamItem(
            title = "Big Buck Bunny 4K HDR",
            infoHash = "2b14421b5be26a42a983ef9a782803b0c8d9c123"
        )
        assertTrue(torrentStream.isTorrent)
        assertEquals("magnet:?xt=urn:btih:2b14421b5be26a42a983ef9a782803b0c8d9c123", torrentStream.streamPlayUrl)

        val directStream = StreamItem(
            title = "Big Buck Bunny 1080p MP4",
            url = "https://example.com/video.mp4"
        )
        assertEquals(false, directStream.isTorrent)
        assertEquals("https://example.com/video.mp4", directStream.streamPlayUrl)
    }

    @Test
    fun testWatchProgressFraction() {
        val progress = WatchProgress(
            mediaId = "test_media",
            title = "Test Media",
            poster = null,
            type = "movie",
            positionMs = 300000,
            durationMs = 600000
        )
        assertEquals(0.5f, progress.progressFraction, 0.001f)
    }
}

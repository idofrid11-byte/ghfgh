package com.example.data.network

import com.example.data.model.AddonManifest
import com.example.data.model.MetaDetail
import com.example.data.model.MetaPreview
import com.example.data.model.StreamItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AddonApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    // Normalizes stremio:// URLs to https://
    fun normalizeAddonUrl(rawUrl: String): String {
        var url = rawUrl.trim()
        if (url.startsWith("stremio://")) {
            url = "https://" + url.removePrefix("stremio://")
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://$url"
        }
        // If it doesn't end with manifest.json, append it
        if (!url.endsWith("/manifest.json") && !url.endsWith(".json")) {
            url = if (url.endsWith("/")) "${url}manifest.json" else "$url/manifest.json"
        }
        return url
    }

    suspend fun fetchManifest(transportUrl: String): Result<Pair<AddonManifest, Long>> = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            val url = normalizeAddonUrl(transportUrl)
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                val latency = System.currentTimeMillis() - startTime
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
                }
                val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
                val adapter = moshi.adapter(AddonManifest::class.java)
                val manifest = adapter.fromJson(body) ?: return@withContext Result.failure(Exception("Failed to parse manifest"))
                Result.success(Pair(manifest, latency))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchCatalog(
        addonBaseUrl: String,
        type: String,
        catalogId: String,
        extraParams: String? = null
    ): Result<List<MetaPreview>> = withContext(Dispatchers.IO) {
        try {
            val base = addonBaseUrl.removeSuffix("/manifest.json").removeSuffix("/")
            val path = if (!extraParams.isNullOrBlank()) {
                "$base/catalog/$type/$catalogId/$extraParams.json"
            } else {
                "$base/catalog/$type/$catalogId.json"
            }
            val request = Request.Builder().url(path).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP ${response.code}"))
                }
                val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
                val json = JSONObject(body)
                val metasArray = json.optJSONArray("metas") ?: return@withContext Result.success(emptyList())
                val metas = mutableListOf<MetaPreview>()
                for (i in 0 until metasArray.length()) {
                    val item = metasArray.getJSONObject(i)
                    metas.add(
                        MetaPreview(
                            id = item.optString("id"),
                            type = item.optString("type", type),
                            name = item.optString("name", "Untitled"),
                            poster = item.optString("poster").takeIf { it.isNotEmpty() },
                            description = item.optString("description").takeIf { it.isNotEmpty() },
                            releaseInfo = item.optString("releaseInfo").takeIf { it.isNotEmpty() },
                            imdbRating = item.optString("imdbRating").takeIf { it.isNotEmpty() },
                            addonSourceId = base
                        )
                    )
                }
                Result.success(metas)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchStreams(
        addonBaseUrl: String,
        type: String,
        mediaId: String
    ): Result<List<StreamItem>> = withContext(Dispatchers.IO) {
        try {
            val base = addonBaseUrl.removeSuffix("/manifest.json").removeSuffix("/")
            val path = "$base/stream/$type/$mediaId.json"
            val request = Request.Builder().url(path).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP ${response.code}"))
                }
                val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
                val json = JSONObject(body)
                val streamsArray = json.optJSONArray("streams") ?: return@withContext Result.success(emptyList())
                val streams = mutableListOf<StreamItem>()
                for (i in 0 until streamsArray.length()) {
                    val s = streamsArray.getJSONObject(i)
                    streams.add(
                        StreamItem(
                            title = s.optString("title", s.optString("name", "Stream source")),
                            name = s.optString("name"),
                            url = s.optString("url").takeIf { it.isNotEmpty() },
                            infoHash = s.optString("infoHash").takeIf { it.isNotEmpty() },
                            fileIdx = if (s.has("fileIdx")) s.getInt("fileIdx") else null,
                            externalUrl = s.optString("externalUrl").takeIf { it.isNotEmpty() },
                            quality = if (s.optString("title").contains("4K", ignoreCase = true)) "4K UHD" 
                                      else if (s.optString("title").contains("720", ignoreCase = true)) "720p" 
                                      else "1080p",
                            sourceAddon = base
                        )
                    )
                }
                Result.success(streams)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

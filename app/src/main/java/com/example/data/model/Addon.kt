package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddonManifest(
    val id: String,
    val name: String,
    val version: String,
    val description: String,
    val types: List<String> = listOf("movie", "series"),
    val resources: List<String> = listOf("catalog", "meta", "stream"),
    val catalogs: List<AddonCatalog> = emptyList(),
    val idPrefixes: List<String>? = null,
    val background: String? = null,
    val logo: String? = null,
    val contactEmail: String? = null,
    val behaviorHints: AddonBehaviorHints? = null
)

@JsonClass(generateAdapter = true)
data class AddonCatalog(
    val type: String,
    val id: String,
    val name: String,
    val extra: List<CatalogExtra>? = null
)

@JsonClass(generateAdapter = true)
data class CatalogExtra(
    val name: String,
    val isRequired: Boolean = false,
    val options: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class AddonBehaviorHints(
    val adult: Boolean = false,
    val p2p: Boolean = false,
    val configurable: Boolean = false,
    val configurationRequired: Boolean = false
)

data class InstalledAddon(
    val manifest: AddonManifest,
    val transportUrl: String,
    val isEnabled: Boolean = true,
    val isBuiltIn: Boolean = false,
    val installedAt: Long = System.currentTimeMillis(),
    val latencyMs: Long? = null,
    val repositoryName: String = "Decentralized Community"
)

data class CommunityRepository(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val isOfficial: Boolean = false,
    val addonsCount: Int = 0
)

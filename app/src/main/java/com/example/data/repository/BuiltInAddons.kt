package com.example.data.repository

import com.example.data.model.AddonBehaviorHints
import com.example.data.model.AddonCatalog
import com.example.data.model.AddonManifest
import com.example.data.model.CommunityRepository
import com.example.data.model.InstalledAddon
import com.example.data.model.MetaDetail
import com.example.data.model.MetaPreview
import com.example.data.model.StreamItem
import com.example.data.model.VideoEpisode

object BuiltInAddons {

    val COMMUNITY_REPOSITORIES = listOf(
        CommunityRepository(
            id = "repo_official_mesh",
            name = "Decentralized Official Community Hub",
            description = "Verified community streaming & metadata addons adhering to Stremio Addon Protocol v3.",
            url = "https://addons.stremio-community.org/repository.json",
            isOfficial = true,
            addonsCount = 14
        ),
        CommunityRepository(
            id = "repo_open_culture",
            name = "Open Culture & Archive Media Federation",
            description = "Public domain films, creative commons animations, NASA feeds, and open educational streams.",
            url = "https://archive.org/addons/manifest_registry.json",
            isOfficial = false,
            addonsCount = 8
        ),
        CommunityRepository(
            id = "repo_p2p_mesh",
            name = "Decentralized P2P & WebTorrent Mesh",
            description = "BitTorrent distributed swarm resolvers, DHT indexing nodes, and decentralized scrapers.",
            url = "https://p2p-addons.decentralized.stream/repo.json",
            isOfficial = false,
            addonsCount = 6
        )
    )

    val BUILT_IN_ADDONS = listOf(
        InstalledAddon(
            manifest = AddonManifest(
                id = "org.community.publicdomain",
                name = "Open Cinema & Public Domain Streams",
                version = "2.4.0",
                description = "Watch classic masterpieces, silent comedies, and Creative Commons open movies with instant 4K & 1080p high-speed streams.",
                types = listOf("movie", "other"),
                resources = listOf("catalog", "meta", "stream"),
                catalogs = listOf(
                    AddonCatalog(type = "movie", id = "public_classics", name = "Open Classics"),
                    AddonCatalog(type = "movie", id = "cc_movies", name = "Creative Commons Cinema")
                ),
                logo = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=400",
                behaviorHints = AddonBehaviorHints(p2p = false)
            ),
            transportUrl = "https://open-cinema.novastream.community/manifest.json",
            isEnabled = true,
            isBuiltIn = true,
            latencyMs = 38,
            repositoryName = "Open Culture Federation"
        ),
        InstalledAddon(
            manifest = AddonManifest(
                id = "org.community.openiptv",
                name = "Open Live TV & IPTV Channels",
                version = "3.1.2",
                description = "Decentralized live 24/7 video streams including NASA TV HD spacewalks, DW News, Bloomberg Global, and Action Sports.",
                types = listOf("channel", "tv"),
                resources = listOf("catalog", "meta", "stream"),
                catalogs = listOf(
                    AddonCatalog(type = "channel", id = "live_channels", name = "Live Channels")
                ),
                logo = "https://images.unsplash.com/photo-1593784991095-a205069470b6?w=400",
                behaviorHints = AddonBehaviorHints(p2p = false)
            ),
            transportUrl = "https://iptv.novastream.community/manifest.json",
            isEnabled = true,
            isBuiltIn = true,
            latencyMs = 45,
            repositoryName = "Decentralized Hub"
        ),
        InstalledAddon(
            manifest = AddonManifest(
                id = "org.blender.openmovies",
                name = "Blender Studio 4K Open Animation",
                version = "1.8.0",
                description = "High-definition 4K animations, sci-fi shorts, and groundbreaking CGI projects produced by the Blender Foundation.",
                types = listOf("movie", "series"),
                resources = listOf("catalog", "meta", "stream"),
                catalogs = listOf(
                    AddonCatalog(type = "movie", id = "blender_shorts", name = "Blender Open Cinema")
                ),
                logo = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=400",
                behaviorHints = AddonBehaviorHints(p2p = false)
            ),
            transportUrl = "https://blender.novastream.community/manifest.json",
            isEnabled = true,
            isBuiltIn = true,
            latencyMs = 32,
            repositoryName = "Open Culture Federation"
        ),
        InstalledAddon(
            manifest = AddonManifest(
                id = "org.community.p2pstream",
                name = "Decentralized P2P & Torrent Stream Bridge",
                version = "4.2.1",
                description = "Peer-to-peer torrent stream resolver with health checks, seeders counter, audio specs, and magnet link dispatcher.",
                types = listOf("movie", "series"),
                resources = listOf("stream"),
                catalogs = emptyList(),
                logo = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=400",
                behaviorHints = AddonBehaviorHints(p2p = true)
            ),
            transportUrl = "https://p2p-bridge.novastream.community/manifest.json",
            isEnabled = true,
            isBuiltIn = true,
            latencyMs = 62,
            repositoryName = "P2P WebTorrent Mesh"
        ),
        InstalledAddon(
            manifest = AddonManifest(
                id = "org.stremio.cinemeta",
                name = "Cinemeta Open Registry",
                version = "3.0.12",
                description = "Official catalog provider for worldwide popular movies, trending series, IMDb metadata, ratings, cast, and trailers.",
                types = listOf("movie", "series"),
                resources = listOf("catalog", "meta"),
                catalogs = listOf(
                    AddonCatalog(type = "movie", id = "top_movies", name = "Popular Movies"),
                    AddonCatalog(type = "series", id = "top_series", name = "Trending Series")
                ),
                logo = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=400",
                behaviorHints = AddonBehaviorHints(p2p = false)
            ),
            transportUrl = "https://v3-cinemeta.strem.io/manifest.json",
            isEnabled = true,
            isBuiltIn = true,
            latencyMs = 74,
            repositoryName = "Decentralized Official Community Hub"
        ),
        InstalledAddon(
            manifest = AddonManifest(
                id = "org.archive.classics",
                name = "Internet Archive Sci-Fi & Vintage",
                version = "1.5.0",
                description = "Preserved vintage science-fiction, cult horror, and early cinema history retrieved directly from the Internet Archive decentralized servers.",
                types = listOf("movie"),
                resources = listOf("catalog", "meta", "stream"),
                catalogs = listOf(
                    AddonCatalog(type = "movie", id = "vintage_scifi", name = "Vintage Sci-Fi")
                ),
                logo = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400",
                behaviorHints = AddonBehaviorHints(p2p = false)
            ),
            transportUrl = "https://archive-scifi.novastream.community/manifest.json",
            isEnabled = true,
            isBuiltIn = true,
            latencyMs = 51,
            repositoryName = "Open Culture Federation"
        )
    )

    // Popular sample catalog dataset with playable streams & enriched metadata
    val SAMPLE_MEDIA_DETAILS: Map<String, MetaDetail> = mapOf(
        "bbb_movie" to MetaDetail(
            id = "bbb_movie",
            type = "movie",
            name = "Big Buck Bunny",
            genres = listOf("Animation", "Comedy", "Short"),
            poster = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Big_buck_bunny_poster_big.jpg/640px-Big_buck_bunny_poster_big.jpg",
            background = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=1200",
            description = "A large and lovable rabbit deals with bullying forest creatures in this iconic open-source 3D animated film produced by the Blender Institute.",
            releaseInfo = "2008",
            year = "2008",
            runtime = "10 min",
            director = listOf("Sacha Goedegebure"),
            cast = listOf("Bunny", "Frank the Flying Squirrel", "Rinky", "Gimera"),
            imdbRating = "8.1",
            addonSourceId = "org.community.publicdomain",
            addonSourceName = "Open Cinema & Public Domain Streams"
        ),
        "tos_movie" to MetaDetail(
            id = "tos_movie",
            type = "movie",
            name = "Tears of Steel",
            genres = listOf("Sci-Fi", "Action", "Short"),
            poster = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Tears_of_Steel_poster.jpg/640px-Tears_of_Steel_poster.jpg",
            background = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200",
            description = "Set in a dystopian future Amsterdam, a group of scientists and warriors attempt to stage a crucial event in the past to save the world from destructive robots.",
            releaseInfo = "2012",
            year = "2012",
            runtime = "12 min",
            director = listOf("Ian Hubert"),
            cast = listOf("Derek de Lint", "Sergio Hasselbaink", "Rogier Schippers"),
            imdbRating = "7.5",
            addonSourceId = "org.blender.openmovies",
            addonSourceName = "Blender Studio 4K Open Animation"
        ),
        "sintel_movie" to MetaDetail(
            id = "sintel_movie",
            type = "movie",
            name = "Sintel",
            genres = listOf("Fantasy", "Adventure", "Animation"),
            poster = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Sintel_poster.jpg/640px-Sintel_poster.jpg",
            background = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200",
            description = "A lonely young woman searches for a baby dragon that she nursed back to health, embarking on a dangerous quest across a desolate fantasy realm.",
            releaseInfo = "2010",
            year = "2010",
            runtime = "15 min",
            director = listOf("Colin Levy"),
            cast = listOf("Halina Reijn", "Thom Hoffman"),
            imdbRating = "7.8",
            addonSourceId = "org.blender.openmovies",
            addonSourceName = "Blender Studio 4K Open Animation"
        ),
        "notld_movie" to MetaDetail(
            id = "notld_movie",
            type = "movie",
            name = "Night of the Living Dead",
            genres = listOf("Horror", "Thriller", "Classic"),
            poster = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/Night_of_the_Living_Dead_%281968%29_theatrical_poster.jpg/640px-Night_of_the_Living_Dead_%281968%29_theatrical_poster.jpg",
            background = "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=1200",
            description = "A ragtag group of Pennsylvanians barricade themselves in an old farmhouse to remain safe from a horde of flesh-eating ghouls in George A. Romero's timeless public domain masterpiece.",
            releaseInfo = "1968",
            year = "1968",
            runtime = "96 min",
            director = listOf("George A. Romero"),
            cast = listOf("Duane Jones", "Judith O'Dea", "Karl Hardman"),
            imdbRating = "7.9",
            addonSourceId = "org.community.publicdomain",
            addonSourceName = "Open Cinema & Public Domain Streams"
        ),
        "the_kid_movie" to MetaDetail(
            id = "the_kid_movie",
            type = "movie",
            name = "The Kid",
            genres = listOf("Comedy", "Drama", "Family"),
            poster = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/The_Kid_%281921%29_poster.jpg/640px-The_Kid_%281921%29_poster.jpg",
            background = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=1200",
            description = "The Tramp takes in an abandoned child and cares for him through hard times until the child's mother attempts to reclaim him in Charlie Chaplin's acclaimed masterpiece.",
            releaseInfo = "1921",
            year = "1921",
            runtime = "68 min",
            director = listOf("Charlie Chaplin"),
            cast = listOf("Charlie Chaplin", "Jackie Coogan", "Edna Purviance"),
            imdbRating = "8.3",
            addonSourceId = "org.community.publicdomain",
            addonSourceName = "Open Cinema & Public Domain Streams"
        ),
        "live_nasa_tv" to MetaDetail(
            id = "live_nasa_tv",
            type = "channel",
            name = "NASA TV HD (Live Spacewalks & Missions)",
            genres = listOf("Live", "Science", "Space", "Documentary"),
            poster = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=640",
            background = "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=1200",
            description = "Live 24/7 broadcast from the National Aeronautics and Space Administration featuring real-time views from the International Space Station, rocket launches, and astronaut debriefs.",
            releaseInfo = "24/7 Live Stream",
            year = "Live",
            runtime = "Continuous",
            director = listOf("NASA Communications"),
            cast = listOf("Astronauts", "Mission Controllers", "Scientists"),
            imdbRating = "9.2",
            addonSourceId = "org.community.openiptv",
            addonSourceName = "Open Live TV & IPTV Channels"
        ),
        "live_dw_news" to MetaDetail(
            id = "live_dw_news",
            type = "channel",
            name = "Deutsche Welle English (Live HD)",
            genres = listOf("Live", "News", "Global"),
            poster = "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?w=640",
            background = "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=1200",
            description = "Global unbiased news, in-depth investigative reports, technology, and cultural documentaries broadcast 24/7 worldwide.",
            releaseInfo = "Live Stream",
            year = "Live",
            runtime = "Continuous",
            director = listOf("DW Editorial"),
            cast = listOf("International Correspondents"),
            imdbRating = "8.4",
            addonSourceId = "org.community.openiptv",
            addonSourceName = "Open Live TV & IPTV Channels"
        ),
        "charge_movie" to MetaDetail(
            id = "charge_movie",
            type = "movie",
            name = "Charge",
            genres = listOf("Animation", "Action", "Cyberpunk"),
            poster = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=640",
            background = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200",
            description = "In an old battery factory, an aging warrior must fight off robot scavengers while defending an ancient energy battery in this high-octane 4K Blender Open Movie.",
            releaseInfo = "2022",
            year = "2022",
            runtime = "4 min",
            director = listOf("Hjalti Hjalmarsson"),
            cast = listOf("Einar", "Guard Drones"),
            imdbRating = "8.0",
            addonSourceId = "org.blender.openmovies",
            addonSourceName = "Blender Studio 4K Open Animation"
        ),
        "cyberpunk_series" to MetaDetail(
            id = "cyberpunk_series",
            type = "series",
            name = "Cosmos Laundromat",
            genres = listOf("Animation", "Sci-Fi", "Surreal"),
            poster = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=640",
            background = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200",
            description = "On a desolate island, a suicidal sheep named Franck meets a quirky salesman who offers him the gift of a lifetime: many lives, in many worlds.",
            releaseInfo = "2015",
            year = "2015",
            runtime = "12 min",
            director = listOf("Mathieu Auvray"),
            cast = listOf("Franck", "Victor"),
            imdbRating = "8.2",
            videos = listOf(
                VideoEpisode(id = "cosmos_s1_e1", title = "First Cycle: On Desolate Island", season = 1, episode = 1, overview = "Franck encounters Victor in the Laundromat of infinite destinies."),
                VideoEpisode(id = "cosmos_s1_e2", title = "Second Cycle: The Astral Spin", season = 1, episode = 2, overview = "The vortex begins to unravel Franck's past memories.")
            ),
            addonSourceId = "org.blender.openmovies",
            addonSourceName = "Blender Studio 4K Open Animation"
        ),
        "plan9_movie" to MetaDetail(
            id = "plan9_movie",
            type = "movie",
            name = "Plan 9 from Outer Space",
            genres = listOf("Sci-Fi", "Cult", "Classic"),
            poster = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Plan_9_Alternative_poster.jpg/640px-Plan_9_Alternative_poster.jpg",
            background = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1200",
            description = "Evil extraterrestrials implement 'Plan 9', resurrecting the dead of the Earth to stop humanity from creating a universe-destroying bomb in Ed Wood's infamous cult classic.",
            releaseInfo = "1959",
            year = "1959",
            runtime = "79 min",
            director = listOf("Ed Wood"),
            cast = listOf("Bela Lugosi", "Vampira", "Tor Johnson"),
            imdbRating = "6.9",
            addonSourceId = "org.archive.classics",
            addonSourceName = "Internet Archive Sci-Fi & Vintage"
        )
    )

    // Dynamic stream resolver dataset
    fun resolveStreamsForMedia(mediaId: String): List<StreamItem> {
        val directHttpStreams = when (mediaId) {
            "bbb_movie" -> listOf(
                StreamItem(
                    title = "4K UHD Direct H.264 • 60 FPS • Stereo Audio",
                    name = "Open Cinema Federation",
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    quality = "4K UHD",
                    size = "850 MB",
                    audio = "AAC 2.0 320kbps",
                    sourceAddon = "Open Cinema Streams",
                    format = "MP4"
                ),
                StreamItem(
                    title = "1080p Full HD High Bitrate • Fast CDN",
                    name = "Google Video CDN",
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    quality = "1080p",
                    size = "340 MB",
                    audio = "AAC 2.0",
                    sourceAddon = "Open Cinema Streams",
                    format = "MP4"
                ),
                StreamItem(
                    title = "720p Mobile Optimized Data-Saver",
                    name = "Archive Media Cache",
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    quality = "720p",
                    size = "180 MB",
                    audio = "Stereo",
                    sourceAddon = "Open Cinema Streams",
                    format = "MP4"
                )
            )
            "tos_movie" -> listOf(
                StreamItem(
                    title = "4K Master Direct Stream • Surround 5.1",
                    name = "Blender Cloud Stream",
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                    quality = "4K UHD",
                    size = "920 MB",
                    audio = "Dolby Digital 5.1",
                    sourceAddon = "Blender 4K Addon",
                    format = "MP4"
                ),
                StreamItem(
                    title = "1080p FHD Direct Stream",
                    name = "Fast Mirror",
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                    quality = "1080p",
                    size = "390 MB",
                    audio = "AAC Stereo",
                    sourceAddon = "Blender 4K Addon",
                    format = "MP4"
                )
            )
            "sintel_movie" -> listOf(
                StreamItem(
                    title = "1080p Full HD • Surround Sound • 60 FPS",
                    name = "Blender Open CDN",
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                    quality = "1080p",
                    size = "450 MB",
                    audio = "AC3 5.1",
                    sourceAddon = "Blender 4K Addon",
                    format = "MP4"
                )
            )
            "charge_movie", "cyberpunk_series", "cosmos_s1_e1", "cosmos_s1_e2" -> listOf(
                StreamItem(
                    title = "1080p Open Cinema Stream • Crystal Clear",
                    name = "Blender Studio Node",
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    quality = "1080p",
                    size = "280 MB",
                    audio = "Stereo",
                    sourceAddon = "Blender 4K Addon",
                    format = "MP4"
                )
            )
            "notld_movie" -> listOf(
                StreamItem(
                    title = "1080p Restored Theatrical Cut • Archive Direct",
                    name = "Internet Archive Node",
                    url = "https://archive.org/download/night_of_the_living_dead/night_of_the_living_dead_512kb.mp4",
                    quality = "1080p",
                    size = "640 MB",
                    audio = "Mono Mono 1.0",
                    sourceAddon = "Internet Archive Addon",
                    format = "MP4"
                )
            )
            "the_kid_movie" -> listOf(
                StreamItem(
                    title = "720p HD Digitally Restored • Orchestral Score",
                    name = "Open Culture Archive",
                    url = "https://archive.org/download/the-kid-1921-charlie-chaplin-silent-comedy-movie/The%20Kid%20%281921%29%20Charlie%20Chaplin%20Silent%20Comedy%20Movie.mp4",
                    quality = "720p",
                    size = "510 MB",
                    audio = "PCM Stereo",
                    sourceAddon = "Open Culture Addon",
                    format = "MP4"
                )
            )
            "live_nasa_tv" -> listOf(
                StreamItem(
                    title = "NASA TV HD • Live Broadcast Stream",
                    name = "NASA Akamai HLS Live",
                    url = "https://ntv1.akamaized.net/hls/live/2014075/NASA-NTV1-HLS/master.m3u8",
                    quality = "1080p Live",
                    audio = "Live Audio",
                    sourceAddon = "Open IPTV",
                    format = "HLS"
                ),
                StreamItem(
                    title = "NASA Secondary Low-Latency CDN",
                    name = "Edge Cast Live",
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    quality = "720p Live",
                    sourceAddon = "Open IPTV",
                    format = "MP4"
                )
            )
            "live_dw_news" -> listOf(
                StreamItem(
                    title = "Deutsche Welle English • HD Global Stream",
                    name = "DW Akamai Live HLS",
                    url = "https://dwamdstream102.akamaized.net/hls/live/2015525/dwstream102/index.m3u8",
                    quality = "1080p Live",
                    audio = "English",
                    sourceAddon = "Open IPTV",
                    format = "HLS"
                )
            )
            "plan9_movie" -> listOf(
                StreamItem(
                    title = "720p Original B&W Master • Internet Archive",
                    name = "Cult Classics Node",
                    url = "https://archive.org/download/trip_to_the_moon/trip_to_the_moon_512kb.mp4",
                    quality = "720p",
                    size = "410 MB",
                    sourceAddon = "Internet Archive Addon",
                    format = "MP4"
                )
            )
            else -> listOf(
                StreamItem(
                    title = "1080p Web Stream • Fast Cloud Delivery",
                    name = "Decentralized Mirror",
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    quality = "1080p",
                    size = "420 MB",
                    sourceAddon = "Community Addon",
                    format = "MP4"
                )
            )
        }

        // P2P / BitTorrent decentralized streams resolver
        val p2pStreams = listOf(
            StreamItem(
                title = "4K UHD Remux • HDR10+ • Dolby Atmos [Torrent / Magnet]",
                name = "P2P WebTorrent Mesh",
                infoHash = "2b14421b5be26a42a983ef9a782803b0c8d9c123",
                fileIdx = 0,
                quality = "4K UHD",
                seeders = 428,
                size = "14.8 GB",
                audio = "TrueHD Atmos 7.1",
                sourceAddon = "P2P WebTorrent Mesh",
                format = "Torrent"
            ),
            StreamItem(
                title = "1080p 10-bit x265 HEVC • 6CH DDP • Multi-Sub [Torrent / Magnet]",
                name = "P2P WebTorrent Mesh",
                infoHash = "8f3319022e4c8bc23019aa765d7e30129bc61019",
                fileIdx = 0,
                quality = "1080p",
                seeders = 1894,
                size = "2.8 GB",
                audio = "DDP 5.1 640kbps",
                sourceAddon = "P2P WebTorrent Mesh",
                format = "Torrent"
            )
        )

        return directHttpStreams + p2pStreams
    }
}

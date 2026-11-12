package org.application.shikiapp.shared.network.parser

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.io.IOException
import kotlinx.serialization.json.*
import org.application.shikiapp.shared.models.ui.*
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.parser.yandex.YandexMapping
import org.application.shikiapp.shared.network.parser.yandex.YandexMapping.YandexSeason
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.basicJson
import org.application.shikiapp.shared.utils.enums.VideoSource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.video_source_yandex
import kotlin.time.Duration.Companion.seconds

class YandexParser private constructor(client: HttpClient, override val token: String) : BaseParser(client) {
    private val mutex = Mutex()
    private val manifestMutex = Mutex()
    private var manifest: Pair<String, String>? = null
    private val queries = HashMap<String, String>()
    private val episodes = HashMap<String, List<EpisodeModel>>()

    override suspend fun searchById(id: String) = Unit

    suspend fun searchById(id: String, targetSeason: Int, episodeCount: Int?, isSeries: Boolean): VideoSourceData {
        val catalogId = id
            .toLongOrNull()
            ?.takeIf { it > 0 }
            ?: return VideoSourceData(VideoSource.YANDEX, emptyList())

        val catalog = catalog(catalogId, isSeries)
        val contentId = catalog.string("contentId") ?: throw IOException()

        val film = if (!isSeries) catalog else filmPage(contentId, 1, 1)
        val seasons = if (!isSeries) emptyList() else {
            val available = ArrayList<YandexSeason>()

            for (value in film.obj("seasons").array("items")) {
                val season = value as? JsonObject ?: continue
                val number = season.int("number") ?: continue
                val count = season.obj("episodes").int("total") ?: continue
                val seasonId = season.string("contentId") ?: continue
                if (number > 0 && count > 0) {
                    available.add(YandexSeason(number, seasonId, count, 1))
                }
            }

            available.sortBy(YandexSeason::number)
            YandexMapping.selectSeasons(available, targetSeason, episodeCount)
        }

        if (isSeries && seasons.isEmpty()) {
            return VideoSourceData(VideoSource.YANDEX, emptyList())
        }

        val firstId = if (!isSeries) contentId
        else {
            val firstSeason = seasons.first().number
            val firstEpisode = if (firstSeason == 1) film else filmPage(contentId, firstSeason, 1)
            firstEpisode.obj("episode").string("contentId") ?: throw IOException()
        }

        val stream = resolve(firstId)
        val manifest = if (stream.second != "HLS") BLANK else loadManifest(stream.first)

        val cacheKey = "$catalogId/$targetSeason/$episodeCount/$isSeries"
        var items = mutex.withLock { episodes[cacheKey] }
        if (items == null) {
            val metadata = HashMap<Int, EpisodeModel>()
            if (isSeries) {
                val semaphore = Semaphore(2)
                val batches = coroutineScope {
                    seasons.map { season ->
                        async { semaphore.withPermit { loadSeason(season) } }
                    }.awaitAll()
                }
                for (batch in batches) {
                    for (episode in batch) {
                        metadata[episode.number] = episode
                    }
                }
            }

            items = buildList {
                if (!isSeries) {
                    add(
                        element = EpisodeModel(
                            number = 1,
                            link = contentId,
                            title = catalog.obj("title").string("localized")
                        )
                    )
                }
                for (season in seasons) {
                    for (number in 1..season.count) {
                        val absolute = season.firstEpisode + number - 1
                        add(
                            element = metadata[absolute] ?: EpisodeModel(
                                number = absolute,
                                link = "$contentId/${season.number}/$number"
                            )
                        )
                    }
                }
            }

            mutex.withLock { episodes[cacheKey] = items }
        }

        val availableMetadata = film
            .obj("ott")
            .obj("preview")
            .obj("availableMetadata")

        val audio = audioTracks(manifest).ifEmpty {
            availableMetadata.array("audioMeta").mapNotNull {
                val track = it as? JsonObject ?: return@mapNotNull null
                val language = track.string("language") ?: return@mapNotNull null
                val name = listOfNotNull(track.string("languageName"), track.string("studio"), track.string("type")).joinToString(" ")
                name to language
            }
        }

        val lastEpisode = items.last().number
        val subtitles = manifest.contains("TYPE=SUBTITLES") ||
                availableMetadata
                    .array("subtitleMeta")
                    .isNotEmpty()

        return VideoSourceData(
            type = VideoSource.YANDEX,
            voices = audio.mapIndexed { index, (name, language) ->
                VideoVoice(
                    id = "$language:$index",
                    title = ResourceText.StaticString(name),
                    hasDubbers = !language.startsWith("jpn"),
                    hasSubtitles = subtitles,
                    episodes = items.map { it.copy(audioIndex = index) },
                    quality = null,
                    lastEpisode = lastEpisode
                )
            }.ifEmpty {
                listOf(
                    element = VideoVoice(
                        id = "yandex",
                        title = ResourceText.StringResource(Res.string.video_source_yandex),
                        hasDubbers = true,
                        hasSubtitles = subtitles,
                        episodes = items,
                        quality = null,
                        lastEpisode = lastEpisode
                    )
                )
            }
        )
    }

    private suspend fun loadSeason(season: YandexSeason): List<EpisodeModel> {
        val items = HashMap<String, EpisodeModel>()
        val numbers = HashSet<Int>()
        var offset = 0

        while (true) {
            val variables = buildJsonObject {
                put("contentId", season.contentId)
                put("limit", 50)
                put("offset", offset)
                put("withUserData", false)
            }
            val data = graphql("SerialStructureSeason", variables).obj("seasonByContentId")
            if (
                data.int("number") != season.number ||
                data.string("contentId") != season.contentId
            ) throw IOException()

            val page = data.obj("episodes")
            val total = page.int("total") ?: 0
            if (total > 0 && total != season.count) throw IOException()

            val batch = page.array("items")
            if (batch.isEmpty()) break

            val previousSize = items.size
            for (value in batch) {
                val episode = value as? JsonObject ?: throw IOException()
                val number = episode.int("number") ?: throw IOException()
                val contentId = episode.string("contentId") ?: throw IOException()
                val episodeSeason = episode.obj("season")
                if (
                    number !in 1..season.count ||
                    episodeSeason.int("number") != season.number ||
                    episodeSeason.string("contentId") != season.contentId
                ) throw IOException()

                val absolute = season.firstEpisode + number - 1
                val previous = items[contentId]
                if (previous != null && previous.number != absolute) throw IOException()
                if (previous == null && !numbers.add(absolute)) throw IOException()

                val title = episode.obj("title")
                val cover = episode.obj("cover")
                val avatarsUrl = cover.string("avatarsUrl")
                val image = if (avatarsUrl == null) cover.string("fallbackUrl")
                else (if (avatarsUrl.startsWith("//")) "https:$avatarsUrl" else avatarsUrl).trimEnd('/') + "/400x225"

                items[contentId] = EpisodeModel(
                    number = absolute,
                    link = contentId,
                    title = title.string("localized") ?: title.string("original"),
                    screenshot = image
                )
            }

            offset += batch.size
            if (items.size == season.count || items.size == previousSize) break
            if (offset > season.count * 2) throw IOException()
        }

        return items.values.sortedBy(EpisodeModel::number)
    }

    private suspend fun filmPage(contentId: String, season: Int, episode: Int): JsonObject {
        val variables = buildJsonObject {
            put("contentUuid", contentId)
            put("isAuthorized", false)
            put("withPrechosenEpisode", true)
            put("seasonNumber", season)
            put("episodeNumber", episode)
            put("withUserData", false)
            put("socialArgumentLimit", 0)
            put("includeSocialArgumentTypes", JsonArray(emptyList()))
        }
        val film = graphql("FilmPage", variables).obj("movieByContentUuid")
        val selected = film.obj("episode")
        if (
            film.string("contentId") != contentId ||
            selected.int("number") != episode ||
            selected.obj("season").int("number") != season
        ) throw IOException()

        return film
    }

    private suspend fun catalog(id: Long, isSeries: Boolean): JsonObject {
        val operation = if (isSeries) "TvSeriesBaseInfo" else "FilmBaseInfo"
        val field = if (isSeries) "tvSeries" else "film"
        val variables = buildJsonObject {
            put("${field}Id", id)
            put("isAuthorized", false)
            put("clientContext", JsonObject(emptyMap()))
            put("checkSilentInvoiceAvailability", false)
            put("withPurchaseOptions", false)
            put("actorsLimit", 0)
            put("voiceOverActorsLimit", 0)
            put("relatedMoviesLimit", 0)
            put("watchabilityLimit", 0)
            put("includeSocialArgumentTypes", JsonArray(emptyList()))
            put("socialArgumentLimit", 0)
        }
        val result = graphql(operation, variables).obj(field)
        if ((result["id"] as? JsonPrimitive)?.longOrNull != id) throw IOException()

        return result
    }

    private suspend fun graphql(operation: String, variables: JsonObject): JsonObject {
        val document = mutex.withLock {
            queries.getOrPut(operation) {
                Res.readBytes("files/yandex/$operation.gql").decodeToString()
            }
        }
        val response = client.post("$API_HOST/graphql") {
            contentType(ContentType.Application.Json)
            header("service-id", "25")
            setBody(
                body = buildJsonObject {
                    put("operationName", operation)
                    put("query", document)
                    put("variables", variables)
                }.toString()
            )
        }

        if (!response.status.isSuccess()) throw IOException()

        val json = basicJson.parseToJsonElement(response.bodyAsText()) as? JsonObject ?: throw IOException()
        for (value in json.array("errors")) {
            val error = value as? JsonObject ?: throw IOException()
            val field = (error.array("path").firstOrNull() as? JsonPrimitive)?.contentOrNull
            if (
                operation == "FilmBaseInfo" &&
                field == "tvSeries" &&
                error.obj("extensions").string("code") == "NotFoundError"
            ) continue

            throw IOException()
        }

        return json["data"] as? JsonObject ?: throw IOException()
    }

    override suspend fun getPlaylistLink(parseUrl: String, quality: Int): PlaylistResult {
        val contentId = if ('/' !in parseUrl) parseUrl
        else {
            val parts = parseUrl.split('/', limit = 4)
            if (parts.size != 3) throw IOException()

            val series = parts[0]
            if (!contentIdRegex.matches(series)) throw IOException()
            val season = parts[1].toIntOrNull() ?: throw IOException()
            val episode = parts[2].toIntOrNull() ?: throw IOException()
            if (season <= 0 || episode <= 0) throw IOException()

            filmPage(series, season, episode)
                .obj("episode")
                .string("contentId")
                ?: throw IOException()
        }

        val stream = resolve(contentId)
        val subtitles = if (stream.second != "HLS") emptyList()
        else {
            try {
                loadSubtitles(stream.first, loadManifest(stream.first))
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                emptyList()
            }
        }

        return PlaylistResult(
            url = stream.first,
            subtitles = subtitles
        )
    }

    private suspend fun loadManifest(url: String, cache: Boolean = true): String {
        if (cache) {
            manifestMutex.lock()
        }

        try {
            val cached = if (cache) manifest else null
            if (cached != null && cached.first == url) return cached.second

            val response = client.get(url)
            if (!response.status.isSuccess()) throw IOException()

            val text = response.bodyAsText().removePrefix("\uFEFF").trimStart()
            if (!text.startsWith("#EXTM3U")) throw IOException()

            if (cache) {
                manifest = url to text
            }
            return text
        } finally {
            if (cache) {
                manifestMutex.unlock()
            }
        }
    }

    private suspend fun loadSubtitles(url: String, manifest: String): List<SubtitleTrack> {
        val tracks = LinkedHashMap<String, SubtitleTrack>()

        for (line in manifest.lineSequence()) {
            if (!line.startsWith("#EXT-X-MEDIA:")) continue
            val values = attributes(line)

            if (values["TYPE"] != "SUBTITLES") continue
            val name = values["NAME"] ?: values["LANGUAGE"] ?: continue
            if (name in tracks) continue
            val path = values["URI"] ?: continue

            val playlistUrl = resolveUrl(url, path)
            val playlist = loadManifest(playlistUrl, cache = false)

            if (
                !playlist.contains("#EXT-X-ENDLIST") ||
                playlist.contains("#EXT-X-KEY:") ||
                playlist.contains("#EXT-X-BYTERANGE:") ||
                playlist.contains("#EXT-X-MAP:")
            ) continue

            val file = playlist.lineSequence()
                .map(String::trim)
                .singleOrNull { it.isNotEmpty() && !it.startsWith('#') }
                ?: continue

            tracks[name] = SubtitleTrack(name, resolveUrl(playlistUrl, file))
        }

        return tracks.values.toList()
    }

    private fun resolveUrl(base: String, value: String): String {
        if (value.startsWith("https://")) {
            return value
        }

        var endIndex = base.indexOf('?')
        if (endIndex == -1) endIndex = base.indexOf('#')
        else {
            val hashIndex = base.indexOf('#')
            if (hashIndex in 0 until endIndex) {
                endIndex = hashIndex
            }
        }

        val cleaned = if (endIndex != -1) base.substring(0, endIndex) else base
        val url = URLBuilder(cleaned)
            .takeFrom(value)
            .buildString()

        if (!url.startsWith("https://")) {
            throw IOException()
        }

        return url
    }

    private suspend fun resolve(contentId: String): Pair<String, String> = withTimeoutOrNull(30.seconds) {
        if (!contentIdRegex.matches(contentId)) throw IOException()
        val response = client.get("$PLAYER_URL/v23/player/$contentId.json") {
          //  header(HttpHeaders.UserAgent, USER_AGENT)
            parameter("stream_options", "hires")
            parameter("disable_trackings", "1")
        }
        if (!response.status.isSuccess()) throw IOException()
        selectStream(basicJson.parseToJsonElement(response.bodyAsText()).jsonObject, contentId)
    } ?: throw IOException()

    companion object {
        private val API_HOST by lazy { cypher13("uggcf://tencudy.xvabcbvfx.eh") }
        private val PLAYER_URL by lazy { cypher13("uggcf://sebagraq.iu.lnaqrk.eh") }
        private val contentIdRegex = Regex("[a-fA-F0-9]{1,64}")
        private val attribute = Regex("""(?:^|,)([A-Z0-9-]+)=("[^"]*"|[^,]*)""")
        val instance: YandexParser by lazy { YandexParser(Network.watchClient, BLANK) }

        internal fun selectStream(json: JsonObject, contentId: String): Pair<String, String> {
            if (!json.string("error").isNullOrBlank()) throw IOException()

            val content = json.obj("content")
            if (content.string("content_id") != contentId) throw IOException()

            val streams = content["streams"] as? JsonArray ?: throw IOException()
            var fallback: String? = null

            for (element in streams) {
                val stream = element as? JsonObject ?: continue
                val drm = stream["drmConfig"]
                if (drm != null && drm != JsonNull) continue

                val url = stream.string("url") ?: continue
                if (!url.startsWith("https://")) continue

                val type = stream.string("stream_type")
                if (type.equals("HLS", ignoreCase = true)) return url to "HLS"
                if (fallback == null && type.equals("DASH", ignoreCase = true)) {
                    fallback = url
                }
            }

            return (fallback ?: throw IOException()) to "DASH"
        }

        internal fun audioTracks(manifest: String): List<Pair<String, String>> {
            val tracks = ArrayList<Map<String, String>>()
            var group: String? = null

            for (line in manifest.lineSequence()) {
                if (line.startsWith("#EXT-X-MEDIA:")) {
                    val values = attributes(line)
                    if (values["TYPE"] == "AUDIO") tracks.add(values)
                } else if (group == null && line.startsWith("#EXT-X-STREAM-INF:")) {
                    group = attributes(line)["AUDIO"]
                }
            }

            val result = LinkedHashSet<Pair<String, String>>()
            val selectedGroup = group ?: tracks.firstOrNull()?.get("GROUP-ID")

            for (i in tracks.indices) {
                val track = tracks[i]
                if (track["GROUP-ID"] == selectedGroup) {
                    val name = track["NAME"]
                    if (name != null) {
                        result.add(name to track["LANGUAGE"].orEmpty())
                    }
                }
            }

            return result.toList()
        }

        private fun attributes(line: String) = attribute.findAll(line.substringAfter(':'))
            .associate { it.groupValues[1] to it.groupValues[2].removeSurrounding("\"") }
        private fun JsonObject.obj(name: String) = this[name] as? JsonObject ?: JsonObject(emptyMap())
        private fun JsonObject.array(name: String) = this[name] as? JsonArray ?: JsonArray(emptyList())
        private fun JsonObject.string(name: String) = (this[name] as? JsonPrimitive)?.contentOrNull?.takeIf(String::isNotBlank)
        private fun JsonObject.int(name: String) = (this[name] as? JsonPrimitive)?.intOrNull
    }
}

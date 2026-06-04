package org.application.shikiapp.shared.network.parser

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.jsonPrimitive
import org.application.shikiapp.shared.models.ui.PlaylistResult
import org.application.shikiapp.shared.models.ui.SubtitleTrack
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.utils.basicJson
import kotlin.concurrent.Volatile

class CollapsParser private constructor(client: HttpClient) : BaseParser(client) {

    companion object {
        private const val API_HOST_ENC = "uggcf://ncvpbyyncf.pp"
        private const val TOKEN_ENC = "rrqrso541nron871qpsp756r6o31p02r"

        private val API_HOST by lazy { cypher13(API_HOST_ENC) }
        private val DECRYPTED_TOKEN by lazy { cypher13(TOKEN_ENC) }

        @Volatile
        private var INSTANCE: CollapsParser? = null
        private val mutex = Mutex()

        suspend fun getInstance(): CollapsParser {
            INSTANCE?.let { return it }
            return mutex.withLock {
                INSTANCE ?: CollapsParser(Network.watchClient).also { INSTANCE = it }
            }
        }
    }

    override val token: String
        get() = DECRYPTED_TOKEN

    override suspend fun searchById(id: String): CollapsTitleDetails {
        val response = client.get("$API_HOST/franchise/details") {
            parameter("token", token)
            parameter("imdb_id", id.removePrefix("tt"))
        }

        return basicJson.decodeFromString<CollapsTitleDetails>(response.bodyAsText())
    }

    override suspend fun getPlaylistLink(parseUrl: String, quality: Int): PlaylistResult {
        val request = client.get(parseUrl)
        val html = request.bodyAsText()

        val seasonNum = request.call.request.url.parameters["season"]?.toIntOrNull()
        val episodeNum = request.call.request.url.parameters["episode"]

        val isMovie = !html.contains("seasons:")
        val mediaJson = extractMedia(html, isMovie) ?: throw Exception()
        val episode = if (isMovie) {
            try {
                basicJson.decodeFromString<CollapsEpisodeData>(mediaJson.replace(Regex("""([{,]\s*)([a-zA-Z0-9_]+)\s*:"""), "$1\"$2\":"))
            } catch (e: Exception) {
                throw Exception(e)
            }
        } else {
            val seasonsData = try {
                basicJson.decodeFromString<List<CollapsSeasonData>>(mediaJson)
            } catch (e: Exception) {
                throw Exception(e)
            }

            val season = if (seasonNum != null) {
                seasonsData.find { it.season == seasonNum }
            } else {
                seasonsData.firstOrNull()
            } ?: throw Exception()

            if (episodeNum != null) {
                season.episodes.find { it.episode == episodeNum }
            } else {
                season.episodes.firstOrNull()
            } ?: throw Exception()
        }

        val subsList = episode.cc?.mapNotNullTo(mutableListOf()) { obj ->
            val subName = obj["name"]?.jsonPrimitive?.content
            val subUrl = obj["url"]?.jsonPrimitive?.content?.replace("\\/", "/")?.let {
                if (it.startsWith("//")) "https:$it" else it
            }

            if (subUrl == null || subName == null) null
            else SubtitleTrack(subName, subUrl)
        }

        val mediaList = listOfNotNull(episode.dasha, episode.hls, episode.dash).map { url ->
            url.replace("\\/", "/").let { if (it.startsWith("//")) "https:$it" else it }
        }

        if (mediaList.isEmpty()) throw Exception()

        return PlaylistResult(
            url = mediaList[0],
            fallbackUrls = mediaList.drop(1),
            subtitles = subsList.orEmpty(),
            headers = mapOf(
                "Origin" to parseUrl,
                "Referer" to "$parseUrl/",
                "User-Agent" to (request.call.request.headers["User-Agent"].orEmpty())
            )
        )
    }

    private fun extractMedia(html: String, isMovie: Boolean): String? {
        val targetKey = if (isMovie) "source:" else "seasons:"
        val openBracket = if (isMovie) '{' else '['
        val closeBracket = if (isMovie) '}' else ']'

        val scriptText = Ksoup.parse(html)
            .getElementsByTag("script")
            .firstOrNull { it.data().contains(targetKey) }
            ?.data() ?: return null

        val startIndex = scriptText.indexOf(targetKey)
        if (startIndex == -1) return null

        val blockStart = scriptText.indexOf(openBracket, startIndex)
        if (blockStart == -1) return null

        // По скобкам --> объект --> конец объекта --> конец списка/блока
        var brackets = 0
        var quoteChar: Char? = null
        var isEscaped = false

        for (i in blockStart until scriptText.length) {
            val char = scriptText[i]

            if (isEscaped) {
                isEscaped = false
                continue
            }

            if (char == '\\') {
                isEscaped = true
                continue
            }

            if (quoteChar != null) {
                if (char == quoteChar) {
                    quoteChar = null
                }
            } else {
                when (char) {
                    '"', '\'' -> quoteChar = char
                    openBracket -> brackets++
                    closeBracket -> {
                        if (--brackets == 0) {
                            return scriptText.substring(blockStart, i + 1)
                        }
                    }
                }
            }
        }

        return null
    }
}
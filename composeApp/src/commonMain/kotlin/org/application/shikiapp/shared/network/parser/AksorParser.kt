package org.application.shikiapp.shared.network.parser

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import org.application.shikiapp.shared.models.ui.PlaylistResult
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.basicJson

class AksorParser private constructor(client: HttpClient, override val token: String) : BaseParser(client) {
    override suspend fun searchById(id: String): AksorPlaylistResponse {
        val shikimoriId = id.toIntOrNull() ?: return AksorPlaylistResponse()
        if (shikimoriId <= 0) return AksorPlaylistResponse()

        val search = client.get("$API_HOST/anime") {
            parameter("shikimori_ids", shikimoriId)
            parameter("limit", 100)
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }

        val anime = basicJson.decodeFromString<AksorSearchResponse>(search.bodyAsText())
            .response
            .firstOrNull { it.remoteIds.shikimoriId == shikimoriId }
            ?: return AksorPlaylistResponse()

        val response = client.get("$API_HOST/anime/${anime.id}/videos") {
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }

        val text = response.bodyAsText()
        return withContext(Dispatchers.Default) {
            val playlist = basicJson.decodeFromString<AksorPlaylistResponse>(text)
            playlist.copy(
                response = playlist.response.filter { videoUrl.matches(it.iframeUrl) }
            )
        }
    }

    override suspend fun getPlaylistLink(parseUrl: String, quality: Int): PlaylistResult {
        val id = videoUrl.matchEntire(parseUrl)?.groupValues?.get(1) ?: throw IOException()
        val request = client.get("$PLAYER_URL/api/video/$id") {
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Referer", "$ORIGIN/")
        }

        val response = basicJson.decodeFromString<AksorVideoResponse>(request.bodyAsText())
        val qualityMap = buildMap {
            for ((name, url) in response.qualities) {
                if (url.isNullOrBlank()) continue

                val height = when (name) {
                    "q4k" -> 2160
                    "q2k" -> 1440
                    else -> name.removePrefix("q").toIntOrNull() ?: 0
                }

                if (height > 0) {
                    put(height, if (url.startsWith("//")) "https:$url" else url)
                }
            }
        }

        val mainUrl = qualityMap[quality]
            ?: qualityMap.maxByOrNull { it.key }?.value
            ?: throw IOException()

        return PlaylistResult(
            url = mainUrl,
            fallbackUrls = qualityMap.values.filterTo(LinkedHashSet()) { it != mainUrl }.toList(),
            qualityList = qualityMap.keys.sortedDescending(),
            headers = buildMap {
                put("Referer", "$PLAYER_URL/")
                put("Origin", PLAYER_URL)

                val userAgent = request.call.request.headers[HttpHeaders.UserAgent]
                if (userAgent != null) put(HttpHeaders.UserAgent, userAgent)
            }
        )
    }

    companion object {
        private val API_HOST by lazy { cypher13("uggcf://ncv.lnav.gi") }
        private val PLAYER_URL by lazy { cypher13("uggcf://cynlre.nxfbe.gi") }
        private val ORIGIN by lazy { cypher13("uggcf://byq.lhzzlnav.zr") }
        private val videoUrl = Regex(
            pattern = """(?:https?:)?//(?:www\.)?(?:player\.)?aksor(?:\.yani)?\.tv/video/([A-Za-z0-9_-]+)(?:[/?#].*)?""",
            option = RegexOption.IGNORE_CASE
        )

        val instance: AksorParser by lazy { AksorParser(Network.watchClient, BLANK) }
    }
}

package org.application.shikiapp.shared.network.parser

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.models.ui.PlaylistResult
import org.application.shikiapp.shared.models.ui.SubtitleTrack
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.utils.basicJson
import kotlin.concurrent.Volatile
import kotlin.coroutines.cancellation.CancellationException

class AnimeLibParser private constructor(client: HttpClient) : BaseParser(client) {

    companion object {
        private const val API_HOST_ENC = "uggcf://ncv.pqayvof.bet/ncv"
        val API_HOST by lazy { cypher13(API_HOST_ENC) }
        val API_AUTH_URL by lazy { cypher13("uggcf://nhgu.uragnvpqa.bet/nhgu/bnhgu/nhgubevmr") }

        val ORIGIN by lazy { cypher13("uggcf://i5.navzryvo.bet") }

        @Volatile
        private var INSTANCE: AnimeLibParser? = null

        private val mutex = Mutex()

        suspend fun getInstance(): AnimeLibParser {
            INSTANCE?.let { return it }
            return mutex.withLock {
                INSTANCE ?: AnimeLibParser(Network.watchClient).also { INSTANCE = it }
            }
        }
    }

    override val token: String
        get() = Preferences.libToken?.accessToken.orEmpty()

    override suspend fun searchById(id: String): AnimeLibEpisodesList {
        val response = client.get("$API_HOST/episodes") {
            parameter("anime_id", id)
        }

        return basicJson.decodeFromString<AnimeLibEpisodesList>(response.bodyAsText())
    }

    override suspend fun getPlaylistLink(parseUrl: String, quality: Int): PlaylistResult {
        val (episodeId, team) = parseUrl.split(",", limit = 2)

        val detailResponse = getEpisodeDetails(episodeId)

        val targetPlayer = detailResponse.data.players.firstOrNull {
            it.player.equals("Animelib", ignoreCase = true) && it.team?.id == team.toLongOrNull()
        } ?: throw Exception("No Animelib")

        val videoHost = "${cypher13("uggcf://ivqrb1.pqayvof.bet")}/.%D0%B0s/"

        val sortedQualities = targetPlayer.video?.quality
            ?.sortedByDescending(AnimeLibVideoQuality::quality)
            .orEmpty()

        val targetIndex = sortedQualities.indexOfFirst { it.quality == quality }
        val targetQuality = sortedQualities.getOrNull(targetIndex)

        val defaultUrl = targetQuality?.href?.let { "$videoHost$it" }.orEmpty()
        val fallbackUrls = sortedQualities.mapIndexedNotNull { index, item ->
            if (index != targetIndex) "$videoHost${item.href}" else null
        }

        val subtitleTracks = targetPlayer.subtitles.mapIndexedNotNull { index, sub ->
            when (val format = sub.format.lowercase()) {
                "vtt", "srt", "ass", "ssa" -> SubtitleTrack(
                    name = "${format.uppercase()} ${index + 1}",
                    url = sub.src
                )

                else -> null
            }
        }

        return PlaylistResult(
            url = defaultUrl,
            fallbackUrls = fallbackUrls,
            qualityList = sortedQualities.map(AnimeLibVideoQuality::quality),
            subtitles = subtitleTracks,
            headers = mapOf(
                "Origin" to ORIGIN,
                "Referer" to "$ORIGIN/"
            )
        )
    }

    suspend fun searchByTitle(id: String, title: String): AnimeLibResultItem? {
        val titles = title.split('/').map(String::trim)
        val search = channelFlow {
            titles.forEach { title ->
                launch {
                    runCatching {
                        val responseText = client.get("$API_HOST/anime") {
                            parameter("q", title)
                        }

                        basicJson.decodeFromString<AnimeLibSearchResult>(responseText.bodyAsText())
                            .data
                            .firstOrNull { it.shikimoriHref.substringAfterLast('/') == id }

                    }.onSuccess { foundItem -> foundItem?.let { send(it) } }
                        .onFailure { e -> if (e is CancellationException) throw e }
                }
            }
        }.firstOrNull()

        if (search == null) return null

        val anime = client.get("$API_HOST/anime/${search.slugUrl}") {
            parameter("fields[]", "teams")
        }

        return basicJson.decodeFromString<AnimeLibSearchResultItem>(anime.bodyAsText()).data
    }

    suspend fun getEpisodeDetails(episodeId: String): AnimeLibEpisodeDetailResponse {
        val response = client.get("$API_HOST/episodes/$episodeId") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        return basicJson.decodeFromString<AnimeLibEpisodeDetailResponse>(response.bodyAsText())
    }
}
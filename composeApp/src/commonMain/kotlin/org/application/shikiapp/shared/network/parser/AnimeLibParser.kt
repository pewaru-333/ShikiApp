package org.application.shikiapp.shared.network.parser

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.models.ui.PlaylistResult
import org.application.shikiapp.shared.models.ui.SubtitleTrack
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.utils.basicJson
import kotlin.coroutines.cancellation.CancellationException

class AnimeLibParser private constructor(client: HttpClient) : BaseParser(client) {
    override val token: String
        get() = Preferences.libToken?.accessToken.orEmpty()

    override suspend fun searchById(id: String): AnimeLibEpisodesList {
        val response = client.get("$API_HOST/episodes") {
            parameter("anime_id", id)
            header("Referer", "$ORIGIN/")
        }

        return basicJson.decodeFromString<AnimeLibEpisodesList>(response.bodyAsText())
    }

    override suspend fun getPlaylistLink(parseUrl: String, quality: Int): PlaylistResult {
        val (episodeId, team) = parseUrl.split(",", limit = 2)

        val detailResponse = getEpisodeDetails(episodeId)

        val targetPlayer = detailResponse.data.players.firstOrNull {
            it.player.equals("Animelib", ignoreCase = true) && it.team?.id == team.toLongOrNull()
        } ?: throw IOException()

        val sortedQualities = targetPlayer.video?.quality.orEmpty()
            .filterTo(ArrayList()) { it.href.isNotBlank() }
            .also { it.sortByDescending(AnimeLibVideoQuality::quality) }

        val targetQuality = sortedQualities.firstOrNull { it.quality == quality }
            ?: sortedQualities.firstOrNull()
            ?: throw IOException()

        val urls = LinkedHashSet(videoUrls(targetQuality.href))
        for (item in sortedQualities) {
            if (item == targetQuality) continue
            urls.addAll(videoUrls(item.href))
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
            url = urls.first(),
            fallbackUrls = urls.drop(1),
            qualityList = sortedQualities.map(AnimeLibVideoQuality::quality),
            subtitles = subtitleTracks,
            headers = mapOf(
                "Origin" to ORIGIN,
                "Referer" to "$ORIGIN/"
            )
        )
    }

    suspend fun searchByTitle(id: String, titles: String): AnimeLibResultItem? {
        val queries = titles
            .splitToSequence('/')
            .mapNotNullTo(HashSet()) { it.trim().ifEmpty { null } }

        return channelFlow {
            queries.forEach { title ->
                launch {
                    runCatching {
                        val responseText = client.get("$API_HOST/anime") {
                            parameter("q", title)
                            header("Referer", "$ORIGIN/")
                        }

                        basicJson.decodeFromString<AnimeLibSearchResult>(responseText.bodyAsText())
                            .data
                            .firstOrNull { it.shikimoriHref.substringAfterLast('/') == id }

                    }.onSuccess { foundItem -> foundItem?.let { send(it) } }
                        .onFailure { e -> if (e is CancellationException) throw e }
                }
            }
        }.firstOrNull()
    }

    suspend fun getTeams(slugUrl: String): List<AnimeLibTeamItem> {
        val response = client.get("$API_HOST/anime/$slugUrl") {
            parameter("fields[]", "teams")
            header("Referer", "$ORIGIN/")
        }

        return basicJson.decodeFromString<AnimeLibSearchResultItem>(response.bodyAsText()).data.teams
    }

    suspend fun getEpisodeDetails(episodeId: String): AnimeLibEpisodeDetailResponse {
        val response = client.get("$API_HOST/episodes/$episodeId") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
                append("Referer", "$ORIGIN/")
            }
        }
        return basicJson.decodeFromString<AnimeLibEpisodeDetailResponse>(response.bodyAsText())
    }

    private fun videoUrls(href: String): List<String> {
        if (href.startsWith("https://") || href.startsWith("http://")) return listOf(href)
        if (href.startsWith("//")) return listOf("https:$href")

        val path = href.trimStart('/')
        return VIDEO_HOSTS.map { "$it/$path" }
    }

    companion object {
        private const val API_HOST_ENC = "uggcf://ncv.pqayvof.bet/ncv"
        val API_HOST by lazy { cypher13(API_HOST_ENC) }
        val API_AUTH_URL by lazy { cypher13("uggcf://nhgu.uragnvpqa.bet/nhgu/bnhgu/nhgubevmr") }

        val ORIGIN by lazy { cypher13("uggcf://i5.navzryvo.bet") }
        private val VIDEO_HOSTS by lazy {
            listOf(
                cypher13("uggcf://ivqrb1.vztyvo.vasb"),
                "${cypher13("uggcf://ivqrb1.pqayvof.bet")}/.%D0%B0s",
                cypher13("uggcf://ivqrb2.pqayvof.bet")
            )
        }

        val instance: AnimeLibParser by lazy { AnimeLibParser(Network.watchClient) }
    }
}
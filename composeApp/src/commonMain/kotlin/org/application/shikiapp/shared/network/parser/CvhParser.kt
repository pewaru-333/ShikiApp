package org.application.shikiapp.shared.network.parser

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.util.flattenEntries
import org.application.shikiapp.shared.models.ui.PlaylistResult
import org.application.shikiapp.shared.models.ui.mappers.getQualityMap
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.basicJson

class CvhParser private constructor(client: HttpClient, override val token: String) : BaseParser(client) {
    override suspend fun searchById(id: String): CvhPlaylistResponse {
        val response = client.get("$API_HOST/playlist") {
            parameter("id", id)
            parameter("pub", "747")
            parameter("aggr", "mali")
        }

        return basicJson.decodeFromString<CvhPlaylistResponse>(response.bodyAsText())
    }

    override suspend fun getPlaylistLink(parseUrl: String, quality: Int): PlaylistResult {
        val request = client.get("$API_HOST/video/$parseUrl")
        val response = basicJson.decodeFromString<CvhVideoResponse>(request.bodyAsText())

        val qualityMap = response.sources.getQualityMap()
        val isAuto = quality == 0 || quality == -1

        val mainUrl = if (!isAuto && qualityMap.containsKey(quality)) {
            qualityMap.getValue(quality)
        } else {
            response.sources.hlsUrl?.takeIf { it.isNotBlank() }
                ?: response.sources.dashUrl?.takeIf { it.isNotBlank() }
                ?: qualityMap.maxByOrNull { it.key }?.value.orEmpty()
        }

        val isStream = (mainUrl == response.sources.hlsUrl || mainUrl == response.sources.dashUrl)
        val qualityList = if (isStream) emptyList() else qualityMap.keys.sortedDescending()

        return PlaylistResult(
            url = mainUrl,
            fallbackUrls = qualityMap.values.filter { it != mainUrl },
            qualityList = qualityList,
            headers = request.call.request.headers.flattenEntries().toMap()
        )
    }

    companion object {
        private const val API_HOST_ENC = "uggcf://cyncv.pqaivqrbuho.pbz/ncv/i1/cynlre/fi"
        private val API_HOST by lazy { cypher13(API_HOST_ENC) }

        val instance: CvhParser by lazy { CvhParser(Network.watchClient, BLANK) }
    }
}
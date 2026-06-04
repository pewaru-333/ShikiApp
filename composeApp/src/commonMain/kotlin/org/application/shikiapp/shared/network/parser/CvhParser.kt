package org.application.shikiapp.shared.network.parser

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.util.flattenEntries
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.application.shikiapp.shared.models.ui.PlaylistResult
import org.application.shikiapp.shared.models.ui.mappers.getQualityMap
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.basicJson
import kotlin.concurrent.Volatile

class CvhParser private constructor(client: HttpClient) : BaseParser(client) {
    companion object {
        private const val API_HOST_ENC = "uggcf://cyncv.pqaivqrbuho.pbz/ncv/i1/cynlre/fi"
        private val API_HOST by lazy { cypher13(API_HOST_ENC) }

        @Volatile
        private var INSTANCE: CvhParser? = null

        private val mutex = Mutex()
        suspend fun getInstance(): CvhParser {
            INSTANCE?.let { return it }
            return mutex.withLock {
                INSTANCE ?: CvhParser(Network.watchClient).also { INSTANCE = it }
            }
        }

    }

    override val token: String = BLANK

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
}
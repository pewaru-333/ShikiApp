package org.application.shikiapp.shared.utils.data

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.application.shikiapp.shared.network.client.Network
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock

class DataManager(private val manager: IDataManager) {
    suspend fun downloadImage(url: String): Boolean {
        var path: String? = null

        return try {
            Network.client.prepareGet(url).execute { response ->
                if (response.status != HttpStatusCode.OK) return@execute false

                val name = response.call.request.url.segments
                    .lastOrNull()
                    ?.takeIf(String::isNotBlank)
                    ?.decodeURLPart()
                    ?: "img_${Clock.System.now().toEpochMilliseconds()}.jpg"

                manager.saveImage(response.readRawBytes(), name) { path = it }
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                manager.onDeleteDamagedFile(path)
                throw e
            }

            manager.onDeleteDamagedFile(path)

            false
        }
    }
}
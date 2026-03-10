package org.application.shikiapp.shared.utils.data

import io.ktor.client.request.prepareGet
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpStatusCode
import org.application.shikiapp.shared.network.client.Network

class DataManager(private val manager: IDataManager) {
    suspend fun downloadImage(url: String): Boolean {
        var path: String? = null

        return try {
            Network.client.prepareGet(url).execute { response ->
                if (response.status != HttpStatusCode.OK) return@execute false

                val name = response.call.request.url.segments
                    .lastOrNull()
                    ?.takeIf(String::isNotBlank)
                    ?: "img_${System.currentTimeMillis()}.jpg"

                manager.saveImage(response.readRawBytes(), name) { path = it }
            }
        } catch (_: Exception) {
            manager.onDeleteDamagedFile(path)

            false
        }
    }
}
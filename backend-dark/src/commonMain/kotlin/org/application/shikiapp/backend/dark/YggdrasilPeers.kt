package org.application.shikiapp.backend.dark

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import io.ktor.http.isSuccess
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import org.application.shikiapp.shared.utils.basicJson
import kotlin.coroutines.cancellation.CancellationException

internal object YggdrasilPeers {
    suspend fun discover(baseClient: HttpClient): List<String> {
        val client = baseClient.config {
            install(HttpTimeout) {
                requestTimeoutMillis = 8000
                connectTimeoutMillis = 4000
                socketTimeoutMillis = 4000
            }
        }

        return client.use {
            try {
                val response = client.get("https://yggpeers.fsky.io/api/v1/peers") {
                    parameter("protocol", "tls")
                    parameter("status", "up")
                    parameter("is_network", false)
                }

                if (response.status.isSuccess()) parse(response.bodyAsText())
                else emptyList()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    private fun parse(text: String): List<String> {
        val json = basicJson.parseToJsonElement(text) as? JsonObject ?: return emptyList()
        val peers = json["peers"]?.jsonArray.orEmpty().mapNotNull { item ->
            val peer = item as? JsonObject ?: return@mapNotNull null
            if (
                (peer["status"] as? JsonPrimitive)?.contentOrNull != "up" ||
                (peer["is_network"] as? JsonPrimitive)?.contentOrNull == "true"
            ) return@mapNotNull null

            (peer["uri"] as? JsonPrimitive)?.contentOrNull
        }

        val hosts = HashSet<String>()
        return peers.filter { peer ->
            try {
                val parsed = Url(peer)
                parsed.protocol.name == "tls" &&
                        parsed.host.isNotBlank() &&
                        parsed.port in 1..65535 &&
                        hosts.add(parsed.host.lowercase())
            } catch (_: Exception) {
                false
            }
        }.shuffled().take(6)
    }
}
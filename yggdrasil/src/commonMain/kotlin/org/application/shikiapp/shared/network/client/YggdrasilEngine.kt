package org.application.shikiapp.shared.network.client

import io.ktor.client.engine.*
import io.ktor.client.plugins.HttpTimeoutCapability
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.forEachHeader
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

class YggdrasilEngine(
    private val yggdrasil: YggdrasilClient,
    private val settings: () -> YggdrasilConfig,
    private val onPrivateKeyGenerated: (String) -> Unit
) : HttpClientEngineFactory<YggdrasilEngineConfig> {

    override fun create(block: YggdrasilEngineConfig.() -> Unit): HttpClientEngine =
        YggdrasilClientEngine(
            config = YggdrasilEngineConfig().apply(block),
            yggdrasil = yggdrasil,
            settings = settings,
            onPrivateKeyGenerated = onPrivateKeyGenerated
        )

    @OptIn(InternalAPI::class)
    private class YggdrasilClientEngine(
        override val config: YggdrasilEngineConfig,
        private val yggdrasil: YggdrasilClient,
        private val settings: () -> YggdrasilConfig,
        private val onPrivateKeyGenerated: (String) -> Unit
    ) : HttpClientEngineBase("Yggdrasil") {

        override val supportedCapabilities: Set<HttpClientEngineCapability<*>> = setOf(HttpTimeoutCapability)

        private val json = Json {
            ignoreUnknownKeys = true
        }

        private suspend fun ensureStarted() {
            val config = settings()
            if (!config.enabled || config.peers.isEmpty()) throw IOException()

            yggdrasil.start(config.peers, config.privateKeyPem)

            if (config.privateKeyPem == null) {
                onPrivateKeyGenerated(yggdrasil.privateKeyPem())
            }
        }

        override suspend fun execute(data: HttpRequestData): HttpResponseData {
            ensureStarted()

            val callContext = callContext()
            val requestTime = GMTDate()

            val body = when (val outgoing = data.body) {
                is OutgoingContent.NoContent -> byteArrayOf()
                is OutgoingContent.ByteArrayContent -> outgoing.bytes()
                else -> throw IOException()
            }

            val requestHeaders = buildMap {
                data.forEachHeader { name, value ->
                    getOrPut(name) { mutableListOf() }
                        .add(value)
                }
            }

            val requestTimeout = data.getCapabilityOrNull(HttpTimeoutCapability)
                ?.requestTimeoutMillis
                ?: 60_000L

            val bridgeResponse = try {
                yggdrasil.request(
                    method = data.method.value,
                    url = data.url.toString(),
                    headersJson = json.encodeToString(requestHeaders),
                    body = body,
                    timeoutMillis = requestTimeout
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                throw IOException(e)
            }

            val responseHeadersMap = json.decodeFromString<Map<String, List<String>>>(bridgeResponse.headersJson)
            val responseHeaders = Headers.build {
                responseHeadersMap.forEach { (name, values) ->
                    values.forEach { value ->
                        append(name, value)
                    }
                }
            }

            return HttpResponseData(
                statusCode = HttpStatusCode.fromValue(bridgeResponse.statusCode),
                requestTime = requestTime,
                headers = responseHeaders,
                version = HttpProtocolVersion.HTTP_1_1,
                body = ByteReadChannel(bridgeResponse.body),
                callContext = callContext
            )
        }
    }
}
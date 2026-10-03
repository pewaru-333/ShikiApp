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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

class YggdrasilEngineConfig : HttpClientEngineConfig()

class YggdrasilEngine(
    private val yggdrasil: YggdrasilClient,
    peers: List<String>,
    privateKeyPem: String?,
    private val onPrivateKeyGenerated: (String) -> Unit,
) : HttpClientEngineFactory<YggdrasilEngineConfig> {

    private val peers = peers
        .mapNotNullTo(LinkedHashSet()) { it.trim().takeIf(String::isNotEmpty) }
        .toList()

    private val privateKeyPem = privateKeyPem
        ?.trim()
        ?.takeIf(String::isNotEmpty)

    override fun create(block: YggdrasilEngineConfig.() -> Unit): HttpClientEngine =
        YggdrasilClientEngine(
            config = YggdrasilEngineConfig().apply(block),
            yggdrasil = yggdrasil,
            peers = peers,
            privateKeyPem = privateKeyPem,
            onPrivateKeyGenerated = onPrivateKeyGenerated,
        )
}

@OptIn(InternalAPI::class)
private class YggdrasilClientEngine(
    override val config: YggdrasilEngineConfig,
    private val yggdrasil: YggdrasilClient,
    private val peers: List<String>,
    private val privateKeyPem: String?,
    private val onPrivateKeyGenerated: (String) -> Unit,
) : HttpClientEngineBase("Yggdrasil") {

    override val supportedCapabilities: Set<HttpClientEngineCapability<*>> = setOf(HttpTimeoutCapability)

    private val mutex = Mutex()

    private var started = false

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private suspend fun ensureStarted() {
        if (started) {
            return
        }

        mutex.withLock {
            if (started) {
                return
            }

            yggdrasil.start(peers, privateKeyPem)

            if (privateKeyPem == null) {
                onPrivateKeyGenerated(yggdrasil.privateKeyPem())
            }

            started = true
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
                timeoutMillis = requestTimeout,
            )
        } catch (e: Throwable) {
            throw IOException("Yggdrasil request failed: ${data.url}", e)
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
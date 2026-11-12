package org.application.shikiapp.backend.dark

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineBase
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutCapability
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import org.application.shikiapp.backend.dark.resources.Res
import org.application.shikiapp.backend.dark.resources.text_yggdrasil_login_unavailable
import org.application.shikiapp.shared.network.client.NetworkExtension
import org.application.shikiapp.shared.network.client.YggdrasilConfig
import org.application.shikiapp.shared.network.client.YggdrasilTransport
import org.application.shikiapp.shared.utils.basicJson
import kotlin.concurrent.Volatile
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

internal class YggdrasilNetworkExtension(private val transport: YggdrasilTransport) : NetworkExtension {
    private val address = "http://[201:601c:da30:ddb7:77a0:9bb0:135f:b1e2]"

    @Volatile
    private var automatic = false

    @Volatile
    private var activePeers: List<String> = emptyList()
    private var lastFailure: TimeMark? = null
    private var authProxyActive = false

    private val config: YggdrasilConfig
        get() = YggdrasilConfig(
            enabled = activePeers.isNotEmpty() || isActive,
            peers = activePeers.ifEmpty { YggdrasilPreferences.peerList.filter(String::isNotBlank) },
            privateKeyPem = YggdrasilPreferences.privateKey.value.takeIf(String::isNotBlank)
        )

    override val isActive: Boolean
        get() = YggdrasilPreferences.enabled.value || automatic

    override val requiresAuthProxy: Boolean get() = isActive
    override val authUnavailableMessage = Res.string.text_yggdrasil_login_unavailable

    override fun createClient(normal: () -> HttpClient, block: HttpClientConfig<*>.() -> Unit) =
        HttpClient(
            block = block,
            engineFactory = object : HttpClientEngineFactory<HttpClientEngineConfig> {
                override fun create(block: HttpClientEngineConfig.() -> Unit) = RoutingEngine(
                    config = HttpClientEngineConfig().apply(block),
                    normal = normal(),
                    host = Url(address).host,
                    overlay = lazy(::createOverlayClient)
                )
            }
        )

    private fun createOverlayClient(): HttpClient = transport.createClient(
        config = { config },
        onPrivateKeyGenerated = { YggdrasilPreferences.privateKey.value = it },
        block = {}
    )

    override suspend fun resolve(client: HttpClient): String {
        if (lastFailure?.elapsedNow()?.let { it < 30.seconds } == true) {
            throw IOException()
        }

        val probe = client.config {
            followRedirects = false
            install(HttpTimeout) {
                requestTimeoutMillis = 12000
                connectTimeoutMillis = 8000
                socketTimeoutMillis = 8000
            }
        }

        var attempts = 0
        var connected = false

        suspend fun connect(peers: List<String>): Boolean {
            if (peers.isEmpty()) return false
            attempts++
            activePeers = peers

            return try {
                val response = probe.get("$address/api/animes/1")
                val data = basicJson.parseToJsonElement(response.bodyAsText()) as? JsonObject
                if (response.status != HttpStatusCode.OK || (data?.get("id") as? JsonPrimitive)?.intOrNull != 1) {
                    false
                } else {
                    YggdrasilPreferences.cachedPeers.value = peers.joinToString(",")
                    connected = true
                    automatic = true
                    lastFailure = null
                    true
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                false
            }
        }

        try {
            probe.use {
                val saved = listOf(YggdrasilPreferences.peerList, YggdrasilPreferences.cachedPeers.value.split(','))
                    .mapNotNullTo(linkedSetOf()) { peers ->
                        peers.mapNotNullTo(linkedSetOf()) { it.trim().ifEmpty { null } }
                            .take(3)
                            .takeIf { it.isNotEmpty() }
                    }

                for (peers in saved) {
                    if (connect(peers)) return address
                }
                for (peers in YggdrasilPeers.discover(client).chunked(3).take(3 - attempts)) {
                    if (connect(peers)) return address
                }
            }
            lastFailure = TimeSource.Monotonic.markNow()
            throw IOException()
        } finally {
            if (!connected) {
                automatic = false
                activePeers = emptyList()
                withContext(NonCancellable) {
                    transport.stop()
                }
            }
        }
    }

    override suspend fun startAuthProxy(): Int? {
        if (!isActive) return null

        authProxyActive = true
        return transport.startProxy(config) { privateKey ->
            YggdrasilPreferences.privateKey.value = privateKey
        }
    }

    override suspend fun stopAuthProxy() {
        if (!authProxyActive) return
        try {
            transport.stopProxy()
        } finally {
            authProxyActive = false
        }
    }

    @OptIn(InternalAPI::class)
    private class RoutingEngine(
        override val config: HttpClientEngineConfig,
        private val normal: HttpClient,
        private val host: String,
        private val overlay: Lazy<HttpClient>
    ) : HttpClientEngineBase("Routing") {
        override val supportedCapabilities = setOf(HttpTimeoutCapability)

        override suspend fun execute(data: HttpRequestData): HttpResponseData {
            if (!data.url.host.equals(host, ignoreCase = true)) {
                return normal.engine.execute(data)
            }

            val headers = Headers.build {
                appendAll(data.headers)
                remove(HttpHeaders.ProxyAuthorization)
            }

            return overlay.value.engine.execute(HttpRequestData(data.url, data.method, headers, data.body, data.executionContext, data.attributes))
        }

        override fun close() {
            super.close()
            normal.close()
            if (overlay.isInitialized()) {
                overlay.value.close()
            }
        }
    }
}


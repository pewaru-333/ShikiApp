package org.application.shikiapp.shared.network.client

import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.get
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.IOException
import org.application.shikiapp.shared.di.Preferences
import kotlin.concurrent.Volatile

internal class BaseUrlResolver {
    private val mutex = Mutex()
    val isResolving: StateFlow<Boolean>
        field = MutableStateFlow(false)

    @Volatile
    var workingUrl: String? = null
        private set

    suspend fun resolve(): String {
        val cached = workingUrl
        if (cached != null) return cached

        return mutex.withLock {
            val cached = workingUrl
            if (cached != null) return@withLock cached

            isResolving.value = true
            try {
                val url = if (Network.isConnectionActive) Network.resolveFallback()
                else {
                    try {
                        resolveUrl(Preferences.appUrlList)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: IOException) {
                        if (!Network.hasNetworkExtension) throw e
                        Network.resolveFallback()
                    }
                }

                workingUrl = url
                url
            } finally {
                isResolving.value = false
            }
        }
    }

    suspend fun invalidate(url: String) = mutex.withLock {
        if (workingUrl == url) {
            workingUrl = null
        }
    }

    private suspend fun resolveUrl(urls: List<String>): String = coroutineScope {
        val pingClient = Network.baseClient.config {
            expectSuccess = false
            followRedirects = false

            install(HttpTimeout) {
                requestTimeoutMillis = 3000
                connectTimeoutMillis = 3000
                socketTimeoutMillis = 3000
            }
        }

        pingClient.use { client ->
            val winner = CompletableDeferred<String>()
            val probes = urls.map { url ->
                launch {
                    try {
                        val all = HashSet<String>()
                        var now = Url(url.trim())

                        repeat(5) {
                            if (!all.add(now.toString())) return@launch
                            if (now.protocol != URLProtocol.HTTP && now.protocol != URLProtocol.HTTPS) {
                                return@launch
                            }

                            val response = client.get(now)
                            if (response.status.value in 200..299) {
                                val origin = URLBuilder(now.protocol, now.host, now.port).buildString()

                                winner.complete(origin.removeSuffix("/"))
                                return@launch
                            }

                            when (response.status) {
                                HttpStatusCode.MovedPermanently,
                                HttpStatusCode.Found,
                                HttpStatusCode.SeeOther,
                                HttpStatusCode.TemporaryRedirect,
                                HttpStatusCode.PermanentRedirect -> Unit

                                else -> return@launch
                            }

                            val location = response.headers[HttpHeaders.Location] ?: return@launch
                            val next = URLBuilder(now).takeFrom(location).build()
                            if (now.protocol == URLProtocol.HTTPS && next.protocol != URLProtocol.HTTPS) {
                                return@launch
                            }

                            now = next
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {

                    }
                }
            }

            launch {
                probes.joinAll()
                winner.completeExceptionally(IOException())
            }

            try {
                winner.await()
            } finally {
                coroutineContext.cancelChildren()
            }
        }
    }
}

val BaseUrlResolverPlugin = createClientPlugin("BaseUrlResolverPlugin") {
    val resolver = Network.baseUrlResolver

    on(Send) { request ->
        val url = resolver.resolve()
        val finalUrl = Url(url)
        request.url {
            protocol = finalUrl.protocol
            host = finalUrl.host
            port = finalUrl.port
        }

        request.headers.remove(HttpHeaders.Host)

        try {
            proceed(request)
        } catch (e: Throwable) {
            if (e !is ResponseException && e !is CancellationException) {
                resolver.invalidate(url)
            }
            throw e
        }
    }
}
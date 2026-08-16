package org.application.shikiapp.shared.network.client

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.application.shikiapp.shared.utils.BLANK

class BaseUrlResolverConfig {
    var baseUrl: String = BLANK
    var mirrors: List<String> = emptyList()
    var onNewUrl: ((String) -> Unit)? = null
}

val BaseUrlResolverPlugin = createClientPlugin("BaseUrlResolverPlugin", ::BaseUrlResolverConfig) {
    val baseUrl = pluginConfig.baseUrl
    val mirrors = pluginConfig.mirrors
    val onNewUrl = pluginConfig.onNewUrl

    val urls = listOf(baseUrl) + mirrors
    val mutex = Mutex()
    var workingUrl: String? = null

    suspend fun resolveUrl(): String = coroutineScope {
        val pingClient = HttpClient {
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

            urls.forEach { url ->
                launch {
                    try {
                        val response = client.get(url)
                        if (response.status.value in 200..399) {
                            val location = response.headers[HttpHeaders.Location]
                            winner.complete(location ?: url)
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {

                    }
                }
            }

            val result = winner.await()
            coroutineContext.cancelChildren()

            return@coroutineScope result
        }
    }

    on(Send) { request ->
        if (workingUrl == null) {
            mutex.withLock {
                if (workingUrl == null) {
                    workingUrl = resolveUrl()
                    workingUrl?.let { onNewUrl?.invoke(it) }
                }
            }
        }

        workingUrl?.let {
            val finalUrl = Url(it)
            request.url {
                protocol = finalUrl.protocol
                host = finalUrl.host
                port = finalUrl.port
            }
        }

        request.headers.remove(HttpHeaders.Host)

        try {
            proceed(request)
        } catch (e: Throwable) {
            if (e !is ResponseException && e !is CancellationException) {
                workingUrl = null
            }
            throw e
        }
    }
}
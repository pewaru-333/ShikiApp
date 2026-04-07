package org.application.shikiapp.shared.network.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import kotlinx.coroutines.CancellationException
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

    suspend fun resolveUrl(): String {
        val pingClient = HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 3000
                connectTimeoutMillis = 3000
                socketTimeoutMillis = 3000
            }
        }

        pingClient.use { pingClient ->
            for (url in urls) {
                try {
                    if (pingClient.get(url).status.value in 200..499) {
                        return url
                    }
                } catch (_: Exception) {
                }
            }
        }

        return baseUrl
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
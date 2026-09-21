package org.application.shikiapp.shared.network.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.Credentials
import java.net.Authenticator
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy
import java.util.concurrent.TimeUnit

internal actual fun createHttpClient(proxyConfig: ProxyConfig?, block: HttpClientConfig<*>.() -> Unit) =
    HttpClient(OkHttp) {
        block()

        engine {
            config {
                retryOnConnectionFailure(true)
                connectTimeout(30, TimeUnit.SECONDS)

                if (proxyConfig != null) {
                    val proxyType = if (proxyConfig.isSocks) Proxy.Type.SOCKS else Proxy.Type.HTTP
                    proxy = Proxy(proxyType, InetSocketAddress(proxyConfig.host, proxyConfig.port))

                    if (proxyConfig.hasAuth) {
                        if (proxyConfig.isSocks) Authenticator.setDefault(ProxyAuthenticator(proxyConfig))
                        else proxyAuthenticator { _, response ->
                            response.request.newBuilder()
                                .header("Proxy-Authorization", Credentials.basic(proxyConfig.user, proxyConfig.pass))
                                .build()
                        }
                    }
                }
            }
        }
    }

internal class ProxyAuthenticator(private val proxyConfig: ProxyConfig) : Authenticator() {
    override fun getPasswordAuthentication(): PasswordAuthentication? {
        if (requestingHost.equals(proxyConfig.host, ignoreCase = true) || requestingPort == proxyConfig.port) {
            return PasswordAuthentication(proxyConfig.user, proxyConfig.pass.toCharArray())
        }

        return null
    }
}
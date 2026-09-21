package org.application.shikiapp.shared.network.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSNumber

internal actual fun createHttpClient(proxyConfig: ProxyConfig?, block: HttpClientConfig<*>.() -> Unit) = HttpClient(Darwin) {
    block()

    engine {
        configureSession {
            timeoutIntervalForRequest = 60.0
            timeoutIntervalForResource = 300.0

            if (proxyConfig != null) {
                connectionProxyDictionary = buildMap {
                    if (proxyConfig.isSocks) {
                        put("SOCKSEnable", NSNumber(int = 1))
                        put("SOCKSProxy", proxyConfig.host)
                        put("SOCKSPort", NSNumber(int = proxyConfig.port))

                        if (proxyConfig.hasAuth) {
                            put("SOCKSUser", proxyConfig.user)
                            put("SOCKSPassword", proxyConfig.pass)
                        }
                    } else {
                        put("HTTPSEnable", NSNumber(int = 1))
                        put("HTTPSProxy", proxyConfig.host)
                        put("HTTPSPort", NSNumber(int = proxyConfig.port))

                        put("HTTPEnable", NSNumber(int = 1))
                        put("HTTPProxy", proxyConfig.host)
                        put("HTTPPort", NSNumber(int = proxyConfig.port))
                    }
                }
            }
        }
    }
}
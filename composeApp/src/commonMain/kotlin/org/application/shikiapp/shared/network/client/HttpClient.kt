package org.application.shikiapp.shared.network.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import org.application.shikiapp.shared.di.AppServices
import org.application.shikiapp.shared.di.Preferences

internal fun createHttpClient(
    proxyConfig: ProxyConfig?,
    yggdrasilConfig: YggdrasilConfig,
    block: HttpClientConfig<*>.() -> Unit
): HttpClient {
    if (!yggdrasilConfig.enabled) {
        return createPlatformHttpClient(proxyConfig, block)
    }

    val transport = checkNotNull(AppServices.yggdrasilTransport) // точно не null по конфигурации
    return transport.createClient(
        config = yggdrasilConfig,
        onPrivateKeyGenerated = { Preferences.yggdrasilPrivateKey.value = it },
        block = block
    )
}

internal expect fun createPlatformHttpClient(
    proxyConfig: ProxyConfig?,
    block: HttpClientConfig<*>.() -> Unit
): HttpClient
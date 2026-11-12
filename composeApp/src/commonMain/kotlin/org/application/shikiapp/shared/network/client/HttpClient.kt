package org.application.shikiapp.shared.network.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

internal fun createHttpClient(
    proxyConfig: ProxyConfig?,
    extension: NetworkExtension?,
    block: HttpClientConfig<*>.() -> Unit
) = extension?.createClient(
    block = block,
    normal = { createPlatformHttpClient(proxyConfig) {} }
) ?: createPlatformHttpClient(proxyConfig, block)

internal expect fun createPlatformHttpClient(
    proxyConfig: ProxyConfig?,
    block: HttpClientConfig<*>.() -> Unit
): HttpClient
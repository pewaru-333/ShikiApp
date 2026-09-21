package org.application.shikiapp.shared.network.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

internal expect fun createHttpClient(proxyConfig: ProxyConfig?, block: HttpClientConfig<*>.() -> Unit): HttpClient
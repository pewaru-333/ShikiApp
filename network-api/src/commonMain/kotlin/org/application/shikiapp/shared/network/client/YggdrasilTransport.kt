package org.application.shikiapp.shared.network.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

interface YggdrasilTransport {
    fun createClient(
        config: YggdrasilConfig,
        onPrivateKeyGenerated: (String) -> Unit,
        block: HttpClientConfig<*>.() -> Unit
    ): HttpClient

    suspend fun startProxy(config: YggdrasilConfig, onPrivateKeyGenerated: (String) -> Unit): Int
    suspend fun stopProxy()
}
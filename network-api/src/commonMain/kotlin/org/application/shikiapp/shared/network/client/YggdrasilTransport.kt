package org.application.shikiapp.shared.network.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

interface YggdrasilTransport {
    fun createClient(
        config: YggdrasilConfig,
        onPrivateKeyGenerated: (String) -> Unit,
        block: HttpClientConfig<*>.() -> Unit,
    ): HttpClient
}

data class YggdrasilConfig(
    val enabled: Boolean,
    val peers: List<String>,
    val privateKeyPem: String?,
)
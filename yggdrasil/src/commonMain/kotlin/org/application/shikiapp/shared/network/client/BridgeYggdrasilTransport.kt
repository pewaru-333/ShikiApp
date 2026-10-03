package org.application.shikiapp.shared.network.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

object BridgeYggdrasilTransport : YggdrasilTransport {
    override fun createClient(
        config: YggdrasilConfig,
        onPrivateKeyGenerated: (String) -> Unit,
        block: HttpClientConfig<*>.() -> Unit,
    ): HttpClient = HttpClient(
        YggdrasilEngine(
            yggdrasil = platformYggdrasilClient(),
            peers = config.peers,
            privateKeyPem = config.privateKeyPem,
            onPrivateKeyGenerated = onPrivateKeyGenerated,
        )
    ) {
        block()
    }
}

internal expect fun platformYggdrasilClient(): YggdrasilClient

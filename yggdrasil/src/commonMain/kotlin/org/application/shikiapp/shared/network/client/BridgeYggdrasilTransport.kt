package org.application.shikiapp.shared.network.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

object BridgeYggdrasilTransport : YggdrasilTransport {
    override fun createClient(
        config: YggdrasilConfig,
        onPrivateKeyGenerated: (String) -> Unit,
        block: HttpClientConfig<*>.() -> Unit
    ): HttpClient = HttpClient(
        YggdrasilEngine(
            yggdrasil = platformYggdrasilClient(),
            peers = config.peers,
            privateKeyPem = config.privateKeyPem,
            onPrivateKeyGenerated = onPrivateKeyGenerated
        )
    ) {
        block()
    }

    override suspend fun startProxy(config: YggdrasilConfig, onPrivateKeyGenerated: (String) -> Unit): Int {
        val client = platformYggdrasilClient()
        client.start(config.peers, config.privateKeyPem)

        val privateKey = client.privateKeyPem()
        if (privateKey.isNotBlank() && privateKey != config.privateKeyPem) {
            onPrivateKeyGenerated(privateKey)
        }

        return client.startProxy()
    }

    override suspend fun stopProxy() = platformYggdrasilClient().stopProxy()
}

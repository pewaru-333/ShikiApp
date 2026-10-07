package org.application.shikiapp.shared.network.client

interface YggdrasilClient {
    fun address(): String
    fun privateKeyPem(): String

    suspend fun start(peers: List<String>, privateKeyPem: String?)
    suspend fun stop()

    suspend fun startProxy(): Int
    suspend fun stopProxy()

    suspend fun request(
        method: String,
        url: String,
        headersJson: String = "{}",
        body: ByteArray = byteArrayOf(),
        timeoutMillis: Long = 60_000L
    ): YggResponse
}

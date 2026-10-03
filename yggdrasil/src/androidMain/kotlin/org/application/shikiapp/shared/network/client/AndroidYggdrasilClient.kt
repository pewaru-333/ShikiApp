package org.application.shikiapp.shared.network.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import yggbridge.Yggbridge

internal object AndroidYggdrasilClient : YggdrasilClient {
    private val mutex = Mutex()

    private var startedPeers: List<String>? = null

    override fun address(): String = Yggbridge.address()

    override fun privateKeyPem(): String = Yggbridge.privateKeyPEM()

    override suspend fun start(peers: List<String>, privateKeyPem: String?) {
        val normalizedPeers = peers
            .mapNotNullTo(LinkedHashSet()) { it.trim().takeIf(String::isNotEmpty) }
            .toList()

        mutex.withLock {
            if (Yggbridge.isStarted()) {
                if (startedPeers == normalizedPeers) {
                    return
                }
            }

            val peersJson = Json.encodeToString(normalizedPeers)

            withContext(Dispatchers.IO) {
                Yggbridge.start(peersJson, privateKeyPem.orEmpty())
            }

            startedPeers = normalizedPeers
        }
    }

    override suspend fun request(
        method: String,
        url: String,
        headersJson: String,
        body: ByteArray,
        timeoutMillis: Long,
    ) = withContext(Dispatchers.IO) {
        val response = Yggbridge.request(method, url, headersJson, body, timeoutMillis)

        YggResponse(
            statusCode = response.statusCode.toInt(),
            headersJson = response.headers.orEmpty(),
            body = response.body ?: byteArrayOf(),
        )
    }

    override suspend fun stop() {
        mutex.withLock {
            if (Yggbridge.isStarted()) {
                withContext(Dispatchers.IO) {
                    Yggbridge.stop()
                }
            }

            startedPeers = null
        }
    }
}
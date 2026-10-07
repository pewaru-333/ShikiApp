package org.application.shikiapp.shared.network.client

import com.sun.jna.Memory
import com.sun.jna.Pointer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.application.shikiapp.shared.network.NativeYggResponse
import org.application.shikiapp.shared.utils.data.YggbridgeNativeLoader
import java.io.IOException

internal object DesktopYggdrasilClient : YggdrasilClient {
    private val native by lazy {
        YggbridgeNativeLoader.instance
    }

    private val mutex = Mutex()

    private var startedPeers: List<String>? = null

    override fun address(): String = native.YggAddress()
        .consumeNativeString()
        .orEmpty()

    override fun privateKeyPem(): String = native.YggPrivateKeyPEM()
        .consumeNativeString()
        .orEmpty()

    override suspend fun start(peers: List<String>, privateKeyPem: String?) {
        val normalizedPeers = peers
            .mapNotNullTo(LinkedHashSet()) { it.trim().takeIf(String::isNotEmpty) }
            .toList()

        mutex.withLock {
            if (native.YggIsStarted() != 0) {
                if (startedPeers == normalizedPeers) {
                    return
                }
            }

            val peersJson = Json.encodeToString(normalizedPeers)

            withContext(Dispatchers.IO) {
                native.YggStart(peersJson, privateKeyPem.orEmpty())
                    .consumeNativeString()
            }

            startedPeers = normalizedPeers
        }
    }

    override suspend fun startProxy(): Int = withContext(Dispatchers.IO) {
        native.YggStartProxy().consumeNativeString()?.let { throw IOException(it) }
        native.YggProxyPort()
    }

    override suspend fun stopProxy() = withContext(Dispatchers.IO) {
        native.YggStopProxy().consumeNativeString()?.let { throw IOException(it) }
        Unit
    }

    override suspend fun request(
        method: String,
        url: String,
        headersJson: String,
        body: ByteArray,
        timeoutMillis: Long
    ): YggResponse = withContext(Dispatchers.IO) {
        body.withNativeMemory { bodyPointer ->
            val responsePointer = native.YggRequest(
                method,
                url,
                headersJson,
                bodyPointer,
                body.size.toLong(),
                timeoutMillis
            ) ?: throw IOException()

            try {
                val response = NativeYggResponse(responsePointer)
                val responseBody = if (response.body == null || response.bodyLen == 0L) {
                    byteArrayOf()
                } else {
                    response.body!!.getByteArray(0, response.bodyLen.toInt())
                }

                val responseHeaders = response.headers
                    ?.getString(0, "UTF-8")
                    .orEmpty()

                YggResponse(
                    statusCode = response.statusCode.toInt(),
                    headersJson = responseHeaders,
                    body = responseBody
                )
            } finally {
                native.YggFreeResponse(responsePointer)
            }
        }
    }

    override suspend fun stop() {
        mutex.withLock {
            if (native.YggIsStarted() == 0) {
                startedPeers = null
                return
            }

            withContext(Dispatchers.IO) {
                native.YggStop().consumeNativeString()
            }

            startedPeers = null
        }
    }

    private fun Pointer?.consumeNativeString(): String? {
        if (this == null) {
            return null
        }

        return try {
            getString(0, "UTF-8")
        } finally {
            native.YggFreeString(this)
        }
    }

    private inline fun <T> ByteArray.withNativeMemory(block: (Pointer?) -> T): T {
        if (isEmpty()) {
            return block(null)
        }

        val memory = Memory(size.toLong())

        memory.write(
            /* bOff = */ 0,
            /* buf = */ this,
            /* index = */ 0,
            /* length = */ size
        )

        return block(memory)
    }
}
@file:OptIn(ExperimentalForeignApi::class)

package org.application.shikiapp.shared.network.client

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.create
import platform.Foundation.getBytes
import yggbridge.ShikiYggbridgeRequest
import yggbridge.ShikiYggbridgeStart
import yggbridge.ShikiYggbridgeStop
import yggbridge.YggbridgeAddress
import yggbridge.YggbridgeIsStarted
import yggbridge.YggbridgePrivateKeyPEM

internal object IosYggdrasilClient : YggdrasilClient {
    private val mutex = Mutex()

    private var startedPeers: List<String>? = null

    override fun address(): String = YggbridgeAddress()

    override fun privateKeyPem(): String = YggbridgePrivateKeyPEM()

    @OptIn(BetaInteropApi::class)
    override suspend fun start(peers: List<String>, privateKeyPem: String?) {
        val normalizedPeers = peers
            .mapNotNullTo(LinkedHashSet()) { it.trim().takeIf(String::isNotEmpty) }
            .toList()

        mutex.withLock {
            if (YggbridgeIsStarted()) {
                if (startedPeers == normalizedPeers) {
                    return
                }
            }

            val peersJson = Json.encodeToString(normalizedPeers)

            withContext(Dispatchers.Default) {
                memScoped {
                    val nativeError = alloc<ObjCObjectVar<NSError?>>()
                    nativeError.value = null

                    val started = ShikiYggbridgeStart(
                        peersJSON = peersJson,
                        savedPrivateKeyPEM = privateKeyPem.orEmpty(),
                        error = nativeError.ptr,
                    )
                    check(started) {
                        nativeError.value?.localizedDescription ?: "Failed to start Yggdrasil"
                    }
                }
            }

            startedPeers = normalizedPeers
        }
    }

    @OptIn(BetaInteropApi::class)
    override suspend fun request(
        method: String,
        url: String,
        headersJson: String,
        body: ByteArray,
        timeoutMillis: Long,
    ) = withContext(Dispatchers.Default) {
        memScoped {
            val nativeError = alloc<ObjCObjectVar<NSError?>>()
            nativeError.value = null

            val response = ShikiYggbridgeRequest(
                method = method,
                url = url,
                headersJSON = headersJson,
                body = body.toNSDataOrNull(),
                timeoutMillis = timeoutMillis,
                error = nativeError.ptr,
            ) ?: error(nativeError.value?.localizedDescription ?: "Yggdrasil request failed")

            val responseBody = response.body
                ?.toByteArray()
                ?: byteArrayOf()

            YggResponse(
                statusCode = response.statusCode.toInt(),
                headersJson = response.headers,
                body = responseBody,
            )
        }
    }

    @OptIn(BetaInteropApi::class)
    override suspend fun stop() {
        mutex.withLock {
            if (!YggbridgeIsStarted()) {
                startedPeers = null
                return
            }

            withContext(Dispatchers.Default) {
                memScoped {
                    val nativeError = alloc<ObjCObjectVar<NSError?>>()
                    nativeError.value = null

                    check(ShikiYggbridgeStop(error = nativeError.ptr)) {
                        nativeError.value?.localizedDescription ?: "Failed to stop Yggdrasil"
                    }
                }
            }

            startedPeers = null
        }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun ByteArray.toNSDataOrNull(): NSData? {
        if (isEmpty()) {
            return null
        }

        return usePinned { pinned ->
            NSData.create(
                bytes = pinned.addressOf(0),
                length = size.toULong(),
            )
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun NSData.toByteArray(): ByteArray {
        if (length == 0uL) {
            return byteArrayOf()
        }

        return ByteArray(length.toInt()).also { result ->
            result.usePinned { pinned ->
                getBytes(
                    buffer = pinned.addressOf(0),
                    length = length,
                )
            }
        }
    }
}
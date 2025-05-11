package org.application.shikiapp.network.client

import coil3.network.NetworkClient
import coil3.network.NetworkHeaders
import coil3.network.NetworkRequest
import coil3.network.NetworkRequestBody
import coil3.network.NetworkResponse
import coil3.network.NetworkResponseBody
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpMethod
import io.ktor.http.takeFrom
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.copyTo
import okio.Buffer
import okio.BufferedSink
import okio.FileSystem
import okio.Path
import java.io.RandomAccessFile

object CoilClient : NetworkClient {
    override suspend fun <T> executeRequest(
        request: NetworkRequest,
        block: suspend (NetworkResponse) -> T
    ) = Network.client.prepareRequest(request.toHttpRequestBuilder())
        .execute { response ->
            block(response.toNetworkResponse())
        }

    private suspend fun NetworkRequest.toHttpRequestBuilder(): HttpRequestBuilder {
        val request = HttpRequestBuilder()
        request.url.takeFrom(url)
        request.method = HttpMethod.parse(method)
        request.headers.takeFrom(headers)
        body?.readByteArray()?.let(request::setBody)
        return request
    }

    private suspend fun NetworkRequestBody.readByteArray(): ByteArray {
        val buffer = Buffer()
        writeTo(buffer)
        return buffer.readByteArray()
    }

    private suspend fun HttpResponse.toNetworkResponse() = NetworkResponse(
        code = status.value,
        requestMillis = requestTime.timestamp,
        responseMillis = responseTime.timestamp,
        headers = headers.toNetworkHeaders(),
        body = CoilResponseBody(bodyAsChannel()),
        delegate = this,
    )

    private fun HeadersBuilder.takeFrom(headers: NetworkHeaders) {
        for ((key, values) in headers.asMap()) {
            appendAll(key, values)
        }
    }

    private fun Headers.toNetworkHeaders(): NetworkHeaders {
        val headers = NetworkHeaders.Builder()
        for ((key, values) in entries()) {
            headers[key] = values
        }
        return headers.build()
    }

    @JvmInline
    private value class CoilResponseBody(private val channel: ByteReadChannel) : NetworkResponseBody {
        @Suppress("INVISIBLE_MEMBER")
        override suspend fun writeTo(sink: BufferedSink) = channel.writeTo(sink)

        @Suppress("INVISIBLE_MEMBER")
        override suspend fun writeTo(fileSystem: FileSystem, path: Path) = channel.writeTo(fileSystem, path)

        override fun close() = channel.cancel(Exception())
    }
}

internal suspend fun ByteReadChannel.writeTo(sink: BufferedSink) {
    copyTo(sink)
}

internal suspend fun ByteReadChannel.writeTo(fileSystem: FileSystem, path: Path) {
    if (fileSystem === FileSystem.SYSTEM) {
        RandomAccessFile(path.toFile(), "rw").use {
            copyTo(it.channel)
        }
    } else {
        fileSystem.write(path) {
            copyTo(this)
        }
    }
}
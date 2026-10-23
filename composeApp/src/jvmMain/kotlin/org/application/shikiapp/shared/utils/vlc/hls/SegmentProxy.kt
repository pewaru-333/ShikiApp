package org.application.shikiapp.shared.utils.vlc.hls

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.future.await
import kotlinx.io.IOException
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.UUID
import java.util.concurrent.Executors

internal class SegmentProxy private constructor(
    private val client: HttpClient,
    source: URI,
    manifest: String,
    private val headers: Map<String, String>
) : AutoCloseable {
    private val prefix = "/${UUID.randomUUID()}/"
    private val uriList = mutableListOf<URI>()
    private val playlist = manifest
        .lineSequence()
        .joinToString(separator = "\n", postfix = "\n") { line ->
            if (line.isBlank()) line
            else if (line.startsWith("#")) URI_REGEX.replace(line) { "URI=\"${register(source.resolve(it.groupValues[1]))}\"" }
            else register(source.resolve(line.trim()))
        }.toByteArray(Charsets.UTF_8)
    private val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    private val executor = Executors.newVirtualThreadPerTaskExecutor()

    val url = "http://127.0.0.1:${server.address.port}${prefix}playlist.m3u8"

    init {
        server.executor = executor
        server.createContext(prefix, ::serve)
        server.start()
    }

    private fun register(uri: URI): String {
        uriList.add(uri)
        return "$prefix${uriList.lastIndex}"
    }

    private fun serve(exchange: HttpExchange) {
        try {
            if (exchange.requestMethod != "GET" && exchange.requestMethod != "HEAD") {
                exchange.sendResponseHeaders(405, -1)
                return
            }

            val path = exchange.requestURI.path.removePrefix(prefix)
            if (path == "playlist.m3u8") {
                respond(exchange, 200, "application/vnd.apple.mpegurl", playlist)
                return
            }

            val resource = path.toIntOrNull()?.let(uriList::getOrNull)
            if (resource == null) {
                exchange.sendResponseHeaders(404, -1)
                return
            }

            val request = HttpRequest.newBuilder(resource).timeout(Duration.ofSeconds(15))
            headers.forEach { (name, value) ->
                if (!name.equals("Range", ignoreCase = true)) {
                    request.header(name, value)
                }
            }

            exchange.requestHeaders.getFirst("Range")?.let {
                request.header("Range", it)
            }

            if (exchange.requestMethod == "HEAD") {
                val response = client.send(request.build(), HttpResponse.BodyHandlers.ofInputStream())
                response.body().use { respond(exchange, response, byteArrayOf()) }
            } else {
                val response = client.send(request.build(), HttpResponse.BodyHandlers.ofByteArray())
                respond(exchange, response, response.body())
            }
        } catch (_: Exception) {
            if (exchange.responseCode == -1) {
                try {
                    exchange.sendResponseHeaders(502, -1)
                } catch (_: IOException) {

                }
            }
        } finally {
            exchange.close()
        }
    }

    private fun respond(exchange: HttpExchange, response: HttpResponse<*>, body: ByteArray) {
        response.headers().firstValue("Content-Range").ifPresent {
            exchange.responseHeaders.set("Content-Range", it)
        }
        response.headers().firstValue("Accept-Ranges").ifPresent {
            exchange.responseHeaders.set("Accept-Ranges", it)
        }
        respond(
            body = body,
            exchange = exchange,
            status = response.statusCode(),
            contentType = response.headers().firstValue("Content-Type").orElse(null) ?: "application/octet-stream",
            contentLength = response.headers().firstValueAsLong("Content-Length").orElse(-1)
        )
    }

    private fun respond(
        exchange: HttpExchange,
        status: Int,
        contentType: String,
        body: ByteArray,
        contentLength: Long = body.size.toLong()
    ) {
        val isHead = exchange.requestMethod == "HEAD"
        val length = if (isHead) contentLength else body.size.toLong()

        exchange.responseHeaders.set("Content-Type", contentType)
        if (length >= 0) {
            exchange.responseHeaders.set("Content-Length", length.toString())
        }

        exchange.sendResponseHeaders(status, if (isHead || body.isEmpty()) -1 else length)
        if (!isHead) {
            exchange.responseBody.write(body)
        }
    }

    override fun close() {
        server.stop(0)
        executor.shutdownNow()
    }

    companion object {
        private val URI_REGEX = Regex("URI=\"([^\"]+)\"")

        suspend fun open(client: HttpClient, url: String, headers: Map<String, String>): SegmentProxy? {
            if (!url.substringBefore('?').endsWith(".mp4:hls:manifest.m3u8")) return null

            val request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(15))
            headers.forEach { (name, value) -> request.header(name, value) }

            val response = client
                .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
                .await()

            if (response.statusCode() !in 200..299) {
                throw IOException("HLS playlist error: HTTP ${response.statusCode()}")
            }

            val manifest = response.body().removePrefix("\uFEFF").trimStart()
            if (!manifest.startsWith("#EXTM3U")) {
                throw IOException("No HLS playlist!")
            }

            return SegmentProxy(client, response.uri(), manifest, headers)
        }
    }
}

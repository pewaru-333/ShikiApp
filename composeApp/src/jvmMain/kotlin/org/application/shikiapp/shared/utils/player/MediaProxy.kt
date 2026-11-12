package org.application.shikiapp.shared.utils.player

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import io.ktor.http.*
import io.ktor.http.header.AcceptEncoding
import org.application.shikiapp.shared.utils.BLANK
import java.io.BufferedInputStream
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

internal class MediaProxy(url: String, private val headers: Map<String, String>) : AutoCloseable {
    private val source = URI.create(url).also {
        require(it.scheme == URLProtocol.HTTPS.name || it.scheme == URLProtocol.HTTP.name)
    }
    private val sequence = AtomicInteger()
    private val prefix = "/${UUID.randomUUID()}/"
    private val resources = ConcurrentHashMap<String, URI>()
    private val paths = ConcurrentHashMap<URI, String>()
    private val executor = Executors.newVirtualThreadPerTaskExecutor()
    private val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .followRedirects(HttpClient.Redirect.NEVER)
        .build()

    val url = "${URLProtocol.HTTP.name}://127.0.0.1:${server.address.port}${register(source)}"

    init {
        server.executor = executor
        server.createContext(prefix, ::serve)
        server.start()
    }

    private fun register(uri: URI): String {
        require(uri.scheme == URLProtocol.HTTPS.name || uri.scheme == URLProtocol.HTTP.name)

        return paths.computeIfAbsent(uri) {
            val extension = when {
                it.path.endsWith(".m3u8", true) -> ".m3u8"
                it.path.endsWith(".mp4", true) -> ".mp4"
                else -> BLANK
            }

            val key = "${sequence.getAndIncrement()}$extension"
            resources[key] = it
            "$prefix$key"
        }
    }

    private fun serve(exchange: HttpExchange) {
        try {
            if (exchange.requestMethod != HttpMethod.Get.value && exchange.requestMethod != HttpMethod.Head.value) {
                exchange.sendResponseHeaders(HttpStatusCode.MethodNotAllowed.value, -1)
                return
            }

            var uri: URI = resources[exchange.requestURI.path.removePrefix(prefix)] ?: run {
                exchange.sendResponseHeaders(HttpStatusCode.NotFound.value, -1)
                return
            }

            repeat(6) {
                val request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(20))
                headers.forEach { (name, value) ->
                    if (
                        name.lowercase() !in excludedHeaders &&
                        (name.lowercase() !in credentialHeaders || sameOrigin(uri))
                    ) request.header(name, value)
                }

                request.header(HttpHeaders.AcceptEncoding, AcceptEncoding.Identity.acceptEncoding)
                exchange.requestHeaders.getFirst(HttpHeaders.Range)?.let {
                    request.header(HttpHeaders.Range, it)
                }

                if (exchange.requestMethod == HttpMethod.Head.value) {
                    request.method(HttpMethod.Head.value, HttpRequest.BodyPublishers.noBody())
                }

                val response = client.send(request.build(), HttpResponse.BodyHandlers.ofInputStream())
                response.body().use { input ->
                    val status = response.statusCode()
                    if (status in redirectStatuses) {
                        val location = response.headers()
                            .firstValue(HttpHeaders.Location)
                            .orElse(null)
                            ?: throw IllegalStateException()

                        val next = uri.resolve(location)
                        require(next.scheme == URLProtocol.HTTPS.name || next.scheme == URLProtocol.HTTP.name)
                        require(uri.scheme != URLProtocol.HTTPS.name || next.scheme == URLProtocol.HTTPS.name)
                        uri = next
                        return@repeat
                    }

                    val contentType = response.headers()
                        .firstValue(HttpHeaders.ContentType)
                        .orElse(ContentType.Application.OctetStream.toString())

                    if (exchange.requestMethod == HttpMethod.Head.value) {
                        copyHeaders(exchange, response)
                        exchange.sendResponseHeaders(status, -1)
                        return
                    }

                    val buffered = BufferedInputStream(input)
                    buffered.mark(16)
                    val beginning = buffered.readNBytes(10).toString(Charsets.UTF_8).removePrefix("\uFEFF")
                    buffered.reset()

                    if (status == HttpStatusCode.OK.value && beginning.startsWith("#EXTM3U")) {
                        val bytes = buffered.readNBytes(4 * 1024 * 1024 + 1)
                        require(bytes.size <= 4 * 1024 * 1024)

                        val base = uri
                        val playlist = bytes.toString(Charsets.UTF_8)
                            .removePrefix("\uFEFF")
                            .lineSequence()
                            .joinToString("\n") { line ->
                                when {
                                    line.isBlank() -> line
                                    line.startsWith('#') -> uriAttribute.replace(line) { "URI=\"${register(base.resolve(it.groupValues[1]))}\"" }
                                    else -> register(base.resolve(line.trim()))
                                }
                            }.toByteArray(Charsets.UTF_8)

                        exchange.responseHeaders.set(HttpHeaders.ContentType, "application/vnd.apple.mpegurl")
                        exchange.sendResponseHeaders(HttpStatusCode.OK.value, playlist.size.toLong())
                        exchange.responseBody.write(playlist)
                    } else {
                        copyHeaders(exchange, response)
                        exchange.responseHeaders.set(HttpHeaders.ContentType, contentType)

                        val length = response.headers()
                            .firstValueAsLong(HttpHeaders.ContentLength)
                            .orElse(-1)

                        val responseLength = if (length > 0) length else if (length == 0L) -1 else 0
                        exchange.sendResponseHeaders(status, responseLength)
                        if (length != 0L) buffered.copyTo(exchange.responseBody)
                    }

                    return
                }
            }
            exchange.sendResponseHeaders(HttpStatusCode.BadGateway.value, -1)
        } catch (_: Exception) {
            if (exchange.responseCode == -1) {
                runCatching { exchange.sendResponseHeaders(HttpStatusCode.BadGateway.value, -1) }
            }
        } finally {
            exchange.close()
        }
    }

    private fun sameOrigin(uri: URI) = uri.scheme.equals(source.scheme, true) &&
            uri.host.equals(source.host, true) &&
            effectivePort(uri) == effectivePort(source)

    private fun effectivePort(uri: URI) = if (uri.port >= 0) uri.port
    else if (uri.scheme == URLProtocol.HTTPS.name) URLProtocol.HTTPS.defaultPort
    else URLProtocol.HTTP.defaultPort

    private fun copyHeaders(exchange: HttpExchange, response: HttpResponse<*>) {
        for (name in listOf(HttpHeaders.ContentType, HttpHeaders.ContentLength, HttpHeaders.ContentRange, HttpHeaders.AcceptRanges)) {
            response.headers().firstValue(name).ifPresent { exchange.responseHeaders.set(name, it) }
        }
    }

    override fun close() {
        server.stop(0)
        executor.shutdownNow()
        client.shutdownNow()
    }

    companion object {
        private val uriAttribute = Regex("URI=\"([^\"]+)\"")
        private val excludedHeaders = listOf(HttpHeaders.Host, HttpHeaders.Connection, HttpHeaders.ContentLength, HttpHeaders.Range, HttpHeaders.AcceptEncoding, HttpHeaders.ProxyAuthorization)
            .mapTo(HashSet(), String::lowercase)
        private val credentialHeaders = listOf(HttpHeaders.Authorization, HttpHeaders.Cookie)
            .mapTo(HashSet(), String::lowercase)
        private val redirectStatuses = setOf(
            HttpStatusCode.MovedPermanently.value,
            HttpStatusCode.Found.value,
            HttpStatusCode.SeeOther.value,
            HttpStatusCode.TemporaryRedirect.value,
            HttpStatusCode.PermanentRedirect.value
        )
    }
}

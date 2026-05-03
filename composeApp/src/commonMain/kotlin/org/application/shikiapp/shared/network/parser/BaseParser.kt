package org.application.shikiapp.shared.network.parser

import io.ktor.client.HttpClient
import org.application.shikiapp.shared.models.ui.PlaylistResult
import org.application.shikiapp.shared.utils.BLANK
import kotlin.io.encoding.Base64

abstract class BaseParser(protected val client: HttpClient) {
    abstract val token: String
    abstract suspend fun searchById(id: String): Any
    abstract suspend fun getPlaylistLink(parseUrl: String, quality: Int = 720): PlaylistResult

    companion object {
        @JvmStatic
        protected fun cypher13(input: String): String = input.map { char ->
            when (char) {
                in 'a'..'z' -> ((char - 'a' + 13) % 26 + 'a'.code).toChar()
                in 'A'..'Z' -> ((char - 'A' + 13) % 26 + 'A'.code).toChar()
                else -> char
            }
        }.joinToString(BLANK)

        @JvmStatic
        protected fun decodeBase64(str: String): String {
            val padding = (4 - (str.length % 4)) % 4
            val padded = str + "=".repeat(padding)
            return try {
                Base64.decode(padded.encodeToByteArray()).decodeToString()
            } catch (_: Exception) {
                str
            }
        }
    }
}
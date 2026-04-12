package org.application.shikiapp.shared.network.parser

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.application.shikiapp.shared.models.ui.KodikPlaylistResult
import org.application.shikiapp.shared.utils.BLANK
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class KodikParser private constructor(
    private val token: String,
    private val client: HttpClient
) {
    private var cryptStep: Int? = null

    companion object {
        private const val TOKENS_URL_ENC = "uggcf://enj.tvguhohfrepbagrag.pbz/lnArflGbegvX/NavzrCnefref/ersf/urnqf/znva/xqx_gbxaf/gbxraf.wfba"
        private const val SEARCH_URL_ENC = "uggcf://xbqvx-ncv.pbz/frnepu"
        private const val PLAYER_URL_ENC = "uggcf://xbqvxcynlre.pbz"

        private val TOKENS_URL by lazy { cypher13(TOKENS_URL_ENC) }
        private val SEARCH_URL by lazy { cypher13(SEARCH_URL_ENC) }
        private val PLAYER_URL by lazy { cypher13(PLAYER_URL_ENC) }

        @Volatile
        private var INSTANCE: KodikParser? = null
        private val mutex = Mutex()

        private val json = Json { ignoreUnknownKeys = true }

        suspend fun getInstance(client: HttpClient): KodikParser {
            INSTANCE?.let { return it }

            return mutex.withLock {
                INSTANCE?.let { return@withLock it }

                val tokensText = client.get(TOKENS_URL).bodyAsText()
                val tokensData = json.decodeFromString<KodikTokensResponse>(tokensText)

                val allTokens = tokensData.stable + tokensData.unstable + tokensData.legacy

                var validParser: KodikParser? = null
                for (tokenItem in allTokens) {
                    val decryptedToken = decryptToken(tokenItem.tokn)
                    if (validateToken(decryptedToken, client)) {
                        validParser = KodikParser(decryptedToken, client)
                        break
                    }
                }

                if (validParser == null) {
                    throw NullPointerException()
                }

                INSTANCE = validParser
                validParser
            }
        }

        private fun cypher13(input: String) = input.map { char ->
            when (char) {
                in 'a'..'z' -> ((char - 'a' + 13) % 26 + 'a'.code).toChar()
                in 'A'..'Z' -> ((char - 'A' + 13) % 26 + 'A'.code).toChar()
                else -> char
            }
        }.joinToString(BLANK)

        private fun decryptToken(token: String): String {
            val half = token.length / 2
            val p1Reversed = token.substring(0, half).reversed()
            val p2Reversed = token.substring(half).reversed()

            val p1Decoded = Base64.decode(p1Reversed.encodeToByteArray()).decodeToString()
            val p2Decoded = Base64.decode(p2Reversed.encodeToByteArray()).decodeToString()

            return p2Decoded + p1Decoded
        }


        private suspend fun validateToken(testToken: String, client: HttpClient) = try {
            val response = client.submitForm(
                url = SEARCH_URL,
                formParameters = parameters {
                    append("token", testToken)
                    append("title", "Наруто")
                    append("limit", "1")
                }
            )
            val text = response.bodyAsText()
            val obj = json.parseToJsonElement(text).jsonObject

            obj["error"]?.jsonPrimitive?.content != "Отсутствует или неверный токен"
        } catch (_: Exception) {
            false
        }
    }

    suspend fun searchByShikimoriId(id: String): List<KodikResultItem> {
        val response = client.submitForm(
            url = SEARCH_URL,
            formParameters = parameters {
                append("token", token)
                append("shikimori_id", id)
                append("limit", "100")
                append("with_episodes_data", "true")
            }
        )

        val text = response.bodyAsText()
        val data = json.decodeFromString<KodikSearchResponse>(text)
        return data.results
    }

    suspend fun getPlaylistLink(episodeLink: String, quality: Int = 720): KodikPlaylistResult {
        val iframeUrl = if (episodeLink.startsWith("//")) "https:$episodeLink" else episodeLink
        val playerHtml = client.get(iframeUrl).bodyAsText()

        val urlParamsMatch = Regex("""urlParams\s*=\s*['"]?(\{.*?\})['"]?\s*;""", RegexOption.DOT_MATCHES_ALL)
            .find(playerHtml)?.groupValues?.get(1)

        val urlParamsJsonString = urlParamsMatch ?: run {
            val index = playerHtml.indexOf("urlParams")
            if (index == -1) throw NullPointerException()

            val substring = playerHtml.substring(index + 9)
            val startObj = substring.indexOf('{')
            val endObj = substring.indexOf(';')

            var jsonStr = substring.substring(startObj, endObj).trim()
            if (jsonStr.endsWith("'") || jsonStr.endsWith("\"")) {
                jsonStr = jsonStr.substring(0, jsonStr.length - 1)
            }

            jsonStr
        }

        val paramsObj = json.parseToJsonElement(urlParamsJsonString).jsonObject

        val videoType = Regex("""\.type\s*=\s*'([^']*)'""").find(playerHtml)?.groupValues?.get(1).orEmpty()
        val videoHash = Regex("""\.hash\s*=\s*'([^']*)'""").find(playerHtml)?.groupValues?.get(1).orEmpty()
        val videoId = Regex("""\.id\s*=\s*'([^']*)'""").find(playerHtml)?.groupValues?.get(1).orEmpty()

        val scriptUrl = Regex("""<script\s+src="(/assets/js/[^"]+\.js)"></script>""").find(playerHtml)?.groupValues?.get(1)
            ?: Regex("""src="(/assets/js/[^"]+\.js)"""").find(playerHtml)?.groupValues?.get(1)
            ?: throw NullPointerException()


        val postLink = getPostLink(scriptUrl)
        val videoLinksResponse = client.submitForm(
            url = "$PLAYER_URL$postLink",
            formParameters = parameters {
                append("hash", videoHash)
                append("id", videoId)
                append("type", videoType)
                append("d", paramsObj["d"]?.jsonPrimitive?.content.orEmpty())
                append("d_sign", paramsObj["d_sign"]?.jsonPrimitive?.content.orEmpty())
                append("pd", paramsObj["pd"]?.jsonPrimitive?.content.orEmpty())
                append("pd_sign", paramsObj["pd_sign"]?.jsonPrimitive?.content.orEmpty())
                append("ref", BLANK)
                append("ref_sign", paramsObj["ref_sign"]?.jsonPrimitive?.content.orEmpty())
                append("bad_user", "true")
                append("cdn_is_working", "true")
            }
        ).bodyAsText()

        val linksJson = json.parseToJsonElement(videoLinksResponse).jsonObject
        if (linksJson.containsKey("error")) {
            throw Exception(linksJson["error"]?.jsonPrimitive?.content.toString())
        }

        val linksMap = linksJson["links"]?.jsonObject ?: throw NullPointerException()

        val dataUrl = linksMap["360"]?.jsonArray?.get(0)?.jsonObject?.get("src")?.jsonPrimitive?.content
            ?: throw NullPointerException()

        val decryptedUrl = if (dataUrl.contains("mp4:hls:manifest")) dataUrl
        else decryptUrl(dataUrl)

        val qualityList = linksMap.keys.mapNotNull(String::toIntOrNull)
        val qualityMax = qualityList.maxOrNull() ?: quality
        val qualitySelected = if (quality in qualityList) quality else qualityMax

        val finalLink = decryptedUrl
            .replace("https:", BLANK)
            .substringBeforeLast("/") + "/$qualitySelected.mp4:hls:manifest.m3u8"

        return KodikPlaylistResult("https:$finalLink", qualityList)
    }

    private suspend fun getPostLink(scriptUrl: String): String {
        val jsCode = client.get("https://kodikplayer.com$scriptUrl").bodyAsText()
        val base64Url = Regex("""\$.ajax\(\{type:"POST",url:(?:atob\()?["']([^"']+)["']\)?""").find(jsCode)?.groupValues?.get(1)
            ?: throw NullPointerException()

        return Base64.decode(base64Url.encodeToByteArray()).decodeToString()
    }

    private fun decryptUrl(cryptedString: String): String {
        if (cryptStep != null) {
            val decrypted = attemptDecode(cryptedString, cryptStep!!)
            if (decrypted?.contains("mp4:hls:manifest") == true) return decrypted
        }

        for (rot in 0..25) {
            val decrypted = attemptDecode(cryptedString, rot)
            if (decrypted?.contains("mp4:hls:manifest") == true) {
                cryptStep = rot
                return decrypted
            }
        }

        throw Exception("Не удалось расшифровать ссылку на видео!")
    }

    private fun attemptDecode(str: String, shift: Int): String? {
        val rotated = str.map { char ->
            if (char.isLetter()) {
                val base = if (char.isUpperCase()) 'A' else 'a'
                ((char - base + shift) % 26 + base.code).toChar()
            } else {
                char
            }
        }.joinToString(BLANK)

        val padding = (4 - (rotated.length % 4)) % 4
        val padded = rotated + "=".repeat(padding)

        return try {
            Base64.decode(padded.encodeToByteArray()).decodeToString()
        } catch (_: Exception) {
            null
        }
    }
}
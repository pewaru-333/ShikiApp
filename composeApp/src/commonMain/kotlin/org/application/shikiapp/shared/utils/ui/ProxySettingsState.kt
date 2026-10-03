package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.*
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.data.preferences.rememberPreference

const val MAX_URL_COUNT = 5
const val MAX_YGGDRASIL_PEER_COUNT = 3

private val URL_REGEX = Regex(
    pattern = "^https://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$",
    option = RegexOption.IGNORE_CASE,
)

private val PROXY_HOST_REGEX = Regex(
    pattern = """^(https?|socks5)://(localhost|(?:(?:25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(?:25[0-5]|2[0-4]\d|[01]?\d\d?)|(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z]{2,})$""",
    option = RegexOption.IGNORE_CASE,
)

private val YGGDRASIL_PEER_REGEX = Regex(
    pattern = """^(tcp|tls|quic|ws|wss)://\S+$""",
    option = RegexOption.IGNORE_CASE,
)

@Stable
class ProxySettingsState(
    urlList: List<String>,
    isUserMode: Boolean,
    isProxy: Boolean,
    proxyHost: String,
    proxyPort: String,
    proxyUsername: String,
    proxyPassword: String,
    yggdrasilEnabled: Boolean,
    yggdrasilPeers: List<String>,
) {
    val urlList = mutableStateListOf<String>().apply {
        addAll(urlList.ifEmpty { listOf(BLANK) })
    }

    val peerList = mutableStateListOf<String>().apply {
        addAll(yggdrasilPeers.ifEmpty { listOf(BLANK) })
    }

    var isUserMode by mutableStateOf(isUserMode)
        private set

    var isProxyEnabled by mutableStateOf(isProxy)
        private set

    var proxyHost by mutableStateOf(proxyHost)
        private set

    var proxyPort by mutableStateOf(proxyPort)
        private set

    var proxyUser by mutableStateOf(proxyUsername)
        private set

    var proxyPass by mutableStateOf(proxyPassword)
        private set

    var isYggdrasilEnabled by mutableStateOf(yggdrasilEnabled)
        private set

    var anyError by mutableStateOf(false)
        private set

    val urlListItems: List<InputItem>
        get() = urlList.map { value ->
            InputItem(
                value = value,
                isError = anyError && isUserMode && !isValidBaseUrl(value)
            )
        }

    val peerListItems: List<InputItem>
        get() = peerList.map { value ->
            InputItem(
                value = value,
                isError = anyError && isYggdrasilEnabled && !isValidPeer(value)
            )
        }

    val isNetworkUrl: Boolean
        get() = !isYggdrasilEnabled

    val isHostError: Boolean
        get() = anyError && isProxyEnabled && !validateProxyHost()

    val isPortError: Boolean
        get() = anyError && isProxyEnabled && !validateProxyPort()

    fun toggleUserMode(enabled: Boolean) {
        if (!isNetworkUrl) {
            return
        }

        isUserMode = enabled
        anyError = false
    }

    fun toggleProxy(enabled: Boolean) {
        if (!isNetworkUrl) {
            return
        }

        isProxyEnabled = enabled
        anyError = false
    }

    fun toggleYggdrasil(enabled: Boolean) {
        isYggdrasilEnabled = enabled
        anyError = false
    }

    fun validateProxyHost() = proxyHost.matches(PROXY_HOST_REGEX)
    fun validateProxyPort() = proxyPort.toIntOrNull() in 1..65535

    fun updateUrl(index: Int, value: String) {
        urlList[index] = value.trim()
        anyError = false
    }

    fun updatePeer(index: Int, value: String) {
        peerList[index] = value.trim()
        anyError = false
    }

    fun updateHost(value: String) {
        proxyHost = value
            .trim()
            .lowercase()

        anyError = false
    }

    fun updatePort(value: String) {
        proxyPort = value
            .filter(Char::isDigit)
            .take(5)

        anyError = false
    }

    fun updateProxyUser(value: String) {
        proxyUser = value
    }

    fun updateProxyPassword(value: String) {
        proxyPass = value
    }

    fun addUrl() {
        if (urlList.size < MAX_URL_COUNT) {
            urlList.add(BLANK)
        }
    }

    fun removeLastUrl() {
        if (urlList.size > 1) {
            urlList.removeLast()
        }
    }

    fun addPeer() {
        if (peerList.size < MAX_YGGDRASIL_PEER_COUNT) {
            peerList.add(BLANK)
        }
    }

    fun removeLastPeer() {
        if (peerList.size > 1) {
            peerList.removeLast()
        }
    }

    fun clear(onCleared: () -> Unit) {
        isUserMode = false
        isProxyEnabled = false
        isYggdrasilEnabled = false
        anyError = false

        Preferences.clearUserNetworkSettings()

        onCleared()
    }

    fun save(onSaved: () -> Unit) {
        val hasLinkError = isNetworkUrl && isUserMode && urlList.any { !isValidBaseUrl(it) }
        val hasProxyError = isNetworkUrl && isProxyEnabled && (!validateProxyHost() || !validateProxyPort())
        val hasYggdrasilError = isYggdrasilEnabled && (peerList.isEmpty() || peerList.any { !isValidPeer(it) })

        anyError = hasLinkError || hasProxyError || hasYggdrasilError

        if (anyError) {
            return
        }

        val urls = urlList.mapNotNull {
            it.trim().takeIf(String::isNotEmpty)
        }

        val peers = peerList.mapNotNullTo(LinkedHashSet()) {
            it.trim().takeIf(String::isNotEmpty)
        }

        Preferences.setLinksSettings(isUserMode, urls)
        Preferences.setProxySettings(isProxyEnabled, proxyHost, proxyPort, proxyUser, proxyPass)
        Preferences.setYggdrasilSettings(isYggdrasilEnabled, peers.toList())

        onSaved()
    }

    private fun isValidBaseUrl(value: String) = value.isNotBlank() && URL_REGEX.matches(value)
    private fun isValidPeer(value: String) = value.isNotBlank() && YGGDRASIL_PEER_REGEX.matches(value)

    data class InputItem(
        val value: String,
        val isError: Boolean,
    )
}

@Composable
fun rememberProxySettingsState(): ProxySettingsState {
    val urls by rememberPreference { appUrlsString }
    val userMode by rememberPreference { useUserUrlList }

    val isProxy by rememberPreference { useProxy }
    val proxyHost by rememberPreference { proxyHost }
    val proxyPort by rememberPreference { proxyPort }
    val proxyUser by rememberPreference { proxyUsername }
    val proxyPass by rememberPreference { proxyPassword }

    val yggdrasilEnabled by rememberPreference { yggdrasilEnabled }
    val yggdrasilPeers by rememberPreference { yggdrasilPeers }

    return remember(urls, userMode, isProxy, proxyHost, proxyPort, proxyUser, proxyPass, yggdrasilEnabled, yggdrasilPeers) {
        ProxySettingsState(
            urlList = urls.split(',').ifEmpty { listOf(BLANK) },
            isUserMode = userMode,
            isProxy = isProxy,
            proxyHost = proxyHost,
            proxyPort = proxyPort,
            proxyUsername = proxyUser,
            proxyPassword = proxyPass,
            yggdrasilEnabled = Network.isYggdrasilAvailable && yggdrasilEnabled,
            yggdrasilPeers = yggdrasilPeers.split(',').ifEmpty { listOf(BLANK) },
        )
    }
}
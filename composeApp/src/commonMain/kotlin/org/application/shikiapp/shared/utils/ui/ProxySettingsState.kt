package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.*
import io.ktor.client.engine.ProxyType
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.data.preferences.rememberPreference

private val URL_REGEX = Regex("^https://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$", RegexOption.IGNORE_CASE)
private val PROXY_HOST_REGEX = Regex("""^(https?|socks5)://(localhost|(?:(?:25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(?:25[0-5]|2[0-4]\d|[01]?\d\d?)|(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z]{2,})$""", RegexOption.IGNORE_CASE)

@Stable
class ProxySettingsState(
    urlList: List<String>,
    isUserMode: Boolean,
    isProxy: Boolean,
    proxyHost: String,
    proxyPort: String,
    proxyUsername: String,
    proxyPassword: String
) {
    val urlList = mutableStateListOf<String>().apply {
        addAll(urlList)
    }

    val urlListItems: List<LinkItem>
        get() = urlList.map {
            LinkItem(
                url = it,
                isError = isUserMode && (it.isBlank() || !URL_REGEX.matches(it))
            )
        }

    var isUserMode by mutableStateOf(isUserMode)
    var isProxyEnabled by mutableStateOf(isProxy)
    var proxyHost by mutableStateOf(proxyHost)
    var proxyPort by mutableStateOf(proxyPort)
    var proxyUser by mutableStateOf(proxyUsername)
    var proxyPass by mutableStateOf(proxyPassword)
    var anyError by mutableStateOf(false)


    fun validateProxyHost(): Boolean = proxyHost.matches(PROXY_HOST_REGEX)
    fun validateProxyPort(): Boolean = proxyPort.toIntOrNull() in 1..65535

    val proxyType: ProxyType
        get() = when {
            proxyHost.startsWith("http") -> ProxyType.HTTP
            proxyHost.startsWith("socks5") -> ProxyType.SOCKS
            else -> ProxyType.UNKNOWN
        }

    val isHostError: Boolean
        get() = anyError && isProxyEnabled && !validateProxyHost()

    val isPortError: Boolean
        get() = anyError && isProxyEnabled && !validateProxyPort()

    fun updateUrl(index: Int, value: String) {
        urlList[index] = value.trim()

        anyError = false
    }

    fun addUrl() {
        if (urlList.size < 5) {
            urlList.add(BLANK)
        }
    }

    fun removeLastUrl() {
        urlList.removeLastOrNull()
    }

    fun updateHost(value: String) {
        proxyHost = value.trim().lowercase()

        anyError = false
    }

    fun updatePort(value: String) {
        proxyPort = value.filter(Char::isDigit).take(5)

        anyError = false
    }

    fun clear(onCleared: () -> Unit) {
        isUserMode = false
        isProxyEnabled = false

        Preferences.clearUserNetworkSettings()

        onCleared()
    }

    fun save(onSaved: () -> Unit) {
        val hasLinkError = isUserMode && urlListItems.any { it.isError }
        val hasProxyError = isProxyEnabled && (!validateProxyHost() || !validateProxyPort())

        anyError = hasLinkError || hasProxyError
        if (anyError) return

        Preferences.setLinksSettings(isUserMode, urlList)
        Preferences.setProxySettings(isProxyEnabled, proxyHost, proxyPort, proxyUser, proxyPass)

        onSaved()
    }

    data class LinkItem(
        val url: String,
        val isError: Boolean
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

    return remember(urls, userMode, isProxy, proxyHost, proxyPort, proxyUser, proxyPass) {
        ProxySettingsState(
            urlList = urls.split(','),
            isUserMode = userMode,
            isProxy = isProxy,
            proxyHost = proxyHost,
            proxyPort = proxyPort,
            proxyUsername = proxyUser,
            proxyPassword = proxyPass,
        )
    }
}
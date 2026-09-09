package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.ktor.client.engine.ProxyType
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.utils.BLANK

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

        Preferences.setUseUserUrlList(false)
        Preferences.setAppUrlList()

        Preferences.setUseProxy(false)
        Preferences.setProxyHost(BLANK)
        Preferences.setProxyPort(BLANK)
        Preferences.setProxyUsername(BLANK)
        Preferences.setProxyPassword(BLANK)

        onCleared()
    }

    fun save(onSaved: () -> Unit) {
        val hasLinkError = isUserMode && urlListItems.any { it.isError }
        val hasProxyError = isProxyEnabled && (!validateProxyHost() || !validateProxyPort())

        anyError = hasLinkError || hasProxyError
        if (anyError) return

        if (isUserMode) {
            Preferences.setUseUserUrlList(true)
            Preferences.setAppUrlList(urlList.joinToString(","))
        } else {
            Preferences.setUseUserUrlList(false)
        }

        if (isProxyEnabled) {
            Preferences.setUseProxy(true)
            Preferences.setProxyHost(proxyHost)
            Preferences.setProxyPort(proxyPort)
            Preferences.setProxyUsername(proxyUser)
            Preferences.setProxyPassword(proxyPass)
        } else {
            Preferences.setUseProxy(false)
        }

        onSaved()
    }

    data class LinkItem(
        val url: String,
        val isError: Boolean
    )
}

@Composable
fun rememberProxySettingsState(): ProxySettingsState {
    val urls by Preferences.appUrlListFlow.collectAsStateWithLifecycle()
    val userMode by Preferences.useUserUrlListFlow.collectAsStateWithLifecycle()

    val isProxy by Preferences.useProxyFlow.collectAsStateWithLifecycle()
    val proxyHost by Preferences.proxyHostFlow.collectAsStateWithLifecycle()
    val proxyPort by Preferences.proxyPortFlow.collectAsStateWithLifecycle()
    val proxyUser by Preferences.proxyUsernameFlow.collectAsStateWithLifecycle()
    val proxyPass by Preferences.proxyPasswordFlow.collectAsStateWithLifecycle()

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
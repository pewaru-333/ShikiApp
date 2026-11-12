package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.*
import org.application.shikiapp.shared.di.AppServices
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.data.preferences.rememberPreference

const val MAX_URL_COUNT = 5

private val URL_REGEX = Regex(
    pattern = "^https://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$",
    option = RegexOption.IGNORE_CASE,
)

private val PROXY_HOST_REGEX = Regex(
    pattern = """^(https?|socks5)://(localhost|(?:(?:25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(?:25[0-5]|2[0-4]\d|[01]?\d\d?)|(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z]{2,})$""",
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
    val extraSettings: NetworkSettings? = null,
) {
    val urlList = mutableStateListOf<String>().apply {
        addAll(urlList.ifEmpty { listOf(BLANK) })
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

    var anyError by mutableStateOf(false)
        private set

    fun isUrlError(value: String) = anyError && isUserMode && !isValidBaseUrl(value)

    val isNetworkUrl: Boolean
        get() = extraSettings?.usesOwnConnection != true

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

    private fun validateProxyHost() = proxyHost.matches(PROXY_HOST_REGEX)
    private fun validateProxyPort() = proxyPort.toIntOrNull() in 1..65535

    fun updateUrl(index: Int, value: String) {
        urlList[index] = value.trim()
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

    fun clear(onCleared: () -> Unit) {
        isUserMode = false
        isProxyEnabled = false
        anyError = false

        Preferences.clearUserNetworkSettings()
        extraSettings?.clear()

        onCleared()
    }

    fun save(onSaved: () -> Unit) {
        val hasLinkError = isNetworkUrl && isUserMode && urlList.any { !isValidBaseUrl(it) }
        val hasProxyError = isNetworkUrl && isProxyEnabled && (!validateProxyHost() || !validateProxyPort())

        anyError = hasLinkError || hasProxyError

        if (anyError || extraSettings?.save() == false) {
            return
        }

        val urls = urlList.mapNotNull {
            it.trim().takeIf(String::isNotEmpty)
        }

        Preferences.setLinksSettings(isUserMode, urls)
        Preferences.setProxySettings(isProxyEnabled, proxyHost, proxyPort, proxyUser, proxyPass)

        onSaved()
    }

    private fun isValidBaseUrl(value: String) = value.isNotBlank() && URL_REGEX.matches(value)

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

    val factory = AppServices.networkSettings
    val extraSettings = remember(factory) { factory?.invoke() }

    return remember(urls, userMode, isProxy, proxyHost, proxyPort, proxyUser, proxyPass, extraSettings) {
        ProxySettingsState(
            urlList = urls.split(',').ifEmpty { listOf(BLANK) },
            isUserMode = userMode,
            isProxy = isProxy,
            proxyHost = proxyHost,
            proxyPort = proxyPort,
            proxyUsername = proxyUser,
            proxyPassword = proxyPass,
            extraSettings = extraSettings,
        )
    }
}
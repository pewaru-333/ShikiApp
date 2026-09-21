package org.application.shikiapp.shared.utils.data.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.application.shikiapp.shared.di.AppConfig
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.models.data.Token
import org.application.shikiapp.shared.utils.*
import org.application.shikiapp.shared.utils.enums.*
import org.application.shikiapp.shared.utils.extensions.edit
import org.application.shikiapp.shared.utils.extensions.getEnum
import org.application.shikiapp.shared.utils.extensions.getEnumFlow
import org.application.shikiapp.shared.utils.extensions.getFlow

class Preferences(private val app: IPreferences) {
    val startPage = PreferenceEnum(PREF_START_PAGE, Menu.NEWS)
    val listView = PreferenceEnum(PREF_CATALOG_LIST_VIEW, ListView.COLUMN)
    val lastCatalogOrder = PreferenceEnum(PREF_LAST_SORTING_ORDER_CATALOG, Order.RANKED)
    val lastListOrder = PreferenceEnum(PREF_LAST_SORTING_ORDER_RATES, OrderRates.TITLE)
    val lastListOrderDirection = PreferenceEnum(PREF_LAST_SORTING_ORDER_DIRECTION_RATES, OrderDirection.ASCENDING)
    val rememberCatalogOrder = PreferenceBoolean(PREF_REMEMBER_CATALOG_LAST_ORDER, false)
    val rememberRatesOrder = PreferenceBoolean(PREF_REMEMBER_RATES_LAST_ORDER, false)
    val episodeAutoAdd = PreferenceBoolean(PREF_EPISODE_AUTO_ADD, false)

    val theme = PreferenceEnum(PREF_APP_THEME, Theme.SYSTEM)
    val dynamicColors = PreferenceBoolean(PREF_DYNAMIC_COLORS, false)
    val colorPalette = PreferenceEnum(PREF_COLOR_PALETTE, Palette.SAKURA)

    val showUserRateListSize = PreferenceBoolean(PREF_SHOW_USER_RATES_LIST_TAB_SIZE, false)
    val userRatesStartType = PreferenceEnum(PREF_USER_RATES_START_TYPE, LinkedType.ANIME)
    val userRatesStartWatchStatus = PreferenceEnum(PREF_USER_RATES_START_WATCH_STATUS, WatchStatus.PLANNED)

    val language = PreferenceString(PREF_APP_LANGUAGE, "ru")
    val cache = PreferenceInt(PREF_APP_CACHE, 16)
    val canWatch = PreferenceBoolean(PREF_HAS_AGREED_TO_WATCH, false)
    val userId = PreferenceLong(USER_ID, 0L)

    val useUserUrlList = PreferenceBoolean(PREF_USE_USER_URL_LIST, false)
    val appUrlsString = PreferenceString(PREF_APP_URL_LIST, getDefaultUrls())

    val useProxy = PreferenceBoolean(PREF_USE_PROXY, false)
    val proxyHost = PreferenceString(PREF_PROXY_HOST, BLANK)
    val proxyPort = PreferenceString(PREF_PROXY_PORT, BLANK)
    val proxyUsername = PreferenceString(PREF_PROXY_USERNAME, BLANK)
    val proxyPassword = PreferenceString(PREF_PROXY_PASSWORD, BLANK)


    val token: Token?
        get() {
            val accessToken = app.getString(ACCESS_TOKEN, BLANK)
            val refreshToken = app.getString(REFRESH_TOKEN, BLANK)

            if (accessToken.isBlank() || refreshToken.isBlank())
                return null

            return Token(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }

    val libToken: Token?
        get() {
            val accessToken = app.getString(ACCESS_TOKEN_LIB, BLANK)
            val refreshToken = app.getString(REFRESH_TOKEN_LIB, BLANK)

            if (accessToken.isBlank() || refreshToken.isBlank())
                return null

            return Token(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }

    val libTokenFlow: Flow<Token?>
        get() = combine(app.getFlow(ACCESS_TOKEN_LIB, BLANK), app.getFlow(REFRESH_TOKEN_LIB, BLANK)) { accessToken, refreshToken ->
            if (accessToken.isBlank() || refreshToken.isBlank()) {
                null
            } else {
                Token(accessToken, refreshToken)
            }
        }

    val appUrlList: List<String>
        get() = appUrlsString.value.split(',')

    val appUrlPair: Pair<String, List<String>>
        get() {
            val list = appUrlList
            return list[0] to list.drop(1)
        }

    fun saveToken(token: Token) = app.edit {
        putString(ACCESS_TOKEN, token.accessToken)
        putString(REFRESH_TOKEN, token.refreshToken)
        putLong(EXPIRES_IN, token.expiresIn)
        putLong(CREATED_AT, token.createdAt)
    }

    fun saveTokenLib(accessToken: String, refreshToken: String) = app.edit {
        putString(ACCESS_TOKEN_LIB, accessToken)
        putString(REFRESH_TOKEN_LIB, refreshToken)
    }

    fun toggleRememberLastCatalogOrder(value: Boolean) {
        rememberCatalogOrder.value = value

        if (!value) {
            lastCatalogOrder.value = Order.RANKED
        }
    }

    fun toggleRememberLastRatesOrder(value: Boolean) {
        rememberRatesOrder.value = value

        if (!value) {
            lastListOrder.value = OrderRates.TITLE
            lastListOrderDirection.value = OrderDirection.ASCENDING
        }
    }

    fun clearUserNetworkSettings() {
        useUserUrlList.value = false
        setAppUrlList()

        useProxy.value = false
        proxyHost.value = BLANK
        proxyPort.value = BLANK
        proxyUsername.value = BLANK
        proxyPassword.value = BLANK
    }

    fun setLinksSettings(isUserMode: Boolean, urlList: List<String>) {
        useUserUrlList.value = isUserMode

        if (isUserMode) {
            setAppUrlList(urlList.joinToString(","))
        }
    }

    fun setProxySettings(checked: Boolean, host: String, port: String, user: String, pass: String) {
        useProxy.value = checked

        if (checked) {
            proxyHost.value = host
            proxyPort.value = port
            proxyUsername.value = user
            proxyPassword.value = pass
        }
    }

    private fun setAppUrlList(urls: String = BLANK) {
        appUrlsString.value = urls.ifBlank(::getDefaultUrls)
    }

    private fun getDefaultUrls(): String {
        val stringBuilder = StringBuilder(AppConfig.baseUrl)
        for (url in AppConfig.urlMirrors) {
            stringBuilder.append(',')
            stringBuilder.append(url)
        }
        return stringBuilder.toString()
    }

    inner class PreferenceBoolean(key: String, defaultValue: Boolean) : PreferenceSetting<Boolean>(
        key = key,
        defaultValue = defaultValue,
        getter = IPreferences::getBoolean,
        setter = IPreferences::putBoolean,
        flowGetter = IPreferences::getFlow
    )

    inner class PreferenceInt(key: String, defaultValue: Int) : PreferenceSetting<Int>(
        key = key,
        defaultValue = defaultValue,
        getter = IPreferences::getInt,
        setter = IPreferences::putInt,
        flowGetter = IPreferences::getFlow
    )

    inner class PreferenceLong(key: String, defaultValue: Long) : PreferenceSetting<Long>(
        key = key,
        defaultValue = defaultValue,
        getter = IPreferences::getLong,
        setter = IPreferences::putLong,
        flowGetter = IPreferences::getFlow
    )

    inner class PreferenceString(key: String, defaultValue: String) : PreferenceSetting<String>(
        key = key,
        defaultValue = defaultValue,
        getter = IPreferences::getString,
        setter = IPreferences::putString,
        flowGetter = IPreferences::getFlow
    )

    @Suppress("FunctionName")
    private inline fun <reified E : Enum<E>> PreferenceEnum(key: String, def: E) =
        PreferenceSetting(
            key = key,
            defaultValue = def,
            getter = IPreferences::getEnum,
            setter = IPreferences::putEnum,
            flowGetter = IPreferences::getEnumFlow
        )

    open inner class PreferenceSetting<T>(
        val key: String,
        private val defaultValue: T,
        private val getter: IPreferences.(String, T) -> T,
        private val setter: IPreferences.(String, T) -> Unit,
        private val flowGetter: IPreferences.(String, T) -> Flow<T>
    ) {
        var value: T
            get() = app.getter(key, defaultValue)
            set(newValue) = app.edit { setter(key, newValue) }

        val flow: Flow<T> by lazy { app.flowGetter(key, defaultValue) }
    }
}

@Composable
fun <T> rememberPreference(selector: Preferences.() -> Preferences.PreferenceSetting<T>): State<T> {
    val preference = remember { Preferences.selector() }
    return preference.flow.collectAsStateWithLifecycle(preference.value)
}

@Composable
fun <T> rememberPreference(initialValue: T, selector: Preferences.() -> Flow<T>): State<T> {
    val flow = remember { Preferences.selector() }
    return flow.collectAsStateWithLifecycle(initialValue)
}
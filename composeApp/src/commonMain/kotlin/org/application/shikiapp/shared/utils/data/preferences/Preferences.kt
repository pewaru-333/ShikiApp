package org.application.shikiapp.shared.utils.data.preferences

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import me.zhanghai.compose.preference.Preferences
import org.application.shikiapp.shared.di.AppConfig
import org.application.shikiapp.shared.models.data.Token
import org.application.shikiapp.shared.utils.*
import org.application.shikiapp.shared.utils.enums.*
import org.application.shikiapp.shared.utils.extensions.*

class Preferences(private val app: IPreferences, private val auth: IPreferences, scope: CoroutineScope) {
    val startPage: Menu
        get() = app.getEnum(PREF_START_PAGE, Menu.NEWS)

    val startPageFlow = app.getEnumStateFlow(PREF_START_PAGE, Menu.NEWS, scope)

    val listView: ListView
        get() = app.getEnum(PREF_CATALOG_LIST_VIEW, ListView.COLUMN)

    val listViewFlow = app.getEnumStateFlow(PREF_CATALOG_LIST_VIEW, ListView.COLUMN, scope)

    val lastCatalogOrder: Order
        get() = app.getEnum(PREF_LAST_SORTING_ORDER, Order.RANKED)

    val rememberCatalogOrder: Boolean
        get() = app.getBoolean(PREF_REMEMBER_CATALOG_LAST_ORDER, false)

    val rememberCatalogOrderFlow = app.getStateFlow(PREF_REMEMBER_CATALOG_LAST_ORDER, false, scope)

    val episodeAutoAdd: Boolean
        get() = app.getBoolean(PREF_EPISODE_AUTO_ADD, false)

    val episodeAutoAddFlow = app.getStateFlow(PREF_EPISODE_AUTO_ADD, false, scope)

    val theme = app.getEnumStateFlow(PREF_APP_THEME, Theme.SYSTEM, scope)

    val dynamicColors = app.getStateFlow(PREF_DYNAMIC_COLORS, false, scope)

    val colorPaletteFlow = app.getEnumStateFlow(PREF_COLOR_PALETTE, Palette.SAKURA, scope)

    val showUserRateListSize: Boolean
        get() = app.getBoolean(PREF_SHOW_USER_RATES_LIST_TAB_SIZE, false)

    val userRatesStartType: LinkedType
        get() = app.getEnum(PREF_USER_RATES_START_TYPE, LinkedType.ANIME)

    val userRatesStartWatchStatus: WatchStatus
        get() = app.getEnum(PREF_USER_RATES_START_WATCH_STATUS, WatchStatus.PLANNED)

    val showUserRateListSizeFlow = app.getStateFlow(PREF_SHOW_USER_RATES_LIST_TAB_SIZE, false, scope)

    val userRatesStartTypeFlow =
        app.getEnumStateFlow(PREF_USER_RATES_START_TYPE, LinkedType.ANIME, scope)

    val userRatesStartWatchStatusFlow =
        app.getEnumStateFlow(PREF_USER_RATES_START_WATCH_STATUS, WatchStatus.PLANNED, scope)

    val language: String
        get() = app.getString(PREF_APP_LANGUAGE, "ru")

    val languageFlow = app.getStateFlow(PREF_APP_LANGUAGE, "ru", scope)

    val cache: Int
        get() = app.getInt(PREF_APP_CACHE, 16)

    val cacheFlow = app.getStateFlow(PREF_APP_CACHE, 16, scope)

    val userId: Long
        get() = auth.getLong(USER_ID, 0L)

    val canWatch: Boolean
        get() = app.getBoolean(PREF_HAS_AGREED_TO_WATCH, false)

    val canWatchFlow = app.getStateFlow(PREF_HAS_AGREED_TO_WATCH, false, scope)

    val token: Token?
        get() {
            val accessToken = auth.getString(ACCESS_TOKEN, BLANK)
            val refreshToken = auth.getString(REFRESH_TOKEN, BLANK)

            if (accessToken.isBlank() || refreshToken.isBlank())
                return null

            return Token(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }

    val libToken: Token?
        get() {
            val accessToken = auth.getString(ACCESS_TOKEN_LIB, BLANK)
            val refreshToken = auth.getString(REFRESH_TOKEN_LIB, BLANK)

            if (accessToken.isBlank() || refreshToken.isBlank())
                return null

            return Token(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }

    val libTokenFlow: Flow<Token?>
        get() = combine(auth.getFlow(ACCESS_TOKEN_LIB, BLANK), auth.getFlow(REFRESH_TOKEN_LIB, BLANK)) { accessToken, refreshToken ->
            if (accessToken.isBlank() || refreshToken.isBlank()) {
                null
            } else {
                Token(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            }
        }

    val useUserUrlList: Boolean
        get() = app.getBoolean(PREF_USE_USER_URL_LIST, false)

    val useUserUrlListFlow = app.getStateFlow(PREF_USE_USER_URL_LIST, false, scope)

    val appUrlsString: String
        get() = app.getString(PREF_APP_URL_LIST, getDefaultUrls())

    val appUrlList: List<String>
        get() = appUrlsString.split(',')

    val appUrlPair: Pair<String, List<String>>
        get() {
            val list = appUrlList

            return list[0] to list.drop(1)
        }

    val appUrlListFlow = app.getStateFlow(PREF_APP_URL_LIST, appUrlsString, scope)

    val useProxy: Boolean
        get() = app.getBoolean(PREF_USE_PROXY, false)

    val useProxyFlow = app.getStateFlow(PREF_USE_PROXY, false, scope)

    val proxyHost: String
        get() = app.getString(PREF_PROXY_HOST, BLANK)

    val proxyHostFlow = app.getStateFlow(PREF_PROXY_HOST, BLANK, scope)

    val proxyPort: String
        get() = app.getString(PREF_PROXY_PORT, BLANK)

    val proxyPortFlow = app.getStateFlow(PREF_PROXY_PORT, BLANK, scope)

    val proxyUsername: String
        get() = app.getString(PREF_PROXY_USERNAME, BLANK)

    val proxyUsernameFlow = app.getStateFlow(PREF_PROXY_USERNAME, BLANK, scope)

    val proxyPassword: String
        get() = app.getString(PREF_PROXY_PASSWORD, BLANK)

    val proxyPasswordFlow = app.getStateFlow(PREF_PROXY_PASSWORD, BLANK, scope)

    fun setStartPage(page: Menu) = app.edit {
        putEnum(PREF_START_PAGE, page)
    }

    fun setListView(view: ListView) = app.edit {
        putEnum(PREF_CATALOG_LIST_VIEW, view)
    }

    fun setAutoIncrementEpisode(flag: Boolean) = app.edit {
        putBoolean(PREF_EPISODE_AUTO_ADD, flag)
    }

    fun setTheme(theme: Theme) = app.edit {
        putEnum(PREF_APP_THEME, theme)
    }

    fun setDynamicColors(enabled: Boolean) = app.edit {
        putBoolean(PREF_DYNAMIC_COLORS, enabled)
    }

    fun setPalette(palette: Palette) = app.edit {
        putEnum(PREF_COLOR_PALETTE, palette)
    }

    fun setLastCatalogOrder(order: Order = Order.RANKED) = app.edit {
        putEnum(PREF_LAST_SORTING_ORDER, order)
    }

    fun setUserRatesStartType(type: LinkedType) = app.edit {
        putEnum(PREF_USER_RATES_START_TYPE, type)
    }

    fun setUserRatesStartWatchStatus(status: WatchStatus) = app.edit {
        putEnum(PREF_USER_RATES_START_WATCH_STATUS, status)
    }

    fun setShowUserRatesListSize(show: Boolean) = app.edit {
        putBoolean(PREF_SHOW_USER_RATES_LIST_TAB_SIZE, show)
    }

    fun setLanguage(locale: String) = app.edit {
        putString(PREF_APP_LANGUAGE, locale)
    }

    fun setCache(size: Int) = app.edit {
        putInt(PREF_APP_CACHE, size)
    }

    fun saveToken(token: Token) = auth.edit {
        putString(ACCESS_TOKEN, token.accessToken)
        putString(REFRESH_TOKEN, token.refreshToken)
        putLong(EXPIRES_IN, token.expiresIn)
        putLong(CREATED_AT, token.createdAt)
    }

    fun saveTokenLib(accessToken: String, refreshToken: String) = auth.edit {
        putString(ACCESS_TOKEN_LIB, accessToken)
        putString(REFRESH_TOKEN_LIB, refreshToken)
    }

    fun setUserId(userId: Long) = auth.edit {
        putLong(USER_ID, userId)
    }

    fun setCanWatch() = app.edit {
        putBoolean(PREF_HAS_AGREED_TO_WATCH, true)
    }

    fun toggleRememberLastCatalogOrder(value: Boolean) = app.edit {
        putBoolean(PREF_REMEMBER_CATALOG_LAST_ORDER, value)

        if (!value) {
            setLastCatalogOrder()
        }
    }

    fun setAppUrlList(urls: String = BLANK) = app.edit {
        putString(PREF_APP_URL_LIST, urls.ifBlank(::getDefaultUrls))
    }

    fun setUseUserUrlList(enabled: Boolean) = app.edit {
        putBoolean(PREF_USE_USER_URL_LIST, enabled)
    }

    fun setUseProxy(enabled: Boolean) = app.edit {
        putBoolean(PREF_USE_PROXY, enabled)
    }

    fun setProxyHost(host: String) = app.edit {
        putString(PREF_PROXY_HOST, host)
    }

    fun setProxyPort(port: String) = app.edit {
        putString(PREF_PROXY_PORT, port)
    }

    fun setProxyUsername(username: String) = app.edit {
        putString(PREF_PROXY_USERNAME, username)
    }

    fun setProxyPassword(password: String) = app.edit {
        putString(PREF_PROXY_PASSWORD, password)
    }

    private fun getDefaultUrls(): String {
        val stringBuilder = StringBuilder(AppConfig.baseUrl)
        for (url in AppConfig.urlMirrors) {
            stringBuilder.append(',')
            stringBuilder.append(url)
        }

        return stringBuilder.toString()
    }
}

@Composable
expect fun rememberAppPreferences(): MutableStateFlow<Preferences>
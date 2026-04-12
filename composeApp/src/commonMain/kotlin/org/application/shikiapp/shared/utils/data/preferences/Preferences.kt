package org.application.shikiapp.shared.utils.data.preferences

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import me.zhanghai.compose.preference.Preferences
import org.application.shikiapp.shared.models.data.Token
import org.application.shikiapp.shared.utils.ACCESS_TOKEN
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.CREATED_AT
import org.application.shikiapp.shared.utils.EXPIRES_IN
import org.application.shikiapp.shared.utils.PREF_APP_CACHE
import org.application.shikiapp.shared.utils.PREF_APP_LANGUAGE
import org.application.shikiapp.shared.utils.PREF_APP_THEME
import org.application.shikiapp.shared.utils.PREF_CATALOG_LIST_VIEW
import org.application.shikiapp.shared.utils.PREF_DYNAMIC_COLORS
import org.application.shikiapp.shared.utils.PREF_HAS_AGREED_TO_WATCH
import org.application.shikiapp.shared.utils.PREF_START_PAGE
import org.application.shikiapp.shared.utils.REFRESH_TOKEN
import org.application.shikiapp.shared.utils.USER_ID
import org.application.shikiapp.shared.utils.enums.ListView
import org.application.shikiapp.shared.utils.enums.Menu
import org.application.shikiapp.shared.utils.enums.Theme
import org.application.shikiapp.shared.utils.extensions.edit
import org.application.shikiapp.shared.utils.extensions.getEnum
import org.application.shikiapp.shared.utils.extensions.getEnumFlow
import org.application.shikiapp.shared.utils.extensions.getFlow

class Preferences(private val app: IPreferences, private val auth: IPreferences) {
    val startPage: Menu
        get() = app.getEnum(PREF_START_PAGE, Menu.NEWS)

    val startPageFlow: Flow<Menu>
        get() = app.getEnumFlow(PREF_START_PAGE, Menu.NEWS)

    val listView: ListView
        get() = app.getEnum(PREF_CATALOG_LIST_VIEW, ListView.COLUMN)

    val listViewFlow: Flow<ListView>
        get() = app.getEnumFlow(PREF_CATALOG_LIST_VIEW, ListView.COLUMN)

    val cache: Int
        get() = app.getInt(PREF_APP_CACHE, 16)

    val cacheFlow: Flow<Int>
        get() = app.getFlow(PREF_APP_CACHE, 16)

    val theme: Flow<Theme>
        get() = app.getEnumFlow(PREF_APP_THEME, Theme.SYSTEM)

    val language: String
        get() = app.getString(PREF_APP_LANGUAGE, "ru")

    val languageFlow: Flow<String>
        get() = app.getFlow(PREF_APP_LANGUAGE, "ru")

    val dynamicColors: Flow<Boolean>
        get() = app.getFlow(PREF_DYNAMIC_COLORS, false)

    val userId: Long
        get() = auth.getLong(USER_ID, 0L)

    val canWatch: Boolean
        get() = app.getBoolean(PREF_HAS_AGREED_TO_WATCH, false)

    val canWatchFlow: Flow<Boolean>
        get() = app.getFlow(PREF_HAS_AGREED_TO_WATCH, false)

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

    fun saveToken(token: Token) = auth.edit {
        putString(ACCESS_TOKEN, token.accessToken)
        putString(REFRESH_TOKEN, token.refreshToken)
        putLong(EXPIRES_IN, token.expiresIn)
        putLong(CREATED_AT, token.createdAt)
    }

    fun setUserId(userId: Long) = auth.edit {
        putLong(USER_ID, userId)
    }

    fun setStartPage(page: Menu) = app.edit {
        putEnum(PREF_START_PAGE, page)
    }

    fun setListView(view: ListView) = app.edit {
        putEnum(PREF_CATALOG_LIST_VIEW, view)
    }

    fun setTheme(theme: Theme) = app.edit {
        putEnum(PREF_APP_THEME, theme)
    }

    fun setCache(size: Int) = app.edit { putInt(PREF_APP_CACHE, size) }

    fun setLanguage(locale: String) = app.edit { putString(PREF_APP_LANGUAGE, locale) }

    fun setCanWatch() = app.edit { putBoolean(PREF_HAS_AGREED_TO_WATCH, true) }
}

@Composable
expect fun rememberAppPreferences(): MutableStateFlow<Preferences>
package org.application.shikiapp.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.LocaleList
import androidx.core.content.edit
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.Token
import org.application.shikiapp.utils.enums.ListView
import org.application.shikiapp.utils.enums.Theme
import org.application.shikiapp.utils.extensions.getActivity
import org.application.shikiapp.utils.extensions.getEnum
import org.application.shikiapp.utils.extensions.getEnumFlow
import org.application.shikiapp.utils.extensions.getFlow
import org.application.shikiapp.utils.extensions.getSelectedLanguage
import org.application.shikiapp.utils.extensions.putEnum
import java.util.Locale

class Preferences(context: Context) {
    private val auth = context.getSharedPreferences("auth_${context.packageName}", MODE_PRIVATE)
    val app = context.getSharedPreferences("preferences_${context.packageName}", MODE_PRIVATE)

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

    val dynamicColors: Flow<Boolean>
        get() = app.getFlow(PREF_DYNAMIC_COLORS, false)

    val userId: Long
        get() = auth.getLong(USER_ID, 0L)

    val token: Token?
        get() {
            val accessToken = auth.getString(ACCESS_TOKEN, BLANK)
            val refreshToken = auth.getString(REFRESH_TOKEN, BLANK)

            if (accessToken == null || refreshToken == null)
                return null

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

    fun setListView(view: ListView) = app.edit {
        putEnum(PREF_CATALOG_LIST_VIEW, view)
    }

    fun setTheme(theme: Theme) = app.edit {
        putEnum(PREF_APP_THEME, theme)
    }

    fun setDynamicColors(flag: Boolean) = app.edit {
        putBoolean(PREF_DYNAMIC_COLORS, flag)
    }

    fun setCache(cache: Int) = app.edit {
        putInt(PREF_APP_CACHE, cache)
    }

    fun getLanguage(context: Context) = app.getString(PREF_APP_LANGUAGE, context.getSelectedLanguage()) ?: Locale.ENGLISH.language

    fun changeLanguage(context: Context) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) context
        else {
            val newLocale = Locale.forLanguageTag(getLanguage(context))
            Locale.setDefault(newLocale)

            val configuration = context.resources?.configuration
            configuration?.setLocales(LocaleList(newLocale))

            configuration?.let { context.createConfigurationContext(it) } ?: context
        }

    fun setLocale(context: Context, locale: String) {
        app.edit { putString(PREF_APP_LANGUAGE, locale) }
        changeLanguage(context)
        context.getActivity()?.recreate()
    }

    companion object {
        @Volatile
        private var INSTANCE: Preferences? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) { Preferences(context) }.also { INSTANCE = it }
    }
}
package org.application.shikiapp.utils

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.os.LocaleList
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.application.shikiapp.models.data.Token
import org.application.shikiapp.utils.enums.ListView
import org.application.shikiapp.utils.enums.Theme
import org.application.shikiapp.utils.extensions.getColorsFlow
import org.application.shikiapp.utils.extensions.getEnum
import org.application.shikiapp.utils.extensions.getSelectedLanguage
import org.application.shikiapp.utils.extensions.getThemeFlow
import org.application.shikiapp.utils.extensions.putEnum
import java.util.Locale

object Preferences : ViewModel() {
    private lateinit var auth: SharedPreferences
    lateinit var app: SharedPreferences

    lateinit var theme: StateFlow<Theme>
    lateinit var dynamicColors: StateFlow<Boolean>

    fun getInstance(context: Context) {
        app = context.getSharedPreferences("preferences_${context.packageName}", MODE_PRIVATE)
        auth = context.getSharedPreferences("auth_${context.packageName}", MODE_PRIVATE)
        theme = app.getThemeFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Theme.SYSTEM)
        dynamicColors = app.getColorsFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
    }

    var listView: ListView
        get() = app.getEnum(PREF_CATALOG_LIST_VIEW, ListView.COLUMN)
        set(value) = app.edit { putEnum(PREF_CATALOG_LIST_VIEW, value) }

    val cache: Int
        get() = app.getInt(PREF_APP_CACHE, 16)

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

    fun setTheme(theme: Theme) = app.edit {
        putEnum(PREF_APP_THEME, theme)
    }

    fun getLanguage(context: Context?) = if (context == null) Locale.ENGLISH.language
    else app.getString(PREF_APP_LANGUAGE, context.getSelectedLanguage()) ?: Locale.ENGLISH.language

    fun changeLanguage(context: Context?) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) context
        else {
            val newLocale = Locale.forLanguageTag(getLanguage(context))
            Locale.setDefault(newLocale)

            val resources = context?.resources
            val configuration = resources?.configuration
            configuration?.setLocales(LocaleList(newLocale))

            configuration?.let { context.createConfigurationContext(it) } ?: context
        }

    fun setLocale(context: Context, locale: String) {
        app.edit { putString(PREF_APP_LANGUAGE, locale) }
        changeLanguage(context)
        (context as Activity).recreate()
    }
}
package org.application.shikiapp.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
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
import org.application.shikiapp.utils.extensions.getThemeFlow
import org.application.shikiapp.utils.extensions.putEnum

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
}
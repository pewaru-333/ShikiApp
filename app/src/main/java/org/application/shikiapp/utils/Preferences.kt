package org.application.shikiapp.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.core.content.edit
import org.application.shikiapp.models.data.Token

private const val PREF_APP_THEME = "app_theme"
private const val PREF_DYNAMIC_COLORS = "dynamic_colors"

object Preferences {
    private lateinit var app: SharedPreferences
    private lateinit var auth: SharedPreferences

    fun getInstance(context: Context) {
        app = context.getSharedPreferences("preferences_${context.packageName}", MODE_PRIVATE)
        auth = context.getSharedPreferences("auth_${context.packageName}", MODE_PRIVATE)
    }

    fun getAppTheme() = app.getString(PREF_APP_THEME, THEMES[0]) ?: THEMES[0]
    fun setAppTheme(theme: String) = when (theme) {
        THEMES[1] -> setDefaultNightMode(MODE_NIGHT_NO)
        THEMES[2] -> setDefaultNightMode(MODE_NIGHT_YES)
        else -> setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
    }.also { app.edit().putString(PREF_APP_THEME, theme).apply() }

    fun getDynamicColors() = app.getBoolean(PREF_DYNAMIC_COLORS, false)
    fun setDynamicColors(value: Boolean) = app.edit { putBoolean(PREF_DYNAMIC_COLORS, value).apply() }

    fun saveToken(token: Token) = auth.edit {
        putString(ACCESS_TOKEN, token.accessToken).apply()
        putString(REFRESH_TOKEN, token.refreshToken).apply()
        putLong(EXPIRES_IN, token.expiresIn).apply()
        putLong(CREATED_AT, token.createdAt).apply()
    }

    fun setUserId(userId: Long) = auth.edit { putLong(USER_ID, userId).apply() }

    fun getToken() = auth.getString(ACCESS_TOKEN, BLANK) ?: BLANK
    fun isTokenExists() = auth.contains(ACCESS_TOKEN) && auth.getString(ACCESS_TOKEN, BLANK) != BLANK
    fun isTokenExpired() = auth.getLong(CREATED_AT, 0L) + auth.getLong(EXPIRES_IN, 0L) < System.currentTimeMillis() / 1000
    fun refreshToken() = auth.getString(REFRESH_TOKEN, BLANK) ?: BLANK
    fun getUserId() = auth.getLong(USER_ID, 0L)
}
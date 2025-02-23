package org.application.shikiapp.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import org.application.shikiapp.models.data.Token

const val PREF_APP_THEME = "app_theme"
const val PREF_APP_CACHE = "app_cache"
const val PREF_DYNAMIC_COLORS = "dynamic_colors"
const val PREF_CATALOG_LIST_VIEW = "catalog_list_view"

object Preferences : ViewModel() {

    private lateinit var auth: SharedPreferences
    lateinit var app: SharedPreferences

    lateinit var theme: StateFlow<String>
    lateinit var dynamicColors: StateFlow<Boolean>

    fun getInstance(context: Context) {
        app = context.getSharedPreferences("preferences_${context.packageName}", MODE_PRIVATE)
        auth = context.getSharedPreferences("auth_${context.packageName}", MODE_PRIVATE)
        theme = app.getThemeFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), THEMES[0])
        dynamicColors = app.getColorsFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
    }

    fun getListView() = app.getString(PREF_CATALOG_LIST_VIEW, ListView.COLUMN.name) ?: ListView.COLUMN.name

    fun getCache() = app.getInt(PREF_APP_CACHE, 16)

    fun saveToken(token: Token) = auth.edit {
        putString(ACCESS_TOKEN, token.accessToken).apply()
        putString(REFRESH_TOKEN, token.refreshToken).apply()
        putLong(EXPIRES_IN, token.expiresIn).apply()
        putLong(CREATED_AT, token.createdAt).apply()
    }

    fun setUserId(userId: Long) = auth.edit { putLong(USER_ID, userId).apply() }

    fun isTokenExists() = auth.contains(ACCESS_TOKEN) && auth.getString(ACCESS_TOKEN, BLANK) != BLANK
    fun getToken() = auth.getString(ACCESS_TOKEN, BLANK) ?: BLANK
    fun getRefreshToken() = auth.getString(REFRESH_TOKEN, BLANK) ?: BLANK
    fun getUserId() = auth.getLong(USER_ID, 0L)
}

fun SharedPreferences.getThemeFlow(changedKey: String = PREF_APP_THEME) = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (changedKey == key) trySend(getString(key, THEMES[0]) ?: THEMES[0])
    }

    registerOnSharedPreferenceChangeListener(listener)

    if (contains(changedKey)) send(getString(changedKey, THEMES[0]) ?: THEMES[0])

    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}.buffer(Channel.UNLIMITED)

fun SharedPreferences.getColorsFlow(changedKey: String = PREF_DYNAMIC_COLORS) = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (changedKey == key) trySend(getBoolean(key, false))
    }

    registerOnSharedPreferenceChangeListener(listener)

    if (contains(changedKey)) send(getBoolean(changedKey, false))

    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}.buffer(Channel.UNLIMITED)
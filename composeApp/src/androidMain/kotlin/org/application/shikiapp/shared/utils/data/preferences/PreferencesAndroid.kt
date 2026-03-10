package org.application.shikiapp.shared.utils.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import me.zhanghai.compose.preference.Preferences
import me.zhanghai.compose.preference.createPreferenceFlow

class PreferencesAndroid(private val prefs: SharedPreferences) : IPreferences {
    override fun getBoolean(key: String, defaultValue: Boolean) = prefs.getBoolean(key, defaultValue)
    override fun putBoolean(key: String, value: Boolean) = prefs.edit { putBoolean(key, value) }

    override fun getInt(key: String, defaultValue: Int) = prefs.getInt(key, defaultValue)
    override fun putInt(key: String, value: Int) = prefs.edit { putInt(key, value) }

    override fun getLong(key: String, defaultValue: Long): Long = prefs.getLong(key, defaultValue)
    override fun putLong(key: String, value: Long) = prefs.edit { putLong(key, value) }

    override fun getString(key: String, defaultValue: String) = prefs.getString(key, defaultValue) ?: defaultValue
    override fun putString(key: String, value: String) = prefs.edit { putString(key, value) }

    override fun remove(key: String) = prefs.edit { remove(key) }

    override fun flow(key: String): Flow<Unit> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (key == changedKey) {
                trySend(Unit)
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)

        trySend(Unit)

        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.onStart { emit(Unit) }
}

@Composable
actual fun rememberAppPreferences(): MutableStateFlow<Preferences> {
    val context = LocalContext.current
    return remember(context) {
        val prefs = context.getSharedPreferences("preferences_${context.packageName}", Context.MODE_PRIVATE)

        createPreferenceFlow(prefs)
    }
}
package org.application.shikiapp.shared.utils.data.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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
            if (changedKey == key || changedKey == null) {
                trySend(Unit)
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)

        trySend(Unit)

        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
}
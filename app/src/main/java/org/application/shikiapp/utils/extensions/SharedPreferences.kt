package org.application.shikiapp.utils.extensions

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.application.shikiapp.utils.PREF_APP_THEME
import org.application.shikiapp.utils.PREF_DYNAMIC_COLORS
import org.application.shikiapp.utils.enums.Theme

fun <E : Enum<E>> SharedPreferences.Editor.putEnum(key: String, value: E) {
    putString(key, value.name)
}

fun <E : Enum<E>> SharedPreferences.getEnum(key: String, enum: Class<E>): E? {
    val stringValue = getString(key, null) ?: return null
    return enum.enumConstants?.find {
        it.name == stringValue
    }
}

fun <E : Enum<E>> SharedPreferences.getEnum(key: String, defaultValue: E): E {
    return getEnum(key, defaultValue.javaClass) ?: defaultValue
}

fun SharedPreferences.getThemeFlow(changedKey: String = PREF_APP_THEME) = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (changedKey == key) trySend(getEnum(key, Theme.SYSTEM))
    }

    registerOnSharedPreferenceChangeListener(listener)

    if (contains(changedKey)) send(getEnum(changedKey, Theme.SYSTEM))

    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}

fun SharedPreferences.getColorsFlow(changedKey: String = PREF_DYNAMIC_COLORS) = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (changedKey == key) trySend(getBoolean(key, false))
    }

    registerOnSharedPreferenceChangeListener(listener)

    if (contains(changedKey)) send(getBoolean(changedKey, false))

    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}
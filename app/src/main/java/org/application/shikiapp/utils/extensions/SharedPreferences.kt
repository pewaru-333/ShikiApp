package org.application.shikiapp.utils.extensions

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.MapPreferences
import me.zhanghai.compose.preference.Preferences
import org.application.shikiapp.utils.PREF_APP_THEME
import org.application.shikiapp.utils.PREF_DYNAMIC_COLORS
import org.application.shikiapp.utils.enums.Theme

@OptIn(DelicateCoroutinesApi::class)
fun SharedPreferences.getPreferenceFlow(): MutableStateFlow<Preferences> =
    MutableStateFlow(preferences).also {
        GlobalScope.launch(Dispatchers.Main.immediate) { it.drop(1).collect { preferences = it } }
    }

private var SharedPreferences.preferences: Preferences
    get() = @Suppress("UNCHECKED_CAST") (MapPreferences(all as Map<String, Any>))
    set(value) {
        edit {
            clear()
            value.asMap().forEach { (key, value) ->
                when (value) {
                    is Boolean -> putBoolean(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Float -> putFloat(key, value)
                    is String -> putString(key, value)
                    is Set<*> -> @Suppress("UNCHECKED_CAST") putStringSet(key, value as Set<String>)
                    else -> throw IllegalArgumentException("Unsupported type for value $value")
                }
            }
        }
    }

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
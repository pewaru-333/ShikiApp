package org.application.shikiapp.shared.utils.extensions

import kotlinx.coroutines.flow.map
import org.application.shikiapp.shared.utils.data.preferences.IPreferences

fun <T> IPreferences.flow(key: String, mapper: () -> T) = flow(key).map { mapper() }

inline fun IPreferences.edit(action: IPreferences.() -> Unit) {
    action()
}

inline fun <reified E : Enum<E>> IPreferences.getEnum(key: String, defaultValue: E) =
    runCatching { enumValueOf<E>(getString(key, defaultValue.name)) }.getOrDefault(defaultValue)

inline fun <reified T> IPreferences.getFlow(key: String, defaultValue: T) = flow(key) {
    safeGetValue(key, defaultValue)
}

inline fun <reified E : Enum<E>> IPreferences.getEnumFlow(key: String, defaultValue: E) =
    flow(key) { getEnum(key, defaultValue) }
inline fun <reified T> IPreferences.safeGetValue(key: String, defaultValue: T) =
    when (defaultValue) {
        is Boolean -> runCatching { getBoolean(key, defaultValue as Boolean) as T }.getOrDefault(defaultValue)
        is Int -> runCatching { getInt(key, defaultValue as Int) as T }.getOrDefault(defaultValue)
        is Long -> runCatching { getLong(key, defaultValue as Long) as T }.getOrDefault(defaultValue)
      //  is Float -> runCatching { getFloat(key, defaultValue as Float) as T }.getOrDefault(defaultValue)
        is String -> runCatching { getString(key, defaultValue as String) as T }.getOrDefault(defaultValue)
      //  is Set<*> -> runCatching { getStringSet(key, defaultValue as Set<String>) as T }.getOrDefault(defaultValue)
        else -> run { edit { remove(key) } }.let { defaultValue }
    }


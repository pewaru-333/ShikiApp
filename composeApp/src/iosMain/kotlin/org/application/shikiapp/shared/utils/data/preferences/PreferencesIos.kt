package org.application.shikiapp.shared.utils.data.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.MapPreferences
import org.application.shikiapp.shared.di.AppContext
import org.application.shikiapp.shared.di.AppModuleInitializer
import org.application.shikiapp.shared.utils.BLANK
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults
import me.zhanghai.compose.preference.Preferences as Prefs

class PreferencesIos : IPreferences {
    private val bundleId = NSBundle.mainBundle.bundleIdentifier.orEmpty()
    private val prefs = NSUserDefaults("$bundleId.preferences")

    private val _updates = MutableSharedFlow<String>(extraBufferCapacity = 64)

    override fun getBoolean(key: String, defaultValue: Boolean) =
        if (prefs.objectForKey(key) == null) defaultValue
        else prefs.boolForKey(key)

    override fun putBoolean(key: String, value: Boolean) {
        if (getBoolean(key, !value) == value && prefs.objectForKey(key) != null) return

        prefs.setBool(value, forKey = key)
        _updates.tryEmit(key)
    }

    override fun getInt(key: String, defaultValue: Int) = if (prefs.objectForKey(key) != null) {
        prefs.integerForKey(key).toInt()
    } else {
        defaultValue
    }

    override fun putInt(key: String, value: Int) {
        if (getInt(key, value + 1) == value && prefs.objectForKey(key) != null) return

        prefs.setInteger(value.toLong(), forKey = key)
        _updates.tryEmit(key)
    }

    override fun getLong(key: String, defaultValue: Long) = if (prefs.objectForKey(key) != null) {
        prefs.integerForKey(key)
    } else {
        defaultValue
    }

    override fun putLong(key: String, value: Long) {
        if (getLong(key, value + 1L) == value && prefs.objectForKey(key) != null) return

        prefs.setInteger(value, forKey = key)
        _updates.tryEmit(key)
    }

    override fun getString(key: String, defaultValue: String): String {
        return prefs.stringForKey(key) ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        if (getString(key, BLANK) == value && prefs.objectForKey(key) != null) return

        prefs.setObject(value, forKey = key)
        _updates.tryEmit(key)
    }

    override fun remove(key: String) {
        prefs.removeObjectForKey(key)
        _updates.tryEmit(key)
    }

    override fun flow(key: String): Flow<Unit> = _updates
        .filter { it == key }
        .onStart { emit(key) }
        .map { }

    fun createFlow(scope: CoroutineScope): MutableStateFlow<Prefs> {
        val initialState = MapPreferences(getCurrentMap())
        val stateFlow = MutableStateFlow<Prefs>(initialState)

        scope.launch(Dispatchers.IO) {
            _updates.collect {
                stateFlow.value = MapPreferences(getCurrentMap())
            }
        }

        return stateFlow
    }

    @Suppress("UNCHECKED_CAST")
    private fun getCurrentMap(): Map<String, Any> {
        val dict = prefs.dictionaryRepresentation()
        val result = mutableMapOf<String, Any>()

        for ((key, value) in dict) {
            if (key is String && value != null) {
                result[key] = value.toString().toTypedValue()
            }
        }
        return result
    }

    private fun String.toTypedValue() = when {
        equals("true", true) -> true
        equals("false", true) -> false
        toIntOrNull() != null -> toInt()
        toLongOrNull() != null -> toLong()
        else -> this
    }
}

@Composable
actual fun rememberAppPreferences(): MutableStateFlow<Prefs> {
    val scope = rememberCoroutineScope()
    val initializer = AppContext.app as AppModuleInitializer

    return remember(initializer) {
        initializer.preferencesIos.createFlow(scope)
    }
}
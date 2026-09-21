package org.application.shikiapp.shared.utils.data.preferences

import kotlinx.coroutines.flow.*
import org.application.shikiapp.shared.utils.BLANK
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

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
}
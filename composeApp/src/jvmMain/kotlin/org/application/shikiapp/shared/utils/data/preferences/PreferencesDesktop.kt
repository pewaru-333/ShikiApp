package org.application.shikiapp.shared.utils.data.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import jdk.internal.vm.vector.VectorSupport.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.MapPreferences
import okhttp3.internal.toLongOrDefault
import org.application.shikiapp.shared.di.AppContext
import org.application.shikiapp.shared.di.AppModuleInitializer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties
import me.zhanghai.compose.preference.Preferences as Prefs

class PreferencesDesktop : IPreferences {
    private val prefsDir: File = run {
        val os = System.getProperty("os.name").lowercase()
        val baseDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), "ShikiApp")

            else -> File(System.getProperty("user.home"), ".config/ShikiApp")
        }

        baseDir.also { if (!it.exists()) it.mkdirs() }
    }

    private val prefsFile = File(prefsDir, "preferences_shikiapp.properties")
    private val properties = Properties()

    private val _updates = MutableSharedFlow<String>(extraBufferCapacity = 64)

    init {
        if (!prefsDir.exists()) prefsDir.mkdirs()
        if (prefsFile.exists()) {
            FileInputStream(prefsFile).use { properties.load(it) }
        }
    }

    private fun save() {
        synchronized(properties) {
            FileOutputStream(prefsFile).use { properties.store(it, null) }
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean) =
        properties.getProperty(key, defaultValue.toString()).toBooleanStrict()

    override fun putBoolean(key: String, value: Boolean) {
        if (properties.getProperty(key)?.toBooleanStrictOrNull() == value) return

        properties.setProperty(key, value.toString())
        save()
        _updates.tryEmit(key)
    }


    override fun getInt(key: String, defaultValue: Int) =
        properties.getProperty(key, defaultValue.toString()).toInt()


    override fun putInt(key: String, value: Int) {
        if (properties.getProperty(key)?.toIntOrNull() == value) return

        properties.setProperty(key, value.toString())
        save()
        _updates.tryEmit(key)
    }

    override fun getLong(key: String, defaultValue: Long) =
        properties.getProperty(key, defaultValue.toString()).toLong()

    override fun putLong(key: String, value: Long) {
        if (properties.getProperty(key)?.toLongOrNull() == value) return

        properties.setProperty(key, value.toString())
        save()
        _updates.tryEmit(key)
    }

    override fun getString(key: String, defaultValue: String) =
        properties.getProperty(key, defaultValue)

    override fun putString(key: String, value: String) {
        if (properties.getProperty(key) == value) return

        properties.setProperty(key, value)
        save()
        _updates.tryEmit(key)
    }

    override fun remove(key: String) {
        properties.remove(key)
        save()
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

    private fun getCurrentMap() = properties.entries.associate {
        it.key.toString() to it.value.toString().toTypedValue()
    }

    private fun String.toTypedValue(): Any {
        return when {
            equals("true", true) -> true
            equals("false", true) -> false
            toIntOrNull() != null -> toInt()
            toLongOrNull() != null -> toLong()
            else -> this
        }
    }
}

@Composable
actual fun rememberAppPreferences(): MutableStateFlow<Prefs> {
    val scope = rememberCoroutineScope()
    val initializer = AppContext.app as AppModuleInitializer

    return remember(initializer) {
        initializer.preferencesDesktop.createFlow(scope)
    }
}
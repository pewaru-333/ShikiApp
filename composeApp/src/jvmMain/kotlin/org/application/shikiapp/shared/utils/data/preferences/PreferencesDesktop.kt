package org.application.shikiapp.shared.utils.data.preferences

import kotlinx.coroutines.flow.*
import org.application.shikiapp.shared.AppConfig
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

class PreferencesDesktop(appConfig: AppConfig) : IPreferences {
    private val userAgent = appConfig.userAgent
    private val prefsDir: File = run {
        val os = System.getProperty("os.name").lowercase()
        val baseDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), userAgent)

            else -> File(System.getProperty("user.home"), ".config/$userAgent")
        }

        baseDir.also { if (!it.exists()) it.mkdirs() }
    }

    private val prefsFile = File(prefsDir, "preferences_${userAgent.lowercase()}.properties")
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
}
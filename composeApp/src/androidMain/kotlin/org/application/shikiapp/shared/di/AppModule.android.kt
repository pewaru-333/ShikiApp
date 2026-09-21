package org.application.shikiapp.shared.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.utils.data.preferences.Preferences
import org.application.shikiapp.shared.utils.data.preferences.PreferencesAndroid


actual typealias PlatformContext = Context

actual class AppModuleInitializer actual constructor(override val context: Context, private val appConfig: AppConfig) : AppModule {
    override val config: AppConfig
        get() = appConfig

    override val preferences by lazy {
        val authFileName = "auth_${context.packageName}"
        val auth = context.getSharedPreferences(authFileName, MODE_PRIVATE)
        val app = context.getSharedPreferences("preferences_${context.packageName}", MODE_PRIVATE)

        val authData = auth.all
        if (authData.isNotEmpty()) {
            app.edit {
                authData.forEach { (key, value) ->
                    when (value) {
                        is String -> putString(key, value)
                        is Int -> putInt(key, value)
                        is Long -> putLong(key, value)
                        is Boolean -> putBoolean(key, value)
                        is Float -> putFloat(key, value)
                        is Set<*> -> @Suppress("UNCHECKED_CAST") putStringSet(key, value as Set<String>)
                    }
                }
            }

            context.deleteSharedPreferences(authFileName)
        }

        Preferences(PreferencesAndroid(app))
    }
}
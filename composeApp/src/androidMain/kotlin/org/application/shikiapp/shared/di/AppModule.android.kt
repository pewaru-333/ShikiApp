package org.application.shikiapp.shared.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.utils.data.preferences.Preferences
import org.application.shikiapp.shared.utils.data.preferences.PreferencesAndroid


actual typealias PlatformContext = Context

actual class AppModuleInitializer actual constructor(context: Context, private val appConfig: AppConfig) : AppModule {

    override val config: AppConfig
        get() = appConfig

    override val preferences by lazy {
        val auth = context.getSharedPreferences("auth_${context.packageName}", MODE_PRIVATE)
        val app = context.getSharedPreferences("preferences_${context.packageName}", MODE_PRIVATE)

        Preferences(PreferencesAndroid(app), PreferencesAndroid(auth))
    }
}
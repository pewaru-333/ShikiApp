package org.application.shikiapp.shared.di

import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.utils.data.preferences.Preferences
import org.application.shikiapp.shared.utils.data.preferences.PreferencesIos

actual abstract class PlatformContext

class AppleContext : PlatformContext()

actual class AppModuleInitializer actual constructor(override val context: PlatformContext, private val appConfig: AppConfig) : AppModule {
    override val config: AppConfig
        get() = appConfig

    override val preferences by lazy {
        Preferences(PreferencesIos())
    }
}
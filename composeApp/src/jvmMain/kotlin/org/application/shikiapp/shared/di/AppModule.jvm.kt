package org.application.shikiapp.shared.di

import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.utils.data.preferences.Preferences
import org.application.shikiapp.shared.utils.data.preferences.PreferencesDesktop

actual abstract class PlatformContext

class DesktopContext : PlatformContext()
actual class AppModuleInitializer actual constructor(context: PlatformContext, private val appConfig: AppConfig) : AppModule {
    val preferencesDesktop = PreferencesDesktop()

    override val config: AppConfig
        get() = appConfig

    override val preferences by lazy {
        Preferences(preferencesDesktop, preferencesDesktop)
    }
}
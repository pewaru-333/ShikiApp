package org.application.shikiapp.shared.di

import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.utils.data.preferences.Preferences

interface AppModule {
    val config: AppConfig
    val preferences: Preferences
}

object AppContext {
    private var _app: AppModule? = null

    val app: AppModule
        get() = _app ?: throw NullPointerException()

    fun init(module: AppModule) {
        if (_app == null) {
            _app = module
        }
    }
}

val AppConfig: AppConfig get() = AppContext.app.config
val Preferences: Preferences get() = AppContext.app.preferences

expect abstract class PlatformContext

expect class AppModuleInitializer(context: PlatformContext, appConfig: AppConfig) : AppModule

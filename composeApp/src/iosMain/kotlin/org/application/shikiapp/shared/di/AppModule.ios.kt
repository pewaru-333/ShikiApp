package org.application.shikiapp.shared.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.utils.data.preferences.Preferences
import org.application.shikiapp.shared.utils.data.preferences.PreferencesIos

actual abstract class PlatformContext

class AppleContext : PlatformContext()

actual class AppModuleInitializer actual constructor(
    override val context: PlatformContext,
    private val appConfig: AppConfig
) : AppModule {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val preferencesIos = PreferencesIos()

    override val config: AppConfig
        get() = appConfig

    override val preferences by lazy {
        Preferences(preferencesIos, preferencesIos, applicationScope)
    }
}
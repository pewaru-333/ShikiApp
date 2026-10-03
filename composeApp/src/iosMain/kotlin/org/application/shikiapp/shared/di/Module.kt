package org.application.shikiapp.shared.di

import org.application.shikiapp.shared.AppServices
import org.application.shikiapp.shared.utils.data.preferences.Preferences
import org.application.shikiapp.shared.utils.data.preferences.PreferencesIos

class Module(override val services: AppServices) : AppModule {
    override val preferences by lazy {
        Preferences(PreferencesIos())
    }
}
package org.application.shikiapp.shared.di

import org.application.shikiapp.shared.AppServices
import org.application.shikiapp.shared.utils.data.preferences.Preferences

interface AppModule {
    val preferences: Preferences
    val services: AppServices

    companion object {
        private var _instance: AppModule? = null
        internal val instance: AppModule
            get() = checkNotNull(_instance)

        fun init(module: AppModule) {
            if (_instance == null) {
                _instance = module
            }
        }
    }
}
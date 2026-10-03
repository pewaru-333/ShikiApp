package org.application.shikiapp.shared.di

import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.AppServices
import org.application.shikiapp.shared.utils.data.preferences.Preferences

val Preferences: Preferences
    get() = AppModule.instance.preferences

val AppServices: AppServices
    get() = AppModule.instance.services

val AppConfig: AppConfig
    get() = AppServices.config

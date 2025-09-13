package org.application.shikiapp.di

import android.content.Context
import org.application.shikiapp.ShikiApp.Companion.app
import org.application.shikiapp.utils.Preferences

interface AppModule {
    val preferences: Preferences
}

class AppModuleInitializer(context: Context) : AppModule {
    override val preferences by lazy { Preferences.getInstance(context) }
}

val Preferences: Preferences get() = app.preferences
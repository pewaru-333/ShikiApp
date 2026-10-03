package org.application.shikiapp.shared.di

import android.content.Context
import org.application.shikiapp.shared.AppServices
import org.application.shikiapp.shared.utils.data.preferences.Preferences
import org.application.shikiapp.shared.utils.data.preferences.PreferencesAndroid

class Module(private val context: Context, override val services: AppServices) : AppModule {
    override val preferences by lazy {
        val preferences = PreferencesAndroid(context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE))
        val previousName = "preferences_${context.packageName}"
        val previous = context.getSharedPreferences(previousName, Context.MODE_PRIVATE)

        if (previous.all.isNotEmpty() && preferences.migrateFrom(previous)) {
            context.deleteSharedPreferences(previousName)
        }

        Preferences(preferences)
    }
}
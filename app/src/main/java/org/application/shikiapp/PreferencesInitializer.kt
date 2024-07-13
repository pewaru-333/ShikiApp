package org.application.shikiapp

import android.content.Context
import androidx.startup.Initializer
import org.application.shikiapp.utils.Preferences

class PreferencesInitializer: Initializer<Unit> {
    override fun create(context: Context) = Preferences.getInstance(context)
    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}
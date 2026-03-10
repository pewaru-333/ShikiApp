package org.application.shikiapp.shared.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.LocaleList
import org.application.shikiapp.shared.di.AppModuleInitializer
import org.application.shikiapp.shared.di.Preferences
import java.util.Locale

class LocaleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        if (intent.action == Intent.ACTION_LOCALE_CHANGED && intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME) == context.packageName) {
            val localeList = intent.getParcelableExtra(Intent.EXTRA_LOCALE_LIST, LocaleList::class.java) ?: return

            val targetLocale = if (localeList.isEmpty) {
                LocaleList.getAdjustedDefault()[0]
            } else {
                localeList[0]
            }

            val newLocale = if (targetLocale.language in AppLanguages.list) {
                targetLocale.language
            } else {
                Locale.ENGLISH.language
            }


            if (Preferences.language != newLocale) {
                Preferences.setLanguage(newLocale)
            }
        }
    }
}
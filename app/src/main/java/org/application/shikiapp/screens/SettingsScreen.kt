package org.application.shikiapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.getPreferenceFlow
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.switchPreference
import org.application.shikiapp.ui.theme.isDynamicColorAvailable
import org.application.shikiapp.utils.CACHE_LIST
import org.application.shikiapp.utils.PREF_APP_CACHE
import org.application.shikiapp.utils.PREF_APP_THEME
import org.application.shikiapp.utils.PREF_DYNAMIC_COLORS
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.THEMES

@Destination<RootGraph>
@Composable
fun SettingsScreen() {
    ProvidePreferenceLocals(Preferences.app.getPreferenceFlow()) {
        LazyColumn(Modifier.fillMaxSize()) {
            preferenceCategory(
                key = "app_system",
                title = { Text(text = "Системные") }
            )

            listPreference(
                key = PREF_APP_THEME,
                defaultValue = THEMES[0],
                values = THEMES,
                title = { Text("Тема приложения") },
                summary = { Text(it) }
            )

            switchPreference(
                key = PREF_DYNAMIC_COLORS,
                defaultValue = false,
                title = { Text("Системные цвета") },
                enabled = { isDynamicColorAvailable() }
            )

            listPreference(
                key = PREF_APP_CACHE,
                defaultValue = CACHE_LIST[0],
                values = CACHE_LIST,
                title = { Text("Размер кэша") },
                summary = { Text("$it МБ") },
                valueToText = { AnnotatedString("$it МБ") }
            )
        }
    }
}
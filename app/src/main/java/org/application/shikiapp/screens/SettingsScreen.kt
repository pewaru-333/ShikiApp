package org.application.shikiapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.getPreferenceFlow
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.switchPreference
import org.application.shikiapp.ui.theme.isDynamicColorAvailable
import org.application.shikiapp.utils.CACHE_LIST
import org.application.shikiapp.utils.PREF_APP_CACHE
import org.application.shikiapp.utils.PREF_DYNAMIC_COLORS
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.enums.ListView
import org.application.shikiapp.utils.enums.Themes

@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    ProvidePreferenceLocals(Preferences.app.getPreferenceFlow()) {
        LazyColumn(Modifier.fillMaxSize()) {
            preferenceCategory(
                key = "app_view",
                title = { Text("Вид приложения") }
            )

            item {
                var value by remember { mutableStateOf(Preferences.listView) }

                ListPreference<ListView>(
                    value = value,
                    onValueChange = { value = it; Preferences.listView = it },
                    values = ListView.entries,
                    title = { Text("Вид списков") },
                    summary = { Text(stringResource(value.title)) },
                    valueToText = { AnnotatedString(context.getString(it.title)) }
                )
            }

            preferenceCategory(
                key = "app_system",
                title = { Text(text = "Системные") }
            )

            item {
                val value by Preferences.theme.collectAsStateWithLifecycle()

                ListPreference<Themes>(
                    value = value,
                    onValueChange = Preferences::setTheme,
                    values = Themes.entries,
                    title = { Text("Тема приложения") },
                    summary = { Text(stringResource(value.title)) },
                    valueToText = { AnnotatedString(context.getString(it.title)) }
                )
            }

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
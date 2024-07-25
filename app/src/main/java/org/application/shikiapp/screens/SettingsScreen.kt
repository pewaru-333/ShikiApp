package org.application.shikiapp.screens

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SwitchPreference
import me.zhanghai.compose.preference.preferenceCategory
import org.application.shikiapp.ui.theme.isDynamicColorAvailable
import org.application.shikiapp.utils.CACHE_LIST
import org.application.shikiapp.utils.Preferences.getAppTheme
import org.application.shikiapp.utils.Preferences.getCache
import org.application.shikiapp.utils.Preferences.getDynamicColors
import org.application.shikiapp.utils.Preferences.setAppTheme
import org.application.shikiapp.utils.Preferences.setCache
import org.application.shikiapp.utils.Preferences.setDynamicColors
import org.application.shikiapp.utils.THEMES

@Destination<RootGraph>
@Composable
fun SettingsScreen(activity: Activity = LocalContext.current as Activity) {
    ProvidePreferenceLocals {
        LazyColumn(Modifier.fillMaxSize()) {
            preferenceCategory(
                key = "app_system",
                title = { Text(text = "Системные") }
            )

            item {
                var value by remember { mutableStateOf(getAppTheme()) }

                ListPreference(
                    value = value,
                    onValueChange = { value = it; setAppTheme(it) },
                    values = THEMES,
                    title = { Text("Тема приложения") },
                    summary = { Text(value) }
                )
            }

            item {
                var value by remember { mutableStateOf(getDynamicColors()) }

                SwitchPreference(
                    value = value,
                    onValueChange = { value = it; setDynamicColors(it); activity.recreate() },
                    title = { Text("Системные цвета") },
                    enabled = isDynamicColorAvailable()
                )
            }

            item {
                var value by remember { mutableIntStateOf(getCache()) }

                ListPreference(
                    value = value,
                    onValueChange = { value = it; setCache(it) },
                    values = CACHE_LIST,
                    title = { Text("Размер кэша") },
                    summary = { Text("$value МБ") },
                    valueToText = { AnnotatedString("$it МБ") }
                )
            }
        }
    }
}
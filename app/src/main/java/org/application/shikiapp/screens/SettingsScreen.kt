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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.getPreferenceFlow
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.switchPreference
import org.application.shikiapp.R
import org.application.shikiapp.ui.theme.isDynamicColorAvailable
import org.application.shikiapp.utils.CACHE_LIST
import org.application.shikiapp.utils.PREF_APP_CACHE
import org.application.shikiapp.utils.PREF_DYNAMIC_COLORS
import org.application.shikiapp.utils.PREF_GROUP_APP_SYSTEM
import org.application.shikiapp.utils.PREF_GROUP_APP_VIEW
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.enums.ListView
import org.application.shikiapp.utils.enums.Theme
import org.application.shikiapp.utils.extensions.valueToText

@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    ProvidePreferenceLocals(Preferences.app.getPreferenceFlow()) {
        LazyColumn(Modifier.fillMaxSize()) {
            preferenceCategory(
                key = PREF_GROUP_APP_VIEW,
                title = { Text(stringResource(R.string.preference_category_app_view)) }
            )

            item {
                var value by remember { mutableStateOf(Preferences.listView) }

                ListPreference<ListView>(
                    value = value,
                    onValueChange = { value = it; Preferences.listView = it },
                    values = ListView.entries,
                    title = { Text(stringResource(R.string.preference_list_view)) },
                    summary = { Text(stringResource(value.title)) },
                    valueToText = { it.title.valueToText(context) }
                )
            }

            preferenceCategory(
                key = PREF_GROUP_APP_SYSTEM,
                title = { Text(stringResource(R.string.preference_category_system)) }
            )

            item {
                val value by Preferences.theme.collectAsStateWithLifecycle()

                ListPreference<Theme>(
                    value = value,
                    onValueChange = Preferences::setTheme,
                    values = Theme.entries,
                    title = { Text(stringResource(R.string.preference_theme)) },
                    summary = { Text(stringResource(value.title)) },
                    valueToText = { it.title.valueToText(context) }
                )
            }

            switchPreference(
                key = PREF_DYNAMIC_COLORS,
                defaultValue = false,
                title = { Text(stringResource(R.string.preference_dynamic_colors)) },
                enabled = { isDynamicColorAvailable() }
            )

            listPreference(
                key = PREF_APP_CACHE,
                defaultValue = CACHE_LIST[0],
                values = CACHE_LIST,
                title = { Text(stringResource(R.string.preference_cache_size)) },
                summary = { Text(stringResource(R.string.preference_cache_size_mb, it)) },
                valueToText = { it.valueToText(context, R.string.preference_cache_size_mb) }
            )
        }
    }
}
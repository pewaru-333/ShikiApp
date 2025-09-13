package org.application.shikiapp.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SwitchPreference
import me.zhanghai.compose.preference.createPreferenceFlow
import me.zhanghai.compose.preference.preferenceCategory
import org.application.shikiapp.R
import org.application.shikiapp.di.Preferences
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.theme.isDynamicColorAvailable
import org.application.shikiapp.utils.CACHE_LIST
import org.application.shikiapp.utils.PREF_GROUP_APP_SYSTEM
import org.application.shikiapp.utils.PREF_GROUP_APP_VIEW
import org.application.shikiapp.utils.enums.ListView
import org.application.shikiapp.utils.enums.Theme
import org.application.shikiapp.utils.extensions.getDisplayRegionName
import org.application.shikiapp.utils.extensions.getLanguageList
import org.application.shikiapp.utils.extensions.valueToText
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(visible: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current

    val listView by Preferences.listViewFlow.collectAsStateWithLifecycle(ListView.COLUMN)
    val dynamicColors by Preferences.dynamicColors.collectAsStateWithLifecycle(false)
    val theme by Preferences.theme.collectAsStateWithLifecycle(Theme.SYSTEM)
    val cache by Preferences.cacheFlow.collectAsStateWithLifecycle(16)

    BackHandler(visible, onBack)
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.text_settings)) },
                    navigationIcon = { NavigationIcon(onBack) }
                )
            }
        ) { values ->
            ProvidePreferenceLocals(createPreferenceFlow(Preferences.app)) {
                LazyColumn(Modifier.padding(values)) {
                    preferenceCategory(
                        key = PREF_GROUP_APP_VIEW,
                        title = { Text(stringResource(R.string.preference_category_app_view)) }
                    )

                    item {
                        ListPreference(
                            value = listView,
                            onValueChange = Preferences::setListView,
                            values = ListView.entries,
                            title = { Text(stringResource(R.string.preference_list_view)) },
                            summary = { Text(stringResource(listView.title)) },
                            valueToText = { it.title.valueToText(context) }
                        )
                    }

                    preferenceCategory(
                        key = PREF_GROUP_APP_SYSTEM,
                        title = { Text(stringResource(R.string.preference_category_system)) }
                    )

                    item {
                        ListPreference(
                            value = theme,
                            onValueChange = Preferences::setTheme,
                            values = Theme.entries,
                            title = { Text(stringResource(R.string.preference_theme)) },
                            summary = { Text(stringResource(theme.title)) },
                            valueToText = { it.title.valueToText(context) }
                        )
                    }

                    item {
                        SwitchPreference(
                            value = dynamicColors,
                            onValueChange = Preferences::setDynamicColors,
                            title = { Text(stringResource(R.string.preference_dynamic_colors)) },
                            enabled = isDynamicColorAvailable()
                        )
                    }

                    item {
                        ListPreference(
                            value = cache,
                            onValueChange = Preferences::setCache,
                            values = CACHE_LIST,
                            title = { Text(stringResource(R.string.preference_cache_size)) },
                            summary = { Text(stringResource(R.string.preference_cache_size_mb, cache)) },
                            valueToText = { it.valueToText(context, R.string.preference_cache_size_mb) }
                        )
                    }

                    item {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                            var value by remember { mutableStateOf(Preferences.getLanguage(context)) }

                            ListPreference(
                                value = value,
                                onValueChange = { value = it; Preferences.setLocale(context, it) },
                                values = context.getLanguageList(),
                                title = { Text(stringResource(R.string.preference_language)) },
                                summary = { Text(Locale.forLanguageTag(value).getDisplayRegionName()) },
                                valueToText = { AnnotatedString(Locale.forLanguageTag(it).getDisplayRegionName()) }
                            )
                        } else Preference(
                            title = { Text(stringResource(R.string.preference_language)) },
                            onClick = {
                                context.startActivity(
                                    Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
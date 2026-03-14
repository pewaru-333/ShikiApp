@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.screens

import AppLanguages
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.switchPreference
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.ui.templates.NavigationIcon
import org.application.shikiapp.shared.utils.AppLocale
import org.application.shikiapp.shared.utils.CACHE_LIST
import org.application.shikiapp.shared.utils.PREF_DYNAMIC_COLORS
import org.application.shikiapp.shared.utils.PREF_GROUP_APP_SYSTEM
import org.application.shikiapp.shared.utils.PREF_GROUP_APP_VIEW
import org.application.shikiapp.shared.utils.data.preferences.rememberAppPreferences
import org.application.shikiapp.shared.utils.enums.ListView
import org.application.shikiapp.shared.utils.enums.Menu
import org.application.shikiapp.shared.utils.enums.Theme
import org.application.shikiapp.shared.utils.extensions.getDisplayRegionName
import org.application.shikiapp.shared.utils.isDynamicColorAvailable
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.preference_cache_size
import shikiapp.composeapp.generated.resources.preference_cache_size_mb
import shikiapp.composeapp.generated.resources.preference_category_app_view
import shikiapp.composeapp.generated.resources.preference_category_system
import shikiapp.composeapp.generated.resources.preference_dynamic_colors
import shikiapp.composeapp.generated.resources.preference_language
import shikiapp.composeapp.generated.resources.preference_list_view
import shikiapp.composeapp.generated.resources.preference_start_page
import shikiapp.composeapp.generated.resources.preference_theme
import shikiapp.composeapp.generated.resources.text_settings
import java.util.Locale

@Composable
fun SettingsScreen(isVisible: Boolean, onBack: () -> Unit) {
    val startPage by Preferences.startPageFlow.collectAsStateWithLifecycle(Menu.NEWS)
    val listView by Preferences.listViewFlow.collectAsStateWithLifecycle(ListView.COLUMN)
    val cache by Preferences.cacheFlow.collectAsStateWithLifecycle(CACHE_LIST.first())
    val theme by Preferences.theme.collectAsStateWithLifecycle(Theme.SYSTEM)

    val isCompact = rememberWindowSize().isCompact

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isVisible,
        onBackCompleted = onBack
    )
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.text_settings)) },
                    navigationIcon = { NavigationIcon(onBack) }
                )
            }
        ) { values ->
            ProvidePreferenceLocals(rememberAppPreferences()) {
                LazyColumn(Modifier.padding(values)) {
                    preferenceCategory(
                        key = PREF_GROUP_APP_VIEW,
                        title = { Text(stringResource(Res.string.preference_category_app_view)) }
                    )

                    item {
                        ListPreference(
                            value = startPage,
                            onValueChange = Preferences::setStartPage,
                            values = Menu.entries,
                            title = { Text(stringResource(Res.string.preference_start_page)) },
                            summary = { Text(stringResource(startPage.title)) },
                            valueToText = { AnnotatedString(stringResource(it.title)) }
                        )
                    }

                    if (isCompact) {
                        item {
                            ListPreference(
                                value = listView,
                                onValueChange = Preferences::setListView,
                                values = ListView.entries,
                                title = { Text(stringResource(Res.string.preference_list_view)) },
                                summary = { Text(stringResource(listView.title)) },
                                valueToText = { AnnotatedString(stringResource(it.title)) }
                            )
                        }
                    }

                    preferenceCategory(
                        key = PREF_GROUP_APP_SYSTEM,
                        title = { Text(stringResource(Res.string.preference_category_system)) }
                    )

                    item {
                        ListPreference(
                            value = theme,
                            onValueChange = Preferences::setTheme,
                            values = Theme.entries,
                            title = { Text(stringResource(Res.string.preference_theme)) },
                            summary = { Text(stringResource(theme.title)) },
                            valueToText = { AnnotatedString(stringResource(it.title)) }
                        )
                    }

                    switchPreference(
                        key = PREF_DYNAMIC_COLORS,
                        defaultValue = false,
                        enabled = { isDynamicColorAvailable() },
                        title = { Text(stringResource(Res.string.preference_dynamic_colors)) },
                    )

                    item {
                        val locale = AppLocale.current

                        ListPreference(
                            value = locale,
                            onValueChange = Preferences::setLanguage,
                            values = AppLanguages.list,
                            title = { Text(stringResource(Res.string.preference_language)) },
                            summary = { Text(Locale.forLanguageTag(locale).getDisplayRegionName()) },
                            valueToText = { AnnotatedString(Locale.forLanguageTag(it).getDisplayRegionName()) }
                        )
                    }

                    item {
                        ListPreference(
                            value = cache,
                            values = CACHE_LIST,
                            onValueChange = Preferences::setCache,
                            title = { Text(stringResource(Res.string.preference_cache_size)) },
                            summary = { Text(stringResource(Res.string.preference_cache_size_mb, cache)) },
                            valueToText = { AnnotatedString(stringResource(Res.string.preference_cache_size_mb, it)) }
                        )
                    }
                }
            }
        }
    }
}
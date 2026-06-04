@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.screens

import AppLanguages
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SwitchPreference
import me.zhanghai.compose.preference.preferenceCategory
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.ui.templates.AnimatedDialogScreen
import org.application.shikiapp.shared.utils.AppLocale
import org.application.shikiapp.shared.utils.CACHE_LIST
import org.application.shikiapp.shared.utils.PREF_GROUP_APP_LISTS
import org.application.shikiapp.shared.utils.PREF_GROUP_APP_SYSTEM
import org.application.shikiapp.shared.utils.PREF_GROUP_APP_VIEW
import org.application.shikiapp.shared.utils.data.preferences.rememberAppPreferences
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.ListView
import org.application.shikiapp.shared.utils.enums.Menu
import org.application.shikiapp.shared.utils.enums.Palette
import org.application.shikiapp.shared.utils.enums.Theme
import org.application.shikiapp.shared.utils.enums.WatchStatus
import org.application.shikiapp.shared.utils.extensions.getLocaleLocalizedName
import org.application.shikiapp.shared.utils.isDynamicColorAvailable
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.preference_cache_size
import shikiapp.composeapp.generated.resources.preference_cache_size_mb
import shikiapp.composeapp.generated.resources.preference_category_app_view
import shikiapp.composeapp.generated.resources.preference_category_lists
import shikiapp.composeapp.generated.resources.preference_category_system
import shikiapp.composeapp.generated.resources.preference_dynamic_colors
import shikiapp.composeapp.generated.resources.preference_episode_auto_add
import shikiapp.composeapp.generated.resources.preference_episode_auto_add_summary
import shikiapp.composeapp.generated.resources.preference_language
import shikiapp.composeapp.generated.resources.preference_list_view
import shikiapp.composeapp.generated.resources.preference_start_page
import shikiapp.composeapp.generated.resources.preference_theme
import shikiapp.composeapp.generated.resources.preference_user_rates_list_size_show
import shikiapp.composeapp.generated.resources.preference_user_rates_start_status
import shikiapp.composeapp.generated.resources.preference_user_rates_start_type
import shikiapp.composeapp.generated.resources.text_palette
import shikiapp.composeapp.generated.resources.text_settings

@Composable
fun SettingsScreen(isVisible: Boolean, onBack: () -> Unit) {
    val startPage by Preferences.startPageFlow.collectAsStateWithLifecycle()
    val listView by Preferences.listViewFlow.collectAsStateWithLifecycle()
    val isAutoAdd by Preferences.episodeAutoAddFlow.collectAsStateWithLifecycle()
    val showUserRatesListSize by Preferences.showUserRateListSizeFlow.collectAsStateWithLifecycle()
    val userRatesWatchType by Preferences.userRatesStartTypeFlow.collectAsStateWithLifecycle()
    val userRatesWatchStatus by Preferences.userRatesStartWatchStatusFlow.collectAsStateWithLifecycle()
    val cache by Preferences.cacheFlow.collectAsStateWithLifecycle()
    val theme by Preferences.theme.collectAsStateWithLifecycle()
    val dynamicColors by Preferences.dynamicColors.collectAsStateWithLifecycle()
    val palette by Preferences.colorPaletteFlow.collectAsStateWithLifecycle()

    val isCompact = rememberWindowSize().isCompact

    var showDeeplinkSetting by rememberSaveable { mutableStateOf(false) }

    AnimatedDialogScreen(isVisible, stringResource(Res.string.text_settings), onBack) { values ->
        ProvidePreferenceLocals(rememberAppPreferences()) {
            LazyColumn(Modifier.padding(values)) {
                preferenceCategory(
                    key = PREF_GROUP_APP_VIEW,
                    title = { Text(stringResource(Res.string.preference_category_app_view)) }
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

                item {
                    SwitchPreference(
                        value = dynamicColors,
                        onValueChange = Preferences::setDynamicColors,
                        enabled = isDynamicColorAvailable(),
                        title = { Text(stringResource(Res.string.preference_dynamic_colors)) },
                    )
                }

                item {
                    ListPreference(
                        value = palette,
                        onValueChange = Preferences::setPalette,
                        enabled = !dynamicColors,
                        values = Palette.entries,
                        title = { Text(stringResource(Res.string.text_palette)) },
                        summary = { Text(stringResource(palette.title)) },
                        valueToText = { AnnotatedString(stringResource(it.title)) },
                    )
                }

                preferenceCategory(
                    key = PREF_GROUP_APP_LISTS,
                    title = { Text(stringResource(Res.string.preference_category_lists)) }
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

                item {
                    ListPreference(
                        value = userRatesWatchType,
                        onValueChange = Preferences::setUserRatesStartType,
                        values = LinkedType.userRatesType,
                        title = { Text(stringResource(Res.string.preference_user_rates_start_type)) },
                        summary = { Text(stringResource(userRatesWatchType.title)) },
                        valueToText = { AnnotatedString(stringResource(it.title)) }
                    )
                }

                item {
                    ListPreference(
                        value = userRatesWatchStatus,
                        onValueChange = Preferences::setUserRatesStartWatchStatus,
                        values = WatchStatus.entries,
                        title = { Text(stringResource(Res.string.preference_user_rates_start_status)) },
                        summary = {
                            Text(
                                text = buildString {
                                    append(stringResource(userRatesWatchStatus.titleAnime))
                                    userRatesWatchStatus.titleManga?.let {
                                        append(" (${stringResource(it)})")
                                    }
                                }
                            )
                        },
                        valueToText = {
                            AnnotatedString(
                                text = buildString {
                                    append(stringResource(it.titleAnime))
                                    it.titleManga?.let { mangaTitle ->
                                        append(" (${stringResource(mangaTitle)})")
                                    }
                                }
                            )
                        }
                    )
                }

                item {
                    SwitchPreference(
                        value = showUserRatesListSize,
                        onValueChange = Preferences::setShowUserRatesListSize,
                        title = { Text(stringResource(Res.string.preference_user_rates_list_size_show)) }
                    )
                }

                item {
                    SwitchPreference(
                        value = isAutoAdd,
                        onValueChange = Preferences::setAutoIncrementEpisode,
                        enabled = Preferences.token != null,
                        title = { Text(stringResource(Res.string.preference_episode_auto_add)) },
                        summary = { Text(stringResource(Res.string.preference_episode_auto_add_summary)) }
                    )
                }

                preferenceCategory(
                    key = PREF_GROUP_APP_SYSTEM,
                    title = { Text(stringResource(Res.string.preference_category_system)) }
                )

                item {
                    val locale = AppLocale.current

                    ListPreference(
                        value = locale,
                        onValueChange = Preferences::setLanguage,
                        values = AppLanguages.list,
                        title = { Text(stringResource(Res.string.preference_language)) },
                        summary = { Text(locale.getLocaleLocalizedName()) },
                        valueToText = { AnnotatedString(locale.getLocaleLocalizedName()) }
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

                deeplinkSetting { showDeeplinkSetting = true }
            }
        }
    }

    DeeplinkScreen(showDeeplinkSetting) { showDeeplinkSetting = false }
}

expect fun LazyListScope.deeplinkSetting(onClick: () -> Unit)

@Composable
expect fun DeeplinkScreen(isVisible: Boolean, onBack: () -> Unit)
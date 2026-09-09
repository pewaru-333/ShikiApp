package org.application.shikiapp.shared.screens

import AppLanguages
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.ktor.client.engine.ProxyType
import me.zhanghai.compose.preference.*
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.ui.templates.AnimatedDialogScreen
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.*
import org.application.shikiapp.shared.utils.data.preferences.rememberAppPreferences
import org.application.shikiapp.shared.utils.enums.*
import org.application.shikiapp.shared.utils.extensions.getLocaleLocalizedName
import org.application.shikiapp.shared.utils.ui.rememberProxySettingsState
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.*

@Composable
fun SettingsScreen(isVisible: Boolean, onBack: () -> Unit) {
    val startPage by Preferences.startPageFlow.collectAsStateWithLifecycle()
    val listView by Preferences.listViewFlow.collectAsStateWithLifecycle()
    val isAutoAdd by Preferences.episodeAutoAddFlow.collectAsStateWithLifecycle()
    val rememberCatalogOrder by Preferences.rememberCatalogOrderFlow.collectAsStateWithLifecycle()
    val showUserRatesListSize by Preferences.showUserRateListSizeFlow.collectAsStateWithLifecycle()
    val userRatesWatchType by Preferences.userRatesStartTypeFlow.collectAsStateWithLifecycle()
    val userRatesWatchStatus by Preferences.userRatesStartWatchStatusFlow.collectAsStateWithLifecycle()
    val useUserAppLinks by Preferences.useUserUrlListFlow.collectAsStateWithLifecycle()
    val cache by Preferences.cacheFlow.collectAsStateWithLifecycle()
    val theme by Preferences.theme.collectAsStateWithLifecycle()
    val dynamicColors by Preferences.dynamicColors.collectAsStateWithLifecycle()
    val palette by Preferences.colorPaletteFlow.collectAsStateWithLifecycle()

    val isCompact = rememberWindowSize().isCompact

    var showDeeplinkSetting by rememberSaveable { mutableStateOf(false) }
    var showProxySettings by rememberSaveable { mutableStateOf(false) }

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
                        value = rememberCatalogOrder,
                        onValueChange = Preferences::toggleRememberLastCatalogOrder,
                        title = { Text(stringResource(Res.string.preference_remember_catalog_list_order)) }
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
                        valueToText = { AnnotatedString(it.getLocaleLocalizedName()) }
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

                deeplinkSetting(
                    isEnabled = !useUserAppLinks,
                    onClick = { showDeeplinkSetting = true }
                )

                preference(
                    key = PREF_NETWORK_CONFIG_SETTINGS,
                    title = { Text(stringResource(Res.string.preference_network_config)) },
                    onClick = { showProxySettings = true }
                )
            }
        }
    }

    DeeplinkScreen(showDeeplinkSetting) { showDeeplinkSetting = false }
    ProxySettings(showProxySettings) { showProxySettings = false }
}

@Composable
private fun ProxySettings(isVisible: Boolean, onBack: () -> Unit) =
    AnimatedDialogScreen(isVisible, stringResource(Res.string.preference_network_config), onBack) { values ->
        val state = rememberProxySettingsState()
        val isCompact = rememberWindowSize().isCompact
        val horizontalPadding = if (isCompact) 16.dp else 32.dp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 800.dp)
                    .padding(horizontal = horizontalPadding)
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.text_links),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .toggleable(
                            role = Role.Switch,
                            value = state.isUserMode,
                            onValueChange = { state.isUserMode = it }
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.preference_use_user_url_list),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(Res.string.preference_use_user_url_list_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Switch(state.isUserMode, null)
                }

                AnimatedVisibility(state.isUserMode) {
                    Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                        state.urlListItems.forEachIndexed { index, item ->
                            Row(Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = item.url,
                                    onValueChange = { state.updateUrl(index, it) },
                                    singleLine = true,
                                    enabled = state.isUserMode,
                                    isError = item.isError,
                                    leadingIcon = { Text("${index + 1}") },
                                    supportingText = {
                                        if (item.isError) {
                                            Text(
                                                text = stringResource(Res.string.text_base_url_format),
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                )

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .height(56.dp)
                                ) {
                                    if (index == 0 && state.urlList.size < 5) {
                                        FilledTonalIconButton(
                                            onClick = state::addUrl,
                                            enabled = state.isUserMode,
                                            content = { VectorIcon(Icons.Add) }
                                        )
                                    } else if (index == state.urlList.size - 1) {
                                        FilledTonalIconButton(
                                            onClick = state::removeLastUrl,
                                            enabled = state.isUserMode,
                                            content = { VectorIcon(Icons.Remove) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = stringResource(Res.string.text_proxy),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .toggleable(
                            role = Role.Switch,
                            value = state.isProxyEnabled,
                            onValueChange = { state.isProxyEnabled = it }
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.preference_use_proxy),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(Res.string.preference_use_proxy_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Switch(state.isProxyEnabled, null)
                }

                AnimatedVisibility(state.isProxyEnabled) {
                    Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = state.proxyHost,
                                onValueChange = state::updateHost,
                                label = { Text(stringResource(Res.string.text_host)) },
                                placeholder = { Text("HTTP, HTTPS, SOCKS5") },
                                modifier = Modifier.weight(0.65f),
                                singleLine = true,
                                isError = state.isHostError
                            )

                            OutlinedTextField(
                                value = state.proxyPort,
                                onValueChange = state::updatePort,
                                label = { Text(stringResource(Res.string.text_port)) },
                                modifier = Modifier.weight(0.35f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = state.isPortError
                            )
                        }

                        OutlinedTextField(
                            value = state.proxyUser,
                            onValueChange = { state.proxyUser = it.trim() },
                            label = { Text(stringResource(Res.string.text_username_optional)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = state.proxyType != ProxyType.SOCKS
                        )

                        OutlinedTextField(
                            value = state.proxyPass,
                            onValueChange = { state.proxyPass = it },
                            label = { Text(stringResource(Res.string.text_password_optional)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = state.proxyType != ProxyType.SOCKS,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(Modifier.padding(12.dp), Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
                        VectorIcon(
                            imageVector = Icons.Settings,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = stringResource(Res.string.text_applied_after_relaunching_app_proxy_settings),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    TextButton(
                        onClick = { state.clear(onBack) },
                        content = { Text(stringResource(Res.string.text_clear)) }
                    )

                    Button(
                        onClick = { state.save(onBack) },
                        content = { Text(stringResource(Res.string.text_save)) }
                    )
                }
            }
        }
    }

expect fun LazyListScope.deeplinkSetting(isEnabled: Boolean, onClick: () -> Unit)

@Composable
expect fun DeeplinkScreen(isVisible: Boolean, onBack: () -> Unit)
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.ui.templates.*
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.*
import org.application.shikiapp.shared.utils.data.preferences.rememberPreference
import org.application.shikiapp.shared.utils.enums.*
import org.application.shikiapp.shared.utils.extensions.getLocaleLocalizedName
import org.application.shikiapp.shared.utils.ui.*
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.*

@Composable
fun SettingsScreen(isVisible: Boolean, onBack: () -> Unit) {
    val locale = AppLocale.current
    val rememberListOrder by rememberPreference { rememberCatalogOrder }
    val rememberRatesOrder by rememberPreference { rememberRatesOrder }
    val dynamicColors by rememberPreference { dynamicColors }
    val useUserUrlList by rememberPreference { useUserUrlList }

    val isCompact = rememberWindowSize().isCompact

    var showDeeplinkSetting by rememberSaveable { mutableStateOf(false) }
    var showProxySettings by rememberSaveable { mutableStateOf(false) }

    AnimatedDialogScreen(isVisible, stringResource(Res.string.text_settings), onBack) { values ->
        LazyColumn(Modifier.padding(values)) {
            preferenceCategory(
                key = PREF_GROUP_APP_VIEW,
                title = { Text(stringResource(Res.string.preference_category_app_view)) }
            )

            listPreference(
                setting = Preferences.theme,
                values = Theme.entries,
                title = { Text(stringResource(Res.string.preference_theme)) },
                summary = { Text(stringResource(it.title)) },
                valueToText = { stringResource(it.title) }
            )

            switchPreference(
                setting = Preferences.dynamicColors,
                enabled = { isDynamicColorAvailable() },
                title = { Text(stringResource(Res.string.preference_dynamic_colors)) },
            )

            listPreference(
                setting = Preferences.colorPalette,
                values = Palette.entries,
                enabled = { !dynamicColors },
                title = { Text(stringResource(Res.string.text_palette)) },
                summary = { Text(stringResource(it.title)) },
                valueToText = { stringResource(it.title) },
            )

            preferenceCategory(
                key = PREF_GROUP_APP_LISTS,
                title = { Text(stringResource(Res.string.preference_category_lists)) }
            )

            listPreference(
                setting = Preferences.startPage,
                values = Menu.entries,
                title = { Text(stringResource(Res.string.preference_start_page)) },
                summary = { Text(stringResource(it.title)) },
                valueToText = { stringResource(it.title) }
            )

            if (isCompact) {
                listPreference(
                    setting = Preferences.listView,
                    values = ListView.entries,
                    title = { Text(stringResource(Res.string.preference_list_view)) },
                    summary = { Text(stringResource(it.title)) },
                    valueToText = { stringResource(it.title) }
                )
            }

            listPreference(
                setting = Preferences.userRatesStartType,
                values = LinkedType.userRatesType,
                title = { Text(stringResource(Res.string.preference_user_rates_start_type)) },
                summary = { Text(stringResource(it.title)) },
                valueToText = { stringResource(it.title) }
            )

            listPreference(
                setting = Preferences.userRatesStartWatchStatus,
                values = WatchStatus.entries,
                title = { Text(stringResource(Res.string.preference_user_rates_start_status)) },
                summary = {
                    Text(
                        text = buildString {
                            append(stringResource(it.titleAnime))
                            it.titleManga?.let { mangaTitle ->
                                append(" (${stringResource(mangaTitle)})")
                            }
                        }
                    )
                },
                valueToText = {
                    buildString {
                        append(stringResource(it.titleAnime))
                        it.titleManga?.let { mangaTitle ->
                            append(" (${stringResource(mangaTitle)})")
                        }
                    }
                }
            )

            item {
                SwitchPreference(
                    value = rememberListOrder,
                    onValueChange = Preferences::toggleRememberLastCatalogOrder,
                    title = { Text(stringResource(Res.string.preference_remember_catalog_list_order)) }
                )
            }

            item {
                SwitchPreference(
                    value = rememberRatesOrder,
                    onValueChange = Preferences::toggleRememberLastRatesOrder,
                    title = { Text(stringResource(Res.string.preference_remember_rates_list_order)) }
                )
            }

            switchPreference(
                setting = Preferences.showUserRateListSize,
                title = { Text(stringResource(Res.string.preference_user_rates_list_size_show)) }
            )

            switchPreference(
                setting = Preferences.episodeAutoAdd,
                enabled = { Preferences.token != null },
                title = { Text(stringResource(Res.string.preference_episode_auto_add)) },
                summary = { Text(stringResource(Res.string.preference_episode_auto_add_summary)) }
            )

            preferenceCategory(
                key = PREF_GROUP_APP_SYSTEM,
                title = { Text(stringResource(Res.string.preference_category_system)) }
            )

            listPreference(
                setting = Preferences.language,
                values = AppLanguages.list,
                title = { Text(stringResource(Res.string.preference_language)) },
                summary = { Text(locale.getLocaleLocalizedName()) },
                valueToText = { it.getLocaleLocalizedName() }
            )

            listPreference(
                setting = Preferences.cache,
                values = CACHE_LIST,
                title = { Text(stringResource(Res.string.preference_cache_size)) },
                summary = { Text(stringResource(Res.string.preference_cache_size_mb, it)) },
                valueToText = { stringResource(Res.string.preference_cache_size_mb, it) }
            )

            deeplinkSetting(
                isEnabled = !useUserUrlList,
                onClick = { showDeeplinkSetting = true }
            )

            preference(
                key = PREF_NETWORK_CONFIG_SETTINGS,
                title = { Text(stringResource(Res.string.preference_network_config)) },
                onClick = { showProxySettings = true }
            )
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

        @Composable
        fun SettingsSectionTitle(text: String) = Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
        )

        @Composable
        fun SettingsToggleRow(
            title: String,
            summary: String? = null,
            checked: Boolean,
            onCheckedChange: (Boolean) -> Unit,
            enabled: Boolean = true
        ) = Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .toggleable(
                    value = checked,
                    enabled = enabled,
                    role = Role.Switch,
                    onValueChange = onCheckedChange
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                )

                if (summary != null) {
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (enabled) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                        }
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = null,
                enabled = enabled
            )
        }

        @Composable
        fun ProxyInputRow(
            index: Int,
            item: ProxySettingsState.InputItem,
            itemCount: Int,
            maxItemCount: Int,
            onValueChange: (String) -> Unit,
            onAdd: () -> Unit,
            onRemove: () -> Unit,
            modifier: Modifier = Modifier,
            placeholder: String? = null,
            errorText: String? = null,
            enabled: Boolean = true
        ) = Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            OutlinedTextField(
                value = item.value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = enabled,
                isError = item.isError,
                leadingIcon = { Text("${index + 1}") },
                placeholder = placeholder?.let {
                    {
                        Text(
                            text = it,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                },
                supportingText = if (!item.isError || errorText == null) null else {
                    {
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(48.dp, 56.dp)
            ) {
                when (index) {
                    0 if itemCount < maxItemCount -> FilledTonalIconButton(
                        onClick = onAdd,
                        enabled = enabled,
                        content = { VectorIcon(Icons.Add) }
                    )

                    itemCount - 1 if itemCount > 1 -> FilledTonalIconButton(
                        onClick = onRemove,
                        enabled = enabled,
                        content = { VectorIcon(Icons.Remove) }
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
                .consumeWindowInsets(values)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 800.dp)
                    .padding(horizontal = horizontalPadding)
            ) {
                Spacer(Modifier.height(16.dp))

                SettingsSectionTitle(stringResource(Res.string.text_links))

                SettingsToggleRow(
                    title = stringResource(Res.string.preference_use_user_url_list),
                    summary = stringResource(Res.string.preference_use_user_url_list_summary),
                    checked = state.isUserMode,
                    onCheckedChange = state::toggleUserMode,
                    enabled = state.isNetworkUrl
                )

                AnimatedVisibility(state.isUserMode) {
                    Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                        state.urlListItems.forEachIndexed { index, item ->
                            ProxyInputRow(
                                index = index,
                                item = item,
                                itemCount = state.urlList.size,
                                maxItemCount = MAX_URL_COUNT,
                                errorText = stringResource(Res.string.text_base_url_format),
                                onValueChange = { state.updateUrl(index, it) },
                                onAdd = state::addUrl,
                                onRemove = state::removeLastUrl,
                                enabled = state.isNetworkUrl
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                SettingsSectionTitle(stringResource(Res.string.text_proxy))

                SettingsToggleRow(
                    title = stringResource(Res.string.preference_use_proxy),
                    summary = stringResource(Res.string.preference_use_proxy_summary),
                    checked = state.isProxyEnabled,
                    onCheckedChange = state::toggleProxy,
                    enabled = state.isNetworkUrl
                )

                AnimatedVisibility(state.isProxyEnabled) {
                    Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = state.proxyHost,
                                onValueChange = state::updateHost,
                                label = { Text(stringResource(Res.string.text_host)) },
                                placeholder = { Text("HTTP, HTTPS, SOCKS5") },
                                singleLine = true,
                                enabled = state.isNetworkUrl,
                                isError = state.isHostError,
                                modifier = Modifier.weight(0.65f)
                            )

                            OutlinedTextField(
                                value = state.proxyPort,
                                onValueChange = state::updatePort,
                                label = { Text(stringResource(Res.string.text_port)) },
                                singleLine = true,
                                enabled = state.isNetworkUrl,
                                isError = state.isPortError,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.35f)
                            )
                        }

                        OutlinedTextField(
                            value = state.proxyUser,
                            onValueChange = state::updateProxyUser,
                            label = { Text(stringResource(Res.string.text_username_optional)) },
                            singleLine = true,
                            enabled = state.isNetworkUrl,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        OutlinedTextField(
                            value = state.proxyPass,
                            onValueChange = state::updateProxyPassword,
                            label = { Text(stringResource(Res.string.text_password_optional)) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            enabled = state.isNetworkUrl,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (Network.isYggdrasilAvailable) {
                    Spacer(Modifier.height(24.dp))

                    SettingsToggleRow(
                        title = stringResource(Res.string.preference_use_yggdrasil),
                        summary = stringResource(Res.string.preference_use_yggdrasil_summary),
                        checked = state.isYggdrasilEnabled,
                        onCheckedChange = state::toggleYggdrasil
                    )

                    AnimatedVisibility(state.isYggdrasilEnabled) {
                        Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                            state.peerListItems.forEachIndexed { index, item ->
                                ProxyInputRow(
                                    index = index,
                                    item = item,
                                    itemCount = state.peerList.size,
                                    maxItemCount = MAX_YGGDRASIL_PEER_COUNT,
                                    placeholder = stringResource(Res.string.text_yggdrasil_peer_format),
                                    onValueChange = { state.updatePeer(index, it) },
                                    onAdd = state::addPeer,
                                    onRemove = state::removeLastPeer
                                )
                            }
                        }
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
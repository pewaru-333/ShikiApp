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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import me.zhanghai.compose.preference.createPreferenceFlow
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
import org.application.shikiapp.utils.extensions.getDisplayRegionName
import org.application.shikiapp.utils.extensions.getLanguageList
import org.application.shikiapp.utils.extensions.valueToText
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(visible: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current

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
                    navigationIcon = {
                        IconButton(onBack) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    }
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
                        var value by remember { mutableStateOf(Preferences.listView) }

                        ListPreference(
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

                        ListPreference(
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
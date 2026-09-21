package org.application.shikiapp.shared.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.application.shikiapp.shared.utils.data.preferences.Preferences
import org.application.shikiapp.shared.utils.extensions.copy
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_dismiss

@Stable
class PreferenceTheme(
    val categoryPadding: PaddingValues,
    val categoryColor: Color,
    val categoryTextStyle: TextStyle,
    val padding: PaddingValues,
    val horizontalSpacing: Dp,
    val verticalSpacing: Dp,
    val disabledOpacity: Float,
    val iconContainerMinWidth: Dp,
    val iconColor: Color,
    val titleColor: Color,
    val titleTextStyle: TextStyle,
    val summaryColor: Color,
    val summaryTextStyle: TextStyle,
    val dividerHeight: Dp,
)

@Composable
fun preferenceTheme(
    categoryPadding: PaddingValues = PaddingValues(16.dp, 24.dp, 16.dp, 8.dp),
    categoryColor: Color = MaterialTheme.colorScheme.secondary,
    categoryTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
    padding: PaddingValues = PaddingValues(16.dp),
    horizontalSpacing: Dp = 16.dp,
    verticalSpacing: Dp = 16.dp,
    disabledOpacity: Float = 0.38f,
    iconContainerMinWidth: Dp = 56.dp,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    titleTextStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    summaryColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    summaryTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    dividerHeight: Dp = 32.dp,
): PreferenceTheme = PreferenceTheme(
    categoryPadding = categoryPadding,
    categoryColor = categoryColor,
    categoryTextStyle = categoryTextStyle,
    padding = padding,
    horizontalSpacing = horizontalSpacing,
    verticalSpacing = verticalSpacing,
    disabledOpacity = disabledOpacity,
    iconContainerMinWidth = iconContainerMinWidth,
    iconColor = iconColor,
    titleColor = titleColor,
    titleTextStyle = titleTextStyle,
    summaryColor = summaryColor,
    summaryTextStyle = summaryTextStyle,
    dividerHeight = dividerHeight,
)

@Composable
fun BasicPreference(
    textContainer: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconContainer: @Composable () -> Unit = {},
    widgetContainer: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.then(
            if (onClick != null) {
                Modifier.clickable(enabled, onClick = onClick)
            } else {
                Modifier
            }
        )
    ) {
        iconContainer()
        Box(Modifier.weight(1f)) { textContainer() }
        widgetContainer()
    }
}

@Composable
fun PreferenceCategory(title: @Composable () -> Unit, modifier: Modifier = Modifier, theme: PreferenceTheme = preferenceTheme()) {
    BasicPreference(
        modifier = modifier,
        textContainer = {
            Box(Modifier.padding(theme.categoryPadding), Alignment.CenterStart) {
                CompositionLocalProvider(LocalContentColor provides theme.categoryColor) {
                    ProvideTextStyle(theme.categoryTextStyle, title)
                }
            }
        }
    )
}

fun LazyListScope.preferenceCategory(
    key: String,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    item(key) { PreferenceCategory(title, modifier) }
}

@Composable
fun Preference(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    widgetContainer: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    theme: PreferenceTheme = preferenceTheme()
) {
    BasicPreference(
        textContainer = {
            Column(
                modifier = Modifier.padding(
                    paddingValues = theme.padding.copy(
                        start = if (icon != null) 0.dp else Dp.Unspecified,
                        end = if (widgetContainer != null) 0.dp else Dp.Unspecified,
                    )
                )
            ) {
                CompositionLocalProvider(
                    content = { ProvideTextStyle(theme.titleTextStyle, title) },
                    value = LocalContentColor provides theme.titleColor.let {
                        if (enabled) it else it.copy(alpha = theme.disabledOpacity)
                    }
                )
                if (summary != null) {
                    CompositionLocalProvider(
                        content = { ProvideTextStyle(theme.summaryTextStyle, summary) },
                        value = LocalContentColor provides theme.summaryColor.let {
                            if (enabled) it else it.copy(alpha = theme.disabledOpacity)
                        }
                    )
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        iconContainer = {
            if (icon != null) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .widthIn(min = theme.iconContainerMinWidth)
                        .padding(theme.padding.copy(end = 0.dp))
                ) {
                    CompositionLocalProvider(
                        content = icon,
                        value = LocalContentColor provides theme.iconColor.let {
                            if (enabled) it else it.copy(alpha = theme.disabledOpacity)
                        }
                    )
                }
            }
        },
        widgetContainer = { widgetContainer?.invoke() },
        onClick = onClick,
    )
}

fun LazyListScope.preference(
    key: String,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    widgetContainer: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    item(key) {
        Preference(
            title = title,
            modifier = modifier,
            enabled = enabled,
            icon = icon,
            summary = summary,
            widgetContainer = widgetContainer,
            onClick = onClick
        )
    }
}

@Composable
fun SwitchPreference(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    theme: PreferenceTheme = preferenceTheme()
) {
    Preference(
        title = title,
        enabled = enabled,
        icon = icon,
        summary = summary,
        modifier = modifier.toggleable(
            value = value,
            enabled = enabled,
            role = Role.Switch,
            onValueChange = onValueChange
        ),
        widgetContainer = {
            Switch(
                checked = value,
                onCheckedChange = null,
                modifier = Modifier.padding(theme.padding.copy(start = theme.horizontalSpacing)),
                enabled = enabled,
            )
        }
    )
}

fun LazyListScope.switchPreference(
    setting: Preferences.PreferenceSetting<Boolean>,
    title: @Composable (Boolean) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: (Boolean) -> Boolean = { true },
    icon: @Composable ((Boolean) -> Unit)? = null,
    summary: @Composable ((Boolean) -> Unit)? = null,
) {
    item(setting.key) {
        val value by setting.flow.collectAsStateWithLifecycle(setting.value)

        SwitchPreference(
            value = value,
            onValueChange = { setting.value = it },
            title = { title(value) },
            modifier = modifier,
            enabled = enabled(value),
            icon = icon?.let { { it(value) } },
            summary = summary?.let { { it(value) } },
        )
    }
}

enum class ListPreferenceType {
    ALERT_DIALOG,
    DROPDOWN_MENU,
}

@Composable
fun <T> ListPreference(
    value: T,
    onValueChange: (T) -> Unit,
    values: List<T>,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    type: ListPreferenceType = ListPreferenceType.ALERT_DIALOG,
    cancelText: String = stringResource(Res.string.text_dismiss),
    valueToText: @Composable (T) -> String = { it.toString() },
    item: @Composable (value: T, currentValue: T, onClick: () -> Unit) -> Unit = ListPreferenceDefaults.item(type, valueToText),
) {
    var openSelector by rememberSaveable { mutableStateOf(false) }

    if (openSelector) {
        when (type) {
            ListPreferenceType.ALERT_DIALOG -> {
                AlertDialog(
                    onDismissRequest = { openSelector = false },
                    title = title,
                    confirmButton = {
                        TextButton(
                            onClick = { openSelector = false },
                            content = { Text(cancelText) }
                        )
                    },
                    text = {
                        LazyColumn(Modifier.fillMaxWidth()) {
                            items(values) { itemValue ->
                                item(itemValue, value) {
                                    onValueChange(itemValue)
                                    openSelector = false
                                }
                            }
                        }
                    }
                )
            }
            ListPreferenceType.DROPDOWN_MENU -> {
                val theme = preferenceTheme()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(theme.padding.copy(top = 0.dp, bottom = 0.dp))
                ) {
                    DropdownMenu(
                        expanded = openSelector,
                        onDismissRequest = { openSelector = false },
                    ) {
                        for (itemValue in values) {
                            item(itemValue, value) {
                                onValueChange(itemValue)
                                openSelector = false
                            }
                        }
                    }
                }
            }
        }
    }

    Preference(
        title = title,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        summary = summary,
        onClick = { openSelector = true }
    )
}

fun <T> LazyListScope.listPreference(
    setting: Preferences.PreferenceSetting<T>,
    values: List<T>,
    title: @Composable (T) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: (T) -> Boolean = { true },
    icon: @Composable ((T) -> Unit)? = null,
    summary: @Composable ((T) -> Unit)? = null,
    type: ListPreferenceType = ListPreferenceType.ALERT_DIALOG,
    cancelText: @Composable () -> String = { stringResource(Res.string.text_dismiss) },
    valueToText: @Composable (T) -> String = { it.toString() },
    item: @Composable (value: T, currentValue: T, onClick: () -> Unit) -> Unit = ListPreferenceDefaults.item(type, valueToText),
) {
    item(setting.key) {
        val value by setting.flow.collectAsStateWithLifecycle(setting.value)

        ListPreference(
            value = value,
            onValueChange = { setting.value = it },
            values = values,
            title = { title(value) },
            modifier = modifier,
            enabled = enabled(value),
            icon = icon?.let { { it(value) } },
            summary = summary?.let { { it(value) } },
            type = type,
            cancelText = cancelText.invoke(),
            valueToText = valueToText,
            item = item
        )
    }
}

object ListPreferenceDefaults {
    fun <T> item(
        type: ListPreferenceType,
        valueToText: @Composable (T) -> String,
    ): @Composable (value: T, currentValue: T, onClick: () -> Unit) -> Unit =
        when (type) {
            ListPreferenceType.ALERT_DIALOG -> { value, currentValue, onClick ->
                DialogItem(value, currentValue, valueToText, onClick)
            }

            ListPreferenceType.DROPDOWN_MENU -> { value, currentValue, onClick ->
                DropdownMenuItem(value, currentValue, valueToText, onClick)
            }
        }

    @Composable
    private fun <T> DialogItem(
        value: T,
        currentValue: T,
        valueToText: @Composable (T) -> String,
        onClick: () -> Unit,
    ) {
        val selected = value == currentValue

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .selectable(
                    selected = selected,
                    enabled = true,
                    role = Role.RadioButton,
                    onClick = onClick
                )
        ) {
            RadioButton(selected, null)
            Spacer(Modifier.width(24.dp))
            Text(
                text = valueToText(value),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    @Composable
    private fun <T> DropdownMenuItem(
        value: T,
        currentValue: T,
        valueToText: @Composable (T) -> String,
        onClick: () -> Unit,
    ) {
        DropdownMenuItem(
            text = { Text(valueToText(value)) },
            onClick = onClick,
            colors = MenuDefaults.itemColors(),
            modifier = if (value == currentValue) {
                Modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest)
            } else {
                Modifier
            }
        )
    }
}
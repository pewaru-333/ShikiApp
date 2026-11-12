package org.application.shikiapp.shared.utils.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.ui.theme.Icons

interface NetworkSettings {
    val usesOwnConnection: Boolean
    fun save(): Boolean
    fun clear()

    @Composable
    fun Content()
}

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
    value: String,
    isError: Boolean,
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
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.weight(1f),
        singleLine = true,
        enabled = enabled,
        isError = isError,
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
        supportingText = if (!isError || errorText == null) null else {
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



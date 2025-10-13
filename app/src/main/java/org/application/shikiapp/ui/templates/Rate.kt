@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.ui.templates

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.application.shikiapp.R
import org.application.shikiapp.R.string.text_cancel
import org.application.shikiapp.events.RateEvent
import org.application.shikiapp.models.states.NewRateState
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.Score
import org.application.shikiapp.utils.enums.WatchStatus
import kotlin.enums.EnumEntries

@Composable
fun DialogEditRate(
    state: NewRateState,
    type: LinkedType,
    isExists: Boolean,
    onEvent: (RateEvent) -> Unit = {},
    onCreate: (LinkedType) -> Unit = {},
    onUpdate: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) = AlertDialog(
    onDismissRequest = onDismiss,
    dismissButton = { TextButton(onDismiss) { Text(stringResource(text_cancel)) } },
    confirmButton = {
        TextButton(
            content = { Text(stringResource(R.string.text_save)) },
            enabled = !state.status.isNullOrEmpty(),
            onClick = {
                if (isExists) onUpdate(state.id)
                else onCreate(type)
            }
        )
    },
    title = {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            Text(stringResource(if (isExists) R.string.text_change else R.string.text_rate))
            if (isExists) {
                IconButton(
                    onClick = { onDelete(state.id) },
                    content = { Icon(painterResource(R.drawable.vector_trash), null) }
                )
            }
        }
    },
    text = {
        RateFieldsAll(
            state = state,
            type = type,
            onEvent = onEvent
        )
    }
)

@Composable
fun RateFieldsAll(state: NewRateState, type: LinkedType, onEvent: (RateEvent) -> Unit) =
    Column(Modifier.verticalScroll(rememberScrollState()), spacedBy(16.dp)) {
        RateDropMenu(
            items = WatchStatus.entries,
            title = state.statusName,
            label = R.string.text_status,
            itemTitle = { stringResource(type.getWatchStatusTitle(it)) },
            onEvent = { onEvent(RateEvent.SetStatus(it, type)) }
        )

        if (type == LinkedType.ANIME) {
            RateField(R.string.text_episodes, state.episodes) {
                onEvent(RateEvent.SetEpisodes(it))
            }
        }

        if (type == LinkedType.MANGA) {
            RateField(R.string.text_rate_chapters, state.chapters) {
                onEvent(RateEvent.SetChapters(it))
            }
            RateField(R.string.text_volumes, state.volumes) {
                onEvent(RateEvent.SetVolumes(it))
            }
        }

        RateDropMenu(
            items = Score.entries,
            title = state.score?.title,
            label = R.string.text_score,
            itemTitle = { stringResource(it.title) },
            onEvent = { onEvent(RateEvent.SetScore(it)) }
        )

        RateField(
            label = if (type == LinkedType.ANIME) R.string.text_rewatches else R.string.text_rereadings,
            value = state.rewatches,
            onValueChange = { onEvent(RateEvent.SetRewatches(it)) }
        )

        RateField(
            label = R.string.text_comment,
            value = state.text,
            onValueChange = { onEvent(RateEvent.SetText(it)) },
            singleLine = false,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.None)
        )
    }

@Composable
fun <T : Enum<T>> RateDropMenu(
    items: EnumEntries<T>,
    @StringRes title: Int?,
    @StringRes label: Int,
    itemTitle: @Composable (T) -> String,
    onEvent: (T) -> Unit
) {
    var flag by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = flag,
        onExpandedChange = { flag = it }
    ) {
        OutlinedTextField(
            value = stringResource(title ?: R.string.blank),
            onValueChange = {},
            label = { Text(stringResource(label)) },
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(flag) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = flag,
            onDismissRequest = { flag = false }
        ) {
            items.forEach {
                DropdownMenuItem(
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    text = {
                        Text(
                            text = itemTitle(it),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onEvent(it)
                        flag = false
                    }
                )
            }
        }
    }
}

@Composable
fun RateField(
    @StringRes label: Int,
    value: String?,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next
    ),
    onValueChange: (String) -> Unit
) = OutlinedTextField(
    value = value.orEmpty(),
    onValueChange = onValueChange,
    label = { Text(stringResource(label)) },
    keyboardOptions = keyboardOptions,
    singleLine = singleLine
)
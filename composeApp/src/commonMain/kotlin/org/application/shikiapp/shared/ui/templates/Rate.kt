@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.ui.templates

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.application.shikiapp.shared.events.RateEvent
import org.application.shikiapp.shared.models.states.NewRateState
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.Score
import org.application.shikiapp.shared.utils.enums.WatchStatus
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.blank
import shikiapp.composeapp.generated.resources.text_change
import shikiapp.composeapp.generated.resources.text_comment
import shikiapp.composeapp.generated.resources.text_dismiss
import shikiapp.composeapp.generated.resources.text_episodes
import shikiapp.composeapp.generated.resources.text_rate
import shikiapp.composeapp.generated.resources.text_rate_chapters
import shikiapp.composeapp.generated.resources.text_rereadings
import shikiapp.composeapp.generated.resources.text_rewatches
import shikiapp.composeapp.generated.resources.text_save
import shikiapp.composeapp.generated.resources.text_score
import shikiapp.composeapp.generated.resources.text_status
import shikiapp.composeapp.generated.resources.text_volumes
import shikiapp.composeapp.generated.resources.vector_trash
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
    dismissButton = { TextButton(onDismiss) { Text(stringResource(Res.string.text_dismiss)) } },
    confirmButton = {
        TextButton(
            content = { Text(stringResource(Res.string.text_save)) },
            enabled = !state.status.isNullOrEmpty(),
            onClick = {
                if (isExists) onUpdate(state.id)
                else onCreate(type)
            }
        )
    },
    title = {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            Text(stringResource(if (isExists) Res.string.text_change else Res.string.text_rate))
            if (isExists) {
                IconButton(
                    onClick = { onDelete(state.id) },
                    content = { Icon(painterResource(Res.drawable.vector_trash), null) }
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
            label = Res.string.text_status,
            itemTitle = { stringResource(type.getWatchStatusTitle(it)) },
            onEvent = { onEvent(RateEvent.SetStatus(it, type)) }
        )

        if (type == LinkedType.ANIME) {
            RateField(Res.string.text_episodes, state.episodes) {
                onEvent(RateEvent.SetEpisodes(it))
            }
        }

        if (type == LinkedType.MANGA) {
            RateField(Res.string.text_rate_chapters, state.chapters) {
                onEvent(RateEvent.SetChapters(it))
            }
            RateField(Res.string.text_volumes, state.volumes) {
                onEvent(RateEvent.SetVolumes(it))
            }
        }

        RateDropMenu(
            items = Score.entries,
            title = state.score?.title,
            label = Res.string.text_score,
            itemTitle = { stringResource(it.title) },
            onEvent = { onEvent(RateEvent.SetScore(it)) }
        )

        RateField(
            label = if (type == LinkedType.ANIME) Res.string.text_rewatches else Res.string.text_rereadings,
            value = state.rewatches,
            onValueChange = { onEvent(RateEvent.SetRewatches(it)) }
        )

        RateField(
            label = Res.string.text_comment,
            value = state.text,
            onValueChange = { onEvent(RateEvent.SetText(it)) },
            singleLine = false,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.None)
        )
    }

@Composable
fun <T : Enum<T>> RateDropMenu(
    items: EnumEntries<T>,
    title: StringResource?,
    label: StringResource,
    itemTitle: @Composable (T) -> String,
    onEvent: (T) -> Unit
) {
    var flag by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = flag,
        onExpandedChange = { flag = it }
    ) {
        OutlinedTextField(
            value = stringResource(title ?: Res.string.blank),
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
    label: StringResource,
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
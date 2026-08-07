@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import org.application.shikiapp.shared.events.RateEvent
import org.application.shikiapp.shared.models.states.NewRateState
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.Score
import org.application.shikiapp.shared.utils.enums.WatchStatus
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.*

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
) {
    val focusRequester = remember(::FocusRequester)
    var requestedFocus by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(
                content = { Text(stringResource(Res.string.text_dismiss)) },
                onClick = onDismiss,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onGloballyPositioned {
                        if (!requestedFocus) {
                            focusRequester.requestFocus()
                            requestedFocus = true
                        }
                    }
            )
        },
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
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(stringResource(if (isExists) Res.string.text_change else Res.string.text_rate))

                if (isExists) {
                    IconButton(
                        onClick = { onDelete(state.id) },
                        content = { VectorIcon(Icons.Trash) }
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
}

@Composable
fun RateFieldsAll(state: NewRateState, type: LinkedType, onEvent: (RateEvent) -> Unit) =
    Column(Modifier.verticalScroll(rememberScrollState()), Arrangement.spacedBy(16.dp)) {
        RateDropMenu(
            items = WatchStatus.entries,
            title = state.statusName,
            label = Res.string.text_status,
            selected = { state.statusName == type.getWatchStatusTitle(it) },
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
            selected = { state.score == it },
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
fun <T> RateDropMenu(
    items: List<T>,
    selected: (T) -> Boolean,
    title: StringResource?,
    label: StringResource,
    itemTitle: @Composable (T) -> String,
    onEvent: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = stringResource(title ?: Res.string.blank),
            onValueChange = {},
            label = { Text(stringResource(label)) },
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MenuDefaults.groupStandardContainerColor,
            shape = MenuDefaults.standaloneGroupShape,
        ) {
            items.fastForEachIndexed { index, item ->
                DropdownMenuItem(
                    selected = selected(item),
                    shapes = MenuDefaults.itemShape(index, items.size),
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    text = { Text(itemTitle(item)) },
                    onClick = {
                        onEvent(item)
                        expanded = false
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
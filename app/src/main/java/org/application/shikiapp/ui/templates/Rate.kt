package org.application.shikiapp.ui.templates

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.application.shikiapp.R
import org.application.shikiapp.events.RateEvent
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.models.viewModels.UserRateViewModel
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.Score
import org.application.shikiapp.utils.enums.WatchStatus
import org.application.shikiapp.utils.extensions.safeValueOf

@Composable
fun CreateRate(id: String, type: LinkedType, rateF: UserRate?, reload: () -> Unit, hide: () -> Unit) {
    val model = viewModel<UserRateViewModel>()
    val state by model.newRate.collectAsStateWithLifecycle()
    val exists by rememberSaveable { mutableStateOf(rateF != null) }

    LaunchedEffect(rateF) {
        rateF?.let { rate ->
            model.onEvent(RateEvent.SetRateId(rate.id.toString()))
            model.onEvent(RateEvent.SetStatus(Enum.safeValueOf<WatchStatus>(rate.status), type))
            model.onEvent(RateEvent.SetScore(Score.entries.first { it.score == rate.score }))
            model.onEvent(RateEvent.SetChapters(rate.chapters.toString()))
            model.onEvent(RateEvent.SetEpisodes(rate.episodes.toString()))
            model.onEvent(RateEvent.SetVolumes(rate.volumes.toString()))
            model.onEvent(RateEvent.SetRewatches(rate.rewatches.toString()))
            model.onEvent(RateEvent.SetText(rate.text))
        }
    }

    AlertDialog(
        onDismissRequest = hide,
        confirmButton = {
            TextButton(
                content = { Text(stringResource(R.string.text_save)) },
                enabled = !state.status.isNullOrEmpty(),
                onClick = {
                    if (exists) model.update(state.id, reload)
                    else model.create(id, type, reload)
                }
            )
        },
        dismissButton = {
            if (exists) {
                TextButton(
                    onClick = { model.delete(state.id, reload) },
                    content = { Text(stringResource(R.string.text_remove)) }
                )
            }
        },
        title = {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(stringResource(if (exists) R.string.text_change else R.string.text_rate))
                IconButton(hide) { Icon(Icons.Outlined.Close, null) }
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), Arrangement.spacedBy(16.dp)) {
                RateStatus(model::onEvent, state.statusName, type)
                if (type == LinkedType.ANIME) {
                    RateEpisodes(model::onEvent, state.episodes)
                }
                if (type == LinkedType.MANGA) {
                    RateChapters(model::onEvent, state.chapters)
                    RateVolumes(model::onEvent, state.volumes)
                }
                RateScore(model::onEvent, state.score)
                RateRewatches(model::onEvent, state.rewatches, type)
                RateText(model::onEvent, state.text)
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RateStatus(event: (RateEvent) -> Unit, @StringRes statusName: Int, type: LinkedType) {
    var flag by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(flag, { flag = it }) {
        OutlinedTextField(
            value = stringResource(statusName),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            label = { Text(stringResource(R.string.text_status)) },
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(flag) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(flag, { flag = false }) {
            WatchStatus.entries.forEach {
                DropdownMenuItem(
                    onClick = { event(RateEvent.SetStatus(it, type)); flag = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    text = {
                        Text(
                            text = stringResource(type.getWatchStatusTitle(it)),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateScore(event: (RateEvent) -> Unit, score: Score?) {
    var flag by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(flag, { flag = it }) {
        OutlinedTextField(
            value = stringResource(score?.title ?: R.string.blank),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            label = { Text(stringResource(R.string.text_score)) },
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(flag) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(flag, { flag = false }) {
            Score.entries.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(it.title),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = { event(RateEvent.SetScore(it)); flag = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun RateEpisodes(event: (RateEvent) -> Unit, episodes: String?) = OutlinedTextField(
    value = episodes.orEmpty(),
    onValueChange = { event(RateEvent.SetEpisodes(it)) },
    label = { Text(stringResource(R.string.text_episodes)) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)

@Composable
fun RateVolumes(event: (RateEvent) -> Unit, volumes: String?) = OutlinedTextField(
    value = volumes.orEmpty(),
    onValueChange = { event(RateEvent.SetVolumes(it)) },
    label = { Text(stringResource(R.string.text_volumes)) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)

@Composable
fun RateChapters(event: (RateEvent) -> Unit, chapters: String?) = OutlinedTextField(
    value = chapters.orEmpty(),
    onValueChange = { event(RateEvent.SetChapters(it)) },
    label = { Text(stringResource(R.string.text_rate_chapters)) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)

@Composable
fun RateRewatches(event: (RateEvent) -> Unit, count: String?, type: LinkedType) = OutlinedTextField(
    value = count.orEmpty(),
    onValueChange = { event(RateEvent.SetRewatches(it)) },
    label = { Text(stringResource(if (type == LinkedType.ANIME) R.string.text_rewatches else R.string.text_rereadings)) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)

@Composable
fun RateText(event: (RateEvent) -> Unit, text: String?) = OutlinedTextField(
    value = text.orEmpty(),
    onValueChange = { event(RateEvent.SetText(it)) },
    label = { Text(stringResource(R.string.text_comment)) }
)
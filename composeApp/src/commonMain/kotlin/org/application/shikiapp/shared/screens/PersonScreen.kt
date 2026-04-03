@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.states.PersonState
import org.application.shikiapp.shared.models.ui.Person
import org.application.shikiapp.shared.models.viewModels.PersonViewModel
import org.application.shikiapp.shared.network.response.Response.Success
import org.application.shikiapp.shared.ui.templates.AnimatedScreen
import org.application.shikiapp.shared.ui.templates.BottomSheet
import org.application.shikiapp.shared.ui.templates.Comments
import org.application.shikiapp.shared.ui.templates.DialogPoster
import org.application.shikiapp.shared.ui.templates.LinkListener
import org.application.shikiapp.shared.ui.templates.Names
import org.application.shikiapp.shared.ui.templates.ParagraphTitle
import org.application.shikiapp.shared.ui.templates.Poster
import org.application.shikiapp.shared.ui.templates.ProfilesFull
import org.application.shikiapp.shared.ui.templates.RelatedFull
import org.application.shikiapp.shared.ui.templates.ScaffoldContent
import org.application.shikiapp.shared.ui.templates.profiles
import org.application.shikiapp.shared.ui.templates.related
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.rememberCommentListState
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_birthday
import shikiapp.composeapp.generated.resources.text_characters
import shikiapp.composeapp.generated.resources.text_deathday
import shikiapp.composeapp.generated.resources.text_roles

@Composable
fun PersonScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel(::PersonViewModel)
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    LinkListener(model.openLink) { (response as? Success)?.data?.url }

    AnimatedScreen(response, model::loadData, Person::comments) { person, comments ->
        PersonView(person, state, model::onEvent, onNavigate, back)

        val commentListState = rememberCommentListState(
            list = comments,
            onCommentEvent = model.commentEvent
        )
        Comments(
            state = commentListState,
            isVisible = state.dialogState is BaseDialogState.Comments,
            isSending = state.isSendingComment,
            onNavigate = onNavigate,
            onHide = { model.onEvent(ContentDetailEvent.ToggleDialog(null)) },
            onCreateComment = { text, isOfftopic ->
                model.onEvent(ContentDetailEvent.CreateComment(text, isOfftopic))
            },
            onUpdateComment = { id, text, isOfftopicChanged ->
                model.onEvent(ContentDetailEvent.UpdateComment(id, text, isOfftopicChanged))
            },
            onDeleteComment = { id ->
                model.onEvent(ContentDetailEvent.DeleteComment(id))
            }
        )
    }
}

@Composable
private fun PersonView(
    person: Person,
    state: PersonState,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    ScaffoldContent(
        title = { Text(person.jobTitle) },
        userRate = null,
        isFavoured = person.favoured,
        onBack = onBack,
        onEvent = onEvent,
        onToggleFavourite = { onEvent(ContentDetailEvent.Person.ToggleFavourite(person.personKind)) }
    ) {
        item {
            PersonHeader(
                person = person,
                onOpenPoster = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Poster)) }
            )
        }

        item {
            Roles(person.grouppedRoles)
        }

        related(
            list = person.relatedList,
            onNavigate = onNavigate,
            onShow = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Related)) }
        )

        profiles(
            profiles = person.characters,
            title = Res.string.text_characters,
            onShow = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Characters)) },
            onNavigate = { onNavigate(Screen.Character(it)) }
        )
    }

    ProfilesFull(
        list = person.characters,
        isVisible = state.dialogState is BaseDialogState.Media.Characters,
        title = stringResource(Res.string.text_characters),
        state = rememberLazyListState(),
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
        onNavigate = { onNavigate(Screen.Character(it)) }
    )

    RelatedFull(
        related = person.relatedMap,
        isVisible = state.dialogState is BaseDialogState.Media.Related,
        onNavigate = onNavigate,
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) }
    )

    DialogPoster(
        link = person.image,
        isVisible = state.dialogState is BaseDialogState.Poster,
        onClose = { onEvent(ContentDetailEvent.ToggleDialog(null)) }
    )

    if (state.dialogState is BaseDialogState.Sheet) {
        BottomSheet(
            url = person.url,
            website = person.website,
            onEvent = onEvent
        )
    }
}

@Composable
private fun PersonHeader(person: Person, onOpenPoster: () -> Unit) =
    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp), Alignment.CenterVertically) {
        Poster(
            link = person.image,
            contentScale = ContentScale.Crop,
            onOpenFullscreen = onOpenPoster,
            modifier = Modifier
                .width(130.dp)
                .aspectRatio(2f / 3f)
        )

        Column(Modifier.weight(1f), Arrangement.spacedBy(12.dp)) {
            Names(person.russian, person.english, person.japanese)

            if (person.birthday != null || person.deathday != null) {
                HorizontalDivider()
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                person.birthday?.let {
                    LabelInfoItem(stringResource(Res.string.text_birthday), it.asComposableString())
                }
                person.deathday?.let {
                    LabelInfoItem(stringResource(Res.string.text_deathday), it.asComposableString())
                }
            }
        }
    }

@Composable
fun LabelInfoItem(label: String, value: String, modifier: Modifier = Modifier) =
    Column(modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Light
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }

@Composable
private fun Roles(roles: List<List<String>>) =
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ParagraphTitle(stringResource(Res.string.text_roles))
        Column {
            roles.fastForEachIndexed { index, (name, count) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                ) {
                    Text(
                        text = name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = count,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                if (index < roles.size - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.screens

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.states.CharacterState
import org.application.shikiapp.shared.models.ui.Character
import org.application.shikiapp.shared.models.viewModels.CharacterViewModel
import org.application.shikiapp.shared.network.response.Response.Success
import org.application.shikiapp.shared.ui.templates.AnimatedScreen
import org.application.shikiapp.shared.ui.templates.BottomSheet
import org.application.shikiapp.shared.ui.templates.Comments
import org.application.shikiapp.shared.ui.templates.DialogPoster
import org.application.shikiapp.shared.ui.templates.LinkListener
import org.application.shikiapp.shared.ui.templates.Names
import org.application.shikiapp.shared.ui.templates.Poster
import org.application.shikiapp.shared.ui.templates.ProfilesFull
import org.application.shikiapp.shared.ui.templates.RelatedFull
import org.application.shikiapp.shared.ui.templates.ScaffoldContent
import org.application.shikiapp.shared.ui.templates.description
import org.application.shikiapp.shared.ui.templates.profiles
import org.application.shikiapp.shared.ui.templates.related
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.rememberCommentListState
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_character
import shikiapp.composeapp.generated.resources.text_seyu

typealias Seyu = BaseDialogState.Media.Authors

@Composable
fun CharacterScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel(::CharacterViewModel)
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    LinkListener(model.openLink) { (response as? Success)?.data?.url }

    AnimatedScreen(response, model::loadData, Character::comments) { character, comments ->
        CharacterView(character, state, model::onEvent, onNavigate, back)

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
private fun CharacterView(
    character: Character,
    state: CharacterState,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    ScaffoldContent(
        title = { Text(stringResource(Res.string.text_character)) },
        userRate = null,
        isFavoured = character.favoured,
        onBack = onBack,
        onEvent = onEvent,
        onToggleFavourite = { onEvent(ContentDetailEvent.Character.ToggleFavourite) }
    ) {
        item {
            Row(horizontalArrangement = spacedBy(16.dp)) {
                Poster(
                    link = character.poster,
                    onOpenFullscreen = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Poster)) }
                )
                Names(character.russian, character.japanese, character.altName)
            }
        }

        description(character.description)
        related(
            list = character.relatedList,
            onNavigate = onNavigate,
            onShow = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Related)) }
        )
        profiles(
            profiles = character.seyu,
            title = Res.string.text_seyu,
            onShow = { onEvent(ContentDetailEvent.ToggleDialog(Seyu)) },
            onNavigate = { onNavigate(Screen.Person(it.toLong())) }
        )
    }

    RelatedFull(
        related = character.relatedMap,
        isVisible = state.dialogState is BaseDialogState.Media.Related,
        onNavigate = onNavigate,
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) }
    )

    ProfilesFull(
        list = character.seyu,
        isVisible = state.dialogState is Seyu,
        title = stringResource(Res.string.text_seyu),
        state = rememberLazyListState(),
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
        onNavigate = { onNavigate(Screen.Person(it.toLong())) }
    )

    DialogPoster(
        link = character.poster,
        isVisible = state.dialogState is BaseDialogState.Poster,
        onClose = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
    )

    if (state.dialogState is BaseDialogState.Sheet) {
        BottomSheet(url = character.url, onEvent = onEvent)
    }
}
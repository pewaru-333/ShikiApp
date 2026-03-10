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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import org.application.shikiapp.shared.events.ContentDetailEvent
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
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_character
import shikiapp.composeapp.generated.resources.text_seyu

@Composable
fun CharacterScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel(::CharacterViewModel)
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    LinkListener(model.openLink) { (response as? Success)?.data?.url }

    AnimatedScreen(response, model::loadData) { character ->
        CharacterView(character, state, model::onEvent, onNavigate, back)
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
    val commentsState = rememberLazyListState()
    val comments = character.comments.collectAsLazyPagingItems()

    ScaffoldContent(
        title = { Text(stringResource(Res.string.text_character)) },
        userRate = null,
        isFavoured = character.favoured,
        onBack = onBack,
        onEvent = onEvent,
        onLoadState = { (comments.loadState.refresh is LoadState.Loading) to comments.itemCount },
        onToggleFavourite = { onEvent(ContentDetailEvent.Character.ToggleFavourite) }
    ) {
        item {
            Row(horizontalArrangement = spacedBy(16.dp)) {
                Poster(
                    link = character.poster,
                    onOpenFullscreen = { onEvent(ContentDetailEvent.Media.ShowPoster) }
                )
                Names(character.russian, character.japanese, character.altName)
            }
        }

        description(character.description)
        related(
            related = character.relatedList,
            onShow = { onEvent(ContentDetailEvent.Media.ShowRelated) },
            onNavigate = onNavigate
        )
        profiles(
            profiles = character.seyu,
            title = Res.string.text_seyu,
            onShow = { onEvent(ContentDetailEvent.Character.ShowSeyu) },
            onNavigate = { onNavigate(Screen.Person(it.toLong())) }
        )
    }

    Comments(
        list = comments,
        listState = commentsState,
        isVisible = state.showComments,
        onHide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    RelatedFull(
        related = character.relatedMap,
        isVisible = state.showRelated,
        onHide = { onEvent(ContentDetailEvent.Media.ShowRelated) },
        onNavigate = onNavigate
    )

    ProfilesFull(
        list = character.seyu,
        isVisible = state.showSeyu,
        title = stringResource(Res.string.text_seyu),
        state = rememberLazyListState(),
        onHide = { onEvent(ContentDetailEvent.Character.ShowSeyu) },
        onNavigate = { onNavigate(Screen.Person(it.toLong())) }
    )

    DialogPoster(
        link = character.poster,
        isVisible = state.showPoster,
        onClose = { onEvent(ContentDetailEvent.Media.ShowPoster) }
    )

    if (state.showSheet) {
        BottomSheet(url = character.url, onEvent = onEvent)
    }
}
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.screens

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.R
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.CharacterState
import org.application.shikiapp.models.ui.Character
import org.application.shikiapp.models.viewModels.CharacterViewModel
import org.application.shikiapp.network.response.Response.Success
import org.application.shikiapp.ui.templates.AnimatedScreen
import org.application.shikiapp.ui.templates.BottomSheet
import org.application.shikiapp.ui.templates.Comments
import org.application.shikiapp.ui.templates.DialogPoster
import org.application.shikiapp.ui.templates.Names
import org.application.shikiapp.ui.templates.Poster
import org.application.shikiapp.ui.templates.ProfilesFull
import org.application.shikiapp.ui.templates.RelatedFull
import org.application.shikiapp.ui.templates.ScaffoldContent
import org.application.shikiapp.ui.templates.description
import org.application.shikiapp.ui.templates.profiles
import org.application.shikiapp.ui.templates.related
import org.application.shikiapp.utils.extensions.openLinkInBrowser
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun CharacterScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val context = LocalContext.current

    val model = viewModel<CharacterViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    LaunchedEffect(model.openLink) {
        model.openLink.collectLatest {
            context.openLinkInBrowser((response as Success).data.url)
        }
    }

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
        title = { Text(stringResource(R.string.text_character)) },
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
            title = R.string.text_seyu,
            onShow = { onEvent(ContentDetailEvent.Character.ShowSeyu) },
            onNavigate = { onNavigate(Screen.Person(it.toLong())) }
        )
    }

    Comments(
        list = comments,
        listState = commentsState,
        visible = state.showComments,
        hide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    RelatedFull(
        related = character.relatedMap,
        visible = state.showRelated,
        hide = { onEvent(ContentDetailEvent.Media.ShowRelated) },
        onNavigate = onNavigate
    )

    ProfilesFull(
        list = character.seyu,
        visible = state.showSeyu,
        title = stringResource(R.string.text_seyu),
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
package org.application.shikiapp.screens

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.R.string.text_character
import org.application.shikiapp.R.string.text_seyu
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.CharacterState
import org.application.shikiapp.models.ui.Character
import org.application.shikiapp.models.viewModels.CharacterViewModel
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.network.response.Response.Success
import org.application.shikiapp.ui.templates.BottomSheet
import org.application.shikiapp.ui.templates.Comments
import org.application.shikiapp.ui.templates.Description
import org.application.shikiapp.ui.templates.ErrorScreen
import org.application.shikiapp.ui.templates.IconComment
import org.application.shikiapp.ui.templates.LoadingScreen
import org.application.shikiapp.ui.templates.Names
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.templates.Poster
import org.application.shikiapp.ui.templates.Profiles
import org.application.shikiapp.ui.templates.ProfilesFull
import org.application.shikiapp.ui.templates.Related
import org.application.shikiapp.ui.templates.RelatedFull
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

    when (val data = response) {
        is Response.Error -> ErrorScreen(model::loadData)
        is Response.Loading -> LoadingScreen()
        is Response.Success -> CharacterView(data.data, state, model::onEvent, onNavigate, back)
        else -> Unit
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CharacterView(
    character: Character,
    state: CharacterState,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    back: () -> Unit
) {
    val listState = rememberLazyListState()
    val comments = character.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_character)) },
                navigationIcon = { NavigationIcon(back) },
                actions = {
                    IconComment(
                        comments = comments,
                        onEvent = { onEvent(ContentDetailEvent.ShowComments) }
                    )
                    IconButton(
                        onClick = { onEvent(ContentDetailEvent.ShowSheet) },
                        content = { Icon(Icons.Outlined.MoreVert, null) }
                    )
                }
            )
        }
    ) { values ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
            verticalArrangement = spacedBy(16.dp)
        ) {
            item {
                Row(horizontalArrangement = spacedBy(16.dp)) {
                    Poster(character.poster)
                    Names(character.russian, character.japanese, character.altName)
                }
            }

            character.description.let {
                if (it.isNotEmpty()) {
                    item { Description(it) }
                }
            }

            character.relatedList.let {
                if (it.isNotEmpty()) {
                    item {
                        Related(
                            list = it,
                            showAllRelated = { onEvent(ContentDetailEvent.Media.ShowRelated) },
                            onNavigate = onNavigate
                        )
                    }
                }
            }

            character.seyu.let {
                if (it.isNotEmpty()) {
                    item {
                        Profiles(
                            list = it,
                            title = stringResource(text_seyu),
                            onShowFull = { onEvent(ContentDetailEvent.Character.ShowSeyu) },
                            onNavigate = { onNavigate(Screen.Person(it.toLong())) }
                        )
                    }
                }
            }
        }
    }

    Comments(
        list = comments,
        listState = listState,
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
        title = stringResource(text_seyu),
        state = rememberLazyListState(),
        onHide = { onEvent(ContentDetailEvent.Character.ShowSeyu) },
        onNavigate = { onNavigate(Screen.Person(it.toLong())) }
    )

    if (state.showSheet) {
        BottomSheet(
            sheetState = state.sheetState,
            favoured = character.favoured,
            onEvent = onEvent
        )
    }
}
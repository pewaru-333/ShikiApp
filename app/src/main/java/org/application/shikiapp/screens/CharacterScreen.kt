package org.application.shikiapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.R.string.text_anime
import org.application.shikiapp.R.string.text_character
import org.application.shikiapp.R.string.text_manga
import org.application.shikiapp.R.string.text_seyu
import org.application.shikiapp.R.string.text_show_all_m
import org.application.shikiapp.R.string.text_show_all_w
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.CharacterState
import org.application.shikiapp.models.ui.Character
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.models.viewModels.CharacterViewModel
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun CharacterScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel<CharacterViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

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
    val comments = character.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_character)) },
                navigationIcon = { NavigationIcon(back)},
                actions = {
                    if (comments.itemCount > 0)
                        IconButton(
                            onClick = {onEvent(ContentDetailEvent.ShowComments)}
                        ) {
                            Icon(painterResource(vector_comments), null)
                        }
                    if (Preferences.token != null)
                        IconButton(
                        onClick = {
                            onEvent(ContentDetailEvent.Character.ToggleFavourite(character.favoured))
                        }
                        ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (character.favoured) Color.Red else LocalContentColor.current
                        )
                    }
                }
            )
        }
    ) { values ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
            verticalArrangement = spacedBy(16.dp)
        ) {
            item {
                Row(horizontalArrangement = spacedBy(8.dp)) {
                    Poster(character.poster)
                    Names(listOf(character.russian, character.japanese, character.altName))
                }
            }

            character.description.let {
                if (it.isNotEmpty()) item { Description(it) }
            }
            character.anime.let {
                if (it.isNotEmpty()) item {
                    Catalog(
                        anime = true,
                        show = { onEvent(ContentDetailEvent.Character.ShowAnime) },
                        list = it,
                        onNavigate = onNavigate
                    )
                }
            }
            character.manga.let {
                if (it.isNotEmpty()) item {
                    Catalog(
                        anime = false,
                        show = { onEvent(ContentDetailEvent.Character.ShowManga) },
                        list = it,
                        onNavigate = onNavigate
                    )
                }
            }
            character.seyu.let {
                if (it.isNotEmpty()) item {
                    Seyu(
                        list = it,
                        hide = { onEvent(ContentDetailEvent.Character.ShowSeyu) },
                        onNavigate = onNavigate
                    )
                }
            }
        }
    }

    Comments(
        list = comments,
        visible = state.showComments,
        hide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    CatalogFull(
        list = if (state.showAnime) character.anime else character.manga,
        isAnime = state.showAnime,
        isVisible = state.showAnime || state.showManga,
        hide = { onEvent(ContentDetailEvent.Character.HideAll) },
        onNavigate = onNavigate
    )

    SeyuFull(
        list = character.seyu,
        visible = state.showSeyu,
        hide = { onEvent(ContentDetailEvent.Character.ShowSeyu) },
        onNavigate = onNavigate
    )
}

@Composable
private fun Catalog(
    show: () -> Unit,
    list: List<Content>,
    onNavigate: (Screen) -> Unit,
    anime: Boolean
) = Column(verticalArrangement = spacedBy(4.dp)) {
    Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
        ParagraphTitle(stringResource(if (anime) text_anime else text_manga), Modifier.padding(bottom = 4.dp))
        TextButton(show) { Text(stringResource(if (anime) text_show_all_w else text_show_all_m)) }
    }
    LazyRow(horizontalArrangement = spacedBy(12.dp)) {
        items(list.take(5)) {
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .clickable {
                        onNavigate(
                            if (anime) Screen.Anime(it.id.toString())
                            else Screen.Manga(it.id.toString())
                        )
                    }
            ) {
                RoundedRelatedPoster(it.poster)
                RelatedText(it.title)
            }
        }
    }
}

@Composable
private fun Seyu(list: List<Content>, hide: () -> Unit, onNavigate: (Screen) -> Unit) =
    Column(verticalArrangement = spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_seyu))
            IconButton(hide) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
        }
        LazyRow(horizontalArrangement = spacedBy(12.dp)) {
            items(list.take(3)) {
                Column(Modifier.clickable { onNavigate(Screen.Person(it.id.toLong())) }) {
                    CircleImage(it.poster)
                    TextCircleImage(it.title)
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogFull(
    list: List<Content>,
    isAnime: Boolean,
    isVisible: Boolean,
    hide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = isVisible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(onBack = hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (isAnime) text_anime else text_manga)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(contentPadding = values) {
            items(list) {
                CatalogListItem(
                    title = it.title,
                    kind = it.kind,
                    season = it.season,
                    image = it.poster,
                    click = {
                        onNavigate(
                            if (isAnime) Screen.Anime(it.id.toString())
                            else Screen.Manga(it.id.toString())
                        )
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SeyuFull(
    list: List<Content>,
    visible: Boolean,
    hide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(onBack = hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_seyu)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(contentPadding = values) {
            items(list) {
                OneLineImage(
                    name = it.title,
                    link = it.poster,
                    modifier = Modifier.clickable { onNavigate(Screen.Person(it.id.toLong())) }
                )
            }
        }
    }
}
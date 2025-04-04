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
import org.application.shikiapp.events.CharacterDetailEvent
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.data.BasicContent
import org.application.shikiapp.models.data.BasicInfo
import org.application.shikiapp.models.states.CharacterState
import org.application.shikiapp.models.ui.Character
import org.application.shikiapp.models.viewModels.CharacterViewModel
import org.application.shikiapp.network.Response
import org.application.shikiapp.utils.LINKED_TYPE
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.getImage
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
    onEvent: (CharacterDetailEvent) -> Unit,
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
                    if (Preferences.isTokenExists())
                        IconButton(
                        onClick = {
                            onEvent(CharacterDetailEvent.ToggleFavourite(character.favoured))
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
                        show = { onEvent(CharacterDetailEvent.ShowAnime) },
                        list = it,
                        onNavigate = onNavigate
                    )
                }
            }
            character.manga.let {
                if (it.isNotEmpty()) item {
                    Catalog(
                        show = { onEvent(CharacterDetailEvent.ShowManga) },
                        list = it,
                        onNavigate = onNavigate
                    )
                }
            }
            character.seyu.let {
                if (it.isNotEmpty()) item {
                    Seyu(
                        list = it,
                        hide = { onEvent(CharacterDetailEvent.ShowSeyu) },
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
        hide = { onEvent(CharacterDetailEvent.HideAll) },
        list = if (state.showAnime) character.anime else character.manga,
        visible = state.showAnime || state.showManga,
        onNavigate = onNavigate
    )

    SeyuFull(
        list = character.seyu,
        visible = state.showSeyu,
        hide = { onEvent(CharacterDetailEvent.ShowSeyu) },
        onNavigate = onNavigate
    )
}

@Composable
private fun Catalog(
    show: () -> Unit,
    list: List<BasicContent>,
    onNavigate: (Screen) -> Unit,
    anime: Boolean = list[0].url.contains(LINKED_TYPE[0], true)
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
                RoundedRelatedPoster(it.image.original)
                RelatedText(it.russian.orEmpty().ifEmpty(it::name))
            }
        }
    }
}

@Composable
private fun Seyu(list: List<BasicInfo>, hide: () -> Unit, onNavigate: (Screen) -> Unit) =
    Column(verticalArrangement = spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_seyu))
            IconButton(hide) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
        }
        LazyRow(horizontalArrangement = spacedBy(12.dp)) {
            items(list.take(3)) {
                Column(Modifier.clickable { onNavigate(Screen.Person(it.id)) }) {
                    CircleImage(it.image.original)
                    TextCircleImage(it.russian.orEmpty().ifEmpty(it::name))
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogFull(
    hide: () -> Unit,
    list: List<BasicContent>,
    visible: Boolean,
    onNavigate: (Screen) -> Unit,
    anime: Boolean = list[0].url.contains(LINKED_TYPE[0], true)
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(onBack = hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (anime) text_anime else text_manga)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(
            contentPadding = values,
            verticalArrangement = spacedBy(8.dp)
        ) {
            items(list) {
                CatalogListItem(
                    title = it.russian.orEmpty().ifEmpty(it::name),
                    kind = it.kind,
                    season = it.releasedOn,
                    image = getImage(it.image.original),
                    isBig = false,
                    click = {
                        onNavigate(
                            if (anime) Screen.Anime(it.id.toString())
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
    list: List<BasicInfo>,
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
                    name = it.russian.orEmpty().ifEmpty(it::name),
                    link = it.image.original,
                    modifier = Modifier.clickable { onNavigate(Screen.Person(it.id)) }
                )
            }
        }
    }
}
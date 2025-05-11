package org.application.shikiapp.screens

import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.R.string.text_kind
import org.application.shikiapp.R.string.text_publisher
import org.application.shikiapp.R.string.text_rate_chapters
import org.application.shikiapp.R.string.text_score
import org.application.shikiapp.R.string.text_status
import org.application.shikiapp.R.string.text_volumes
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.events.MangaDetailEvent
import org.application.shikiapp.models.states.MangaState
import org.application.shikiapp.models.ui.Manga
import org.application.shikiapp.models.viewModels.MangaViewModel
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun MangaScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel<MangaViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = response) {
        is Response.Error -> ErrorScreen(model::loadData)
        is Response.Loading -> LoadingScreen()
        is Response.Success -> MangaView(data.data, state, model::onEvent, onNavigate, back)
        else -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MangaView(
    manga: Manga,
    state: MangaState,
    onEvent: (MangaDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    back: () -> Unit
) {
    val comments = manga.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(manga.kindTitle)) },
                navigationIcon = { NavigationIcon(back) },
                actions = {
                    if (comments.itemCount > 0) IconButton(
                        onClick = { onEvent(ContentDetailEvent.ShowComments) }) {
                        Icon(painterResource(vector_comments), null)
                    }
                    IconButton(
                        onClick = { onEvent(ContentDetailEvent.ShowSheet) }) {
                        Icon(Icons.Outlined.MoreVert, null)
                    }
                }
            )
        }
    ) { values ->
        LazyColumn(
            verticalArrangement = spacedBy(16.dp),
            contentPadding = PaddingValues(
                start = 8.dp,
                top = values.calculateTopPadding(),
                end = 8.dp,
                bottom = 0.dp
            )
        ) {
            item {
                Text(
                    text = manga.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            item {
                Row(horizontalArrangement = spacedBy(8.dp)) {
                    Poster(manga.poster)
                    ShortInfo(manga)
                }
            }

            manga.genres?.let {
                item {
                    LazyRow(horizontalArrangement = spacedBy(4.dp)) {
                        items(it) { (russian) ->
                            SuggestionChip(onClick = {}, label = { Text(russian) })
                        }
                    }
                }
            }

            manga.description.let {
                if (it.isNotEmpty()) item { Description(it) }
            }
            manga.related.let {
                if (it.isNotEmpty()) item {
                    Related(
                        list = it,
                        hide = { onEvent(ContentDetailEvent.ShowRelated) },
                        onNavigate = onNavigate
                    )
                }
            }
            manga.characterMain.let {
                if (it.isNotEmpty()) item {
                    Characters(
                        list = it,
                        show = { onEvent(MangaDetailEvent.ShowCharacters) },
                        onNavigate = onNavigate
                    )
                }
            }
            manga.personMain.let {
                if (it.isNotEmpty()) item {
                    Authors(
                        list = it,
                        show = { onEvent(MangaDetailEvent.ShowAuthors) },
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

    RelatedFull(
        list = manga.related,
        visible = state.showRelated,
        hide = { onEvent(ContentDetailEvent.ShowRelated) },
        onNavigate = onNavigate
    )

    AuthorsFull(
        roles = manga.personAll,
        state = state.lazyAuthors,
        visible = state.showAuthors,
        hide = { onEvent(MangaDetailEvent.ShowAuthors) },
        onNavigate = onNavigate
    )

    CharactersFull(
        list = manga.charactersAll,
        state = state.lazyCharacters,
        visible = state.showCharacters,
        hide = { onEvent(MangaDetailEvent.ShowCharacters) },
        onNavigate = onNavigate
    )

    SimilarFull(
        hide = { onEvent(ContentDetailEvent.ShowSimilar) },
        listState = state.lazySimilar,
        visible = state.showSimilar,
        list = manga.similar,
        onNavigate = { onNavigate(Screen.Manga(it)) })

    Statistics(
        statistics = manga.stats,
        visible = state.showStats,
        hide = { onEvent(ContentDetailEvent.ShowStats) }
    )

    when {
        state.showSheet -> BottomSheet(
            state = state.sheetBottom,
            rate = manga.userRate,
            favoured = manga.favoured,
            onEvent = onEvent as (ContentDetailEvent) -> Unit,
            toggleFavourite = { onEvent(MangaDetailEvent.ToggleFavourite(manga.kindEnum, manga.favoured)) }
        )

        state.showRate -> CreateRate(
            id = manga.id,
            type = LinkedType.MANGA,
            rateF = manga.userRate,
            reload = { onEvent(MangaDetailEvent.Reload) },
            hide = { onEvent(MangaDetailEvent.ShowRate) })

        state.showLinks -> LinksSheet(
            list = manga.links,
            state = state.sheetLinks,
            hide = { onEvent(ContentDetailEvent.ShowLinks) })
    }
}

@Composable
private fun ShortInfo(manga: Manga) {
    val name = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)
    val info = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)

    Column(Modifier.height(300.dp), SpaceBetween) {
        Column {
            Text(stringResource(text_kind), style = name)
            Text(stringResource(manga.kindString), style = info)
        }
        if (manga.showChapters) {
            Column {
                Text(stringResource(text_volumes), style = name)
                Text(manga.volumes, style = info)
            }
            Column {
                Text(stringResource(text_rate_chapters), style = name)
                Text(manga.chapters, style = info)
            }
        }
        Column {
            Text(stringResource(text_status), style = name)
            Text(stringResource(manga.status), style = info)
        }
        Column {
            Text(stringResource(text_publisher), style = name)
            Text(manga.publisher, style = info)
        }
        Column {
            Text(stringResource(text_score), style = name)
            Row(horizontalArrangement = spacedBy(4.dp), verticalAlignment = CenterVertically) {
                Icon(Icons.Default.Star, null, Modifier.size(16.dp), Color(0xFFFFC319))
                Text(manga.score, style = info)
            }
        }
    }
}
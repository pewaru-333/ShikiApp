package org.application.shikiapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import org.application.shikiapp.events.CalendarEvent
import org.application.shikiapp.models.states.AnimeCalendarState
import org.application.shikiapp.models.ui.AnimeCalendar
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.models.ui.list.ShortContent
import org.application.shikiapp.models.viewModels.CalendarViewModel
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun CalendarScreen(onNavigate: (Screen) -> Unit) {
    val model = viewModel<CalendarViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = response) {
        is Response.Error -> ErrorScreen(model::loadData)
        is Response.Loading -> LoadingScreen()
        is Response.Success -> CalendarView(data.data, state, model::onEvent, onNavigate)
        else -> Unit
    }
}

@Composable
private fun CalendarView(
    animeCalendar: AnimeCalendar,
    state: AnimeCalendarState,
    onEvent: (CalendarEvent) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val topics = animeCalendar.updates.collectAsLazyPagingItems()

    Scaffold { values ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Trending(animeCalendar.trending, onNavigate)
            }

            item {
                AnimeUpdates(topics, onEvent, onNavigate)
            }
        }
    }

    AnimeUpdatesFull(
        updates = topics,
        visible = state.showFullUpdates,
        onNavigate = onNavigate,
        hide = { onEvent(CalendarEvent.ShowFullUpdates) }
    )
}

@Composable
private fun Trending(trending: List<ShortContent>, onNavigate: (Screen) -> Unit) = Column {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        ParagraphTitle("Сейчас на экранах")
        IconButton(
            onClick = { onNavigate(Screen.Catalog(true)) }
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, null)
        }
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(trending) { anime ->
            Column(
                modifier = Modifier
                    .width(129.dp)
                    .clickable { onNavigate(Screen.Anime(anime.id)) }
            ) {
                AsyncImage(
                    model = anime.poster,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(181.dp)
                        .border(0.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                )
                Text(
                    text = anime.title,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
private fun AnimeUpdates(
    updates: LazyPagingItems<Content>,
    onEvent: (CalendarEvent) -> Unit,
    onNavigate: (Screen) -> Unit
) = Column {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        ParagraphTitle("Обновления аниме")
        IconButton(
            onClick = { onEvent(CalendarEvent.ShowFullUpdates) }
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, null)
        }
    }

    if (updates.loadState.refresh is LoadState.Loading) LoadingScreen()
    else LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(12) { index ->
            updates.takeIf { it.itemCount > index }?.get(index)?.let { anime ->
                Column(
                    modifier = Modifier
                        .width(129.dp)
                        .clickable { onNavigate(Screen.Anime(anime.id)) }
                ) {
                    AsyncImage(
                        model = anime.poster,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(181.dp)
                            .border(0.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Text(
                        text = anime.title,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeUpdatesFull(
    updates: LazyPagingItems<Content>,
    visible: Boolean,
    onNavigate: (Screen) -> Unit,
    hide: () -> Unit,
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(onBack = hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Обновления") },
                navigationIcon = {
                    IconButton(hide) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                }
            )
        }
    ) { values ->
        if (updates.loadState.refresh is LoadState.Loading) LoadingScreen()
        else LazyColumn(contentPadding = values) {
            items(updates.itemCount) { index ->
                updates[index]?.let { anime ->
                    CatalogListItem(
                        title = anime.title,
                        kind = anime.kind,
                        season = anime.season,
                        image = anime.poster,
                        click = { onNavigate(Screen.Anime(anime.id)) }
                    )
                }
            }
        }
    }
}

//@Composable
//private fun Schedule(data: Success, onNavigate: (Screen) -> Unit) =
//    LazyColumn(
//        contentPadding = PaddingValues(8.dp, 16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        items(data.calendar) { (date, list) ->
//            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                ParagraphTitle(date)
//                LazyRow(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    items(list) { (_, _, _, anime) ->
//                        Column(
//                            modifier = Modifier
//                                .width(122.dp)
//                                .clickable { onNavigate(Screen.Anime(anime.id.toString())) },
//                        ) {
//                            RoundedRelatedPoster(anime.image.original, ContentScale.FillBounds)
//                            Text(
//                                text = anime.russian?.ifEmpty(anime::name) ?: BLANK,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                                textAlign = TextAlign.Center,
//                                maxLines = 3,
//                                minLines = 3,
//                                overflow = TextOverflow.Ellipsis,
//                                style = MaterialTheme.typography.titleSmall,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(4.dp)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
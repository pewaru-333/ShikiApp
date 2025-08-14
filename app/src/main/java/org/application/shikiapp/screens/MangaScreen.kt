package org.application.shikiapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.R
import org.application.shikiapp.R.string.text_kind
import org.application.shikiapp.R.string.text_publisher
import org.application.shikiapp.R.string.text_rate_chapters
import org.application.shikiapp.R.string.text_volumes
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.MangaState
import org.application.shikiapp.models.ui.Manga
import org.application.shikiapp.models.viewModels.MangaViewModel
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.network.response.Response.Success
import org.application.shikiapp.ui.templates.BottomSheet
import org.application.shikiapp.ui.templates.Comments
import org.application.shikiapp.ui.templates.CreateRate
import org.application.shikiapp.ui.templates.Description
import org.application.shikiapp.ui.templates.ErrorScreen
import org.application.shikiapp.ui.templates.IconComment
import org.application.shikiapp.ui.templates.LinksSheet
import org.application.shikiapp.ui.templates.LoadingScreen
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.templates.Poster
import org.application.shikiapp.ui.templates.Profiles
import org.application.shikiapp.ui.templates.ProfilesFull
import org.application.shikiapp.ui.templates.Related
import org.application.shikiapp.ui.templates.RelatedFull
import org.application.shikiapp.ui.templates.ScoreInfo
import org.application.shikiapp.ui.templates.SimilarFull
import org.application.shikiapp.ui.templates.Statistics
import org.application.shikiapp.ui.templates.StatusInfo
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.extensions.openLinkInBrowser
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun MangaScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val context = LocalContext.current

    val model = viewModel<MangaViewModel>()
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
        is Response.Success -> MangaView(data.data, state, model::onEvent, onNavigate, back)
        else -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MangaView(
    manga: Manga,
    state: MangaState,
    onEvent: (ContentDetailEvent) -> Unit,
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
                    Column(Modifier.height(300.dp), Arrangement.SpaceBetween) {
                        LabelInfoItem(stringResource(text_kind), stringResource(manga.kindString))
                        StatusInfo(manga.status, manga.airedOn, manga.releasedOn)
                        manga.publisher?.let { LabelInfoItem(stringResource(text_publisher), it.title) }

                        if (!manga.isOngoing) {
                            LabelInfoItem(stringResource(text_volumes), manga.volumes)
                            LabelInfoItem(stringResource(text_rate_chapters), manga.chapters)
                        }

                        ScoreInfo(manga.score)
                    }
                }
            }

            manga.genres?.let {
                item {
                    LazyRow(horizontalArrangement = spacedBy(4.dp)) {
                        items(it) {
                            SuggestionChip(onClick = {}, label = { Text(it) })
                        }
                    }
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    manga.publisher?.let { publisher ->
                        item {
                            DetailBox(
                                icon = R.drawable.vector_anime,
                                label = "Издательство",
                                value = publisher.title,
                                onClick = {
                                    onNavigate(
                                        Screen.Catalog(
                                            publisher = publisher.id,
                                            linkedType = manga.kindEnum.linkedType
                                        )
                                    )
                                }
                            )
                        }
                    }

                    manga.similar.let {
                        if (it.isNotEmpty()) {
                            item {
                                DetailBox(
                                    icon = R.drawable.vector_similar,
                                    label = "Похожее",
                                    onClick = { onEvent(ContentDetailEvent.Media.ShowSimilar) }
                                )
                            }
                        }
                    }

                    item {
                        DetailBox(
                            icon = R.drawable.vector_statistics,
                            label = stringResource(R.string.text_statistics),
                            onClick = { onEvent(ContentDetailEvent.Media.ShowStats) }
                        )
                    }
                }
            }

            manga.description.let {
                if (it.isNotEmpty()) {
                    item { Description(it) }
                }
            }

            manga.related.let {
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
            manga.charactersMain.let {
                if (it.isNotEmpty()) {
                    item {
                        Profiles(
                            list = it,
                            title = stringResource(R.string.text_characters),
                            onShowFull = { onEvent(ContentDetailEvent.Media.ShowCharacters) },
                            onNavigate = { onNavigate(Screen.Character(it)) }
                        )
                    }
                }
            }
            manga.personMain.let {
                if (it.isNotEmpty()) {
                    item {
                        Profiles(
                            list = it,
                            title = stringResource(R.string.text_authors),
                            onShowFull = { onEvent(ContentDetailEvent.Media.ShowAuthors) },
                            onNavigate = { onNavigate(Screen.Person(it.toLong())) }
                        )
                    }
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
        related = manga.related,
        chronology = manga.chronology,
        franchise = manga.franchiseList,
        visible = state.showRelated,
        hide = { onEvent(ContentDetailEvent.Media.ShowRelated) },
        onNavigate = onNavigate
    )

    ProfilesFull(
        list = if (state.showCharacters) manga.charactersAll else manga.personAll,
        visible = state.showCharacters || state.showAuthors,
        title = stringResource(if (state.showCharacters) R.string.text_characters else R.string.text_authors),
        state = if (state.showCharacters) state.lazyCharacters else state.lazyAuthors,
        onHide = {
            onEvent(
                if (state.showCharacters) ContentDetailEvent.Media.ShowCharacters
                else ContentDetailEvent.Media.ShowAuthors
            )
        },
        onNavigate = {
            onNavigate(
                if (state.showCharacters) Screen.Character(it)
                else Screen.Person(it.toLong())
            )
        }
    )

    SimilarFull(
        hide = { onEvent(ContentDetailEvent.Media.ShowSimilar) },
        listState = state.lazySimilar,
        visible = state.showSimilar,
        list = manga.similar,
        onNavigate = { onNavigate(Screen.Manga(it)) }
    )

    Statistics(
        statistics = manga.stats,
        visible = state.showStats,
        hide = { onEvent(ContentDetailEvent.Media.ShowStats) }
    )

    when {
        state.showSheet -> BottomSheet(
            state = state.sheetBottom,
            rate = manga.userRate,
            favoured = manga.favoured,
            onEvent = onEvent,
            toggleFavourite = {
                onEvent(
                    ContentDetailEvent.Media.Manga.ToggleFavourite(
                        manga.kindEnum,
                        manga.favoured
                    )
                )
            }
        )

        state.showRate -> CreateRate(
            id = manga.id,
            type = LinkedType.MANGA,
            rateF = manga.userRate,
            reload = { onEvent(ContentDetailEvent.Media.Reload) },
            hide = { onEvent(ContentDetailEvent.Media.ShowRate) })

        state.showLinks -> LinksSheet(
            list = manga.links,
            state = state.sheetLinks,
            hide = { onEvent(ContentDetailEvent.Media.ShowLinks) })
    }
}
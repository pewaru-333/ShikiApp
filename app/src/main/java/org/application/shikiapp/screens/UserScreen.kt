package org.application.shikiapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AnimeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.CharacterScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ClubScreenDestination
import com.ramcosta.composedestinations.generated.destinations.PersonScreenDestination
import com.ramcosta.composedestinations.generated.destinations.UserScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.Direction
import org.application.shikiapp.R.string.text_empty
import org.application.shikiapp.R.string.text_favourite
import org.application.shikiapp.R.string.text_history
import org.application.shikiapp.R.string.text_profile
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.HistoryAnime
import org.application.shikiapp.models.data.UserShort
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.UserState
import org.application.shikiapp.models.views.UserViewModel
import org.application.shikiapp.models.views.UserViewModel.Response.Error
import org.application.shikiapp.models.views.UserViewModel.Response.Loading
import org.application.shikiapp.models.views.UserViewModel.Response.Success
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.FAVOURITES_ITEMS
import org.application.shikiapp.utils.ProfileMenus
import org.application.shikiapp.utils.getImage

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun UserScreen(userId: Long, navigator: DestinationsNavigator) {
    val model = viewModel<UserViewModel>(factory = factory { UserViewModel(userId) })
    val response by model.response.collectAsStateWithLifecycle()

    when (val data = response) {
        Error -> ErrorScreen()
        Loading -> LoadingScreen()
        is Success -> {
            val user = data.user
            val state by model.state.collectAsStateWithLifecycle()
            val comments = viewModel<CommentViewModel>(factory = factory {
                CommentViewModel(user.id)
            }).comments.collectAsLazyPagingItems()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = { NavigationIcon(navigator::popBackStack) },
                        actions = {
                            IconButton(model::showSheet) { Icon(Icons.Outlined.MoreVert, null) }
                        }
                    )
                }
            ) { values ->
                LazyColumn(
                    contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
                    verticalArrangement = spacedBy(16.dp)
                ) {
                    item { UserBriefItem(user) }
                    item { BriefInfo(model) }
                    item {
                        user.stats.let { stats ->
                            if (stats.statuses.anime.sumOf { it.size } > 0)
                                AnimeStats(userId, stats.statuses.anime, navigator)
                        }
                    }

                    if (comments.itemCount > 0) comments(comments, navigator)
                }
            }

            when {
                state.showDialog -> {
                    val friends = model.friends.collectAsLazyPagingItems()
                    DialogItem(model, state, friends, data.clubs, navigator)
                }

                state.showFavourite -> DialogFavourites(model, state, data.favourites, navigator)
                state.showHistory -> {
                    val history = model.history.collectAsLazyPagingItems()
                    DialogHistory(model, history, navigator)
                }

                state.showSheet -> BottomSheet(model, state)
            }
        }
    }
}

@Composable
private fun BriefInfo(model: UserViewModel) {
    ParagraphTitle(stringResource(text_profile), Modifier.padding(bottom = 4.dp))
    Row(Modifier.fillMaxWidth(), spacedBy(8.dp)) {
        ProfileMenus.entries.forEach { entry ->
            ElevatedCard(
                onClick = { model.setMenu(entry.ordinal) },
                modifier = Modifier
                    .height(64.dp)
                    .weight(1f),
                enabled = entry.ordinal != 2
            ) {
                Row(Modifier.fillMaxSize(), Arrangement.Center, CenterVertically) {
                    Text(stringResource(entry.title), style = MaterialTheme.typography.titleSmall)
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogItem(
    model: UserViewModel,
    state: UserState,
    friends: LazyPagingItems<UserShort>,
    clubs: List<Club>,
    navigator: DestinationsNavigator
) = Dialog(model::close, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(model.getTitle())) },
                navigationIcon = { NavigationIcon(model::close) }
            )
        }
    ) { values ->
        LazyColumn(
            state = when (state.menu) {
                0 -> state.stateF
                1 -> state.stateC
                else -> rememberLazyListState()
            },
            contentPadding = PaddingValues(top = values.calculateTopPadding())
        ) {
            when (state.menu) {
                0 -> friends(friends, navigator)
                1 -> clubs(clubs, navigator)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogFavourites(
    model: UserViewModel,
    state: UserState,
    favourites: Favourites,
    navigator: DestinationsNavigator
) = Dialog(model::hideFavourite, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_favourite)) },
                navigationIcon = { NavigationIcon(model::hideFavourite) }
            )
        }
    ) { values ->
        Column(Modifier.padding(top = values.calculateTopPadding()), spacedBy(8.dp)) {
            ScrollableTabRow(state.tab, edgePadding = 8.dp) {
                FAVOURITES_ITEMS.forEachIndexed { index, title ->
                    Tab(state.tab == index, { model.setTab(index) }) {
                        Text(stringResource(title), Modifier.padding(8.dp, 12.dp))
                    }
                }
            }
            LazyColumn {
                items(
                    when (state.tab) {
                        0 -> favourites.animes
                        1 -> favourites.mangas
                        2 -> favourites.ranobe
                        3 -> favourites.characters
                        4 -> favourites.people
                        5 -> favourites.mangakas
                        6 -> favourites.seyu
                        else -> favourites.producers
                    }
                ) { (id, name, russian, image) ->
                    OneLineImage(
                        name = russian.ifEmpty { name },
                        link = image,
                        modifier = Modifier.clickable(enabled = state.tab !in listOf(1, 2)) {
                            navigator.navigate(
                                when (state.tab) {
                                    0 -> AnimeScreenDestination(id.toString())
                                    1 -> Direction(BLANK)
                                    2 -> Direction(BLANK)
                                    3 -> CharacterScreenDestination(id.toString())
                                    else -> PersonScreenDestination(id)
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogHistory(
    model: UserViewModel,
    history: LazyPagingItems<HistoryAnime>,
    navigator: DestinationsNavigator
) = Dialog(model::hideHistory, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_history)) },
                navigationIcon = { NavigationIcon(model::hideHistory) }
            )
        }
    ) { values ->
        LazyColumn(contentPadding = PaddingValues(top = values.calculateTopPadding())) {
            items(history.itemCount) { index ->
                history[index]?.let { HistoryItem(it, navigator) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheet(model: UserViewModel, state: UserState) {
    ModalBottomSheet(model::hideSheet, sheetState = state.bottomState) {
        ListItem(
            headlineContent = { Text(stringResource(text_favourite)) },
            modifier = Modifier.clickable(onClick = model::showFavourite),
            leadingContent = { Icon(Icons.Outlined.FavoriteBorder, null) }
        )
        ListItem(
            headlineContent = { Text(stringResource(text_history)) },
            modifier = Modifier.clickable(onClick = model::showHistory),
            leadingContent = { Icon(Icons.AutoMirrored.Outlined.List, null) }
        )
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}

// ========================================== Extensions ==========================================

fun LazyListScope.friends(list: LazyPagingItems<UserShort>, navigator: DestinationsNavigator) {
    when (list.loadState.refresh) {
        is LoadState.Error -> item { ErrorScreen(list::retry) }
        is LoadState.Loading -> item { LoadingScreen() }
        is LoadState.NotLoading -> {
            items(list.itemCount) { index ->
                list[index]?.let { (id, nickname, _, image) ->
                    OneLineImage(
                        name = nickname,
                        link = image.x160,
                        modifier = Modifier.clickable { navigator.navigate(UserScreenDestination(id)) }
                    )
                }
            }
            if (list.loadState.append == LoadState.Loading) item { LoadingScreen() }
            if (list.loadState.hasError) item { ErrorScreen(list::retry) }
        }
    }
}

fun LazyListScope.clubs(list: List<Club>, navigator: DestinationsNavigator) =
    if (list.isEmpty()) item { Box(Modifier.fillMaxSize(), Center) { Text(stringResource(text_empty)) } }
    else items(list) { (id, name, logo) ->
        OneLineImage(
            name = name,
            link = getImage(logo.original),
            modifier = Modifier.clickable { navigator.navigate(ClubScreenDestination(id)) }
        )
    }
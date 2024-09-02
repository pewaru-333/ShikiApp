package org.application.shikiapp.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AnimeRatesScreenDestination
import com.ramcosta.composedestinations.generated.destinations.AnimeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.CharacterScreenDestination
import com.ramcosta.composedestinations.generated.destinations.PersonScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.Direction
import org.application.shikiapp.R.string.text_anime_list
import org.application.shikiapp.R.string.text_favourite
import org.application.shikiapp.R.string.text_history
import org.application.shikiapp.R.string.text_login
import org.application.shikiapp.R.string.text_profile
import org.application.shikiapp.R.string.text_show_all_s
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.HistoryAnime
import org.application.shikiapp.models.data.ShortInfo
import org.application.shikiapp.models.data.UserShort
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.ProfileState
import org.application.shikiapp.models.views.ProfileViewModel
import org.application.shikiapp.models.views.ProfileViewModel.LoginState.Logged
import org.application.shikiapp.models.views.ProfileViewModel.LoginState.Logging
import org.application.shikiapp.models.views.ProfileViewModel.LoginState.NotLogged
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.network.AUTH_URL
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.CLIENT_ID
import org.application.shikiapp.utils.CODE
import org.application.shikiapp.utils.FAVOURITES_ITEMS
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.ProfileMenus
import org.application.shikiapp.utils.REDIRECT_URI
import org.application.shikiapp.utils.getWatchStatus

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun ProfileScreen(navigator: DestinationsNavigator, context: Context = LocalContext.current) {
    val model = viewModel<ProfileViewModel>()
    val loginState by model.login.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            val code = (context as Activity).intent.data?.getQueryParameter(CODE)
            if (code != null && Preferences.getUserId() == 0L) model.login(code)
        }
    }

    when (val data = loginState) {
        NotLogged -> LoginPage()
        Logging -> LoadingScreen()
        is Logged -> {
            val user = data.user
            val state by model.state.collectAsStateWithLifecycle()
            val comments = viewModel<CommentViewModel>(factory = factory {
                CommentViewModel(user.id)
            }).comments.collectAsLazyPagingItems()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(model::signOut) { Icon(Icons.AutoMirrored.Outlined.ExitToApp, null) }
                        },
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
                                AnimeStats(user.id, stats.statuses.anime, navigator)
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
private fun LoginPage(context: Context = LocalContext.current) {
    val uri = Uri.parse(AUTH_URL)
        .buildUpon()
        .appendQueryParameter("client_id", CLIENT_ID)
        .appendQueryParameter("redirect_uri", REDIRECT_URI)
        .appendQueryParameter("response_type", CODE)
        .appendQueryParameter("scope", BLANK)
        .build()

    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Button({ (context.startActivity(Intent(Intent.ACTION_VIEW, uri))) }) { Text(stringResource(text_login)) }
    }
}

@Composable
private fun BriefInfo(model: ProfileViewModel) {
    ParagraphTitle(stringResource(text_profile), Modifier.padding(bottom = 4.dp))
    Row(Modifier.fillMaxWidth(), spacedBy(8.dp)) {
        ProfileMenus.entries.forEach { entry ->
            ElevatedCard(
                onClick = { model.setMenu(entry.ordinal) },
                enabled = entry.ordinal != 2,
                modifier = Modifier
                    .height(64.dp)
                    .weight(1f)
            ) {
                Row(Modifier.fillMaxSize(), Arrangement.Center, CenterVertically) {
                    Text(stringResource(entry.title), style = MaterialTheme.typography.titleSmall)
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null)
                }
            }
        }
    }
}

@Composable
fun AnimeStats(id: Long, stats: List<ShortInfo>, navigator: DestinationsNavigator) {
    val sum = stats.sumOf { it.size }.takeIf { it != 0L } ?: 1

    Column(verticalArrangement = spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_anime_list))
            TextButton({ navigator.navigate(AnimeRatesScreenDestination(id)) })
            { Text(stringResource(text_show_all_s)) }
        }
        stats.filter { it.size > 0 }.forEach { (_, _, name, size) ->
            Row(Modifier.fillMaxWidth(), SpaceBetween) {
                Column(Modifier.fillMaxWidth(0.625f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(size.toFloat() / sum + 0.15f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = CenterEnd
                    ) {
                        Text(
                            text = size.toString(),
                            modifier = Modifier.padding(end = 4.dp),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                Text(
                    text = getWatchStatus(name),
                    modifier = Modifier.padding(end = 4.dp),
                    overflow = TextOverflow.Visible,
                    maxLines = 1
                )
            }
        }
    }
}

// =========================================== Dialogs ===========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogItem(
    model: ProfileViewModel,
    state: ProfileState,
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
    model: ProfileViewModel,
    state: ProfileState,
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
    model: ProfileViewModel,
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
private fun BottomSheet(model: ProfileViewModel, state: ProfileState) {
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
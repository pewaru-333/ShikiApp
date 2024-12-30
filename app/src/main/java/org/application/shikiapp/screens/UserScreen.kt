package org.application.shikiapp.screens

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import org.application.shikiapp.models.views.UserViewModel
import org.application.shikiapp.models.views.UserViewModel.Response.Error
import org.application.shikiapp.models.views.UserViewModel.Response.Loading
import org.application.shikiapp.models.views.UserViewModel.Response.Success

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    toAnime: (String) -> Unit,
    toManga: (String) -> Unit,
    toCharacter: (String) -> Unit,
    toPerson: (Long) -> Unit,
    toUser: (Long) -> Unit,
    toClub: (Long) -> Unit,
    back: () -> Unit
) {
    val model = viewModel<UserViewModel>()
    val response by model.response.collectAsStateWithLifecycle()

    when (val data = response) {
        Error -> ErrorScreen()
        Loading -> LoadingScreen()
        is Success -> {
            val user = data.user
            val state by model.state.collectAsStateWithLifecycle()
            val comments = data.comments.collectAsLazyPagingItems()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = { NavigationIcon(back) },
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
                    item { BriefInfo(model::setMenu) }
                    item { UserStats(user.stats, user.id, toAnime, toManga) }

                    if (comments.itemCount > 0) comments(comments, toUser)
                }
            }

            when {
                state.showDialog -> {
                    val friends = model.friends.collectAsLazyPagingItems()
                    DialogItem(model, state, friends, data.clubs, toUser, toClub)
                }

                state.showFavourite -> DialogFavourites(
                    hide = model::hideFavourite,
                    setTab = model::setTab,
                    tab = state.tab,
                    favourites = data.favourites,
                    toAnime = toAnime,
                    toManga = toManga,
                    toCharacter = toCharacter,
                    toPerson = toPerson
                )

                state.showHistory -> {
                    val history = model.history.collectAsLazyPagingItems()
                    DialogHistory(model::hideHistory, history, toAnime, toManga)
                }

                state.showSheet -> BottomSheet(
                    model::hideSheet, model::showFavourite, model::showHistory, state.sheetState
                )
            }
        }
    }
}
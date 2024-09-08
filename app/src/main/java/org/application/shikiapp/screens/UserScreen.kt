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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.UserViewModel
import org.application.shikiapp.models.views.UserViewModel.Response.Error
import org.application.shikiapp.models.views.UserViewModel.Response.Loading
import org.application.shikiapp.models.views.UserViewModel.Response.Success
import org.application.shikiapp.models.views.factory

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
                    item { BriefInfo(model::setMenu) }
                    item { UserStats(user.stats, user.id, navigator) }

                    if (comments.itemCount > 0) comments(comments, navigator)
                }
            }

            when {
                state.showDialog -> {
                    val friends = model.friends.collectAsLazyPagingItems()
                    DialogItem(model, state, friends, data.clubs, navigator)
                }

                state.showFavourite -> DialogFavourites(
                    model::hideFavourite, model::setTab, state.tab, data.favourites, navigator
                )
                state.showHistory -> {
                    val history = model.history.collectAsLazyPagingItems()
                    DialogHistory(model::hideHistory, history, navigator)
                }

                state.showSheet -> BottomSheet(
                    model::hideSheet, model::showFavourite, model::showHistory, state.sheetState
                )
            }
        }
    }
}
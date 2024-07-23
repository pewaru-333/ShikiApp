package org.application.shikiapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ClubScreenDestination
import com.ramcosta.composedestinations.generated.destinations.UserScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.R
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.User
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.LoadingState
import org.application.shikiapp.models.views.UserViewModel
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.utils.UserMenus
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getSex
import org.application.shikiapp.models.data.User as Friend

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun UserScreen(userId: Long, navigator: DestinationsNavigator) {
    val viewModel = viewModel<UserViewModel>(factory = factory { UserViewModel(userId) })
    val response by viewModel.response.collectAsStateWithLifecycle()

    when (val data = response) {
        LoadingState.Error -> ErrorScreen()
        LoadingState.Loading -> LoadingScreen()
        is LoadingState.Success -> {
            val user = data.user
            val comments = viewModel<CommentViewModel>(factory = factory {
                CommentViewModel(user.id)
            }).comments.collectAsLazyPagingItems()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = { NavigationIcon(navigator::popBackStack) }
                    )
                }
            ) { values ->
                LazyColumn(
                    contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
                    verticalArrangement = spacedBy(16.dp)
                ) {
                    item {
                        ListItem(
                            headlineContent = {},
                            modifier = Modifier.offset((-16).dp, (-8).dp),
                            overlineContent = {
                                Text(
                                    text = user.nickname,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            leadingContent = {
                                AsyncImage(
                                    model = user.avatar,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, Color.Gray)
                                )
                            },
                            supportingContent = { Text("${user.lastOnline}") }
                        )
                    }

                    item { BriefInfo(viewModel, user, data.clubs, navigator) }
                    item {
                        user.stats?.let { stats ->
                            if (stats.statuses.anime.sumOf { it.size } > 0)
                                AnimeStats(userId, stats.statuses.anime, navigator)
                        }
                    }

                    if (comments.itemCount > 0) comments(comments, navigator)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BriefInfo(viewModel: UserViewModel, user: User, clubs: List<Club> ,navigator: DestinationsNavigator) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ParagraphTitle(stringResource(R.string.text_profile), Modifier.padding(bottom = 4.dp))
    Column(verticalArrangement = spacedBy(8.dp)) {
        UserMenus.entries.forEach { entry ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp), spacedBy(16.dp)) {
                ElevatedCard(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()) {
                    TitleText(stringResource(entry.row[0]))
                    LabelText(when (entry.ordinal) {
                        0 -> user.name ?: "Неизвестно"
                        1 -> getSex(user.sex)
                        else -> user.fullYears?.let { pluralStringResource(R.plurals.age, it, it) } ?: "Неизвестно"
                    })
                }
                ElevatedCard(
                    onClick = { viewModel.setMenu(entry.ordinal) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    enabled = entry.ordinal != 2
                ) {
                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = CenterVertically) {
                        TitleText(stringResource(entry.row[1]))
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                    }
                }
            }
        }
    }

    if (state.show) { val friends = viewModel.friends.collectAsLazyPagingItems()
        Dialog(viewModel::close, DialogProperties(usePlatformDefaultWidth = false)) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(viewModel.getTitle())) },
                        navigationIcon = { NavigationIcon(viewModel::close) }
                    )
                }
            ) { values ->
                LazyColumn(contentPadding = PaddingValues(top = values.calculateTopPadding())) {
                    when (state.menu) {
                        0 -> friends(friends, navigator)
                        1 -> clubs(clubs, navigator)
                    }
                }
            }
        }
    }
}

fun LazyListScope.friends(list: LazyPagingItems<Friend>, navigator: DestinationsNavigator) {
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
    if (list.isEmpty()) item { Box(Modifier.fillMaxSize(), Center) { Text("Пусто") } }
    else items(list) { (id, name, logo) ->
        OneLineImage(
            name = name,
            link = getImage(logo.original),
            modifier = Modifier.clickable { navigator.navigate(ClubScreenDestination(id)) }
        )
    }

@Composable
fun TitleText(text: String) = Text(
    text = text,
    modifier = Modifier.padding(4.dp),
    style = MaterialTheme.typography.titleMedium
)

@Composable
fun LabelText(text: String) = Text(
    text = text,
    modifier = Modifier.padding(4.dp),
    style = MaterialTheme.typography.labelLarge
)
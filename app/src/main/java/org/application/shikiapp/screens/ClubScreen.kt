package org.application.shikiapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
import com.ramcosta.composedestinations.generated.destinations.AnimeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.CharacterScreenDestination
import com.ramcosta.composedestinations.generated.destinations.UserScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.R
import org.application.shikiapp.models.data.AnimeShort
import org.application.shikiapp.models.data.Character
import org.application.shikiapp.models.data.ClubImages
import org.application.shikiapp.models.data.User
import org.application.shikiapp.models.views.ClubState
import org.application.shikiapp.models.views.ClubViewModel
import org.application.shikiapp.models.views.Menus
import org.application.shikiapp.models.views.Response
import org.application.shikiapp.models.views.UIEvent
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.getImage

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun ClubScreen(clubId: Long, navigator: DestinationsNavigator) {
    val model = viewModel<ClubViewModel>(factory = factory { ClubViewModel(clubId) })
    val response by model.response.collectAsStateWithLifecycle()

    when (val data = response) {
        Response.Error -> ErrorScreen()
        Response.Loading -> LoadingScreen()
        is Response.Success -> {
            val club = data.club
            val state by model.state.collectAsStateWithLifecycle()
            val comments = model.comments.collectAsLazyPagingItems()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = { NavigationIcon(navigator::popBackStack) }
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    contentPadding = PaddingValues(8.dp, paddingValues.calculateTopPadding()),
                    verticalArrangement = spacedBy(16.dp)
                ) {
                    item {
                        ListItem(
                            headlineContent = {},
                            overlineContent = {
                                Text(
                                    text = club.name,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            leadingContent = {
                                AsyncImage(
                                    model = getImage(club.logo.original),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .border(1.dp, Color.Gray)
                                )
                            }
                        )
                    }

                    item { BriefInfo(model, state, navigator) }
                    item { Description(club.descriptionHtml) }

                    if (comments.itemCount > 0) comments(comments, navigator)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun BriefInfo(model: ClubViewModel, state: ClubState, navigator: DestinationsNavigator) {
    ParagraphTitle(stringResource(R.string.text_information), Modifier.padding(bottom = 4.dp))
    FlowRow(Modifier, spacedBy(16.dp), spacedBy(8.dp), 2) {
        Menus.entries.forEach { menu ->
            ElevatedCard(
                onClick = {
                    model.onEvent(UIEvent.SetShow(true))
                    model.onEvent(UIEvent.SetMenu(menu.ordinal))
                },
                modifier = Modifier
                    .weight(0.5f)
                    .height(64.dp),
                enabled = menu.ordinal !in listOf(0, 3)
            ) {
                Row(modifier = Modifier.fillMaxSize(), verticalAlignment = CenterVertically) {
                    Text(
                        text = menu.title,
                        modifier = Modifier.padding(4.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null)
                }
            }
        }
    }

    if (state.show)
        Dialog(
            onDismissRequest = { model.onEvent(UIEvent.SetShow(false)) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(model.getTitle()) },
                        navigationIcon = { NavigationIcon { model.onEvent(UIEvent.SetShow(false)) } }
                    )
                }
            ) { paddingValues ->
                val padding = paddingValues.calculateTopPadding()
                    when (state.menu) {
                        1 -> model.anime.collectAsLazyPagingItems().also {
                            Anime(it, navigator, padding)
                        }

                        2 -> model.members.collectAsLazyPagingItems().also {
                            Members(it, navigator, padding)
                        }

                        4 -> model.characters.collectAsLazyPagingItems().also {
                            Characters(it, navigator, padding)
                        }

                        5 -> model.images.collectAsLazyPagingItems().also {
                            Images(it, padding)
                        }
                    }
            }
        }
}

@Composable
private fun Anime(anime: LazyPagingItems<AnimeShort>, navigator: DestinationsNavigator, padding: Dp) {
    LazyColumn(contentPadding = PaddingValues(top = padding)) {
        when (anime.loadState.refresh) {
            is LoadState.Error -> item { ErrorScreen() }
            is LoadState.Loading -> item { LoadingScreen() }
            is LoadState.NotLoading -> {
               items(anime.itemCount) {
                    anime[it]?.let { anime ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = anime.russian ?: anime.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            },
                            modifier = Modifier.clickable {
                                navigator.navigate(AnimeScreenDestination(anime.id.toString()))
                            },
                            leadingContent = { ClubAnimeImage(anime.image.original) }
                        )
                    }
                }
                if (anime.loadState.append == LoadState.Loading) item { LoadingScreen() }
                if (anime.loadState.hasError) item { ErrorScreen() }
            }
        }
    }
}

@Composable
private fun Members(members: LazyPagingItems<User>, navigator: DestinationsNavigator, padding: Dp) {
    LazyColumn(contentPadding = PaddingValues(top = padding)) {
        when (members.loadState.refresh) {
            is LoadState.Error -> item { ErrorScreen() }
            is LoadState.Loading -> item { LoadingScreen() }
            is LoadState.NotLoading -> {
                items(members.itemCount) {
                    members[it]?.let { member ->
                        OneLineImage(
                            name = member.nickname,
                            link = member.image.x160,
                            modifier = Modifier.clickable {
                                navigator.navigate(UserScreenDestination(member.id))
                            }
                        )
                    }
                }
                if (members.loadState.append == LoadState.Loading) item { LoadingScreen() }
                if (members.loadState.hasError) item { ErrorScreen() }
            }

        }
    }
}

@Composable
private fun Characters(characters: LazyPagingItems<Character>, navigator: DestinationsNavigator, padding: Dp) {
    LazyColumn(contentPadding = PaddingValues(top = padding)) {
        when (characters.loadState.refresh) {
            is LoadState.Error -> item { ErrorScreen() }
            is LoadState.Loading -> item { LoadingScreen() }
            is LoadState.NotLoading -> {
                items(characters.itemCount) {
                    characters[it]?.let { (id, name, russian, image) ->
                        OneLineImage(
                            name = russian ?: name,
                            link = getImage(image.original),
                            modifier = Modifier.clickable {
                                navigator.navigate(CharacterScreenDestination(id.toString()))
                            }
                        )
                    }
                }
                if (characters.loadState.append == LoadState.Loading) item { LoadingScreen() }
                if (characters.loadState.hasError) item { ErrorScreen() }
            }
        }
    }
}

@Composable
private fun Images(images: LazyPagingItems<ClubImages>, padding: Dp) {
    var show by rememberSaveable { mutableStateOf(false) }
    var link by rememberSaveable { mutableStateOf(BLANK) }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(76.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = padding),
        verticalItemSpacing = 2.dp,
        horizontalArrangement = spacedBy(2.dp)
    ) {
        when (images.loadState.refresh) {
            is LoadState.Error -> item { ErrorScreen() }
            is LoadState.Loading -> item { LoadingScreen() }
            is LoadState.NotLoading -> {
                items(images.itemCount) {
                    images[it]?.let { image ->
                        AsyncImage(
                            model = image.previewUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clickable {
                                    link = image.originalUrl.orEmpty(); show = true
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                if (images.loadState.append == LoadState.Loading) item { LoadingScreen() }
                if (images.loadState.hasError) item { ErrorScreen() }
            }
        }
    }

    if (show) Dialog({ show = false }, DialogProperties(usePlatformDefaultWidth = false)) {
        AsyncImage(link, null)
    }
}

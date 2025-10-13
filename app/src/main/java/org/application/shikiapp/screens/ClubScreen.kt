@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import org.application.shikiapp.R
import org.application.shikiapp.di.Preferences
import org.application.shikiapp.events.ClubEvent
import org.application.shikiapp.models.data.ClubImages
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.states.ClubState
import org.application.shikiapp.models.states.showContent
import org.application.shikiapp.models.states.showImages
import org.application.shikiapp.models.states.showMembers
import org.application.shikiapp.models.ui.Club
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.viewModels.ClubViewModel
import org.application.shikiapp.ui.templates.AnimatedScreen
import org.application.shikiapp.ui.templates.BasicContentItem
import org.application.shikiapp.ui.templates.CatalogGridItem
import org.application.shikiapp.ui.templates.Comments
import org.application.shikiapp.ui.templates.ErrorScreen
import org.application.shikiapp.ui.templates.LoadingScreen
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.templates.VectorIcon
import org.application.shikiapp.utils.enums.ClubMenu
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun ClubScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val context = LocalContext.current

    val model = viewModel<ClubViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()
    val content = model.content.collectAsLazyPagingItems()

    AnimatedScreen(response, model::loadData) { club ->
        ClubView(club, state, content, model::onEvent, onNavigate, back)
    }

    LaunchedEffect(Unit) {
        model.joinChannel.collectLatest {
            Toast.makeText(context, it.asString(context), Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun ClubView(
    club: Club,
    state: ClubState,
    content: LazyPagingItems<BasicContent>,
    onEvent: (ClubEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    back: () -> Unit
) {
    val listState = rememberLazyListState()
    val members = club.members.collectAsLazyPagingItems()
    val images = club.images.collectAsLazyPagingItems()
    val clubs = club.clubs.collectAsLazyPagingItems()
    val comments = club.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { NavigationIcon(back) },
                actions = {
                    IconButton(
                        onClick = { onEvent(ClubEvent.ShowComments) }
                    ) {
                        Icon(painterResource(R.drawable.vector_comments), null)
                    }

                    IconButton(
                        onClick = { onEvent(ClubEvent.ShowBottomSheet) }
                    ) {
                        VectorIcon(R.drawable.vector_more)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            model = club.image,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .border(1.dp, Color.Gray)
                        )
                    }
                )
            }

            item {
                HorizontalDivider()
            }

            item {
                ClubMenuItems(onEvent)
            }

            item {
                HorizontalDivider()
            }

            item {
                Text(club.description)
            }
        }
    }

    Members(
        members = members,
        visible = state.showMembers,
        onNavigate = onNavigate,
        hide = { onEvent(ClubEvent.PickItem()) }
    )

    Content(
        content = content,
        state = state,
        visible = state.showContent,
        onNavigate = onNavigate,
        hide = { onEvent(ClubEvent.PickItem()) }
    )

    Images(
        images = images,
        visible = state.showImages,
        onEvent = onEvent,
        hide = { onEvent(ClubEvent.PickItem()) }
    )

    Clubs(
        clubs = clubs,
        visible = state.showClubs,
        onNavigate = onNavigate,
        hide = { onEvent(ClubEvent.ShowClubs) }
    )

    Comments(
        list = comments,
        listState = listState,
        visible = state.showComments,
        hide = { onEvent(ClubEvent.ShowComments) },
        onNavigate = onNavigate
    )

    when {
        state.showFullImage -> DialogImage(state) {
            onEvent(ClubEvent.ShowFullImage())
        }

        state.showBottomSheet -> BottomSheet(state, onEvent)
    }
}

@Composable
private fun ClubMenuItems(onEvent: (ClubEvent) -> Unit) =
    Column(Modifier.wrapContentHeight(), Arrangement.spacedBy(8.dp), Alignment.CenterHorizontally) {
        ClubMenu.entries.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(48.dp), Alignment.CenterVertically) {
                row.forEach {
                    FilterChip(
                        selected = true,
                        label = { Text(stringResource(it.title)) },
                        onClick = { onEvent(ClubEvent.PickItem(it)) },
                        trailingIcon = {
                            VectorIcon(R.drawable.vector_keyboard_arrow_right)
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f)
                    )
                }
            }
        }
    }

@Composable
private fun Members(
    members: LazyPagingItems<UserBasic>,
    visible: Boolean,
    onNavigate: (Screen) -> Unit,
    hide: () -> Unit,
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_members)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { padding ->
        when (members.loadState.refresh) {
            LoadState.Loading -> LoadingScreen()
            is LoadState.Error -> ErrorScreen()
            is LoadState.NotLoading -> LazyVerticalGrid(
                columns = GridCells.FixedSize(70.dp),
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items(members.itemCount) { index ->
                    members[index]?.let {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                onNavigate(Screen.User(it.id))
                            }
                        ) {
                            AsyncImage(
                                model = it.avatar,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                            )

                            Text(
                                maxLines = 1,
                                text = it.nickname,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                if (members.loadState.append == LoadState.Loading) {
                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        LoadingScreen(Modifier.padding(8.dp))
                    }
                }
                if (members.loadState.hasError) {
                    item { ErrorScreen(members::retry) }
                }
            }
        }
    }
}

@Composable
private fun Content(
    content: LazyPagingItems<BasicContent>,
    state: ClubState,
    visible: Boolean,
    onNavigate: (Screen) -> Unit,
    hide: () -> Unit,
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { state.menu?.let { Text(stringResource(it.title)) } },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { padding ->
        when (content.loadState.refresh) {
            LoadState.Loading -> LoadingScreen()
            is LoadState.Error -> ErrorScreen()
            is LoadState.NotLoading -> LazyVerticalGrid(
                columns = GridCells.FixedSize(116.dp),
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items(content.itemCount) { index ->
                    content[index]?.let {
                        CatalogGridItem(
                            title = it.title,
                            image = it.poster,
                            score = null,
                            kind = null,
                            season = null,
                            modifier = Modifier.animateItem(),
                            onClick = {
                                when (state.menu) {
                                    ClubMenu.ANIME -> onNavigate(Screen.Anime(it.id))
                                    ClubMenu.MANGA, ClubMenu.RANOBE -> onNavigate(Screen.Manga(it.id))
                                    ClubMenu.CHARACTERS -> onNavigate(Screen.Character(it.id))

                                    else -> Unit
                                }
                            }
                        )
                    }
                }

                if (content.loadState.append == LoadState.Loading) {
                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        LoadingScreen(Modifier.padding(8.dp))
                    }
                }
                if (content.loadState.hasError) {
                    item { ErrorScreen(content::retry) }
                }
            }
        }
    }
}

@Composable
private fun Images(
    images: LazyPagingItems<ClubImages>,
    visible: Boolean,
    onEvent: (ClubEvent) -> Unit,
    hide: () -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {  Text(stringResource(R.string.text_pictures)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { padding ->
        when (images.loadState.refresh) {
            LoadState.Loading -> LoadingScreen()
            is LoadState.Error -> ErrorScreen()
            is LoadState.NotLoading -> LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                columns = StaggeredGridCells.Adaptive(120.dp),
                contentPadding = padding,
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(images.itemCount, images.itemKey(ClubImages::id)) { index ->
                    images[index]?.let {
                        AsyncImage(
                            model = it.mainUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            filterQuality = FilterQuality.High,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clickable {
                                    onEvent(ClubEvent.ShowFullImage(it.originalUrl))
                                }
                        )
                    }
                }

                if (images.loadState.append == LoadState.Loading) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        LoadingScreen(Modifier.padding(8.dp))
                    }
                }

                if (images.loadState.hasError) {
                    item { ErrorScreen(images::retry) }
                }
            }
        }
    }
}

@Composable
private fun Clubs(
    clubs: LazyPagingItems<BasicContent>,
    visible: Boolean,
    onNavigate: (Screen) -> Unit,
    hide: () -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_clubs)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(clubs.itemCount, clubs.itemKey(BasicContent::id)) { index ->
                clubs[index]?.let {
                    BasicContentItem(
                        name = it.title,
                        link = it.poster,
                        modifier = Modifier.clickable {
                            onNavigate(Screen.Club(it.id.toLong()))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomSheet(state: ClubState, onEvent: (ClubEvent) -> Unit) = ModalBottomSheet(
    onDismissRequest = { onEvent(ClubEvent.ShowBottomSheet) }
) {
    ListItem(
        headlineContent = { Text(stringResource(R.string.text_clubs)) },
        leadingContent = { Icon(painterResource(R.drawable.vector_clubs), null) },
        modifier = Modifier.clickable { onEvent(ClubEvent.ShowClubs) }
    )

    if (Preferences.token != null) {
        if (state.isMember) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.text_leave_club)) },
                leadingContent = { VectorIcon(R.drawable.vector_close) },
                modifier = Modifier.clickable { onEvent(ClubEvent.LeaveClub) }
            )
        } else {
            ListItem(
                headlineContent = { Text(stringResource(R.string.text_join_club)) },
                leadingContent = { VectorIcon(R.drawable.vector_check) },
                modifier = Modifier.clickable { onEvent(ClubEvent.JoinClub) }
            )
        }
    }

    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
}

@Composable
private fun DialogImage(state: ClubState, onDismiss: () -> Unit) = Dialog(onDismiss) {
    ZoomableAsyncImage(
        contentDescription = null,
        model = state.image,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}
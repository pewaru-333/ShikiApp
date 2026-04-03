@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.states.ClubState
import org.application.shikiapp.shared.models.states.showContent
import org.application.shikiapp.shared.models.ui.Club
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.asSource
import org.application.shikiapp.shared.models.viewModels.ClubViewModel
import org.application.shikiapp.shared.ui.templates.AnimatedDialogScreen
import org.application.shikiapp.shared.ui.templates.AnimatedScreen
import org.application.shikiapp.shared.ui.templates.CircleBorderedImage
import org.application.shikiapp.shared.ui.templates.Comments
import org.application.shikiapp.shared.ui.templates.ContentList
import org.application.shikiapp.shared.ui.templates.DialogImage
import org.application.shikiapp.shared.ui.templates.DialogImages
import org.application.shikiapp.shared.ui.templates.DialogPoster
import org.application.shikiapp.shared.ui.templates.MenuItems
import org.application.shikiapp.shared.ui.templates.NavigationIcon
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.ui.templates.about
import org.application.shikiapp.shared.utils.extensions.toContentLarge
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.rememberToastState
import org.application.shikiapp.shared.utils.ui.CommentContent
import org.application.shikiapp.shared.utils.ui.rememberCommentListState
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_clubs
import shikiapp.composeapp.generated.resources.text_description
import shikiapp.composeapp.generated.resources.text_join_club
import shikiapp.composeapp.generated.resources.text_leave_club
import shikiapp.composeapp.generated.resources.vector_check
import shikiapp.composeapp.generated.resources.vector_close
import shikiapp.composeapp.generated.resources.vector_clubs
import shikiapp.composeapp.generated.resources.vector_comments
import shikiapp.composeapp.generated.resources.vector_more

@Composable
fun ClubScreen(onNavigate: (Screen) -> Unit, onBack: () -> Unit) {
    val model = viewModel(::ClubViewModel)
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()
    val content = model.content.collectAsLazyPagingItems()

    val toast = rememberToastState()

    AnimatedScreen(response, model::loadData, Club::comments) { club, comments ->
        ClubView(club, state, content, model::onEvent, onNavigate, onBack)

        val commentListState = rememberCommentListState(
            list = comments,
            onCommentEvent = model.commentEvent
        )
        Comments(
            state = commentListState,
            isVisible = state.dialogState is BaseDialogState.Comments,
            isSending = state.isSendingComment,
            onNavigate = onNavigate,
            onHide = { model.onEvent(ContentDetailEvent.ToggleDialog(null)) },
            onCreateComment = { text, isOfftopic ->
                model.onEvent(ContentDetailEvent.CreateComment(text, isOfftopic))
            },
            onUpdateComment = { id, text, isOfftopicChanged ->
                model.onEvent(ContentDetailEvent.UpdateComment(id, text, isOfftopicChanged))
            },
            onDeleteComment = { id ->
                model.onEvent(ContentDetailEvent.DeleteComment(id))
            }
        )
    }

    LaunchedEffect(model.joinChannel) {
        model.joinChannel.collectLatest {
            toast.onShow(it.asString())
        }
    }
}

@Composable
private fun ClubView(
    club: Club,
    state: ClubState,
    content: LazyPagingItems<BasicContent>,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    var galleryInfo by remember { mutableStateOf<Pair<List<CommentContent.ImageContent>, Int>?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { NavigationIcon(onBack) },
                actions = {
                    IconButton(
                        onClick = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Comments)) },
                        content = { Icon(painterResource(Res.drawable.vector_comments), null) }
                    )

                    IconButton(
                        onClick = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Sheet)) },
                        content = { VectorIcon(Res.drawable.vector_more) }
                    )
                }
            )
        }
    ) { values ->
        LazyColumn(contentPadding = values.toContentLarge()) {
            item {
                ClubProfileHeader(club.name, club.image) {
                    onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Poster))
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
            }

            item {
                MenuItems(BaseDialogState.Club.Menu.items, BaseDialogState.Club.Menu::title) {
                    onEvent(ContentDetailEvent.ToggleDialog(it))
                }
            }

            if (club.description.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(24.dp))
                }

                about(Res.string.text_description, club.description) { images, index ->
                    galleryInfo = images to index
                }
            }
        }
    }

    galleryInfo?.let { (imageContents, initialIndex) ->
        val imageUrls = imageContents.map { it.fullUrl ?: it.previewUrl }

        DialogImages(
            images = imageUrls,
            initialIndex = initialIndex,
            isVisible = true,
            onClose = { galleryInfo = null }
        )
    }

    Content(
        list = content,
        menu = state.dialogState,
        isVisible = state.showContent,
        onNavigate = onNavigate,
        onOpenImage = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Club.Image(it))) },
        onHide = {
            onEvent(
                ContentDetailEvent.ToggleDialog(
                    dialogState = if (state.dialogState == BaseDialogState.Club.Menu.CLUBS) BaseDialogState.Sheet
                    else null
                )
            )
        }
    )

    DialogPoster(
        link = club.image.orEmpty(),
        isVisible = state.dialogState is BaseDialogState.Poster,
        onClose = { onEvent(ContentDetailEvent.ToggleDialog(null)) }
    )

    DialogImage(
        link = state.image.orEmpty(),
        isVisible = state.dialogState is BaseDialogState.Club.Image,
        onClose = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Club.Menu.IMAGES)) }
    )

    if (state.dialogState is BaseDialogState.Sheet) {
        BottomSheet(state.isMember, onEvent)
    }
}

@Composable
private fun ClubProfileHeader(name: String, image: String?, onOpenPoster: () -> Unit) =
    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp), Alignment.CenterVertically) {
        CircleBorderedImage(image, onOpenPoster)
        Text(
            text = name,
            modifier = Modifier.weight(1f, false),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }

@Composable
private fun Content(
    list: LazyPagingItems<BasicContent>,
    menu: BaseDialogState?,
    isVisible: Boolean,
    isCompactWindow: Boolean = rememberWindowSize().isCompact,
    onNavigate: (Screen) -> Unit,
    onOpenImage: (String?) -> Unit,
    onHide: () -> Unit
) {
    var lastMenu by remember { mutableStateOf(menu) }
    if (menu != null) {
        lastMenu = menu
    }

    val targetMenu = lastMenu as? BaseDialogState.Club.Menu
    val shouldShow = isVisible && targetMenu != null || lastMenu as? BaseDialogState.Club.Image != null

    val title = targetMenu?.let { stringResource(it.title) }.orEmpty()

    val listStates = BaseDialogState.Club.Menu.entries.associateWith { rememberLazyListState() }
    val gridStates = BaseDialogState.Club.Menu.entries.associateWith { rememberLazyGridState() }
    val staggeredGridState = rememberLazyStaggeredGridState()

    AnimatedDialogScreen(shouldShow, title, onHide) { padding ->
        if (targetMenu != null) {
            ContentList(
                source = list.asSource(BasicContent::id),
                mode = targetMenu.viewType,
                contentPadding = padding,
                listState = listStates.getValue(targetMenu),
                gridState = gridStates.getValue(targetMenu),
                staggeredGridState = staggeredGridState,
                isCompactWindow = isCompactWindow,
                onImageClick = onOpenImage,
                onItemClick = { id, _ -> onNavigate(targetMenu.navigateTo(id)) }
            )
        }
    }
}

@Composable
private fun BottomSheet(isMember: Boolean, onEvent: (ContentDetailEvent) -> Unit) =
    ModalBottomSheet(
        onDismissRequest = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
        contentWindowInsets = { WindowInsets.systemBars }
    ) {
        ListItem(
            headlineContent = { Text(stringResource(Res.string.text_clubs)) },
            leadingContent = { Icon(painterResource(Res.drawable.vector_clubs), null) },
            modifier = Modifier.clickable { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Club.Menu.CLUBS)) },
            colors = ListItemDefaults.colors(
                containerColor = BottomSheetDefaults.ContainerColor,
                headlineColor = contentColorFor(BottomSheetDefaults.ContainerColor),
                leadingIconColor = contentColorFor(BottomSheetDefaults.ContainerColor)
            )
        )

        if (Preferences.token != null) {
            if (isMember) {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.text_leave_club)) },
                    leadingContent = { VectorIcon(Res.drawable.vector_close) },
                    modifier = Modifier.clickable { onEvent(ContentDetailEvent.Club.LeaveClub) },
                    colors = ListItemDefaults.colors(
                        containerColor = BottomSheetDefaults.ContainerColor,
                        headlineColor = contentColorFor(BottomSheetDefaults.ContainerColor),
                        leadingIconColor = contentColorFor(BottomSheetDefaults.ContainerColor)
                    )
                )
            } else {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.text_join_club)) },
                    leadingContent = { VectorIcon(Res.drawable.vector_check) },
                    modifier = Modifier.clickable { onEvent(ContentDetailEvent.Club.JoinClub) },
                    colors = ListItemDefaults.colors(
                        containerColor = BottomSheetDefaults.ContainerColor,
                        headlineColor = contentColorFor(BottomSheetDefaults.ContainerColor),
                        leadingIconColor = contentColorFor(BottomSheetDefaults.ContainerColor)
                    )
                )
            }
        }
    }
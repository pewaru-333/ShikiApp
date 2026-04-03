@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.AnimeState
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.states.showAuthors
import org.application.shikiapp.shared.models.states.showCharacters
import org.application.shikiapp.shared.models.states.showFandubbers
import org.application.shikiapp.shared.models.states.showScreenshots
import org.application.shikiapp.shared.models.states.showSheetContent
import org.application.shikiapp.shared.models.ui.Anime
import org.application.shikiapp.shared.models.ui.Video
import org.application.shikiapp.shared.models.viewModels.AnimeViewModel
import org.application.shikiapp.shared.models.viewModels.UserRateViewModel
import org.application.shikiapp.shared.network.response.Response.Success
import org.application.shikiapp.shared.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.shared.ui.templates.AnimatedDialogScreen
import org.application.shikiapp.shared.ui.templates.AnimatedScreen
import org.application.shikiapp.shared.ui.templates.BottomSheet
import org.application.shikiapp.shared.ui.templates.Comments
import org.application.shikiapp.shared.ui.templates.DialogEditRate
import org.application.shikiapp.shared.ui.templates.DialogImages
import org.application.shikiapp.shared.ui.templates.DialogPoster
import org.application.shikiapp.shared.ui.templates.LinkListener
import org.application.shikiapp.shared.ui.templates.LinksSheet
import org.application.shikiapp.shared.ui.templates.MediaGridItem
import org.application.shikiapp.shared.ui.templates.MediaGridItemDefaults
import org.application.shikiapp.shared.ui.templates.ParagraphTitle
import org.application.shikiapp.shared.ui.templates.ProfilesFull
import org.application.shikiapp.shared.ui.templates.RelatedFull
import org.application.shikiapp.shared.ui.templates.ScaffoldContent
import org.application.shikiapp.shared.ui.templates.SimilarFull
import org.application.shikiapp.shared.ui.templates.Statistics
import org.application.shikiapp.shared.ui.templates.TextStickyHeader
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.ui.templates.description
import org.application.shikiapp.shared.ui.templates.genres
import org.application.shikiapp.shared.ui.templates.info
import org.application.shikiapp.shared.ui.templates.profiles
import org.application.shikiapp.shared.ui.templates.related
import org.application.shikiapp.shared.ui.templates.summary
import org.application.shikiapp.shared.ui.templates.title
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.VideoKind
import org.application.shikiapp.shared.utils.extensions.toContent
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.rememberCommentListState
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_anime
import shikiapp.composeapp.generated.resources.text_authors
import shikiapp.composeapp.generated.resources.text_characters
import shikiapp.composeapp.generated.resources.text_screenshots
import shikiapp.composeapp.generated.resources.text_subtitles
import shikiapp.composeapp.generated.resources.text_unknown
import shikiapp.composeapp.generated.resources.text_video
import shikiapp.composeapp.generated.resources.text_voices
import shikiapp.composeapp.generated.resources.vector_arrow_forward

@Composable
fun AnimeScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel(::AnimeViewModel)
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    LinkListener(model.openLink) { (response as? Success)?.data?.url }

    AnimatedScreen(response, model::loadData, Anime::comments) { anime, comments ->
        AnimeView(anime, state, model::onEvent, onNavigate, back)

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
}

@Composable
private fun AnimeView(
    anime: Anime,
    state: AnimeState,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val rateModel = viewModel(::UserRateViewModel)
    val rate = anime.userRate.getValue()
    val newRate by rateModel.newRate.collectAsStateWithLifecycle()

    val authorsState = rememberLazyListState()
    val charactersState = rememberLazyListState()
    val similarState = rememberLazyListState()
    val screenshotsState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        rate?.let { rateModel.getRate(it, LinkedType.ANIME) }
    }

    ScaffoldContent(
        title = { Text(stringResource(Res.string.text_anime)) },
        userRate = anime.userRate,
        isFavoured = anime.favoured,
        onBack = onBack,
        onEvent = onEvent,
        onToggleFavourite = { onEvent(ContentDetailEvent.Media.Anime.ToggleFavourite) }
    ) {
        title(anime.title)
        info(
            poster = anime.poster,
            kind = anime.kind,
            score = anime.score,
            status = anime.status,
            airedOn = anime.airedOn,
            releasedOn = anime.releasedOn,
            episodes = anime.episodes,
            origin = anime.origin,
            rating = anime.rating,
            onOpenFullscreenPoster = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Poster)) }
        )

        genres(anime.genres)
        summary(
            similar = anime.similar,
            studio = anime.studio,
            duration = anime.duration,
            nextEpisodeAt = anime.nextEpisodeAt,
            onEvent = onEvent,
            onNavigate = onNavigate
        )

        description(anime.description)
        related(
            list = anime.related,
            onShow = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Related)) },
            onNavigate = onNavigate
        )
        profiles(
            profiles = anime.charactersMain,
            title = Res.string.text_characters,
            onShow = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Characters)) },
            onNavigate = { onNavigate(Screen.Character(it)) }
        )
        profiles(
            profiles = anime.personMain,
            title = Res.string.text_authors,
            onShow = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Authors)) },
            onNavigate = { onNavigate(Screen.Person(it.toLong())) }
        )

        if (anime.screenshots.isNotEmpty()) {
            item {
                Screenshots(
                    list = anime.screenshots,
                    onShow = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Anime.Screenshots)) },
                    onShowScreenshot = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Image(it))) }
                )
            }
        }

        if (anime.video.isNotEmpty()) {
            item {
                Video(anime.video) { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Anime.Video)) }
            }
        }
    }

    RelatedFull(
        related = anime.related,
        chronology = anime.chronology,
        franchise = anime.franchiseList,
        isVisible = state.dialogState is BaseDialogState.Media.Related,
        onNavigate = onNavigate,
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) }
    )

    SimilarFull(
        list = anime.similar,
        listState = similarState,
        isVisible = state.dialogState is BaseDialogState.Media.Similar,
        onNavigate = { onNavigate(Screen.Anime(it)) },
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) }
    )

    Statistics(
        statistics = anime.stats,
        isVisible = state.dialogState is BaseDialogState.Media.Stats,
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
    )

    ProfilesFull(
        list = if (state.showCharacters) anime.charactersAll else anime.personAll,
        isVisible = state.showCharacters || state.showAuthors,
        title = stringResource(if (state.showCharacters) Res.string.text_characters else Res.string.text_authors),
        state = if (state.showCharacters) charactersState else authorsState,
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
        onNavigate = {
            onNavigate(
                if (state.showCharacters) Screen.Character(it)
                else Screen.Person(it.toLong())
            )
        }
    )

    Screenshots(
        list = anime.screenshots,
        listState = screenshotsState,
        isVisible = state.showScreenshots,
        onShowScreenshot = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Image(it, BaseDialogState.Anime.Screenshots))) },
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) }
    )

    DialogImages(
        images = anime.screenshots,
        initialIndex = state.screenshot,
        isVisible = state.dialogState is BaseDialogState.Media.Image,
        onClose = {
            onEvent(
                ContentDetailEvent.ToggleDialog(
                    dialogState = (state.dialogState as BaseDialogState.Media.Image).parentDialog
                )
            )
        }
    )

    DialogPoster(
        link = anime.poster,
        isVisible = state.dialogState is BaseDialogState.Poster,
        onClose = { onEvent(ContentDetailEvent.ToggleDialog(null)) }
    )

    Video(
        video = anime.videoGrouped,
        isVisible = state.dialogState is BaseDialogState.Anime.Video,
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) }
    )

    when (state.dialogState) {
        BaseDialogState.Media.Rate -> DialogEditRate(
            state = newRate,
            type = LinkedType.ANIME,
            isExists = rate != null,
            onEvent = rateModel::onEvent,
            onDismiss = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
            onCreate = { type ->
                rateModel.create(
                    id = anime.id,
                    targetType = type,
                    reload = { onEvent(ContentDetailEvent.Media.ChangeRate) }
                )
            },
            onUpdate = {
                rateModel.update(
                    rateId = rate?.id.toString(),
                    reload = { onEvent(ContentDetailEvent.Media.ChangeRate) }
                )
            },
            onDelete = {
                rateModel.delete(
                    rateId = rate?.id.toString(),
                    reload = { onEvent(ContentDetailEvent.Media.ChangeRate) }
                )
            }
        )

        BaseDialogState.Sheet -> BottomSheet(
            url = anime.url,
            canShowLinks = anime.links.isNotEmpty(),
            onEvent = onEvent
        )

        BaseDialogState.Media.Links -> LinksSheet(anime.links) {
            onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Sheet))
        }

        else -> Unit
    }

    if (state.showSheetContent) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
            contentWindowInsets = { WindowInsets.systemBars }
        ) {
            val (text, list) = if (state.showFandubbers) Res.string.text_voices to anime.fandubbers
            else Res.string.text_subtitles to anime.fansubbers

            Text(
                text = stringResource(text),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, bottom = 8.dp)
            )

            LazyColumn {
                items(list) { item ->
                    Text(item, Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
private fun Screenshots(list: List<String>, onShowScreenshot: (Int) -> Unit, onShow: () -> Unit) =
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            ParagraphTitle(stringResource(Res.string.text_screenshots), Modifier.padding(bottom = 4.dp))
            IconButton(onShow) { VectorIcon(Res.drawable.vector_arrow_forward) }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(list.take(6), ::Pair) { index, item ->
                AnimatedAsyncImage(
                    model = item,
                    modifier = Modifier
                        .size(172.dp, 97.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onShowScreenshot(index) }
                )
            }
        }
    }

@Composable
private fun Video(list: List<Video>, onShow: () -> Unit) {
    val handler = LocalUriHandler.current

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            ParagraphTitle(stringResource(Res.string.text_video), Modifier.padding(bottom = 4.dp))
            IconButton(onShow) { VectorIcon(Res.drawable.vector_arrow_forward) }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(list, Video::url) {
                AnimatedAsyncImage(
                    model = it.imageUrl,
                    modifier = Modifier
                        .size(172.dp, 130.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { handler.openUri(it.url) }
                )
            }
        }
    }
}

@Composable
private fun Screenshots(
    list: List<String>,
    listState: LazyGridState,
    isVisible: Boolean,
    onShowScreenshot: (Int) -> Unit,
    onHide: () -> Unit
) = AnimatedDialogScreen(isVisible, stringResource(Res.string.text_screenshots), onHide) { values ->
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = values.toContent(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(list, ::Pair) { index, item ->
            AnimatedAsyncImage(
                model = item,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onShowScreenshot(index) }
            )
        }
    }
}

@Composable
private fun Video(video: Map<VideoKind, List<Video>>, isVisible: Boolean, onHide: () -> Unit) =
    AnimatedDialogScreen(isVisible, stringResource(Res.string.text_video), onHide) { values ->
        val handler = LocalUriHandler.current

        LazyVerticalGrid(
            columns = GridCells.Adaptive(120.dp),
            modifier = Modifier.padding(values), // Без этого stickyHeader не двигается при прокрутке
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            video.forEach { (entry, values) ->
                stickyHeader { TextStickyHeader(stringResource(entry.title)) }
                items(values, Video::url) { video ->
                    MediaGridItem(
                        title = video.name ?: stringResource(Res.string.text_unknown),
                        poster = video.imageUrl,
                        onClick = { handler.openUri(video.url) },
                        posterModifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(172f / 130f)
                            .clip(MaterialTheme.shapes.small),
                        titleConfig = MediaGridItemDefaults.titleConfig(
                            minLines = 2,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleSmall
                        )
                    )
                }
            }
        }
    }
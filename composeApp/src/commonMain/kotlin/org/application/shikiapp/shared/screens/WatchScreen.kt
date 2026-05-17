@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalFlexBoxApi::class
)

package org.application.shikiapp.shared.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.FlexAlignItems
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.models.states.WatchState
import org.application.shikiapp.shared.models.ui.EpisodeModel
import org.application.shikiapp.shared.models.ui.VideoSourceData
import org.application.shikiapp.shared.models.ui.VideoVoice
import org.application.shikiapp.shared.models.viewModels.WatchViewModel
import org.application.shikiapp.shared.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.shared.ui.templates.IconVideoControl
import org.application.shikiapp.shared.ui.templates.LoadingScreen
import org.application.shikiapp.shared.ui.templates.NavigationIcon
import org.application.shikiapp.shared.ui.templates.TextLabelVideo
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.utils.HideSystemBars
import org.application.shikiapp.shared.utils.LockScreenOrientation
import org.application.shikiapp.shared.utils.enums.PickerStep
import org.application.shikiapp.shared.utils.enums.ScreenOrientation
import org.application.shikiapp.shared.utils.enums.VideoSource
import org.application.shikiapp.shared.utils.extensions.playerFocusRequest
import org.application.shikiapp.shared.utils.extensions.playerKeyEvents
import org.application.shikiapp.shared.utils.extensions.playerMouseEvents
import org.application.shikiapp.shared.utils.invisiblePointer
import org.application.shikiapp.shared.utils.showVideoControls
import org.application.shikiapp.shared.utils.ui.Formatter
import org.application.shikiapp.shared.utils.ui.VideoPlayer
import org.application.shikiapp.shared.utils.ui.VideoPlayerState
import org.application.shikiapp.shared.utils.ui.rememberVideoPlayerState
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.plural_count_dubbers
import shikiapp.composeapp.generated.resources.plural_count_episodes
import shikiapp.composeapp.generated.resources.text_confirm
import shikiapp.composeapp.generated.resources.text_dismiss
import shikiapp.composeapp.generated.resources.text_empty
import shikiapp.composeapp.generated.resources.text_episode_holder
import shikiapp.composeapp.generated.resources.text_episodes
import shikiapp.composeapp.generated.resources.text_explain_before_watch
import shikiapp.composeapp.generated.resources.text_pay_attention
import shikiapp.composeapp.generated.resources.text_video_sources
import shikiapp.composeapp.generated.resources.text_video_subtitles
import shikiapp.composeapp.generated.resources.text_video_voice
import shikiapp.composeapp.generated.resources.vector_arrow_back
import shikiapp.composeapp.generated.resources.vector_close
import shikiapp.composeapp.generated.resources.vector_fullscreen
import shikiapp.composeapp.generated.resources.vector_fullscreen_exit
import shikiapp.composeapp.generated.resources.vector_keyboard_arrow_right
import shikiapp.composeapp.generated.resources.vector_list
import shikiapp.composeapp.generated.resources.vector_pause
import shikiapp.composeapp.generated.resources.vector_play
import shikiapp.composeapp.generated.resources.vector_subtitles
import shikiapp.composeapp.generated.resources.vector_ten_seconds_left
import shikiapp.composeapp.generated.resources.vector_ten_seconds_right
import shikiapp.composeapp.generated.resources.vector_volume_off
import shikiapp.composeapp.generated.resources.vector_volume_on

@Composable
fun WatchScreen(onBack: () -> Unit) {
    val model = viewModel(::WatchViewModel)
    val state by model.state.collectAsStateWithLifecycle()

    val canWatch by Preferences.canWatchFlow.collectAsStateWithLifecycle(Preferences.canWatch)

    val lazyStateSources = rememberLazyListState()
    val lazyStateVoices = rememberLazyListState()
    val lazyStateEpisodes = rememberLazyListState()

    val currentStep = when {
        state.currentVoice != null -> PickerStep.EPISODES
        state.currentSource != null -> PickerStep.VOICES
        else -> PickerStep.SOURCES
    }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        onBackCompleted = {
            when {
                state.isWatching -> model.stopWatching()
                state.currentVoice != null -> model.clearVoice()
                state.currentSource != null -> model.clearSource()
                else -> onBack()
            }
        }
    )

    LaunchedEffect(state.currentSource) {
        if (state.currentSource == null) {
            lazyStateVoices.scrollToItem(0)
        }
    }

    LaunchedEffect(state.currentVoice) {
        if (state.currentVoice == null) {
            lazyStateEpisodes.scrollToItem(0)
        }
    }

    if (!canWatch) {
        DialogAccept(Preferences::setCanWatch, onBack)
    }
    else if (!state.isWatching) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = when (currentStep) {
                                PickerStep.SOURCES -> stringResource(Res.string.text_video_sources)
                                PickerStep.VOICES -> state.currentSource?.type?.title?.let { stringResource(it) }.orEmpty()
                                PickerStep.EPISODES -> state.currentVoice?.title.orEmpty()
                            }
                        )
                    },
                    navigationIcon = {
                        NavigationIcon {
                            when (currentStep) {
                                PickerStep.EPISODES -> model.clearVoice()
                                PickerStep.VOICES -> model.clearSource()
                                PickerStep.SOURCES -> onBack()
                            }
                        }
                    }
                )
            }
        ) { values ->
            if (state.isLoading) {
                LoadingScreen(Modifier.padding(values))
            } else {
                VideoPicker(
                    state = state,
                    currentStep = currentStep,
                    modifier = Modifier.padding(values),
                    lazySources = lazyStateSources,
                    lazyVoices = lazyStateVoices,
                    lazyEpisodes = lazyStateEpisodes,
                    onSelectSource = model::selectSource,
                    onSelectVoice = model::selectVoice,
                    onLoadVideo = model::loadVideo
                )
            }
        }
    } else {
        Player(
            state = state,
            onEvent = model::onEvent,
            onBack = model::stopWatching
        )
    }
}

@Composable
private fun VideoPicker(
    state: WatchState,
    currentStep: PickerStep,
    modifier: Modifier = Modifier,
    lazySources: LazyListState,
    lazyVoices: LazyListState,
    lazyEpisodes: LazyListState,
    onSelectSource: (VideoSource) -> Unit,
    onSelectVoice: (Int) -> Unit,
    onLoadVideo: (EpisodeModel) -> Unit
) = AnimatedContent(
    targetState = currentStep,
    modifier = modifier,
    transitionSpec = {
        if (targetState.ordinal > initialState.ordinal) {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) togetherWith
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        } else {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right) togetherWith
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        }
    }
) { step ->
    when (step) {
        PickerStep.SOURCES -> Sources(state.sources, lazySources, onSelectSource)
        PickerStep.VOICES -> state.currentSource?.voices?.let { Voices(it, lazyVoices, onSelectVoice) }
        PickerStep.EPISODES -> state.currentVoice?.let { Episodes(it, lazyEpisodes, onLoadVideo) }
    }
}

@Composable
private fun Sources(
    sources: List<VideoSourceData>,
    listState: LazyListState,
    onSelectSource: (VideoSource) -> Unit,
    dividerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    if (sources.isNotEmpty()) {
        LazyColumn(Modifier.fillMaxSize(), listState) {
            items(sources, VideoSourceData::type) { source ->
                ListItem(
                    trailingContent = { VectorIcon(Res.drawable.vector_keyboard_arrow_right) },
                    supportingContent = { Text(pluralStringResource(Res.plurals.plural_count_dubbers, source.voices.size, source.voices.size)) },
                    headlineContent = {
                        Text(
                            text = stringResource(source.type.title),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    },
                    modifier = Modifier
                        .clickable { onSelectSource(source.type) }
                        .drawWithContent {
                            drawContent()
                            drawLine(
                                color = dividerColor,
                                strokeWidth = 1.dp.toPx(),
                                start = Offset(0f, size.height - (1.dp.toPx() / 2)),
                                end = Offset(size.width, size.height - (1.dp.toPx() / 2))
                            )
                        }
                )
            }
        }
    } else {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(stringResource(Res.string.text_empty))
        }
    }
}

@Composable
private fun Voices(
    voices: List<VideoVoice>,
    listState: LazyListState,
    onSelectVoice: (Int) -> Unit,
    dividerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    if (voices.isNotEmpty()) {
        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

        val voiceContainer = if (isDark) Color(0xFF163355) else Color(0xFFD3E3FD)
        val voiceContent = if (isDark) Color(0xFFA8C7FA) else Color(0xFF041E49)

        val subContainer = if (isDark) Color(0xFF3F2040) else Color(0xFFF3D5F5)
        val subContent = if (isDark) Color(0xFFF2B8F5) else Color(0xFF310033)

        val epContainer = if (isDark) Color(0xFF00522B) else Color(0xFFC4EECE)
        val epContent = if (isDark) Color(0xFF82F7AB) else Color(0xFF00391A)

        @Composable
        fun LocalChip(text: String, color: Color, contentColor: Color) =
            Surface(
                shape = MaterialTheme.shapes.extraSmall,
                color = color,
                contentColor = contentColor
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(6.dp, 2.dp)
                )
            }

        LazyColumn(Modifier.fillMaxSize(), listState) {
            items(voices, VideoVoice::id) { item ->
                ListItem(
                    trailingContent = { VectorIcon(Res.drawable.vector_keyboard_arrow_right) },
                    headlineContent = {
                        Text(
                            text = item.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    supportingContent = {
                        FlexBox(
                            modifier = Modifier.padding(top = 8.dp),
                            config = {
                                wrap(FlexWrap.Wrap)
                                alignItems(FlexAlignItems.Center)
                                gap(8.dp)
                            }
                        ) {
                            if (item.hasDubbers) {
                                LocalChip(
                                    text = stringResource(Res.string.text_video_voice),
                                    color = voiceContainer,
                                    contentColor = voiceContent
                                )
                            }

                            if (item.hasSubtitles) {
                                LocalChip(
                                    text = stringResource(Res.string.text_video_subtitles),
                                    color = subContainer,
                                    contentColor = subContent
                                )
                            }

                            LocalChip(
                                text = pluralStringResource(Res.plurals.plural_count_episodes, item.episodesCount, item.episodesCount),
                                color = epContainer,
                                contentColor = epContent
                            )

                            item.quality?.let {
                                LocalChip(
                                    text = it,
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .clickable { onSelectVoice(item.id) }
                        .drawWithContent {
                            drawContent()
                            drawLine(
                                color = dividerColor,
                                strokeWidth = 1.dp.toPx(),
                                start = Offset(0f, size.height - (1.dp.toPx() / 2)),
                                end = Offset(size.width, size.height - (1.dp.toPx() / 2))
                            )
                        }
                )
            }
        }
    } else {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(stringResource(Res.string.text_empty))
        }
    }
}

@Composable
private fun Episodes(voice: VideoVoice, listState: LazyListState, onLoadVideo: (EpisodeModel) -> Unit) =
    LazyColumn(Modifier.fillMaxSize(), listState) {
        items(voice.episodes, { Pair(it.number, it.link) }) {
            ListItem(
                modifier = Modifier.clickable { onLoadVideo(it) },
                headlineContent = { Text(stringResource(Res.string.text_episode_holder, it.number)) },
                trailingContent = { VectorIcon(Res.drawable.vector_keyboard_arrow_right) },
                leadingContent = {
                    AnimatedAsyncImage(
                        model = it.screenshot,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(120.dp)
                            .aspectRatio(16f / 9f)
                            .clip(MaterialTheme.shapes.small)
                            .border(Dp.Hairline, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.small)
                    )
                }
            )
        }
    }

@Composable
private fun Player(state: WatchState, onEvent: (PlayerEvent) -> Unit, onBack: () -> Unit) {
    val playerState = rememberVideoPlayerState(onEvent)
    val focusRequester = remember(::FocusRequester)

    playerState.controls.AutoQualityListener()
    playerState.controls.QualityListener(state.qualityList)
    playerState.controls.ControlsVisibilityListener()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(playerState.controls.isControlsVisible) {
        if (!playerState.controls.isControlsVisible) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(state.videoUrl) {
        state.videoUrl?.let {
            playerState.loadUrl(
                newUrl = it,
                fallback = state.fallbackUrls,
                trackIndex = state.audioTrackIndex,
                subs = state.subtitles,
                headerMap = state.videoHeaders
            )
        }
    }

    LaunchedEffect(playerState.isPlaying, playerState.isLoading) {
        if (playerState.isPlaying) {
            playerState.onEvent(PlayerEvent.Play)
        } else {
            playerState.onEvent(PlayerEvent.Pause)
        }
    }

    LaunchedEffect(playerState.currentTime) {
        if (playerState.totalTime > 0f) {
            playerState.onEvent(PlayerEvent.UpdateProgress(playerState.currentTime, playerState.totalTime))
        }
    }

    if (state.currentVoice != null) {
        LockScreenOrientation(ScreenOrientation.LANDSCAPE)
        HideSystemBars()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .keepScreenOn()
                .focusRequester(focusRequester)
                .focusable()
                .pointerHoverIcon(if (playerState.controls.isControlsVisible) PointerIcon.Default else invisiblePointer)
                .playerKeyEvents(playerState)
                .playerMouseEvents(playerState)
                .playerFocusRequest(focusRequester::requestFocus)
        ) {
            VideoPlayer(playerState, Modifier.fillMaxSize())

            if (showVideoControls) {
                GestureEvents(playerState)
            }

            AnimatedVisibility(
                visible = playerState.isLoading,
                modifier = Modifier.align(Alignment.Center),
                enter = fadeIn(),
                exit = fadeOut(),
                content = { CircularProgressIndicator(Modifier.size(64.dp), Color.White, 4.dp) }
            )

            VideoControls(state, playerState, onBack)

            VolumeScale(playerState)

            if (playerState.currentCues.isNotEmpty()) {
                Cues(playerState.currentCues)
            }

            EpisodeList(
                currentVoice = state.currentVoice,
                currentEpisode = state.currentEpisode ?: 0,
                controls = playerState.controls,
                onSelect = { playerState.onEvent(PlayerEvent.SelectEpisode(it)) }
            )
        }
    }
}

@Composable
private fun BoxScope.Cues(cues: List<CharSequence>) =
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(24.dp)
    ) {
        cues.fastForEach { text ->
            Text(
                text = text.toString(),
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), MaterialTheme.shapes.extraSmall)
                    .padding(12.dp, 4.dp)
            )
        }
    }

@Composable
fun BoxScope.SeekPlayPauseSeek(playerState: VideoPlayerState) =
    Row(Modifier.align(Alignment.Center), Arrangement.spacedBy(48.dp), Alignment.CenterVertically) {
        IconVideoControl(
            resId = Res.drawable.vector_ten_seconds_left,
            modifier = Modifier.size(50.dp),
            modifierI = Modifier.padding(8.dp),
            onClick = { playerState.seekTo(playerState.currentTime - 10f) },
        )

        IconVideoControl(
            resId = if (playerState.isPlaying) Res.drawable.vector_pause else Res.drawable.vector_play,
            modifier = Modifier.size(64.dp),
            modifierI = Modifier.padding(4.dp),
            onClick = { playerState.togglePlayPause() },
        )

        IconVideoControl(
            resId = Res.drawable.vector_ten_seconds_right,
            modifier = Modifier.size(50.dp),
            modifierI = Modifier.padding(8.dp),
            onClick = { playerState.seekTo(playerState.currentTime + 10f) },
        )
    }

@Composable
private fun BoxScope.VolumeScale(playerState: VideoPlayerState) =
    AnimatedVisibility(
        visible = playerState.controls.isVolumeDragging,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .align(Alignment.CenterStart)
            .padding(start = 48.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.7f), MaterialTheme.shapes.medium)
                .padding(12.dp)
        ) {
            Text(
                text = "${(playerState.volume * 100).toInt()}%",
                modifier = Modifier.defaultMinSize(minWidth = 48.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )

            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .width(12.dp)
                    .height(120.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(playerState.volume)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }

            VectorIcon(
                modifier = Modifier.size(24.dp),
                tint = Color.White,
                resId = if (playerState.volume == 0f) Res.drawable.vector_volume_off
                else Res.drawable.vector_volume_on
            )
        }
    }

@Composable
private fun BoxScope.EpisodeList(
    currentVoice: VideoVoice,
    currentEpisode: Int,
    controls: VideoPlayerState.Controls,
    onSelect: (Int) -> Unit,
) = AnimatedVisibility(
    visible = controls.expandedEpisodes,
    modifier = Modifier.align(Alignment.CenterEnd),
    enter = slideInHorizontally { it },
    exit = slideOutHorizontally { it }
) {
    val gridState = rememberLazyGridState()

    LaunchedEffect(controls.expandedEpisodes) {
        if (controls.expandedEpisodes) {
            gridState.scrollToItem((currentEpisode - 1).coerceAtLeast(0))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.Black.copy(alpha = 0.85f))
            .pointerInput(Unit) { detectTapGestures() }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.text_episodes),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(
                    onClick = { controls.expandedEpisodes = false },
                    content = {
                        VectorIcon(
                            resId = Res.drawable.vector_close,
                            tint = Color.White
                        )
                    }
                )
            }

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentVoice.episodesCount) { index ->
                    val episode = index + 1
                    val isCurrent = currentEpisode == episode

                    FilterChip(
                        selected = isCurrent,
                        onClick = { onSelect(episode) },
                        label = {
                            Text(
                                text = episode.toString(),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White.copy(alpha = 0.1f),
                            labelColor = Color.White.copy(alpha = 0.8f),
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isCurrent,
                            borderColor = Color.Transparent,
                            selectedBorderColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxScope.TimeCurrentSliderTimeTotal(playerState: VideoPlayerState) =
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .align(Alignment.BottomCenter)
    ) {
        Text(
            text = Formatter.formatTime(playerState.currentTime),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        Slider(
            value = playerState.controls.sliderValue,
            interactionSource = playerState.controls.sliderInteractionSource,
            onValueChange = playerState.controls::setSliderValue,
            onValueChangeFinished = playerState.controls::onSliderActionFinished,
            modifier = Modifier.weight(1f),
            thumb = { sliderState ->
                Label(
                    interactionSource = playerState.controls.sliderInteractionSource,
                    isPersistent = sliderState.isDragging,
                    label = {
                        PlainTooltip {
                            Text(Formatter.formatTime(playerState.controls.sliderValue * playerState.totalTime))
                        }
                    },
                    content = {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color.White, CircleShape)
                        )
                    }
                )
            },
            track = { sliderState ->
                Box(Modifier.fillMaxWidth(), Alignment.CenterStart) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color.White.copy(alpha = 0.3f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(playerState.bufferPercentage.coerceIn(0f, 1f))
                            .height(4.dp)
                            .background(Color.White.copy(alpha = 0.6f), CircleShape)
                    )
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        thumbTrackGapSize = 0.dp,
                        modifier = Modifier.height(4.dp),
                        colors = SliderDefaults.colors(inactiveTrackColor = Color.Transparent),
                        drawStopIndicator = {}
                    )
                }
            }
        )

        Text(
            text = Formatter.formatTime(playerState.totalTime),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        IconButton(playerState::toggleZoom, Modifier.size(32.dp)) {
            VectorIcon(
                modifier = Modifier.fillMaxSize(),
                tint = Color.White,
                resId = if (playerState.isZoomed) Res.drawable.vector_fullscreen_exit
                else Res.drawable.vector_fullscreen
            )
        }
    }

@Composable
private fun VideoControls(state: WatchState, playerState: VideoPlayerState, onBack: () -> Unit) =
    AnimatedVisibility(
        visible = playerState.controls.isControlsVisible || playerState.isVideoEnded,
        modifier = Modifier.fillMaxSize(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 24.dp)
                    .align(Alignment.TopCenter)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onBack) {
                        VectorIcon(
                            resId = Res.drawable.vector_arrow_back,
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    state.currentVoice?.let {
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(playerState::toggleSpeed) {
                        Text(
                            text = playerState.controls.speedLabel,
                            color = Color.White
                        )
                    }

                    Quality(state, playerState)

                    if (state.subtitles.isNotEmpty()) {
                        Subtitles(state, playerState)
                    }

                    state.currentVoice?.let {
                        if (it.episodesCount > 1) {
                            IconButton(
                                onClick = { playerState.controls.expandedEpisodes = true },
                                content = {
                                    VectorIcon(
                                        resId = Res.drawable.vector_list,
                                        tint = Color.White
                                    )
                                }
                            )
                        }
                    }
                }
            }

            if (showVideoControls && !playerState.isVideoEnded) {
                SeekPlayPauseSeek(playerState)
            }

            TimeCurrentSliderTimeTotal(playerState)
        }
    }

@Composable
private fun GestureEvents(playerState: VideoPlayerState) =
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { playerState.controls.toggleControls() },
                    onDoubleTap = { playerState.toggleZoom() }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragCancel = { playerState.controls.isVolumeDragging = false },
                    onDragEnd = { playerState.controls.isVolumeDragging = false },
                    onDragStart = { offset ->
                        val isRightPart = offset.x >= (size.width * 0.7f)
                        val isSafeFromTop = offset.y > (size.height * 0.15f)
                        val isSafeFromBottom = offset.y < (size.height * 0.85f)

                        if (isRightPart && isSafeFromTop && isSafeFromBottom) {
                            playerState.controls.isVolumeDragging = true
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        if (playerState.controls.isVolumeDragging) {
                            change.consume()
                            playerState.setVolume((playerState.volume - dragAmount / 400f))
                        }
                    }
                )
            }
    )

@Composable
private fun Quality(state: WatchState, playerState: VideoPlayerState) = BoxWithConstraints {
    val scrollState = rememberScrollState()

    state.currentQuality?.let { quality ->
        TextButton(playerState.controls::toggleQuality) {
            Text(
                text = "${quality}p",
                color = Color.White
            )
        }
    }

    AnimatedVisibility(
        visible = playerState.controls.expandedQuality,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .requiredSize(0.dp)
            .wrapContentSize(Alignment.TopCenter, true)
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .heightIn(max = maxHeight / 2)
                .background(Color.Black.copy(alpha = 0.85f), MaterialTheme.shapes.medium)
                .verticalScroll(scrollState)
                .padding(vertical = 8.dp)
        ) {
            state.qualityList.fastForEach { quality ->
                TextLabelVideo(
                    text = "${quality}p",
                    isSelected = quality == state.currentQuality,
                    onClick = { playerState.onEvent(PlayerEvent.ChangeQuality(quality)) }
                )
            }
        }
    }
}

@Composable
private fun Subtitles(state: WatchState, playerState: VideoPlayerState) = BoxWithConstraints {
    val scrollState = rememberScrollState()

    IconButton(playerState.controls::toggleSubtitles) {
        VectorIcon(
            resId = Res.drawable.vector_subtitles,
            tint = if (playerState.selectedSubtitlesTrack == null) Color.White
            else MaterialTheme.colorScheme.primary
        )
    }

    AnimatedVisibility(
        visible = playerState.controls.expandedSubtitles,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .requiredSize(0.dp)
            .wrapContentSize(Alignment.TopCenter, true)
    ) {
        Column(
            modifier = Modifier
                .width(180.dp)
                .heightIn(max = maxHeight / 2)
                .background(Color.Black.copy(alpha = 0.85f), MaterialTheme.shapes.medium)
                .verticalScroll(scrollState)
                .padding(vertical = 8.dp)
        ) {
            state.subtitles.forEachIndexed { index, subtitle ->
                TextLabelVideo(
                    text = subtitle.name,
                    isSelected = index == 0 && playerState.selectedSubtitlesTrack == null || subtitle.name == playerState.selectedSubtitlesTrack,
                    onClick = { playerState.showSubtitles(index) }
                )
            }
        }
    }
}

@Composable
private fun DialogAccept(onConfirm: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(Res.string.text_pay_attention)) },
    text = { Text(stringResource(Res.string.text_explain_before_watch)) },
    dismissButton = { TextButton(onDismiss) { Text(stringResource(Res.string.text_dismiss)) } },
    confirmButton = { Button(onConfirm) { Text(stringResource(Res.string.text_confirm)) } }
)
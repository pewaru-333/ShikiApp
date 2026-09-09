@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalFlexBoxApi::class
)

package org.application.shikiapp.shared.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.models.states.*
import org.application.shikiapp.shared.models.ui.EpisodeModel
import org.application.shikiapp.shared.models.ui.SubtitleTrack
import org.application.shikiapp.shared.models.ui.VideoSourceData
import org.application.shikiapp.shared.models.ui.VideoVoice
import org.application.shikiapp.shared.models.viewModels.WatchViewModel
import org.application.shikiapp.shared.network.client.AnimeLibAuth
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.ui.templates.*
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.HideSystemBars
import org.application.shikiapp.shared.utils.LockScreenOrientation
import org.application.shikiapp.shared.utils.enums.PickerStep
import org.application.shikiapp.shared.utils.enums.ScreenOrientation
import org.application.shikiapp.shared.utils.enums.VideoSource
import org.application.shikiapp.shared.utils.extensions.playerFocusRequest
import org.application.shikiapp.shared.utils.extensions.playerKeyEvents
import org.application.shikiapp.shared.utils.extensions.playerMouseEvents
import org.application.shikiapp.shared.utils.extractCodeFromUrl
import org.application.shikiapp.shared.utils.navigation.ExternalUriHandler
import org.application.shikiapp.shared.utils.ui.*
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.*

@Composable
fun WatchScreen(onBack: () -> Unit) {
    val model = viewModel(::WatchViewModel)
    val state by model.state.collectAsStateWithLifecycle()

    val canWatch by Preferences.canWatchFlow.collectAsStateWithLifecycle(Preferences.canWatch)

    val lazyStateSources = rememberLazyListState()
    val lazyStateVoices = rememberLazyListState()
    val lazyStateEpisodes = rememberLazyListState()

    val currentStep = when {
        state.isLib && state.episodeVoices != null -> PickerStep.VOICES
        state.isLib && state.currentSource != null -> PickerStep.EPISODES

        !state.isLib && state.currentVoice != null -> PickerStep.EPISODES
        !state.isLib && state.currentSource != null -> PickerStep.VOICES

        else -> PickerStep.SOURCES
    }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        onBackCompleted = {
            when {
                state.isWatching -> model.stopWatching()
                state.episodeVoices != null -> model.clearEpisodeVoices()
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
                                PickerStep.EPISODES -> state.currentVoice?.title?.asComposableString() ?: state.currentSource?.type?.title?.let { stringResource(it) }.orEmpty()
                            }
                        )
                    },
                    navigationIcon = {
                        NavigationIcon {
                            when {
                                state.episodeVoices != null -> model.clearEpisodeVoices()
                                state.currentVoice != null -> model.clearVoice()
                                state.currentSource != null -> model.clearSource()
                                else -> onBack()
                            }
                        }
                    },
                    actions = {
                        if (state.showIconLogout) {
                            IconButton(model::toggleLogoutDialog) {
                                VectorIcon(Icons.ExitApp)
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
                    onSelectEpisode = model::selectEpisode
                )
            }
        }
    } else {
        Player(
            state = state,
            commands = model.command,
            onEvent = model::onEvent,
            onBack = model::stopWatching
        )
    }

    if (state.showLogoutDialog) {
        DialogLogout(model::logout, model::toggleLogoutDialog)
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
    onSelectEpisode: (EpisodeModel) -> Unit
) {
    val libToken by Preferences.libTokenFlow.collectAsStateWithLifecycle(Preferences.libToken)

    AnimatedContent(
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
            PickerStep.VOICES -> Voices(state.voices, state.isEpisodesLoading, lazyVoices, onSelectVoice)
            PickerStep.EPISODES -> {
                if (state.currentSource?.type == VideoSource.ANIMELIB && libToken == null) {
                    AnimeLibAuth()
                } else {
                    Episodes(state.episodes, lazyEpisodes, onSelectEpisode)
                }
            }
        }
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
                    trailingContent = { VectorIcon(Icons.KeyboardArrowRight) },
                    supportingContent = { Text(pluralStringResource(Res.plurals.plural_count_variants, source.voices.size, source.voices.size)) },
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
    isLoading: Boolean,
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
                    trailingContent = { VectorIcon(Icons.KeyboardArrowRight) },
                    headlineContent = {
                        Text(
                            text = item.title.asComposableString(),
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

                            if (item.hasEpisodes) {
                                LocalChip(
                                    text = pluralStringResource(Res.plurals.plural_count_episodes, item.episodesCount, item.episodesCount),
                                    color = epContainer,
                                    contentColor = epContent
                                )
                            }

                            item.quality?.let {
                                LocalChip(
                                    text = it.asComposableString(),
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
    } else if (isLoading) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(stringResource(Res.string.text_empty))
        }
    }
}

@Composable
private fun Episodes(episodes: List<EpisodeModel>, listState: LazyListState, onLoadVideo: (EpisodeModel) -> Unit) =
    LazyColumn(Modifier.fillMaxSize(), listState) {
        items(episodes, { Pair(it.number, it.link) }) {
            ListItem(
                modifier = Modifier.clickable { onLoadVideo(it) },
                headlineContent = { Text(stringResource(Res.string.text_episode_holder, it.number)) },
                trailingContent = { VectorIcon(Icons.KeyboardArrowRight) },
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
private fun Player(state: WatchState, commands: Flow<PlayerEvent.Command>, onEvent: (PlayerEvent) -> Unit, onBack: () -> Unit) {
    val controller = rememberVideoPlayerController(onEvent)

    val rootFocusRequester = remember(::FocusRequester)
    val playButtonFocusRequester = remember(::FocusRequester)
    val episodeListFocusRequester = remember(::FocusRequester)

    controller.controls.ControlsVisibilityListener()

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        onBackCompleted = {
            when {
                controller.controls.expandedEpisodes -> controller.controls.hideEpisodes()
                controller.controls.expandedQuality -> controller.controls.hideQuality()
                controller.controls.expandedSubtitles -> controller.controls.hideSubtitles()
                else -> onBack()
            }
        }
    )

    LaunchedEffect(controller, commands) {
        commands.collectLatest {
            when (it) {
                is PlayerEvent.Command.LoadQuality -> controller.loadVideo(it.url)
                is PlayerEvent.Command.LoadVideo -> controller.loadVideo(
                    url = it.episodeModel.link,
                    fallback = it.episodeModel.fallback,
                    qualityList = it.episodeModel.qualityList,
                    trackIndex = it.episodeModel.audioIndex,
                    subtitles = it.episodeModel.subtitles,
                    headers = it.episodeModel.videoHeaders
                )
            }
        }
    }

    LaunchedEffect(controller.controls.isControlsVisible, controller.controls.expandedEpisodes) {
        if (controller.controls.expandedEpisodes) {
            episodeListFocusRequester.requestFocus()
        } else {
            if (controller.controls.isControlsVisible) {
                if (controller.feature.showPlayPause) {
                    playButtonFocusRequester.requestFocus()
                }
            } else {
                rootFocusRequester.requestFocus()
            }
        }
    }

    DisposableEffect(controller) {
        controller.create()

        onDispose { controller.release() }
    }

    if (state.currentVoice != null) {
        LockScreenOrientation(ScreenOrientation.LANDSCAPE)
        HideSystemBars()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .keepScreenOn()
                .focusRequester(rootFocusRequester)
                .focusable(controller.controls.isControlsFocusable)
                .pointerHoverIcon(controller.controls.pointerHoverIcon)
                .playerKeyEvents(controller)
                .playerMouseEvents(controller)
                .playerFocusRequest(rootFocusRequester::requestFocus)
        ) {
            VideoPlayer(controller, Modifier.fillMaxSize())

            if (controller.feature.showPlayPause) {
                GestureEvents(controller)
            }

            AnimatedVisibility(
                visible = controller.state.isLoading,
                modifier = Modifier.align(Alignment.Center),
                enter = fadeIn(),
                exit = fadeOut(),
                content = { CircularProgressIndicator(Modifier.size(64.dp), Color.White, 4.dp) }
            )

            VideoControls(state, controller, playButtonFocusRequester, onBack)

            VolumeScale(controller)

            EpisodeList(
                episodesCount = state.currentVoice.episodesCount,
                currentEpisode = state.currentEpisode ?: 0,
                isVisible = controller.controls.expandedEpisodes,
                focusRequester = episodeListFocusRequester,
                onSelect = { controller.onEvent(PlayerEvent.SelectEpisode(it)) },
                onHide = { controller.controls.hideEpisodes() }
            )
        }
    }
}

@Composable
fun BoxScope.SeekPlayPauseSeek(controller: VideoPlayerController, focusRequester: FocusRequester) =
    Row(Modifier.align(Alignment.Center), Arrangement.spacedBy(48.dp), Alignment.CenterVertically) {
        IconVideoControl(
            icon = Icons.TenSecondsLeft,
            onClick = { controller.seek(-10f) },
            modifier = Modifier.size(50.dp),
            modifierI = Modifier.padding(8.dp),
        )

        IconVideoControl(
            icon = if (controller.state.isPlaying) Icons.PauseCircle else Icons.PlayCircle,
            onClick = { controller.togglePlayPause() },
            modifierI = Modifier.padding(4.dp),
            modifier = Modifier
                .size(64.dp)
                .focusRequester(focusRequester)
        )

        IconVideoControl(
            icon = Icons.TenSecondsRight,
            onClick = { controller.seek(10f) },
            modifier = Modifier.size(50.dp),
            modifierI = Modifier.padding(8.dp),
        )
    }

@Composable
private fun BoxScope.VolumeScale(controller: VideoPlayerController) =
    AnimatedVisibility(
        visible = controller.controls.isVolumeDragging,
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
                text = "${(controller.state.volume * 100).toInt()}%",
                modifier = Modifier.defaultMinSize(minWidth = 48.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )

            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .size(12.dp, 120.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(controller.state.volume)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }

            VectorIcon(
                modifier = Modifier.size(24.dp),
                tint = Color.White,
                imageVector = if (controller.state.volume == 0f) Icons.VolumeOff
                else Icons.VolumeUp
            )
        }
    }

@Composable
private fun BoxScope.EpisodeList(
    episodesCount: Int,
    currentEpisode: Int,
    isVisible: Boolean,
    focusRequester: FocusRequester,
    onHide: () -> Unit,
    onSelect: (Int) -> Unit,
) = AnimatedVisibility(
    visible = isVisible,
    modifier = Modifier.align(Alignment.CenterEnd),
    enter = slideInHorizontally { it },
    exit = slideOutHorizontally { it }
) {
    val gridState = rememberLazyGridState()

    val focusRequesterFirst = remember(::FocusRequester)
    val focusRequesterLast = remember(::FocusRequester)

    LaunchedEffect(isVisible) {
        if (isVisible) {
            gridState.scrollToItem((currentEpisode - 1).coerceAtLeast(0))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.Black.copy(alpha = 0.85f))
            .focusable()
            .focusRequester(focusRequester)
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
                ButtonFocused(
                    content = Icons.Close,
                    onClick = onHide,
                    modifier = Modifier.focusProperties { left = FocusRequester.Cancel }
                )
            }

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val containerColor = Color.White.copy(alpha = 0.1f)
                val labelColor = Color.White.copy(alpha = 0.8f)

                items(episodesCount) { index ->
                    val episode = index + 1
                    val isCurrent = currentEpisode == episode
                    val isFirst = index == 0
                    val isLast = index == episodesCount - 1

                    val interactionSource = remember(::MutableInteractionSource)
                    val isFocused by interactionSource.collectIsFocusedAsState()

                    FilterChip(
                        selected = isCurrent,
                        onClick = { onSelect(episode) },
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .focusProperties {
                                left = FocusRequester.Cancel

                                if (isFirst) up = focusRequesterLast
                                if (isLast) down = focusRequesterFirst
                            }
                            .then(
                                other = when {
                                    isFirst -> Modifier.focusRequester(focusRequesterFirst)
                                    isLast -> Modifier.focusRequester(focusRequesterLast)
                                    isCurrent -> Modifier.focusRequester(focusRequester)
                                    else -> Modifier
                                }
                            ),
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
                            containerColor = if (isFocused) labelColor else containerColor,
                            labelColor = if (isFocused) MaterialTheme.colorScheme.onSurface else labelColor,
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
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
private fun BoxScope.TimeCurrentSliderTimeTotal(controller: VideoPlayerController) =
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .align(Alignment.BottomCenter)
    ) {
        Text(
            text = Formatter.formatTime(controller.state.currentTime),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        val isFocused by controller.controls.sliderInteractionSource.collectIsFocusedAsState()
        val thumbColor = if (isFocused) MaterialTheme.colorScheme.primary else Color.White
        val thumbSize = if (isFocused) 20.dp else 16.dp

        Slider(
            value = controller.controls.sliderValue,
            interactionSource = controller.controls.sliderInteractionSource,
            onValueChange = controller.controls::onSetSliderValue,
            onValueChangeFinished = controller.controls::onSliderActionFinished,
            modifier = Modifier.weight(1f),
            thumb = { sliderState ->
                Label(
                    interactionSource = controller.controls.sliderInteractionSource,
                    isPersistent = sliderState.isDragging,
                    label = {
                        PlainTooltip {
                            Text(Formatter.formatTime(controller.controls.sliderValue * controller.state.totalTime))
                        }
                    },
                    content = {
                        Box(
                            modifier = Modifier
                                .size(thumbSize)
                                .background(thumbColor, CircleShape)
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
                            .fillMaxWidth(controller.state.bufferPercentage.coerceIn(0f, 1f))
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
            text = Formatter.formatTime(controller.state.totalTime),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        if (!controller.feature.isTV) {
            Row {
                ButtonFocused(
                    onClick = controller::scale,
                    content = if (controller.state.isZoomed) Icons.FullscreenExit
                    else Icons.Fullscreen
                )

                controller.feature.pictureInPicture?.let {
                    ButtonFocused(
                        onClick = it::enterPIP,
                        content = Icons.PictureInPicture
                    )
                }
            }
        }
    }

@Composable
private fun VideoControls(state: WatchState, controller: VideoPlayerController, focusRequester: FocusRequester, onBack: () -> Unit) =
    AnimatedVisibility(
        visible = controller.controls.isControlsVisible || controller.state.isVideoEnded,
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
                    ButtonFocused(Icons.ArrowBack, onClick = onBack)

                    Spacer(Modifier.width(8.dp))

                    state.currentVoice?.let {
                        Text(
                            text = it.title.asComposableString(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ButtonFocused(controller.controls.speedLabel, onClick = controller::toggleSpeed)

                    Quality(controller)

                    if (state.subtitles.isNotEmpty()) {
                        Subtitles(state, controller)
                    }

                    state.currentVoice?.let {
                        if (it.episodesCount > 1) {
                            ButtonFocused(Icons.List, onClick = controller.controls::toggleEpisodes)
                        }
                    }
                }
            }

            if (controller.feature.showPlayPause && !controller.state.isVideoEnded) {
                SeekPlayPauseSeek(controller, focusRequester)
            }

            TimeCurrentSliderTimeTotal(controller)
        }
    }

@Composable
private fun GestureEvents(controller: VideoPlayerController) =
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { controller.controls.toggleControls() },
                    onDoubleTap = { controller.scale() }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragCancel = { controller.controls.hideVolume() },
                    onDragEnd = { controller.controls.hideVolume() },
                    onDragStart = { offset ->
                        val isRightPart = offset.x >= (size.width * 0.7f)
                        val isSafeFromTop = offset.y > (size.height * 0.15f)
                        val isSafeFromBottom = offset.y < (size.height * 0.85f)

                        if (isRightPart && isSafeFromTop && isSafeFromBottom) {
                            controller.controls.showVolume()
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        if (controller.controls.isVolumeDragging) {
                            change.consume()
                            controller.setVolume((controller.state.volume - dragAmount / 400f))
                        }
                    }
                )
            }
    )

@Composable
private fun Quality(controller: VideoPlayerController) = BoxWithConstraints {
    controller.state.currentQuality?.let { quality ->
        ButtonFocused("${quality}p", onClick = controller.controls::toggleQuality)
    }

    MenuPlayerItems(
        items = controller.state.qualityList,
        expanded = controller.controls.expandedQuality,
        onItemClick = { _, item -> controller.onEvent(PlayerEvent.ChangeQuality(item)) },
        itemSelected = { _, item -> controller.state.currentQuality == item },
        itemLabel = { "${it}p" },
        modifier = Modifier.width(80.dp),
        maxHeight = maxHeight / 2
    )
}

@Composable
private fun Subtitles(state: WatchState, controller: VideoPlayerController) = BoxWithConstraints {
    ButtonFocused(
        content = Icons.Subtitles,
        onClick = controller.controls::toggleSubtitles,
        tint = if (controller.state.selectedSubtitlesTrack == null) Color.White
        else MaterialTheme.colorScheme.primary
    )

    MenuPlayerItems(
        items = state.subtitles,
        expanded = controller.controls.expandedSubtitles,
        onItemClick = { index, _ -> controller.showSubtitles(index) },
        itemSelected = { index, item -> index == 0 && controller.state.selectedSubtitlesTrack == null || item.name == controller.state.selectedSubtitlesTrack },
        itemLabel = SubtitleTrack::name,
        modifier = Modifier.width(140.dp),
        maxHeight = maxHeight / 2,
        menuTextDefaults = MenuPlayerDefaults.MenuItemText(
            maxLines = 2,
            softWrap = true
        )
    )
}

@Composable
private fun ButtonFocused(content: Any, modifier: Modifier = Modifier, tint: Color = Color.White, onClick: () -> Unit) {
    val interactionSource = remember(::MutableInteractionSource)
    val isFocused by interactionSource.collectIsFocusedAsState()

    val containerColor = if (isFocused) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isFocused) MaterialTheme.colorScheme.onPrimary else Color.White

    when (content) {
        is ImageVector -> IconButton(
            onClick = onClick,
            modifier = modifier,
            interactionSource = interactionSource,
            colors = IconButtonDefaults.iconButtonColors(containerColor, contentColor),
            content = { VectorIcon(content, tint = if (isFocused) Color.White else tint) }
        )

        is String -> TextButton(
            onClick = onClick,
            modifier = modifier,
            interactionSource = interactionSource,
            colors = ButtonDefaults.textButtonColors(containerColor, contentColor),
            content = { Text(content) }
        )
    }
}

@Composable
private fun AnimeLibAuth() {
    val scope = rememberCoroutineScope()
    val uriHandler = rememberLinkHandler()

    var isLogging by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val mainListener = ExternalUriHandler.listener

        ExternalUriHandler.listener = { uri ->
            if (uri.startsWith(ApiRoutes.REDIRECT_URI_LIB)) {
                extractCodeFromUrl(uri)?.let { code ->
                    isLogging = true

                    scope.launch {
                        try {
                            AnimeLibAuth.getToken(code)
                        } finally {
                            isLogging = false
                        }
                    }
                }
            }
        }

        onDispose { ExternalUriHandler.listener = mainListener }
    }

    if (isLogging) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Column(Modifier.width(240.dp), Arrangement.spacedBy(8.dp, Alignment.CenterVertically), Alignment.CenterHorizontally) {
                Button(
                    onClick = { uriHandler.onOpenLink(ApiRoutes.authUriLib) },
                    content = {
                        Text(stringResource(Res.string.text_login))
                        VectorIcon(Icons.KeyboardArrowRight)
                    }
                )

                Text(
                    text = stringResource(Res.string.text_login_to_animelib),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
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

@Composable
private fun DialogLogout(onConfirm: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    onDismissRequest = onDismiss,
    text = { Text(stringResource(Res.string.text_sure_to_logout_animelib)) },
    dismissButton = { TextButton(onDismiss) { Text(stringResource(Res.string.text_dismiss)) } },
    confirmButton = { TextButton(onConfirm) { Text(stringResource(Res.string.text_confirm)) } }
)
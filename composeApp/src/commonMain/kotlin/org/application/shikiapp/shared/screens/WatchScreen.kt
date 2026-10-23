@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalFlexBoxApi::class)

package org.application.shikiapp.shared.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
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
import org.application.shikiapp.shared.utils.data.preferences.rememberPreference
import org.application.shikiapp.shared.utils.enums.PickerStep
import org.application.shikiapp.shared.utils.enums.ScreenOrientation
import org.application.shikiapp.shared.utils.enums.VideoSource
import org.application.shikiapp.shared.utils.extensions.playerFocusRequest
import org.application.shikiapp.shared.utils.extensions.playerKeyEvents
import org.application.shikiapp.shared.utils.extensions.playerMouseEvents
import org.application.shikiapp.shared.utils.extractCodeFromUrl
import org.application.shikiapp.shared.utils.navigation.ExternalUriHandler
import org.application.shikiapp.shared.utils.ui.*
import org.application.shikiapp.shared.utils.ui.Formatter
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun WatchScreen(onBack: () -> Unit) {
    val model = viewModel(::WatchViewModel)
    val state by model.state.collectAsStateWithLifecycle()

    val canWatch by rememberPreference { canWatch }

    val lazyStateSources = rememberLazyListState()
    val lazyStateVoices = rememberLazyListState()
    val episodeListStates = rememberSaveableStateHolder()

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

    if (!canWatch) {
        DialogAccept(
            onConfirm = { Preferences.canWatch.value = true },
            onDismiss = onBack
        )
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
                    episodeStates = episodeListStates,
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
            onRetry = model::retryVideo,
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
    episodeStates: SaveableStateHolder,
    onSelectSource: (VideoSource) -> Unit,
    onSelectVoice: (String) -> Unit,
    onSelectEpisode: (EpisodeModel) -> Unit
) {
    val libToken by rememberPreference(Preferences.libToken) { libTokenFlow }

    AnimatedContent(
        targetState = currentStep to state,
        contentKey = Pair<PickerStep, WatchState>::first,
        modifier = modifier,
        transitionSpec = {
            val steps = if (initialState.second.isLib || targetState.second.isLib) {
                listOf(PickerStep.SOURCES, PickerStep.EPISODES, PickerStep.VOICES)
            } else PickerStep.entries

            val direction = if (steps.indexOf(targetState.first) > steps.indexOf(initialState.first)) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else AnimatedContentTransitionScope.SlideDirection.Right

            slideIntoContainer(direction) togetherWith slideOutOfContainer(direction)
        }
    ) { (step, pickerState) ->
        when (step) {
            PickerStep.SOURCES -> Sources(pickerState.sources, lazySources, onSelectSource)
            PickerStep.VOICES -> Voices(pickerState.voices, pickerState.isEpisodesLoading, lazyVoices, onSelectVoice)
            PickerStep.EPISODES -> {
                if (pickerState.isLib && libToken == null) {
                    AnimeLibAuth()
                } else {
                    val key = if (pickerState.isLib) VideoSource.ANIMELIB.name
                    else "${pickerState.currentSource?.type?.name}:${pickerState.currentVoice?.id}"

                    episodeStates.SaveableStateProvider(key) {
                        Episodes(pickerState.episodes, rememberLazyListState(), onSelectEpisode)
                    }
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
                    onClick = { onSelectSource(source.type) },
                    trailingContent = { VectorIcon(Icons.KeyboardArrowRight) },
                    supportingContent = { Text(pluralStringResource(Res.plurals.plural_count_variants, source.voices.size, source.voices.size)) },
                    content = {
                        Text(
                            text = stringResource(source.type.title),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    },
                    modifier = Modifier.drawWithContent {
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
    onSelectVoice: (String) -> Unit,
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
                    onClick = { onSelectVoice(item.id) },
                    trailingContent = { VectorIcon(Icons.KeyboardArrowRight) },
                    content = {
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
                    modifier = Modifier.drawWithContent {
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
        items(episodes, { Pair(it.number, it.link) }) { item ->
            ListItem(
                onClick = { onLoadVideo(item) },
                verticalAlignment = if (item.title == null) Alignment.CenterVertically else ListItemDefaults.verticalAlignment(),
                trailingContent = { VectorIcon(Icons.KeyboardArrowRight) },
                content = {
                    Text(
                        text = stringResource(Res.string.text_episode_holder, item.number),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = if (item.title == null) null
                else {
                    {
                        Text(
                            text = item.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                leadingContent = if (item.screenshot.isNullOrBlank()) null
                else {
                    {
                        AnimatedAsyncImage(
                            model = item.screenshot,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(120.dp)
                                .aspectRatio(16f / 9f)
                                .clip(MaterialTheme.shapes.small)
                                .border(Dp.Hairline, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.small)
                        )
                    }
                }
            )
        }
    }

@Composable
private fun Player(state: WatchState, commands: Flow<PlayerEvent.Command>, onEvent: (PlayerEvent) -> Unit, onRetry: () -> Unit, onBack: () -> Unit) {
    val controller = rememberVideoPlayerController(onEvent)

    val rootFocusRequester = remember(::FocusRequester)
    val playButtonFocusRequester = remember(::FocusRequester)
    val errorFocusRequester = remember(::FocusRequester)

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
                is PlayerEvent.Command.LoadQuality -> controller.loadVideo(it.url, it.headers)
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

    LaunchedEffect(state.isVideoError) {
        if (state.isVideoError) {
            controller.pause()
            controller.controls.hideControls()
        }
    }

    LaunchedEffect(state.isVideoError, controller.controls.isControlsVisible, controller.controls.expandedEpisodes, controller.state.isVideoEnded) {
        if (state.isVideoError) {
            errorFocusRequester.requestFocus()
        } else if (!controller.controls.expandedEpisodes) {
            if (controller.controls.isControlsVisible && controller.feature.showPlayPause && !controller.state.isVideoEnded) {
                playButtonFocusRequester.requestFocus()
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
                .focusable(!state.isVideoError && controller.controls.isControlsFocusable)
                .then(
                    other = if (state.isVideoError) Modifier
                    else Modifier
                        .pointerHoverIcon(controller.controls.pointerHoverIcon)
                        .playerKeyEvents(controller)
                        .playerMouseEvents(controller)
                        .playerFocusRequest(rootFocusRequester::requestFocus)
                )
        ) {
            VideoPlayer(controller, Modifier.fillMaxSize())

            if (state.isVideoError) {
                Box(
                    content = { ErrorScreen(onRetry, onBack) },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .focusRequester(errorFocusRequester)
                )
            } else {
                if (controller.feature.showPlayPause) {
                    GestureEvents(controller)
                }

                AnimatedVisibility(
                    visible = state.isVideoLoading || controller.state.isLoading,
                    modifier = Modifier.align(Alignment.Center),
                    enter = fadeIn(),
                    exit = fadeOut(),
                    content = { CircularProgressIndicator(Modifier.size(64.dp), Color.White, 4.dp) }
                )

                VideoControls(state, controller, playButtonFocusRequester, onBack)

                VolumeScale(controller)

                EpisodeList(
                    episodes = state.currentVoice.episodes,
                    currentEpisode = state.currentEpisode,
                    isVisible = controller.controls.expandedEpisodes,
                    onSelect = { controller.onEvent(PlayerEvent.SelectEpisode(it)) },
                    onHide = { controller.controls.hideEpisodes() }
                )
            }
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
        val fillColor = MaterialTheme.colorScheme.primary
        val isMuted by remember(controller) { derivedStateOf { controller.state.volume == 0f } }

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

            Spacer(
                modifier = Modifier
                    .size(12.dp, 120.dp)
                    .drawBehind {
                        val cornerRadius = CornerRadius(size.width / 2)
                        val height = size.height * controller.state.volume.coerceIn(0f, 1f)

                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.2f),
                            cornerRadius = cornerRadius
                        )
                        drawRoundRect(
                            color = fillColor,
                            topLeft = Offset(0f, size.height - height),
                            size = Size(size.width, height),
                            cornerRadius = cornerRadius
                        )
                    }
            )

            VectorIcon(
                modifier = Modifier.size(24.dp),
                tint = Color.White,
                imageVector = if (isMuted) Icons.VolumeOff
                else Icons.VolumeUp
            )
        }
    }

@Composable
private fun BoxScope.EpisodeList(
    episodes: List<EpisodeModel>,
    currentEpisode: Int?,
    isVisible: Boolean,
    onHide: () -> Unit,
    onSelect: (Int) -> Unit
) = AnimatedVisibility(
    visible = isVisible,
    modifier = Modifier.align(Alignment.CenterEnd),
    enter = slideInHorizontally { it },
    exit = slideOutHorizontally { it }
) {
    val gridState = rememberLazyGridState()

    val focusRequester = remember(::FocusRequester)
    val currentIndex = episodes.indexOfFirst { it.number == currentEpisode }.coerceAtLeast(0)

    LaunchedEffect(isVisible, episodes, currentEpisode) {
        if (isVisible && episodes.isNotEmpty()) {
            gridState.scrollToItem(currentIndex)
            snapshotFlow { gridState.layoutInfo.visibleItemsInfo.any { it.index == currentIndex } }.first { it }
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.Black.copy(alpha = 0.85f))
            .focusGroup()
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

                itemsIndexed(episodes, { _, episode -> episode.number }) { index, episode ->
                    val isCurrent = currentEpisode == episode.number

                    val interactionSource = remember(::MutableInteractionSource)
                    val isFocused by interactionSource.collectIsFocusedAsState()

                    FilterChip(
                        selected = isCurrent,
                        onClick = { onSelect(episode.number) },
                        interactionSource = interactionSource,
                        modifier = if (index == currentIndex) Modifier.focusRequester(focusRequester) else Modifier,
                        label = {
                            Text(
                                text = episode.number.toString(),
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
        val isFocused by controller.controls.sliderInteractionSource.collectIsFocusedAsState()
        val thumbColor = if (isFocused) MaterialTheme.colorScheme.primary else Color.White
        val thumbSize = if (isFocused) 20.dp else 16.dp

        Text(
            text = Formatter.formatTime(controller.state.currentTime),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

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
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .drawBehind {
                                val cornerRadius = CornerRadius(size.height / 2)

                                drawRoundRect(
                                    color = Color.White.copy(alpha = 0.3f),
                                    cornerRadius = cornerRadius
                                )
                                drawRoundRect(
                                    color = Color.White.copy(alpha = 0.6f),
                                    cornerRadius = cornerRadius,
                                    size = Size(
                                        width = size.width * controller.state.bufferPercentage,
                                        height = size.height
                                    )
                                )
                            }
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
private fun GestureEvents(controller: VideoPlayerController) {
    val mediaUrl = controller.state.url
    var seekSeconds by remember(controller) { mutableIntStateOf(0) }
    var isSeekVisible by remember(controller) { mutableStateOf(false) }

    LaunchedEffect(controller, mediaUrl) {
        isSeekVisible = false
    }

    LaunchedEffect(seekSeconds, isSeekVisible) {
        if (isSeekVisible) {
            delay(700.milliseconds)
            isSeekVisible = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(controller, mediaUrl) {
                detectTapGestures(
                    onTap = {
                        if (!isSeekVisible) {
                            controller.controls.toggleControls()
                        }
                    },
                    onDoubleTap = { offset ->
                        if (controller.feature.doubleTapToSeek && controller.state.totalTime > 0f) {
                            val position = controller.state.currentTime
                            val isBack = offset.x < size.width / 2f
                            val target = (position + if (isBack) -10f else 10f).coerceIn(0f, controller.state.totalTime)

                            if (target != position && (isBack || !controller.state.isVideoEnded)) {
                                controller.controls.hideControls()

                                val seconds = abs(target - position)
                                    .roundToInt()
                                    .coerceAtLeast(1) * if (isBack) -1 else 1

                                seekSeconds = if (isSeekVisible && (seekSeconds > 0) == (seconds > 0)) seekSeconds + seconds
                                else seconds

                                isSeekVisible = true

                                controller.seekTo(target)
                            }
                        }
                    }
                )
            }
            .pointerInput(controller, mediaUrl) {
                var isVolumeGesture = false

                fun finishVolumeGesture() {
                    if (isVolumeGesture) {
                        isVolumeGesture = false
                        controller.controls.hideVolume()
                    }
                }

                try {
                    detectVerticalDragGestures(
                        onDragCancel = ::finishVolumeGesture,
                        onDragEnd = ::finishVolumeGesture,
                        onDragStart = { offset ->
                            val isRightPart = offset.x >= (size.width * 0.7f)
                            val isSafeFromTop = offset.y > (size.height * 0.15f)
                            val isSafeFromBottom = offset.y < (size.height * 0.85f)

                            isVolumeGesture = isRightPart && isSafeFromTop && isSafeFromBottom

                            if (isVolumeGesture) {
                                isSeekVisible = false
                                controller.controls.showVolume()
                            }
                        },
                        onVerticalDrag = { _, dragAmount ->
                            if (isVolumeGesture) {
                                controller.controls.showVolume()
                                controller.setVolume(controller.state.volume - dragAmount / 400f)
                            }
                        }
                    )
                } finally {
                    finishVolumeGesture()
                }
            }
    ) {
        AnimatedVisibility(
            visible = isSeekVisible,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(tween(120)),
            exit = fadeOut(tween(220))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                AnimatedContent(
                    targetState = seekSeconds,
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(if (seekSeconds < 0) Alignment.CenterStart else Alignment.CenterEnd)
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight()
                        .background(Color.White.copy(alpha = 0.12f), CircleShape),
                    transitionSpec = {
                        (fadeIn(tween(100)) + scaleIn(tween(160), initialScale = 0.8f)) togetherWith
                                fadeOut(tween(80))
                    }
                ) { seconds ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        VectorIcon(
                            imageVector = if (seconds < 0) Icons.TenSecondsLeft else Icons.TenSecondsRight,
                            modifier = Modifier.size(56.dp),
                            tint = Color.White
                        )

                        Text(
                            text = stringResource(Res.string.text_seek_seconds, if (seconds > 0) "+$seconds" else seconds.toString()),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

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
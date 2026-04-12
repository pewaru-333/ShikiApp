@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
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
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.models.states.WatchState
import org.application.shikiapp.shared.models.ui.EpisodeModel
import org.application.shikiapp.shared.models.ui.VideoVoice
import org.application.shikiapp.shared.models.viewModels.WatchViewModel
import org.application.shikiapp.shared.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.shared.ui.templates.LoadingScreen
import org.application.shikiapp.shared.ui.templates.NavigationIcon
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.utils.HideSystemBars
import org.application.shikiapp.shared.utils.LockScreenOrientation
import org.application.shikiapp.shared.utils.enums.ScreenOrientation
import org.application.shikiapp.shared.utils.ui.VideoPlayer
import org.application.shikiapp.shared.utils.ui.rememberVideoPlayerState
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.plural_count_episodes
import shikiapp.composeapp.generated.resources.text_confirm
import shikiapp.composeapp.generated.resources.text_dismiss
import shikiapp.composeapp.generated.resources.text_episode_holder
import shikiapp.composeapp.generated.resources.text_episodes
import shikiapp.composeapp.generated.resources.text_explain_before_watch
import shikiapp.composeapp.generated.resources.text_pay_attention
import shikiapp.composeapp.generated.resources.text_voices
import shikiapp.composeapp.generated.resources.vector_arrow_back
import shikiapp.composeapp.generated.resources.vector_close
import shikiapp.composeapp.generated.resources.vector_fullscreen
import shikiapp.composeapp.generated.resources.vector_fullscreen_exit
import shikiapp.composeapp.generated.resources.vector_keyboard_arrow_right
import shikiapp.composeapp.generated.resources.vector_list
import shikiapp.composeapp.generated.resources.vector_pause
import shikiapp.composeapp.generated.resources.vector_play
import shikiapp.composeapp.generated.resources.vector_ten_seconds_left
import shikiapp.composeapp.generated.resources.vector_ten_seconds_right
import shikiapp.composeapp.generated.resources.vector_volume_off
import shikiapp.composeapp.generated.resources.vector_volume_on

@Composable
fun WatchScreen(onBack: () -> Unit) {
    val model = viewModel(::WatchViewModel)
    val state by model.state.collectAsStateWithLifecycle()

    val canWatch by Preferences.canWatchFlow.collectAsStateWithLifecycle(Preferences.canWatch)

    val lazyStateVoices = rememberLazyListState()
    val lazyStateEpisodes = rememberLazyListState()

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        onBackCompleted = {
            when {
                state.isWatching -> model.stopWatching()
                state.currentVoice != null -> model.clearVoice()
                else -> onBack()
            }
        }
    )

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
                            text = state.currentVoice?.title ?: stringResource(Res.string.text_voices)
                        )
                    },
                    navigationIcon = {
                        NavigationIcon {
                            if (state.currentVoice == null) onBack()
                            else model.clearVoice()
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
                    modifier = Modifier.padding(values),
                    lazyStateVoices = lazyStateVoices,
                    lazyStateEpisodes = lazyStateEpisodes,
                    onSelectVoice = model::selectVoice,
                    onLoadVideo = model::loadVideo
                )
            }
        }
    } else {
        Player(
            state = state,
            onQualitySelected = model::changeQuality,
            onEpisodeSelected = model::selectEpisode,
            onBack = model::stopWatching
        )
    }
}

@Composable
private fun VideoPicker(
    state: WatchState,
    modifier: Modifier = Modifier,
    lazyStateVoices: LazyListState,
    lazyStateEpisodes: LazyListState,
    onSelectVoice: (Int) -> Unit,
    onLoadVideo: (EpisodeModel) -> Unit
) = AnimatedContent(
    targetState = state.currentVoice != null,
    modifier = modifier,
    transitionSpec = {
        if (targetState) {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) togetherWith
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        } else {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right) togetherWith
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        }
    }
) { showEpisodes ->
    if (showEpisodes) {
        state.currentVoice?.let { voice ->
            LazyColumn(Modifier.fillMaxSize(), lazyStateEpisodes) {
                items(voice.episodes, EpisodeModel::number) { episode ->
                    ListItem(
                        modifier = Modifier.clickable { onLoadVideo(episode) },
                        headlineContent = { Text(stringResource(Res.string.text_episode_holder, episode.number)) },
                        trailingContent = { VectorIcon(Res.drawable.vector_keyboard_arrow_right) },
                        leadingContent = {
                            AnimatedAsyncImage(
                                model = episode.screenshot,
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
        }
    } else {
        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

        val voiceContainer = if (isDark) Color(0xFF163355) else Color(0xFFD3E3FD)
        val voiceContent = if (isDark) Color(0xFFA8C7FA) else Color(0xFF041E49)

        val subContainer = if (isDark) Color(0xFF3F2040) else Color(0xFFF3D5F5)
        val subContent = if (isDark) Color(0xFFF2B8F5) else Color(0xFF310033)

        val epContainer = if (isDark) Color(0xFF00522B) else Color(0xFFC4EECE)
        val epContent = if (isDark) Color(0xFF82F7AB) else Color(0xFF00391A)

        LazyColumn(Modifier.fillMaxSize(), lazyStateVoices) {
            items(state.voices, VideoVoice::id) { item ->
                ListItem(
                    modifier = Modifier.clickable { onSelectVoice(item.id) },
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
                        Row(Modifier.padding(top = 8.dp), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
                            Surface(
                                shape = MaterialTheme.shapes.extraSmall,
                                color = if (item.isSubtitles) subContainer else voiceContainer,
                                contentColor = if (item.isSubtitles) subContent else voiceContent
                            ) {
                                Text(
                                    text = stringResource(item.type),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(6.dp, 2.dp)
                                )
                            }

                            Surface(
                                shape = MaterialTheme.shapes.extraSmall,
                                color = epContainer,
                                contentColor = epContent
                            ) {
                                Text(
                                    text = pluralStringResource(Res.plurals.plural_count_episodes, item.episodesCount, item.episodesCount),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(6.dp, 2.dp)
                                )
                            }

                            item.quality?.let { quality ->
                                Surface(
                                    shape = MaterialTheme.shapes.extraSmall,
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ) {
                                    Text(
                                        text = quality,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(6.dp, 2.dp)
                                    )
                                }
                            }
                        }
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            }
        }
    }
}

@Composable
private fun Player(
    state: WatchState,
    onEpisodeSelected: (Int) -> Unit,
    onQualitySelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val playerState = rememberVideoPlayerState()

    val sliderInteractionSource = remember(::MutableInteractionSource)
    val isDragging by sliderInteractionSource.collectIsDraggedAsState()

    var showEpisodes by remember { mutableStateOf(false) }
    var expandedQuality by remember { mutableStateOf(false) }

    var isControlsVisible by remember { mutableStateOf(true) }
    var isVolumeDragging by remember { mutableStateOf(false) }

    var localSliderValue by remember { mutableFloatStateOf(0f) }
    var savedSeekTime by remember { mutableFloatStateOf(0f) }
    var currentSpeedIndex by remember { mutableIntStateOf(1) }

    val speeds = listOf(0.5f, 1.0f, 1.5f, 2.0f)
    val speedLabels = listOf("0.5x", "1.0x", "1.5x", "2.0x") // технически скорость видео через точку везде?

    LaunchedEffect(playerState.currentTime, playerState.totalTime, isDragging) {
        if (!isDragging && playerState.totalTime > 0f) {
            localSliderValue = (playerState.currentTime / playerState.totalTime).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(state.videoUrl) {
        state.videoUrl?.let {
            playerState.loadUrl(it)

            if (savedSeekTime > 0f) {
                playerState.seekTo(savedSeekTime)
                savedSeekTime = 0f
            }
        }
    }

    LaunchedEffect(isControlsVisible, playerState.isPlaying, isDragging, isVolumeDragging) {
        if (isControlsVisible && playerState.isPlaying && !isDragging && !isVolumeDragging) {
            delay(3000L)
            isControlsVisible = false
        }
    }

    fun formatTime(seconds: Float): String {
        if (seconds.isNaN() || seconds < 0f) return "00:00"

        val totalSecs = seconds.toInt()
        val h = totalSecs / 3600
        val m = (totalSecs % 3600) / 60
        val s = totalSecs % 60

        return if (h > 0) {
            "${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
        } else {
            "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
        }
    }

    if (state.currentVoice != null) {
        LockScreenOrientation(ScreenOrientation.LANDSCAPE)
        HideSystemBars()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            VideoPlayer(playerState, Modifier.fillMaxSize())

            AnimatedVisibility(
                visible = playerState.isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center),
                content = { CircularProgressIndicator(Modifier.size(64.dp), Color.White, 4.dp) }
            )

            Row(Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { isControlsVisible = !isControlsVisible },
                                onDoubleTap = { playerState.toggleZoom() }
                            )
                        }
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragStart = { isVolumeDragging = true },
                                onDragEnd = { isVolumeDragging = false },
                                onDragCancel = { isVolumeDragging = false },
                                onVerticalDrag = { _, dragAmount ->
                                    playerState.setVolume(playerState.volume - dragAmount / 400f)
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { isControlsVisible = !isControlsVisible },
                                onDoubleTap = { playerState.toggleZoom() }
                            )
                        }
                )
            }

            AnimatedVisibility(
                visible = isControlsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
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
                            Text(
                                text = state.currentVoice.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                onClick = {
                                    currentSpeedIndex = (currentSpeedIndex + 1) % speeds.size
                                    playerState.setSpeed(speeds[currentSpeedIndex])
                                },
                                content = {
                                    Text(
                                        text = speedLabels[currentSpeedIndex],
                                        color = Color.White
                                    )
                                }
                            )

                            Box {
                                TextButton(
                                    onClick = { expandedQuality = !expandedQuality },
                                    content = {
                                        Text(
                                            text = "${state.currentQuality}p",
                                            color = Color.White
                                        )
                                    }
                                )

                                androidx.compose.animation.AnimatedVisibility(
                                    visible = expandedQuality,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .requiredSize(0.dp)
                                        .wrapContentSize(Alignment.TopCenter, true)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .width(100.dp)
                                            .background(Color.Black.copy(alpha = 0.85f), MaterialTheme.shapes.medium)
                                            .padding(vertical = 8.dp)
                                    ) {
                                        state.qualityList.forEach { quality ->
                                            val isSelected = quality == state.currentQuality

                                            Text(
                                                text = "${quality}p",
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        expandedQuality = false
                                                        savedSeekTime = playerState.currentTime
                                                        onQualitySelected(quality)
                                                    }
                                                    .padding(16.dp, 12.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (!showEpisodes && state.currentVoice.episodesCount > 1) {
                                IconButton(
                                    onClick = { showEpisodes = true },
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

                    Row(Modifier.align(Alignment.Center), Arrangement.spacedBy(48.dp), Alignment.CenterVertically) {
                        IconButton(
                            onClick = { playerState.seekTo(playerState.currentTime - 10f) },
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            content = {
                                VectorIcon(
                                    resId = Res.drawable.vector_ten_seconds_left,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                )
                            }
                        )

                        IconButton(
                            onClick = { playerState.togglePlayPause() },
                            modifier = Modifier
                                .size(64.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            content = {
                                VectorIcon(
                                    tint = Color.White,
                                    resId = if (playerState.isPlaying) Res.drawable.vector_pause
                                    else Res.drawable.vector_play,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                )
                            }
                        )

                        IconButton(
                            onClick = { playerState.seekTo(playerState.currentTime + 10f) },
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            content = {
                                VectorIcon(
                                    resId = Res.drawable.vector_ten_seconds_right,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                )
                            }
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Text(
                            text = formatTime(playerState.currentTime),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Slider(
                            value = localSliderValue,
                            interactionSource = sliderInteractionSource,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            onValueChange = { percent ->
                                localSliderValue = percent
                                playerState.currentTime = percent * playerState.totalTime
                            },
                            onValueChangeFinished = {
                                playerState.seekTo(localSliderValue * playerState.totalTime)
                            },
                            thumb = {
                                Box(contentAlignment = Alignment.Center) {
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = isDragging,
                                        enter = fadeIn() + slideInVertically { it / 2 },
                                        exit = fadeOut() + slideOutVertically { it / 2 },
                                        modifier = Modifier.offset(y = (-32).dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color.Black.copy(alpha = 0.8f), MaterialTheme.shapes.small)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = formatTime(playerState.currentTime),
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    )
                                }
                            },
                            track = { sliderState ->
                                SliderDefaults.Track(
                                    sliderState = sliderState,
                                    thumbTrackGapSize = 0.dp,
                                    drawStopIndicator = {},
                                    colors = SliderDefaults.colors(
                                        activeTrackColor = MaterialTheme.colorScheme.primary,
                                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                    )
                                )
                            }
                        )

                        Text(
                            text = formatTime(playerState.totalTime),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.width(16.dp))

                        IconButton(
                            onClick = { playerState.toggleZoom() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            VectorIcon(
                                modifier = Modifier.fillMaxSize(),
                                tint = Color.White,
                                resId = if (playerState.isZoomed) Res.drawable.vector_fullscreen_exit
                                else Res.drawable.vector_fullscreen
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isVolumeDragging,
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
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
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

            AnimatedVisibility(
                visible = showEpisodes,
                modifier = Modifier.align(Alignment.CenterEnd),
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .background(Color.Black.copy(alpha = 0.85f))
                        .clickable(
                            interactionSource = remember(::MutableInteractionSource),
                            indication = null,
                            onClick = {}
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.text_episodes),
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge
                            )
                            IconButton(
                                onClick = { showEpisodes = false },
                                content = {
                                    VectorIcon(
                                        resId = Res.drawable.vector_close,
                                        tint = Color.White
                                    )
                                }
                            )
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(64.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items((1..state.currentVoice.episodesCount).toList()) { ep ->
                                Button(
                                    contentPadding = PaddingValues(0.dp),
                                    content = {
                                        Text(
                                            text = ep.toString(),
                                            color = Color.White
                                        )
                                    },
                                    onClick = {
                                        if (state.currentEpisode != ep && !state.isVideoLoading) {
                                            savedSeekTime = 0f
                                            showEpisodes = false
                                            onEpisodeSelected(ep)
                                        }
                                    },
                                    colors = if (state.currentEpisode == ep) ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                                    else ButtonDefaults.buttonColors(Color.DarkGray.copy(alpha = 0.6f))
                                )
                            }
                        }
                    }
                }
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
@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter.Companion.DefaultTransform
import coil3.compose.AsyncImagePainter.State
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.compose.SubcomposeAsyncImageScope
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.EdgeToEdge
import org.application.shikiapp.shared.utils.rememberDataManager
import org.application.shikiapp.shared.utils.rememberToastState
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_error_loading
import shikiapp.composeapp.generated.resources.text_image_of
import shikiapp.composeapp.generated.resources.text_picture
import shikiapp.composeapp.generated.resources.text_poster
import shikiapp.composeapp.generated.resources.text_saved

@Composable
fun ZoomableAsyncImage(
    model: Any?,
    modifier: Modifier = Modifier,
    state: ZoomState = rememberZoomState(),
    placeholder: Painter? = null,
    error: Painter? = null,
    fallback: Painter? = error,
    onLoading: ((State.Loading) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    onError: ((State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
) {
    AsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier.zoomable(state),
        placeholder = placeholder,
        fallback = fallback,
        onLoading = onLoading,
        onSuccess = {
            onSuccess?.invoke(it)
            state.setContentSize(it.painter.intrinsicSize)
        },
        onError = onError,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        clipToBounds = clipToBounds
    )
}

@Composable
fun AnimatedAsyncImage(
    model: Any?,
    modifier: Modifier = Modifier,
    transform: (State) -> State = DefaultTransform,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = FilterQuality.High,
    clipToBounds: Boolean = true,
    success: @Composable (SubcomposeAsyncImageScope.(State.Success) -> Unit)? = { SubcomposeAsyncImageContent() },
) = SubcomposeAsyncImage(
    model = model,
    contentDescription = null,
    modifier = modifier,
    transform = transform,
    alignment = alignment,
    alpha = alpha,
    contentScale = contentScale,
    filterQuality = filterQuality,
    colorFilter = colorFilter,
    clipToBounds = clipToBounds,
    success = success,
    loading = {
        Spacer(
            modifier = modifier
                .fillMaxSize()
                .then(loadingEffectModifier())
        )
    },
    error = {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            VectorIcon(
                imageVector = Icons.SmileBad,
                modifier = Modifier.fillMaxSize(0.75f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
)

@Composable
fun CircleContentImage(image: String?, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Crop) =
    AnimatedAsyncImage(
        model = image,
        contentScale = contentScale,
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .border(Dp.Hairline, MaterialTheme.colorScheme.onSurface, CircleShape)
    )

@Composable
fun Poster(
    link: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillHeight,
    onOpenFullscreen: () -> Unit
) = AnimatedAsyncImage(
    model = link,
    contentScale = contentScale,
    modifier = modifier
        .size(175.dp, 300.dp)
        .clip(MaterialTheme.shapes.medium)
        .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
        .clickable(onClick = onOpenFullscreen)
)

@Composable
fun DialogImage(link: String, isVisible: Boolean, onClose: () -> Unit) =
    DialogImages(
        images = listOf(link),
        initialIndex = 0,
        isVisible = isVisible,
        isClubImage = true,
        onClose = onClose
    )

@Composable
fun DialogPoster(link: String, isVisible: Boolean, onClose: () -> Unit) =
    DialogImages(
        images = listOf(link),
        initialIndex = 0,
        isVisible = isVisible,
        isPoster = true,
        onClose = onClose
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogImages(
    images: List<String>,
    initialIndex: Int,
    isVisible: Boolean,
    isPoster: Boolean = false,
    isClubImage: Boolean = false,
    onClose: () -> Unit
) {
    if (images.isEmpty()) return

    val scope = rememberCoroutineScope()
    val toast = rememberToastState()
    val (dataManager, permissionState) = rememberDataManager()

    var isDownloading by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    fun download(index: Int) {
        if (permissionState.isGranted) {
            if (isDownloading) return

            scope.launch {
                isDownloading = true
                val currentImage = images[index]
                val isDownloaded = dataManager.downloadImage(currentImage)

                toast.onShow(
                    resource = if (isDownloaded) Res.string.text_saved
                    else Res.string.text_error_loading
                )
                isDownloading = false
            }
        } else {
            permissionState.launchRequest()
        }
    }

    EdgeToEdge(darkTheme = isVisible, isAmoled = false)

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(250)) + scaleIn(
            initialScale = 0.85f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ),
        exit = fadeOut(tween(200)) + scaleOut(
            targetScale = 0.85f,
            animationSpec = tween(200)
        )
    ) {
        NavigationBackHandler(
            state = rememberNavigationEventState(NavigationEventInfo.None),
            isBackEnabled = isVisible,
            onBackCompleted = onClose
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        val imageStates = images.map { rememberZoomState() }
        val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = images::size)

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures { focusRequester.requestFocus() } }
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        when (event.key) {
                            Key.DirectionRight -> {
                                scope.launch {
                                    if (pagerState.currentPage < images.size - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                                true
                            }

                            Key.DirectionLeft -> {
                                scope.launch {
                                    if (pagerState.currentPage > 0) {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                                true
                            }

                            else -> false
                        }
                    } else false
                },
            containerColor = Color.Black,
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClose) {
                            VectorIcon(
                                imageVector = Icons.Close,
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            enabled = !isDownloading,
                            onClick = { download(pagerState.currentPage) }
                        ) {
                            if (isDownloading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                VectorIcon(Icons.Download, tint = Color.White)
                            }
                        }
                    },
                    title = {
                        Text(
                            color = Color.White,
                            text = if (isPoster) stringResource(Res.string.text_poster)
                            else if (isClubImage) stringResource(Res.string.text_picture)
                            else stringResource(Res.string.text_image_of, pagerState.currentPage + 1, images.size)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.5f),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                pageSpacing = 16.dp,
                userScrollEnabled = imageStates[pagerState.currentPage].scale == 1f,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) { page ->
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    ZoomableAsyncImage(
                        model = images[page],
                        state = imageStates[page],
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun loadingEffectModifier(
    shimmerColor: Color = Color.LightGray,
    shape: Shape = MaterialTheme.shapes.small
): Modifier {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    return Modifier
        .clip(shape)
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.linearGradient(
                    start = Offset.Zero,
                    end = Offset(translateAnim, translateAnim),
                    colors = listOf(
                        shimmerColor.copy(alpha = 0.6f),
                        shimmerColor.copy(alpha = 0.2f),
                        shimmerColor.copy(alpha = 0.6f)
                    )
                )
            )
        }
}
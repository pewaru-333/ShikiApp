@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.utils

import android.Manifest
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.roundToIntRect
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.core.net.toUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import org.application.shikiapp.R
import org.application.shikiapp.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.templates.VectorIcon
import org.application.shikiapp.utils.data.DataManager
import org.application.shikiapp.utils.extensions.clickableUrl
import org.application.shikiapp.utils.extensions.flattenImages
import org.application.shikiapp.utils.extensions.showToast
import org.application.shikiapp.utils.permissions.rememberPermissionState

@Composable
fun HtmlComment(commentContent: List<CommentContent>?) {
    var anchorBounds by remember { mutableStateOf(IntRect.Zero) }
    var galleryInfo by remember { mutableStateOf<Pair<List<CommentContent.ImageContent>, Int>?>(null) }

    BoxWithConstraints {
        val containerMaxWidth = this.maxWidth

        Column {
            commentContent?.forEach { item ->
                RenderContent(
                    content = item,
                    containerMaxWidth = containerMaxWidth,
                    onImageClick = {
                        val images = commentContent.flattenImages()
                        val index = images.indexOf(it)

                        galleryInfo = images to index
                    },
                    onLayout = {
                        if (galleryInfo != null) {
                            anchorBounds = it.boundsInWindow().roundToIntRect()
                        }
                    }
                )
            }
        }
    }

    galleryInfo?.let { gallery ->
        ImagesGallery(
            gallery = gallery,
            anchorBounds = anchorBounds,
            onClose = { galleryInfo = null }
        )
    }
}

@Composable
private fun RenderContent(
    content: CommentContent,
    containerMaxWidth: Dp,
    onImageClick: (CommentContent.ImageContent) -> Unit,
    onLayout: (LayoutCoordinates) -> Unit
) {
    when (content) {
        is CommentContent.TextContent -> {
            val context = LocalContext.current
            var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

            Text(
                text = content.text,
                style = MaterialTheme.typography.bodyLarge,
                inlineContent = content.inlineContent,
                onTextLayout = { layoutResult = it },
                modifier = Modifier.clickableUrl(context, content.text) { layoutResult }
            )
        }

        is CommentContent.ImageContent -> {
            val density = LocalDensity.current

            val maxHeight = 180.dp

            val originalWidth = with(density) { content.width.toDp() }
            val aspectRatio = if (content.height > 0) content.width / content.height else 1f

            var finalWidth = min(originalWidth, containerMaxWidth)
            var finalHeight = finalWidth / aspectRatio

            if (finalHeight > maxHeight) {
                finalHeight = maxHeight
                finalWidth = finalHeight * aspectRatio
            }

            AnimatedAsyncImage(
                model = content.previewUrl,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(finalWidth, finalHeight)
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onImageClick(content) }
                    .onGloballyPositioned(onLayout)
            )
        }

        is CommentContent.VideoContent -> {
            val context = LocalContext.current

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(12.dp))
                    .clickable {
                        context.startActivity(Intent(Intent.ACTION_VIEW, content.videoUrl.toUri()))
                    }
            ) {
                AnimatedAsyncImage(
                    model = content.previewUrl,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.8f
                )

                Surface(Modifier.size(56.dp), CircleShape, Color.Black.copy(alpha = 0.6f)) {
                    VectorIcon(
                        resId = R.drawable.vector_refresh,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxSize()
                    )
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = content.source.uppercase(),
                        modifier = Modifier.padding(6.dp, 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                        )
                    )
                }
            }
        }

        is CommentContent.SpoilerContent -> {
            var isVisible by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f))
                    .clickable { isVisible = !isVisible }
                    .padding(12.dp, 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VectorIcon(
                        modifier = Modifier.size(20.dp),
                        resId = if (isVisible) R.drawable.vector_keyboard_arrow_down
                        else R.drawable.vector_keyboard_arrow_right,
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = content.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                AnimatedVisibility(isVisible) {
                    Column(Modifier.padding(top = 8.dp)) {
                        content.items.forEach {
                            RenderContent(
                                content = it,
                                containerMaxWidth = containerMaxWidth,
                                onImageClick = onImageClick,
                                onLayout = onLayout
                            )
                        }
                    }
                }
            }
        }

        is CommentContent.QuoteContent -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp)
                    .clip(RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f))
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary)
                )

                Column(Modifier.padding(16.dp, 8.dp, 8.dp, 8.dp)) {
                    Text(
                        text = content.author,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(4.dp))

                    content.items.forEach {
                        RenderContent(it, containerMaxWidth, onImageClick, onLayout)
                    }
                }
            }
        }

        is CommentContent.BanContent -> {
            val context = LocalContext.current
            var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimatedAsyncImage(
                        model = content.moderatorAvatar,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = content.moderatorName,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = ": ",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = content.reason,
                    modifier = Modifier.clickableUrl(context, content.reason) { layoutResult },
                    onTextLayout = { layoutResult = it },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                )
            }
        }

        is CommentContent.LineBreakContent -> Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ImagesGallery(
    gallery: Pair<List<CommentContent.ImageContent>, Int>,
    anchorBounds: IntRect,
    onClose: () -> Unit
) {
    val (images, initialIndex) = gallery

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val imageStates = images.map { rememberZoomableImageState() }
    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = images::size)

    val dataManager = remember { DataManager(context) }
    val permissionState = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    var isDownloading by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isExpanded = true }

    val progress by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = if (isExpanded) {
            spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        } else {
            tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            )
        }
    )

    val provider = remember {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset = IntOffset.Zero
        }
    }

    fun anim(start: Float, stop: Float, fraction: Float) = start + fraction * (stop - start)

    fun close() {
        scope.launch {
            isExpanded = false
            delay(250)
            onClose()
        }
    }

    fun download() {
        if (permissionState.isGranted) {
            if (isDownloading) return

            scope.launch {
                isDownloading = true

                val currentImage = images[pagerState.currentPage]
                val url = currentImage.fullUrl ?: currentImage.previewUrl
                val isDownloaded = dataManager.downloadImage(url)

                context.showToast(
                    text = if (isDownloaded) R.string.text_saved
                    else R.string.text_error_loading
                )

                isDownloading = false
            }
        } else {
            permissionState.launchRequest()
        }
    }

    Popup(
        onDismissRequest = ::close,
        popupPositionProvider = provider,
        properties = PopupProperties(focusable = true, excludeFromSystemGesture = true)
    ) {
        BackHandler(onBack = ::close)

        BoxWithConstraints(Modifier.fillMaxSize()) {
            val screenWidth = constraints.maxWidth.toFloat()
            val screenHeight = constraints.maxHeight.toFloat()

            val startScaleX = anchorBounds.width.toFloat() / screenWidth
            val startScaleY = anchorBounds.height.toFloat() / screenHeight

            val startCenterX = anchorBounds.center.x.toFloat()
            val startCenterY = anchorBounds.center.y.toFloat()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = anim(startScaleX, 1f, progress)
                        scaleY = anim(startScaleY, 1f, progress)

                        if (isExpanded) {
                            translationX = anim(startCenterX - (screenWidth / 2), 0f, progress)
                            translationY = anim(startCenterY - (screenHeight / 2), 0f, progress)
                        } else {
                            translationX = anim(0f - (screenWidth / 2), 0f, progress)
                            translationY = anim(screenHeight - (screenHeight / 2), 0f, progress)
                        }

                        alpha = if (progress < 0.05f) 0f else 1f
                    }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            navigationIcon = { NavigationIcon(::close) },
                            actions = {
                                IconButton(onClick = ::download, enabled = !isDownloading) {
                                    if (isDownloading) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    } else {
                                        VectorIcon(R.drawable.vector_download)
                                    }
                                }
                            },
                            title = {
                                Text(
                                    text = stringResource(
                                        id = R.string.text_image_of,
                                        formatArgs = arrayOf(
                                            pagerState.currentPage + 1,
                                            images.size
                                        )
                                    )
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors().copy(
                                containerColor = Color.Black,
                                navigationIconContentColor = Color.White,
                                titleContentColor = Color.White,
                                actionIconContentColor = Color.White
                            )
                        )
                    }
                )
                { padding ->
                    HorizontalPager(
                        state = pagerState,
                        pageSpacing = 16.dp,
                        contentPadding = padding,
                        userScrollEnabled = (imageStates[pagerState.currentPage].zoomableState.zoomFraction
                            ?: 0f) == 0f,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) { page ->
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            ZoomableAsyncImage(
                                model = images[page].fullUrl ?: images[page].previewUrl,
                                state = imageStates[page],
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )

                            if (!imageStates[page].isImageDisplayed) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
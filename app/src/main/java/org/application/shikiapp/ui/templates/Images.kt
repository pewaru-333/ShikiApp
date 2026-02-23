@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.ui.templates

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter.Companion.DefaultTransform
import coil3.compose.AsyncImagePainter.State
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.compose.SubcomposeAsyncImageScope
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import org.application.shikiapp.R
import org.application.shikiapp.utils.data.DataManager
import org.application.shikiapp.utils.extensions.showToast
import org.application.shikiapp.utils.permissions.rememberPermissionState

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
        val shimmerColors = listOf(
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
        )

        val transition = rememberInfiniteTransition()
        val translateAnim = transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                repeatMode = RepeatMode.Reverse,
                animation = tween(
                    durationMillis = 1200,
                    easing = FastOutSlowInEasing
                )
            )
        )

        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(translateAnim.value, translateAnim.value)
        )

        Spacer(
            modifier = modifier
                .fillMaxSize()
                .background(brush)
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
                resId = R.drawable.vector_bad,
                modifier = Modifier.fillMaxSize(0.75f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
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
fun DialogPoster(link: String, isVisible: Boolean, onClose: () -> Unit) {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()

    val dataManager = remember { DataManager(context) }
    val permissionState = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    var isDownloading by remember { mutableStateOf(false) }

    fun download() {
        if (permissionState.isGranted) {
            if (isDownloading) return

            scope.launch {
                isDownloading = true

                val isDownloaded = dataManager.downloadImage(link)

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

    AnimatedVisibility(isVisible, Modifier, fadeIn(), fadeOut()) {
        BackHandler(isVisible, onClose)
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { NavigationIcon(onClose) },
                    title = { Text(stringResource(R.string.text_poster)) },
                    actions = {
                        IconButton(onClick = ::download, enabled = !isDownloading) {
                            if (isDownloading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                VectorIcon(R.drawable.vector_download)
                            }
                        }
                    }
                )
            }
        ) { values ->
            ZoomableAsyncImage(
                model = link,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
            )
        }
    }
}

@Composable
fun rememberLoadingEffect(shimmerColor: Color = Color.LightGray): Brush {
    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    return Brush.linearGradient(
        start = Offset.Zero,
        end = Offset(translateAnim.value, translateAnim.value),
        colors = listOf(
            shimmerColor.copy(alpha = 0.6f),
            shimmerColor.copy(alpha = 0.2f),
            shimmerColor.copy(alpha = 0.6f)
        )
    )
}
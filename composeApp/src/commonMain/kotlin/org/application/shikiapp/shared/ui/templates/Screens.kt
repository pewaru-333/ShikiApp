@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.shared.network.response.Response
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_error_loading
import shikiapp.composeapp.generated.resources.text_try_again

@Composable
fun <T> AnimatedScreen(
    response: Response<T, *>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    if (response is Response.Success) {
        content(response.data)
    } else {
        Crossfade(response, modifier) { targetState ->
            when (targetState) {
                is Response.Error -> ErrorScreen(onRetry)
                is Response.Loading -> LoadingScreen()
                is Response.Success -> content(targetState.data)
                else -> Unit
            }
        }
    }
}

@Composable
fun <T, P : Any> AnimatedScreen(
    response: Response<T, *>,
    onRetry: () -> Unit,
    pagingFlow: (T) -> Flow<PagingData<P>>,
    modifier: Modifier = Modifier,
    content: @Composable (T, LazyPagingItems<P>) -> Unit
) {
    AnimatedScreen(response, onRetry, modifier) { data ->
        val pagingItems = pagingFlow(data).collectAsLazyPagingItems()
        content(data, pagingItems)
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) =
    Box(
        content = { CircularProgressIndicator() },
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    )

@Composable
fun ErrorScreen(onRetry: () -> Unit = {}, onNavigate: (() -> Unit)? = null) =
    Box(Modifier.fillMaxSize()) {
        if (onNavigate != null) {
            TopAppBar(
                title = {},
                modifier = Modifier.align(Alignment.TopCenter), // не те отступы?
                navigationIcon = { NavigationIcon(onNavigate) }
            )
        }

        Column(Modifier.align(Alignment.Center), Arrangement.Center, Alignment.CenterHorizontally) {
            Text(stringResource(Res.string.text_error_loading))
            Button(onRetry) { Text(stringResource(Res.string.text_try_again)) }
        }
    }
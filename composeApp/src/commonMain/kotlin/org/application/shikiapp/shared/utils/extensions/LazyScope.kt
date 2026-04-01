package org.application.shikiapp.shared.utils.extensions

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.application.shikiapp.shared.models.ui.list.ContentSource
import org.application.shikiapp.shared.ui.templates.ErrorScreen
import org.application.shikiapp.shared.ui.templates.LoadingScreen

fun LazyListScope.appendLoadState(source: ContentSource<*>) {
    if (source.isLoadingAppend) {
        item { LoadingScreen(Modifier.padding(8.dp)) }
    }

    if (source.isError) {
        item { ErrorScreen(onRetry = source.onRetry) }
    }
}

fun LazyGridScope.appendLoadState(source: ContentSource<*>, span: (LazyGridItemSpanScope.() -> GridItemSpan)) {
    if (source.isLoadingAppend) {
        item(span = span) { LoadingScreen(Modifier.padding(8.dp)) }
    }

    if (source.isError) {
        item(span = span) { ErrorScreen(source.onRetry) }
    }
}

fun LazyStaggeredGridScope.appendLoadState(source: ContentSource<*>) {
    if (source.isLoadingAppend) {
        item(span = StaggeredGridItemSpan.FullLine) { LoadingScreen(Modifier.padding(8.dp)) }
    }

    if (source.isError) {
        item(span = StaggeredGridItemSpan.FullLine) { ErrorScreen(source.onRetry) }
    }
}
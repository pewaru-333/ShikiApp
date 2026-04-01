package org.application.shikiapp.shared.models.ui.list

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey

enum class ContentViewType {
    LIST_ITEM,
    ADAPTIVE_ITEM,
    GRID_ITEM_SMALL,
    STAGGERED_GRID_ITEM_IMAGES
}

data class ContentSource<out T>(
    val itemCount: Int,
    val itemProvider: (Int) -> T?,
    val itemKey: ((Int) -> Any)? = null,
    val isLoadingRefresh: Boolean = false,
    val isLoadingAppend: Boolean = false,
    val isError: Boolean = false,
    val onRetry: () -> Unit = {}
) {
    val isEmpty: Boolean get() = itemCount == 0 && !isLoadingRefresh && !isError
}

fun <T : Any> LazyPagingItems<T>.asSource(key: ((T) -> Any)? = null): ContentSource<T> =
    ContentSource(
        itemCount = itemCount,
        itemProvider = { this[it] },
        itemKey = key?.let { itemKey(it) },
        isLoadingRefresh = loadState.refresh is LoadState.Loading,
        isLoadingAppend = loadState.append is LoadState.Loading,
        isError = loadState.refresh is LoadState.Error || loadState.append is LoadState.Error,
        onRetry = ::retry
    )

fun <T : Any> List<T>.asSource(key: ((T) -> Any)? = null): ContentSource<T> =
    ContentSource(
        itemCount = size,
        itemProvider = { this[it] },
        itemKey = key?.let { k -> { index -> k(this[index]) } }
    )
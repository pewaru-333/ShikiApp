package org.application.shikiapp.shared.utils.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.application.shikiapp.shared.models.ui.Comment

class CommentListState(
    val comments: LazyPagingItems<Comment>,
    val listState: LazyListState,
    val textFieldState: TextFieldState,
    val focusRequester: FocusRequester,
    private val scope: CoroutineScope
) {
    fun onEventListener(event: Flow<Unit>) {
        scope.launch {
            event.collectLatest {
                textFieldState.clearText()
                focusRequester.requestFocus()

                snapshotFlow { comments.loadState.refresh }
                    .filter { it is LoadState.Loading }
                    .firstOrNull()

                snapshotFlow { comments.loadState.refresh }
                    .filter { it is LoadState.NotLoading }
                    .firstOrNull()

                yield()

                if (comments.itemCount > 0) {
                    listState.animateScrollToItem(0)
                }
            }
        }
    }

    fun refresh() {
        scope.launch {
            if (comments.itemCount > 0) {
                listState.scrollToItem(0)
            }

            yield()

            comments.refresh()
        }
    }
}

@Composable
fun rememberCommentListState(
    list: LazyPagingItems<Comment>,
    onCommentEvent: Flow<Unit>,
    listState: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope()
): CommentListState {
    val state = remember(list, scope) {
        CommentListState(
            comments = list,
            listState = listState,
            textFieldState = TextFieldState(),
            focusRequester = FocusRequester(),
            scope = scope
        )
    }

    LaunchedEffect(state, onCommentEvent) {
        state.onEventListener(onCommentEvent)
    }

    LaunchedEffect(list.itemCount) {
        if (listState.firstVisibleItemIndex <= 1) {
            listState.requestScrollToItem(0)
        }
    }

    return state
}
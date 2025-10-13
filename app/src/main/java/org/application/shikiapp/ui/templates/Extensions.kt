package org.application.shikiapp.ui.templates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import org.application.shikiapp.R
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.utils.navigation.Screen

fun LazyListScope.friends(list: LazyPagingItems<BasicContent>, onNavigate: (Screen) -> Unit) {
    when (list.loadState.refresh) {
        is LoadState.Error -> item { ErrorScreen(list::retry) }
        LoadState.Loading -> item { LoadingScreen() }
        is LoadState.NotLoading -> {
            items(list.itemCount) { index ->
                list[index]?.let {
                    BasicContentItem(
                        name = it.title,
                        link = it.poster,
                        modifier = Modifier.clickable { onNavigate(Screen.User(it.id.toLong())) }
                    )
                }
            }
            if (list.loadState.append == LoadState.Loading) item { LoadingScreen() }
            if (list.loadState.hasError) item { ErrorScreen(list::retry) }
        }
    }
}

fun LazyListScope.clubs(list: List<BasicContent>, onNavigate: (Screen) -> Unit) =
    if (list.isEmpty()) item {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(stringResource(R.string.text_empty))
        }
    }
    else items(list) {
        BasicContentItem(
            name = it.title,
            link = it.poster,
            modifier = Modifier.clickable { onNavigate(Screen.Club(it.id.toLong())) }
        )
    }
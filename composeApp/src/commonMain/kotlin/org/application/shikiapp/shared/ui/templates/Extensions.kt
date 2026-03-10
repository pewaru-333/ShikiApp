package org.application.shikiapp.shared.ui.templates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.utils.navigation.Screen
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_empty

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
            Text(stringResource(Res.string.text_empty))
        }
    }
    else items(list) {
        BasicContentItem(
            name = it.title,
            link = it.poster,
            modifier = Modifier.clickable { onNavigate(Screen.Club(it.id.toLong())) }
        )
    }
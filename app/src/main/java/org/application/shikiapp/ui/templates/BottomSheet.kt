package org.application.shikiapp.ui.templates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.application.shikiapp.R
import org.application.shikiapp.di.Preferences
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.ui.ExternalLink
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.network.response.AsyncData
import org.application.shikiapp.utils.BLANK

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    state: SheetState,
    rate: AsyncData<UserRate?>,
    favoured: AsyncData<Boolean>,
    toggleFavourite: () -> Unit,
    onEvent: (ContentDetailEvent) -> Unit,
) = ModalBottomSheet(
    sheetState = state,
    onDismissRequest = { onEvent(ContentDetailEvent.ShowSheet) },
) {
    if (Preferences.token != null) {
        ListItem(
            modifier = Modifier.clickable { onEvent(ContentDetailEvent.Media.ShowRate) },
            headlineContent = {
                Text(
                    text = stringResource(
                        when (val data = rate) {
                            AsyncData.Loading -> R.string.text_loading
                            is AsyncData.Success -> if (data.data == null) R.string.text_add_rate
                            else R.string.text_change_rate
                        }
                    )
                )
            },
            leadingContent = {
                when (rate) {
                    AsyncData.Loading -> CircularProgressIndicator(Modifier.size(24.dp))
                    is AsyncData.Success -> Icon(Icons.Outlined.Edit, null)
                }
            }
        )
        ListItem(
            modifier = Modifier
                .clickable(
                    enabled = favoured is AsyncData.Success,
                    onClick = toggleFavourite
                ),
            headlineContent = {
                Text(
                    text = stringResource(
                        when (val data = favoured) {
                            AsyncData.Loading -> R.string.text_loading
                            is AsyncData.Success -> if (data.data) R.string.text_remove_fav
                            else R.string.text_add_fav
                        }
                    )
                )
            },
            leadingContent = {
                when (val data = favoured) {
                    AsyncData.Loading -> CircularProgressIndicator(Modifier.size(24.dp))
                    is AsyncData.Success -> Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = if (data.data) Color.Red else LocalContentColor.current
                    )
                }
            }
        )
    }
    ListItem(
        headlineContent = { Text(stringResource(R.string.text_external_links)) },
        leadingContent = { Icon(Icons.AutoMirrored.Outlined.List, null) },
        modifier = Modifier.clickable { onEvent(ContentDetailEvent.Media.ShowLinks) }
    )
    ListItem(
        headlineContent = { Text(stringResource(R.string.text_open_in_browser)) },
        leadingContent = { Icon(painterResource(R.drawable.vector_website), null) },
        modifier = Modifier.clickable { onEvent(ContentDetailEvent.OpenLink) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetColumn(list: List<String>, state: SheetState, label: String, onHide: () -> Unit) =
    ModalBottomSheet(onHide, sheetState = state) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = label,
                modifier = Modifier.padding(start = 12.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }

        LazyColumn {
            items(list) { item ->
                ListItem(
                    headlineContent = { Text(item) },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                )
            }
        }

        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    sheetState: SheetState,
    website: String = BLANK,
    kind: String = BLANK,
    favoured: AsyncData<Boolean> = AsyncData.Success(false),
    handler: UriHandler = LocalUriHandler.current,
    onEvent: (ContentDetailEvent) -> Unit,
) = ModalBottomSheet(
    sheetState = sheetState,
    onDismissRequest = { onEvent(ContentDetailEvent.ShowSheet) }
) {
    if (Preferences.token != null) {
        ListItem(
            modifier = Modifier
                .clickable(enabled = favoured is AsyncData.Success) {
                    if (kind.isNotEmpty()) {
                        onEvent(ContentDetailEvent.Person.ToggleFavourite(kind))
                    } else {
                        onEvent(ContentDetailEvent.Character.ToggleFavourite)
                    }
                },
            headlineContent = {
                Text(
                    text = stringResource(
                        when (val data = favoured) {
                            AsyncData.Loading -> R.string.text_loading
                            is AsyncData.Success -> if (data.data) R.string.text_remove_fav
                            else R.string.text_add_fav
                        }
                    )
                )
            },
            leadingContent = {
                when (val data = favoured) {
                    AsyncData.Loading -> CircularProgressIndicator(Modifier.size(24.dp))
                    is AsyncData.Success -> Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = if (data.data) Color.Red else LocalContentColor.current
                    )
                }
            }
        )
    }

    if (website.isNotEmpty()) {
        ListItem(
            headlineContent = { Text(stringResource(R.string.text_official_site)) },
            modifier = Modifier.clickable { handler.openUri(website) },
            leadingContent = { Icon(painterResource(R.drawable.vector_website), null) }
        )
    }

    ListItem(
        headlineContent = { Text(stringResource(R.string.text_open_in_browser)) },
        modifier = Modifier.clickable { onEvent(ContentDetailEvent.OpenLink) },
        leadingContent = { Icon(painterResource(R.drawable.vector_website), null) }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LinksSheet(
    list: List<ExternalLink>,
    state: SheetState,
    hide: () -> Unit,
    handler: UriHandler = LocalUriHandler.current
) = ModalBottomSheet(hide, sheetState = state) {
    LazyColumn {
        items(list) {
            ListItem(
                modifier = Modifier.clickable { handler.openUri(it.url.toString()) },
                headlineContent = { Text(it.title) },
                leadingContent = {
                    AsyncImage(
                        contentDescription = null,
                        model = "https://www.google.com/s2/favicons?domain=${it.url.host}&sz=128",
                        modifier = Modifier.size(24.dp),
                        filterQuality = FilterQuality.High
                    )
                }
            )
        }
    }

    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
}
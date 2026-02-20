@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.ui.templates

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.application.shikiapp.R
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.ui.ExternalLink
import org.application.shikiapp.utils.BLANK

@Composable
fun SheetColumn(list: List<String>, label: String, onHide: () -> Unit) =
    ModalBottomSheet(onHide) {
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

@Composable
fun BottomSheet(
    website: String = BLANK,
    canShowLinks: Boolean = false,
    onEvent: (ContentDetailEvent) -> Unit
) = ModalBottomSheet(
    onDismissRequest = { onEvent(ContentDetailEvent.ShowSheet) }
) {
    if (website.isNotEmpty()) {
        val handler = LocalUriHandler.current

        ListItem(
            headlineContent = { Text(stringResource(R.string.text_official_site)) },
            modifier = Modifier.clickable { handler.openUri(website) },
            leadingContent = { VectorIcon(R.drawable.vector_website) }
        )
    }

    if (canShowLinks) {
        ListItem(
            headlineContent = { Text(stringResource(R.string.text_external_links)) },
            leadingContent = { VectorIcon(R.drawable.vector_list) },
            modifier = Modifier.clickable { onEvent(ContentDetailEvent.Media.ShowLinks) }
        )
    }

    ListItem(
        headlineContent = { Text(stringResource(R.string.text_open_in_browser)) },
        modifier = Modifier.clickable { onEvent(ContentDetailEvent.OpenLink) },
        leadingContent = { VectorIcon(R.drawable.vector_open_in_browser) }
    )
}

@Composable
fun LinksSheet(list: List<ExternalLink>, hide: () -> Unit) {
    val handler = LocalUriHandler.current

    ModalBottomSheet(hide) {
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
}
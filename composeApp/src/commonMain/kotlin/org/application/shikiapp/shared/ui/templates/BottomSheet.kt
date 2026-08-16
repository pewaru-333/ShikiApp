@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.ui.ExternalLink
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.extensions.toClipEntry
import org.application.shikiapp.shared.utils.ui.rememberLinkHandler
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.*

@Composable
fun BottomSheet(
    url: String,
    website: String = BLANK,
    canShowLinks: Boolean = false,
    onEvent: (ContentDetailEvent) -> Unit
) = ModalBottomSheet(
    onDismissRequest = { onEvent(ContentDetailEvent.ToggleDialog(null)) }
) {
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current

    val colors = ListItemDefaults.colors(
        containerColor = BottomSheetDefaults.ContainerColor,
        headlineColor = contentColorFor(BottomSheetDefaults.ContainerColor),
        leadingIconColor = contentColorFor(BottomSheetDefaults.ContainerColor)
    )

    if (website.isNotEmpty()) {
        val handler = LocalUriHandler.current

        ListItem(
            colors = colors,
            headlineContent = { Text(stringResource(Res.string.text_official_site)) },
            leadingContent = { VectorIcon(Icons.Website) },
            modifier = Modifier.clickable { handler.openUri(website) }
        )
    }

    ListItem(
        colors = colors,
        headlineContent = { Text(stringResource(Res.string.text_copy_link)) },
        leadingContent = { VectorIcon(Icons.Copy) },
        modifier = Modifier.clickable {
            scope.launch { clipboard.setClipEntry(url.toClipEntry()) }
        }
    )

    if (canShowLinks) {
        ListItem(
            colors = colors,
            headlineContent = { Text(stringResource(Res.string.text_external_links)) },
            leadingContent = { VectorIcon(Icons.List) },
            modifier = Modifier.clickable { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Links)) }
        )
    }

    ListItem(
        colors = colors,
        headlineContent = { Text(stringResource(Res.string.text_open_in_browser)) },
        leadingContent = { VectorIcon(Icons.OpenInBrowser) },
        modifier = Modifier.clickable { onEvent(ContentDetailEvent.OpenLink) }
    )
}

@Composable
fun LinksSheet(list: List<ExternalLink>, onHide: () -> Unit) {
    val handler = rememberLinkHandler()
    val colors = ListItemDefaults.colors(
        containerColor = BottomSheetDefaults.ContainerColor,
        headlineColor = contentColorFor(BottomSheetDefaults.ContainerColor),
        leadingIconColor = contentColorFor(BottomSheetDefaults.ContainerColor)
    )

    ModalBottomSheet(onHide) {
        Text(
            text = stringResource(Res.string.text_external_links),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 12.dp)
        )

        LazyColumn {
            items(list, ExternalLink::url) {
                ListItem(
                    colors = colors,
                    modifier = Modifier.clickable { handler.onOpenLink(it.url) },
                    headlineContent = { Text(stringResource(it.title)) },
                    leadingContent = {
                        AnimatedAsyncImage(
                            model = "https://www.google.com/s2/favicons?domain=${it.url}&sz=128",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
            }
        }
    }
}
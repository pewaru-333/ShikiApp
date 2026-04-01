@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
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
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.extensions.toClipEntry
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_copy_link
import shikiapp.composeapp.generated.resources.text_external_links
import shikiapp.composeapp.generated.resources.text_official_site
import shikiapp.composeapp.generated.resources.text_open_in_browser
import shikiapp.composeapp.generated.resources.vector_copy
import shikiapp.composeapp.generated.resources.vector_list
import shikiapp.composeapp.generated.resources.vector_open_in_browser
import shikiapp.composeapp.generated.resources.vector_website

@Composable
fun BottomSheet(
    url: String,
    website: String = BLANK,
    canShowLinks: Boolean = false,
    onEvent: (ContentDetailEvent) -> Unit
) = ModalBottomSheet(
    onDismissRequest = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
    contentWindowInsets = { WindowInsets.systemBars }
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
            leadingContent = { VectorIcon(Res.drawable.vector_website) },
            modifier = Modifier.clickable { handler.openUri(website) }
        )
    }

    ListItem(
        colors = colors,
        headlineContent = { Text(stringResource(Res.string.text_copy_link)) },
        leadingContent = { VectorIcon(Res.drawable.vector_copy) },
        modifier = Modifier.clickable {
            scope.launch { clipboard.setClipEntry(url.toClipEntry()) }
        }
    )

    if (canShowLinks) {
        ListItem(
            colors = colors,
            headlineContent = { Text(stringResource(Res.string.text_external_links)) },
            leadingContent = { VectorIcon(Res.drawable.vector_list) },
            modifier = Modifier.clickable { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Links)) }
        )
    }

    ListItem(
        colors = colors,
        headlineContent = { Text(stringResource(Res.string.text_open_in_browser)) },
        leadingContent = { VectorIcon(Res.drawable.vector_open_in_browser) },
        modifier = Modifier.clickable { onEvent(ContentDetailEvent.OpenLink) }
    )
}

@Composable
fun LinksSheet(list: List<ExternalLink>, onHide: () -> Unit) {
    val handler = LocalUriHandler.current

    ModalBottomSheet(
        onDismissRequest = onHide,
        contentWindowInsets = { WindowInsets.systemBars }
    ) {
        LazyColumn {
            items(list, ExternalLink::url) {
                ListItem(
                    modifier = Modifier.clickable { handler.openUri(it.url.toString()) },
                    headlineContent = { Text(it.title) },
                    leadingContent = {
                        AnimatedAsyncImage(
                            model = "https://www.google.com/s2/favicons?domain=${it.url.host}&sz=128",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = BottomSheetDefaults.ContainerColor,
                        headlineColor = contentColorFor(BottomSheetDefaults.ContainerColor),
                        leadingIconColor = contentColorFor(BottomSheetDefaults.ContainerColor)
                    )
                )
            }
        }
    }
}
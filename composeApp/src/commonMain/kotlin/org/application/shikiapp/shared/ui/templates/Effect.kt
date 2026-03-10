package org.application.shikiapp.shared.ui.templates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.shared.utils.ui.rememberLinkHandler

@Composable
fun LinkListener(flow: Flow<Unit>, onGetLink: () -> String?) {
    val linkHandler = rememberLinkHandler()

    LaunchedEffect(flow) {
        flow.collectLatest {
            val link = onGetLink() ?: return@collectLatest

            linkHandler.onOpenLink(link)
        }
    }
}
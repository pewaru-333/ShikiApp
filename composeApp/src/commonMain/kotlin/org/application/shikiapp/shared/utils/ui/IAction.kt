package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.Composable

interface IAction {
    fun onOpenLink(url: String)
}

@Composable
expect fun rememberLinkHandler(): IAction
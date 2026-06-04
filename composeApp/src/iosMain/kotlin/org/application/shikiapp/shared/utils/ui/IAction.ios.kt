package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

private class IosLinkHandler : IAction {
    override fun onOpenLink(url: String) = UIApplication.sharedApplication.openURL(
        url = NSURL(string = url),
        options = emptyMap<Any?, Any>(),
        completionHandler = null
    )
}

@Composable
actual fun rememberLinkHandler(): IAction = remember(::IosLinkHandler)
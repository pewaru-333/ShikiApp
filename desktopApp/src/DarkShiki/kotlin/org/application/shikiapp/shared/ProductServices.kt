package org.application.shikiapp.shared

import org.application.shikiapp.backend.dark.ProductServices as DarkServices
import org.application.shikiapp.shared.network.client.BridgeYggdrasilTransport

import org.application.shikiapp.shared.ui.theme.Icons
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.app_name_rip

internal object ProductServices {
    const val userAgent = "ShikiRip"
    val appName = Res.string.app_name_rip
    val appIcon get() = Icons.AppIconRip

    fun create() = DarkServices.create(BridgeYggdrasilTransport)
}

package org.application.shikiapp

import org.application.shikiapp.shared.network.client.BridgeYggdrasilTransport
import org.application.shikiapp.backend.dark.ProductServices as DarkServices

internal object ProductServices {
    fun create() = DarkServices.create(BridgeYggdrasilTransport)
}

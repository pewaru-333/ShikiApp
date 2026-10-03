package org.application.shikiapp

import org.application.shikiapp.backend.shiki.ProductServices as ShikiServices

internal object ProductServices {
    fun create() = ShikiServices.create()
}

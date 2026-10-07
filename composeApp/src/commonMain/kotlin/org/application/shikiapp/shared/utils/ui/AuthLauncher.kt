package org.application.shikiapp.shared.utils.ui

interface AuthLauncher {
    val usesWebView: Boolean
    val isAvailable: Boolean
    val isLaunching: Boolean

    fun launch()
}

package org.application.shikiapp.shared.utils.permissions

import androidx.compose.runtime.Composable

@Composable
actual fun rememberPermissionState(permission: String) = object : PermissionState {
    override var isGranted: Boolean
        get() = true
        set(value) = Unit
    override var showRationale: Boolean
        get() = false
        set(value) = Unit

    override fun launchRequest() = Unit
    override fun refresh() = Unit
    override fun openSettings() = Unit
}
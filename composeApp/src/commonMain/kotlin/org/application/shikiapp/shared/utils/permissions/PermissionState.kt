package org.application.shikiapp.shared.utils.permissions

interface PermissionState {
    var isGranted: Boolean
    var showRationale: Boolean

    fun launchRequest()
    fun refresh()
    fun openSettings()
}
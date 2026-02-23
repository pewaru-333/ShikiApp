package org.application.shikiapp.utils.permissions

interface PermissionState {
    var isGranted: Boolean
    var showRationale: Boolean

    fun launchRequest()
    fun refresh()
    fun openSettings()
}
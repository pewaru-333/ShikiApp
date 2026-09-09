package org.application.shikiapp.shared.utils.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

@Stable
class Permission(private val activity: Activity?, private val permission: String) : PermissionState {
    override var showRationale by mutableStateOf(false)
    override var isGranted by mutableStateOf(hasPermission())

    override fun launchRequest() {
        if (showRationale) openSettings()
        else launcher?.launch(permission)
    }

    override fun refresh() {
        isGranted = hasPermission()
    }

    override fun openSettings() {
        if (activity == null) return

        activity.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                data = Uri.fromParts("package", activity.packageName, null)
            }
        )
    }

    internal var launcher: ActivityResultLauncher<String>? = null

    private fun hasPermission(): Boolean {
        if (activity == null) return false

        val granted = when (permission) {
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

            else -> ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }

        showRationale = !granted && ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

        return granted
    }
}

@Composable
actual fun rememberPermissionState(permission: String): PermissionState {
    val activity = LocalActivity.current
    val permissionState = remember(permission) { Permission(activity, permission) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionState.refresh()
    }

    LifecycleResumeEffect(permission, launcher) {
        if (!permissionState.isGranted) permissionState.refresh()
        if (permissionState.launcher == null) permissionState.launcher = launcher

        onPauseOrDispose { permissionState.launcher = null }
    }

    return permissionState
}
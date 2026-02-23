package org.application.shikiapp.utils.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

@Stable
class Permission(private val context: Context, private val permission: String) : PermissionState {
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
        val action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS


        context.startActivity(
            Intent(action).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                data = Uri.fromParts("package", context.packageName, null)
            }
        )
    }

    internal var launcher: ActivityResultLauncher<String>? = null

    private fun hasPermission(): Boolean {
        val granted = when (permission) {
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) true
            else ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

            else -> ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        showRationale = !granted && ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)

        return granted
    }
}

@Composable
fun rememberPermissionState(permission: String): PermissionState {
    val context = LocalContext.current
    val permissionState = remember(permission) { Permission(context, permission) }
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
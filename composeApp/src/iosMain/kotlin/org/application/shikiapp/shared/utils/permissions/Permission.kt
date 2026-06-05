package org.application.shikiapp.shared.utils.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.Foundation.NSURL
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusRestricted
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString


@Stable
class Permission(private val delegate: PermissionDelegate) : PermissionState {
    override var showRationale by mutableStateOf(false)
    override var isGranted by mutableStateOf(delegate.isGranted())

    override fun launchRequest() {
        if (showRationale) {
            openSettings()
            return
        }

        delegate.request {
            refresh()
        }
    }

    override fun refresh() {
        isGranted = delegate.isGranted()
        showRationale = delegate.isDenied()
    }

    override fun openSettings() {
        val url = NSURL(string = UIApplicationOpenSettingsURLString)
        val application = UIApplication.sharedApplication

        if (application.canOpenURL(url)) {
            application.openURL(
                url = url,
                options = emptyMap<Any?, Any>(),
                completionHandler = null
            )
        }
    }
}

@Composable
actual fun rememberPermissionState(permission: String): PermissionState {
    val permissionState = remember(permission) {
        Permission(getPermissionDelegate(permission))
    }

    LifecycleResumeEffect(permission) {
        permissionState.refresh()
        onPauseOrDispose { }
    }

    return permissionState
}

fun getPermissionDelegate(permission: String) = when (permission) {
    "gallery" -> GalleryPermissionDelegate()
    else -> object : PermissionDelegate {
        override fun isGranted() = false
        override fun isDenied() = true
        override fun request(onResult: () -> Unit) {
            onResult()
        }
    }
}

interface PermissionDelegate {
    fun isGranted(): Boolean
    fun isDenied(): Boolean
    fun request(onResult: () -> Unit)
}

internal class GalleryPermissionDelegate : PermissionDelegate {
    override fun isGranted() =
        PHPhotoLibrary.authorizationStatus() == PHAuthorizationStatusAuthorized

    override fun isDenied(): Boolean {
        val status = PHPhotoLibrary.authorizationStatus()
        return status == PHAuthorizationStatusDenied || status == PHAuthorizationStatusRestricted
    }

    override fun request(onResult: () -> Unit) = PHPhotoLibrary.requestAuthorization { _ ->
        CoroutineScope(Dispatchers.Main).launch {
            onResult()
        }
    }
}
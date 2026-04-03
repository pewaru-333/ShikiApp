package org.application.shikiapp.shared.utils.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlin.collections.emptyMap

fun Context.openAppLinksSettings() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) Unit else
    try {
        val intent = Intent(Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        startActivity(intent)
    } catch (_: Exception) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        startActivity(intent)
    }

@get:RequiresApi(Build.VERSION_CODES.S)
private val Context.domainVerificationState: DomainVerificationUserState?
    get() = getSystemService(DomainVerificationManager::class.java)
        ?.getDomainVerificationUserState(packageName)

fun Context.isAllDomainsVerified(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
    val state = domainVerificationState ?: return false

    return state.isLinkHandlingAllowed &&
            state.hostToStateMap.values.all { it == DomainVerificationUserState.DOMAIN_STATE_SELECTED }
}

fun Context.getLinkDomains() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) emptyMap()
else domainVerificationState?.hostToStateMap.orEmpty()

fun Context.isLinkHandlingAllowed() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) true
else domainVerificationState?.isLinkHandlingAllowed ?: false

fun Context.showToast(text: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, length).show()
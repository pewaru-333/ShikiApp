package org.application.shikiapp.shared.utils.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_error

@RequiresApi(Build.VERSION_CODES.S)
fun Context.openAppLinksSettings() {
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

        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            asyncScope.launch {
                showToast(getString(Res.string.text_error))
            }
        }
    }
}

@get:RequiresApi(Build.VERSION_CODES.S)
private val Context.domainVerificationState: DomainVerificationUserState?
    get() = getSystemService(DomainVerificationManager::class.java)
        ?.getDomainVerificationUserState(packageName)

@RequiresApi(Build.VERSION_CODES.S)
fun Context.isAllDomainsVerified(): Boolean {
    val state = domainVerificationState ?: return false

    return state.isLinkHandlingAllowed &&
            state.hostToStateMap.values.all { it == DomainVerificationUserState.DOMAIN_STATE_SELECTED }
}

@RequiresApi(Build.VERSION_CODES.S)
fun Context.getLinkDomains() = domainVerificationState?.hostToStateMap.orEmpty()

@RequiresApi(Build.VERSION_CODES.S)
fun Context.isLinkHandlingAllowed() = domainVerificationState?.isLinkHandlingAllowed ?: false

fun Context.showToast(text: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, length).show()

inline val Context.asyncScope
    get() = (this as? LifecycleOwner)?.lifecycleScope ?: MainScope()
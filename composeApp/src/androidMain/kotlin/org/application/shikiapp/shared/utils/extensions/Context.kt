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

fun Context.openAppLinksSettings() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        openSettings(Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS)
    ) return

    if (openSettings(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)) return

    asyncScope.launch {
        showToast(getString(Res.string.text_error))
    }
}

private fun Context.openSettings(action: String): Boolean {
    val intent = Intent(action).apply {
        data = Uri.fromParts("package", packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    return try {
        startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: SecurityException) {
        false
    }
}

@get:RequiresApi(Build.VERSION_CODES.S)
private val Context.domainVerificationState: DomainVerificationUserState?
    get() = getSystemService(DomainVerificationManager::class.java)
        ?.getDomainVerificationUserState(packageName)

@RequiresApi(Build.VERSION_CODES.S)
fun Context.isAllDomainsVerified(): Boolean {
    val state = domainVerificationState ?: return false
    val domains = state.hostToStateMap

    return state.isLinkHandlingAllowed && domains.isNotEmpty() && domains.values.all {
        it == DomainVerificationUserState.DOMAIN_STATE_SELECTED || it == DomainVerificationUserState.DOMAIN_STATE_VERIFIED
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun Context.getLinkDomains(): Map<String, Int> = domainVerificationState?.hostToStateMap.orEmpty()

@RequiresApi(Build.VERSION_CODES.S)
fun Context.isLinkHandlingAllowed(): Boolean = domainVerificationState?.isLinkHandlingAllowed ?: false

fun Context.showToast(text: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, length).show()

inline val Context.asyncScope
    get() = (this as? LifecycleOwner)?.lifecycleScope ?: MainScope()
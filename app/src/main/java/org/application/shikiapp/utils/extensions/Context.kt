package org.application.shikiapp.utils.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri


fun Context.appLinksSettings() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) Intent()
else Intent(
    Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
    "package:${packageName}".toUri()
)

fun Context.isDomainVerified() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) true
else {
    val manager = getSystemService(DomainVerificationManager::class.java)
    val userState = manager.getDomainVerificationUserState(packageName)!!

    userState.hostToStateMap.all { it.value == DomainVerificationUserState.DOMAIN_STATE_SELECTED }
}
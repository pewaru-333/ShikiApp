package org.application.shikiapp.utils.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.net.toUri
import org.application.shikiapp.utils.BASE_URL


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

fun Context.openLinkInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, BASE_URL.toUri())
    val resolveInfo = packageManager.run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            resolveActivity(intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
        } else {
            resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }
    }

    if (resolveInfo != null) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, url.toFullUri()).setPackage(resolveInfo.activityInfo.packageName))
        } catch (_: Throwable) {
            Toast.makeText(this, "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(this, "На устройстве не найден подходящий браузер", Toast.LENGTH_SHORT).show()
    }
}
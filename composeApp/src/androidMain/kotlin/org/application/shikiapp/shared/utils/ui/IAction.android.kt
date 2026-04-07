package org.application.shikiapp.shared.utils.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import kotlinx.coroutines.runBlocking
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.utils.extensions.showToast
import org.application.shikiapp.shared.utils.extensions.toFullUrl
import org.jetbrains.compose.resources.getString
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_error_open_link
import shikiapp.composeapp.generated.resources.text_no_browser

private class AndroidLinkHandler(private val context: Context) : IAction {
    override fun onOpenLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, ApiRoutes.workingBaseUrl.toUri())
        val resolveInfo = context.packageManager.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                resolveActivity(intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
            } else {
                resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            }
        }

        if (resolveInfo != null) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, url.toFullUrl().toUri())
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setPackage(resolveInfo.activityInfo.packageName)

                context.startActivity(intent)
            } catch (_: Exception) {
                val text = runBlocking { getString(Res.string.text_error_open_link) }
                context.showToast(text)
            }
        } else {
            val text = runBlocking { getString(Res.string.text_no_browser) }
            context.showToast(text)
        }
    }
}

@Composable
actual fun rememberLinkHandler(): IAction {
    val context = LocalContext.current

    return remember(context) { AndroidLinkHandler(context.applicationContext) }
}
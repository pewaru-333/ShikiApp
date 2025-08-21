package org.application.shikiapp.utils.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.content.res.XmlResourceParser
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.LocaleManagerCompat
import androidx.core.net.toUri
import org.application.shikiapp.R
import org.application.shikiapp.utils.BASE_URL
import org.xmlpull.v1.XmlPullParser
import java.util.Locale


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
            showToast(R.string.text_error_open_link)
        }
    } else {
        showToast(R.string.text_no_browser)
    }
}

fun Context.getLanguageList() = mutableListOf<String>().apply {
    resources.getXml(resources.getIdentifier("_generated_res_locale_config", "xml", packageName)).use { xml ->
        while (xml.eventType != XmlResourceParser.END_DOCUMENT) {
            if (xml.eventType == XmlPullParser.START_TAG && xml.name == "locale") {
                add(xml.getAttributeValue(0))
            }
            xml.next()
        }
    }
}.sortedBy { Locale.forLanguageTag(it).getDisplayRegionName() }

fun Context.getSelectedLanguage() = LocaleManagerCompat.getSystemLocales(this)
    .getFirstMatch(getLanguageList().toTypedArray())?.language ?: Locale.ENGLISH.language

fun Context.showToast(text: Int, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, getString(text), length).show()
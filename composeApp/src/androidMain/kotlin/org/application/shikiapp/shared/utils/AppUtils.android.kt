package org.application.shikiapp.shared.utils

import android.Manifest
import android.app.LocaleManager
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformSpanStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.di.PlatformContext
import org.application.shikiapp.shared.utils.data.DataManager
import org.application.shikiapp.shared.utils.data.DataManagerAndroid
import org.application.shikiapp.shared.utils.enums.ScreenOrientation
import org.application.shikiapp.shared.utils.extensions.isAllDomainsVerified
import org.application.shikiapp.shared.utils.extensions.openAppLinksSettings
import org.application.shikiapp.shared.utils.extensions.showToast
import org.application.shikiapp.shared.utils.permissions.PermissionState
import org.application.shikiapp.shared.utils.permissions.rememberPermissionState
import org.application.shikiapp.shared.utils.ui.Formatter
import org.application.shikiapp.shared.utils.ui.IDomain
import org.application.shikiapp.shared.utils.ui.IToast
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import java.util.Locale

actual fun fromHtml(text: String?) =
    if (text == null) androidx.compose.ui.text.AnnotatedString(BLANK)
    else AnnotatedString.fromHtml(
        htmlString = Formatter.localizeNames(text),
        linkStyles = TextLinkStyles(
            style = SpanStyle(
                color = Color(0xFF33BBFF),
                textDecoration = TextDecoration.Underline,
                platformStyle = PlatformSpanStyle.Default
            )
        )
    )

actual fun getDefaultLocale(context: PlatformContext): String =
    context.resources.configuration.locales[0].toLanguageTag()

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
actual fun isDynamicColorAvailable() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

actual object AppLocale {
    actual val current: String
        @Composable get() = getDefaultLocale(LocalContext.current)

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val context = LocalContext.current
        val configuration = LocalConfiguration.current

        val locale = Locale.forLanguageTag(value ?: current)

        Locale.setDefault(locale)
        configuration.setLocale(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            LaunchedEffect(locale) {
                val localeManager = context.getSystemService(LocaleManager::class.java)
                val currentLocale = localeManager.applicationLocales[0]?.language

                if (currentLocale != locale.language) {
                    localeManager.applicationLocales = LocaleList.forLanguageTags(locale.language)
                }
            }
        }

        return LocalContext provides context.createConfigurationContext(configuration)
    }
}

@Composable
actual fun rememberDataManager(): Pair<DataManager, PermissionState> {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val dataManager = remember { DataManagerAndroid(context) }

    return Pair(DataManager(dataManager), permissionState)
}

@Composable
actual fun rememberVerifiedDomain(): IDomain {
    val context = LocalContext.current

    var verified by remember { mutableStateOf(context.isAllDomainsVerified()) }

    LifecycleResumeEffect(Unit) {
        verified = context.isAllDomainsVerified()

        onPauseOrDispose { }
    }

    return remember(verified) {
        object : IDomain {
            override val isVerified: Boolean
                get() = verified

            override fun onSettingsLaunch() = context.openAppLinksSettings()
        }
    }
}

@Composable
actual fun rememberToastState(): IToast {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    return object : IToast {
        override fun onShow(resource: StringResource) {
            scope.launch {
                context.showToast(getString(resource))
            }
        }

        override fun onShow(text: String) {
            context.showToast(text)
        }
    }
}

@Composable
actual fun platformColorScheme(darkTheme: Boolean, dynamicColor: Boolean) =
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || !dynamicColor) null
    else {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }

@Composable
actual fun EdgeToEdge(darkTheme: Boolean, isAmoled: Boolean) {
    val activity = LocalActivity.current as? ComponentActivity ?: return

    DisposableEffect(darkTheme, isAmoled) {
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT,
                detectDarkMode = { darkTheme }
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                darkScrim = if (isAmoled) {
                    android.graphics.Color.TRANSPARENT
                } else {
                    android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)
                },
                detectDarkMode = { darkTheme }
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.window.isNavigationBarContrastEnforced = false
        }

        onDispose { }
    }
}

@Composable
actual fun LockScreenOrientation(orientation: ScreenOrientation) {
    val activity = LocalActivity.current ?: return

    DisposableEffect(orientation) {
        val lastOrientation = activity.requestedOrientation

        activity.requestedOrientation = when (orientation) {
            ScreenOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            ScreenOrientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            ScreenOrientation.UNSPECIFIED -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        onDispose {
            activity.requestedOrientation = lastOrientation
        }
    }
}

@Composable
actual fun HideSystemBars() {
    val activity = LocalActivity.current ?: return
    val view = LocalView.current

    DisposableEffect(view) {
        val insetsController = WindowCompat.getInsetsController(activity.window, view)

        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())

        onDispose {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}
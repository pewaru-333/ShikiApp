package org.application.shikiapp.shared.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.runBlocking
import org.application.shikiapp.shared.di.PlatformContext
import org.application.shikiapp.shared.utils.data.DataManager
import org.application.shikiapp.shared.utils.data.DataManagerIos
import org.application.shikiapp.shared.utils.enums.ScreenOrientation
import org.application.shikiapp.shared.utils.permissions.PermissionState
import org.application.shikiapp.shared.utils.permissions.rememberPermissionState
import org.application.shikiapp.shared.utils.ui.HtmlParser
import org.application.shikiapp.shared.utils.ui.IDomain
import org.application.shikiapp.shared.utils.ui.IToast
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSBundle
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDateComponents
import platform.Foundation.NSLocale
import platform.Foundation.NSRelativeDateTimeFormatter
import platform.Foundation.NSRelativeDateTimeFormatterStyleNamed
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDomainMask
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.Foundation.localeIdentifier
import platform.Foundation.preferredLanguages
import platform.Foundation.setValue
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIDevice
import platform.UIKit.UIFont
import platform.UIKit.UIInterfaceOrientationLandscapeRight
import platform.UIKit.UIInterfaceOrientationMask
import platform.UIKit.UIInterfaceOrientationMaskAll
import platform.UIKit.UIInterfaceOrientationMaskLandscape
import platform.UIKit.UIInterfaceOrientationMaskPortrait
import platform.UIKit.UIInterfaceOrientationPortrait
import platform.UIKit.UIInterfaceOrientationUnknown
import platform.UIKit.UILabel
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIScreen
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIView
import platform.UIKit.UIViewAnimationOptionCurveEaseOut
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.UIKit.UIWindowSceneGeometryPreferencesIOS
import platform.UIKit.attemptRotationToDeviceOrientation
import platform.UIKit.setStatusBarStyle
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual fun fromHtml(text: String?) = buildAnnotatedString {
    Ksoup.parse(text.orEmpty()).body().childNodes().forEach { parseNode(it, this) }
}

private fun parseNode(node: Node, builder: AnnotatedString.Builder) {
    when (node) {
        is TextNode -> builder.append(node.text())
        is Element -> {
            when (node.tagName().lowercase()) {
                "a" -> {
                    val url = node.attr("abs:href").ifEmpty { node.attr("href") }

                    builder.pushLink(
                        LinkAnnotation.Url(
                            url = url,
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = Color(0xFF33BBFF),
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        )
                    )
                    node.childNodes().forEach { parseNode(it, builder) }
                    builder.pop()
                }
                "li" -> {
                    builder.append("\n  • ")
                    node.childNodes().forEach { parseNode(it, builder) }
                }
                "br" -> builder.append("\n")
                "p" -> {
                    builder.append("\n")
                    node.childNodes().forEach { parseNode(it, builder) }
                    builder.append("\n")
                }

                else -> builder.withStyle(HtmlParser.getStyleForTag(node.tagName())) {
                    node.childNodes().forEach { parseNode(it, builder) }
                }
            }
        }
    }
}

fun getCacheDirectory(): String {
    val paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
    return paths.firstOrNull() as? String ?: NSTemporaryDirectory()
}

fun getUserAgentValue(key: String) =
    NSBundle.mainBundle.objectForInfoDictionaryKey(key) as String? ?: "ShikiApp"

actual fun getDefaultLocale(context: PlatformContext): String {
    val languageTag = NSLocale.preferredLanguages.firstOrNull() as? String
    if (languageTag != null) {
        return languageTag
    }

    return NSLocale.currentLocale.localeIdentifier.replace('_', '-')
}

actual fun isDynamicColorAvailable() = false

private const val LANG_KEY = "AppleLanguages"
private val defaultLocale = NSLocale.preferredLanguages.first() as String
private val LocalAppLocale = staticCompositionLocalOf { defaultLocale }

actual object AppLocale {

    fun getLocale() = NSUserDefaults.standardUserDefaults.stringArrayForKey(LANG_KEY)
        ?.firstOrNull() as? String
        ?: NSLocale.currentLocale.languageCode

    actual val current: String
        @Composable get() = LocalAppLocale.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val newLocale = value ?: defaultLocale

        if (value == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LANG_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(arrayListOf(newLocale), LANG_KEY)
        }

        return LocalAppLocale provides newLocale
    }
}

@Composable
actual fun rememberDataManager(): Pair<DataManager, PermissionState> {
    val permissionState = rememberPermissionState("gallery")
    val dataManager = remember { DataManagerIos() }

    return Pair(DataManager(dataManager), permissionState)
}

@Composable
actual fun rememberVerifiedDomain() = remember {
    object : IDomain {
        override val isVerified = true
        override fun onSettingsLaunch() = Unit
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberToastState() = remember {
    object : IToast {
        override fun onShow(resource: StringResource) {
            val message = runBlocking { getString(resource) }
            showToast(message)
        }

        override fun onShow(text: String) {
            showToast(text)
        }

        private fun showToast(message: String) {
            dispatch_async(dispatch_get_main_queue()) {
                val window = UIApplication.sharedApplication.windows
                    .firstOrNull { (it as UIWindow).isKeyWindow() } as? UIWindow
                    ?: return@dispatch_async

                val width = UIScreen.mainScreen.bounds.useContents { size.width }
                val height = UIScreen.mainScreen.bounds.useContents { size.height }

                val toastLabel = UILabel(
                    frame = CGRectMake(
                        x = (width - 250.0) / 2,
                        y = height - 120.0,
                        width = 250.0,
                        height = 50.0
                    )
                ).apply {
                    backgroundColor = UIColor.blackColor.colorWithAlphaComponent(0.8)
                    textColor = UIColor.whiteColor
                    textAlignment = NSTextAlignmentCenter
                    font = UIFont.systemFontOfSize(15.0)
                    text = message
                    alpha = 0.0
                    layer.cornerRadius = 10.0
                    clipsToBounds = true
                    numberOfLines = 0
                }

                window.addSubview(toastLabel)

                UIView.animateWithDuration(
                    duration = 0.3,
                    animations = { toastLabel.alpha = 1.0 },
                    completion = {
                        UIView.animateWithDuration(
                            duration = 0.3,
                            delay = 2.0,
                            options = UIViewAnimationOptionCurveEaseOut,
                            animations = { toastLabel.alpha = 0.0 },
                            completion = { toastLabel.removeFromSuperview() }
                        )
                    }
                )
            }
        }
    }
}

@Composable
actual fun platformColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme? =
    if (darkTheme) darkColorScheme()
    else lightColorScheme()

@Composable
actual fun EdgeToEdge(darkTheme: Boolean, isAmoled: Boolean) {
    LaunchedEffect(darkTheme) {
        val statusBarStyle = if (darkTheme) UIStatusBarStyleLightContent
        else UIStatusBarStyleDarkContent

        UIApplication.sharedApplication.setStatusBarStyle(statusBarStyle, true)
    }
}

object OrientationManager {
    var currentMask: UIInterfaceOrientationMask = UIInterfaceOrientationMaskAll
}

@Composable
actual fun LockScreenOrientation(orientation: ScreenOrientation) {
    DisposableEffect(orientation) {
        val previousMask = OrientationManager.currentMask

        val mask = when (orientation) {
            ScreenOrientation.PORTRAIT -> UIInterfaceOrientationMaskPortrait
            ScreenOrientation.LANDSCAPE -> UIInterfaceOrientationMaskLandscape
            ScreenOrientation.UNSPECIFIED -> UIInterfaceOrientationMaskAll
        }

        OrientationManager.currentMask = mask
        forceOrientationUpdate(mask)

        onDispose {
            OrientationManager.currentMask = previousMask
            forceOrientationUpdate(previousMask)
        }
    }
}

private fun forceOrientationUpdate(mask: UIInterfaceOrientationMask) {
    val window = UIApplication.sharedApplication.connectedScenes.firstNotNullOfOrNull { scene ->
        (scene as? UIWindowScene)?.takeIf { it.activationState == UISceneActivationStateForegroundActive }
    }

    if (UIDevice.currentDevice.systemVersion.substringBefore('.').toInt() >= 16) {
        if (window != null) {
            val preferences = UIWindowSceneGeometryPreferencesIOS(mask)
            window.requestGeometryUpdateWithPreferences(
                geometryPreferences = preferences,
                errorHandler = { }
            )
        }
    } else {
        val orientation = when (mask) {
            UIInterfaceOrientationMaskPortrait -> UIInterfaceOrientationPortrait
            UIInterfaceOrientationMaskLandscape -> UIInterfaceOrientationLandscapeRight
            else -> UIInterfaceOrientationUnknown
        }

        UIDevice.currentDevice.setValue(orientation, "orientation")
    }

    UIViewController.attemptRotationToDeviceOrientation()
}

object SystemBarsManager {
    var onVisibilityChanged: ((Boolean) -> Unit)? = null

    var isHidden: Boolean = false
        set(value) {
            field = value
            onVisibilityChanged?.invoke(value)
        }
}

@Composable
actual fun HideSystemBars() {
    DisposableEffect(Unit) {
        SystemBarsManager.isHidden = true

        onDispose {
            SystemBarsManager.isHidden = false
        }
    }
}

actual fun formatRelativeDays(daysAgo: Int): String {
    val formatter = NSRelativeDateTimeFormatter().apply {
        dateTimeStyle = NSRelativeDateTimeFormatterStyleNamed
    }
    val components = NSDateComponents().apply {
        day = -daysAgo.toLong()
    }
    return formatter.localizedStringFromDateComponents(components)
}
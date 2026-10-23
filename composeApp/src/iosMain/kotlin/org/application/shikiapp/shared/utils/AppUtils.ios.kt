package org.application.shikiapp.shared.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.window.DialogProperties
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.utils.data.DataManagerIos
import org.application.shikiapp.shared.utils.data.IDataManager
import org.application.shikiapp.shared.utils.enums.ScreenOrientation
import org.application.shikiapp.shared.utils.permissions.PermissionState
import org.application.shikiapp.shared.utils.permissions.rememberPermissionState
import org.application.shikiapp.shared.utils.ui.HtmlParser
import org.application.shikiapp.shared.utils.ui.IDomain
import org.application.shikiapp.shared.utils.ui.IToast
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import platform.Foundation.*
import platform.UIKit.*

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
actual fun rememberDataManager(): Pair<IDataManager, PermissionState> {
    val permissionState = rememberPermissionState("gallery")
    val dataManager = remember { DataManagerIos() }

    return Pair(dataManager, permissionState)
}

@Composable
actual fun rememberVerifiedDomain() = remember {
    object : IDomain {
        override val isVerified = true
        override fun onSettingsLaunch() = Unit
    }
}

@Composable
actual fun rememberToastState(): IToast {
    val controller = LocalUIViewController.current
    val scope = rememberCoroutineScope()

    return remember(controller, scope) {
        object : IToast {
            override fun onShow(resource: StringResource) {
                scope.launch {
                    showToast(getString(resource))
                }
            }

            override fun onShow(text: String) {
                scope.launch {
                    showToast(text)
                }
            }

            private fun showToast(message: String) {
                if (message.isBlank()) return

                val window = controller.view.window ?: return
                val toastView = UIView().apply {
                    translatesAutoresizingMaskIntoConstraints = false
                    backgroundColor = UIColor.blackColor.colorWithAlphaComponent(0.8)
                    alpha = 0.0
                    layer.cornerRadius = 10.0
                    clipsToBounds = true
                }
                val label = UILabel().apply {
                    translatesAutoresizingMaskIntoConstraints = false
                    textColor = UIColor.whiteColor
                    textAlignment = NSTextAlignmentCenter
                    font = UIFont.systemFontOfSize(15.0)
                    text = message
                    numberOfLines = 0
                }

                toastView.addSubview(label)
                window.addSubview(toastView)

                NSLayoutConstraint.activateConstraints(
                    listOf(
                        toastView.centerXAnchor.constraintEqualToAnchor(window.safeAreaLayoutGuide.centerXAnchor),
                        toastView.bottomAnchor.constraintEqualToAnchor(window.safeAreaLayoutGuide.bottomAnchor, -32.0),
                        toastView.widthAnchor.constraintLessThanOrEqualToConstant(320.0),
                        toastView.leadingAnchor.constraintGreaterThanOrEqualToAnchor(window.safeAreaLayoutGuide.leadingAnchor, 16.0),
                        toastView.trailingAnchor.constraintLessThanOrEqualToAnchor(window.safeAreaLayoutGuide.trailingAnchor, -16.0),
                        label.leadingAnchor.constraintEqualToAnchor(toastView.leadingAnchor, 12.0),
                        label.trailingAnchor.constraintEqualToAnchor(toastView.trailingAnchor, -12.0),
                        label.topAnchor.constraintEqualToAnchor(toastView.topAnchor, 8.0),
                        label.bottomAnchor.constraintEqualToAnchor(toastView.bottomAnchor, -8.0)
                    )
                )

                UIView.animateWithDuration(
                    duration = 0.3,
                    animations = { toastView.alpha = 1.0 },
                    completion = {
                        UIView.animateWithDuration(
                            duration = 0.3,
                            delay = 2.0,
                            options = UIViewAnimationOptionCurveEaseOut,
                            animations = { toastView.alpha = 0.0 },
                            completion = { toastView.removeFromSuperview() }
                        )
                    }
                )
            }
        }
    }
}

@Composable
actual fun platformColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme? = null

@Composable
actual fun EdgeToEdge(darkTheme: Boolean, isAmoled: Boolean) = SideEffect {
    SystemBarsManager.isDarkTheme = darkTheme
}

object OrientationManager {
    var currentMask: UIInterfaceOrientationMask = UIInterfaceOrientationMaskPortrait
    var onOrientationChange: ((UIInterfaceOrientationMask) -> Unit)? = null
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
        OrientationManager.onOrientationChange?.invoke(mask)

        onDispose {
            OrientationManager.currentMask = previousMask
            OrientationManager.onOrientationChange?.invoke(previousMask)
        }
    }
}

object SystemBarsManager {
    var onAppearanceChanged: (() -> Unit)? = null

    var isDarkTheme: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            onAppearanceChanged?.invoke()
        }

    var isHidden: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            onAppearanceChanged?.invoke()
        }
}

@Composable
actual fun HideSystemBars() {
    DisposableEffect(Unit) {
        val wasHidden = SystemBarsManager.isHidden
        SystemBarsManager.isHidden = true

        onDispose {
            SystemBarsManager.isHidden = wasHidden
        }
    }
}

@Composable
actual fun DialogSystemBarColors() = Unit

actual fun formatRelativeDays(daysAgo: Int): String {
    val formatter = NSRelativeDateTimeFormatter().apply {
        dateTimeStyle = NSRelativeDateTimeFormatterStyleNamed
        locale = NSLocale(AppLocale.getLocale())
    }
    val components = NSDateComponents().apply {
        day = -daysAgo.toLong()
    }
    return formatter.localizedStringFromDateComponents(components)
}

actual fun getFullscreenDialogProperties() = DialogProperties(
    usePlatformDefaultWidth = false
)
package org.application.shikiapp.shared.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.UriHandler
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
import com.ibm.icu.text.RelativeDateTimeFormatter
import com.sun.jna.Platform
import org.application.shikiapp.shared.di.PlatformContext
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.utils.data.DataManager
import org.application.shikiapp.shared.utils.data.DataManagerDesktop
import org.application.shikiapp.shared.utils.enums.ScreenOrientation
import org.application.shikiapp.shared.utils.permissions.PermissionState
import org.application.shikiapp.shared.utils.ui.HtmlParser
import org.application.shikiapp.shared.utils.ui.IDomain
import org.application.shikiapp.shared.utils.ui.IToast
import org.jetbrains.compose.resources.StringResource
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import java.io.File
import java.util.Locale

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
                "p", "div" -> {
                    if (builder.length > 0) builder.append("\n")
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

fun initVlc() {
    val platform = Platform.RESOURCE_PREFIX
    val resourcesDir = System.getProperty("compose.application.resources.dir")?.let(::File)

    val vlcPath = if (resourcesDir != null && resourcesDir.exists() && File(resourcesDir, platform).exists()) {
        File(resourcesDir, platform).absolutePath
    } else {
        val userDir = System.getProperty("user.dir")
        val baseDir = if (userDir.endsWith("desktopApp")) File(userDir)
        else File(userDir, "desktopApp")

        File(baseDir, "files/vlc/$platform").absolutePath
    }

    val vlcDirectory = File(vlcPath)
    if (vlcDirectory.exists() && vlcDirectory.isDirectory) {
        System.setProperty("jna.library.path", vlcPath)
        System.setProperty("VLC_PLUGIN_PATH", "$vlcPath/plugins")
    }

    NativeDiscovery().discover()
}

actual fun getDefaultLocale(context: PlatformContext): String = Locale.getDefault().language

actual fun isDynamicColorAvailable() = false

actual object AppLocale {
    private class DesktopContext : PlatformContext()

    private val defaultLocale = getDefaultLocale(DesktopContext())

    private val AppLocale = staticCompositionLocalOf { defaultLocale }

    actual val current: String
        @Composable get() = getDefaultLocale(DesktopContext())

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val locale = Locale.forLanguageTag(value ?: current)
        Locale.setDefault(locale)

        return AppLocale provides locale.language
    }
}

@Composable
actual fun rememberDataManager(): Pair<DataManager, PermissionState> {
    val dataManager = remember(::DataManagerDesktop)
    val permissionState = object : PermissionState {
        override var isGranted: Boolean
            get() = true
            set(value) = Unit
        override var showRationale: Boolean
            get() = false
            set(value) = Unit

        override fun launchRequest() = Unit
        override fun refresh() = Unit
        override fun openSettings() = Unit

    }

    return Pair(DataManager(dataManager), permissionState)
}

@Composable
actual fun rememberVerifiedDomain() = object : IDomain {
    override val isVerified = true
    override fun onSettingsLaunch() = Unit
}

@Composable
actual fun rememberToastState(): IToast {
    return object : IToast {
        override fun onShow(resource: StringResource) = Unit
        override fun onShow(text: String) = Unit
    }
}

@Composable
actual fun platformColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme? =
    if (darkTheme) darkColorScheme()
    else lightColorScheme()

@Composable
actual fun EdgeToEdge(darkTheme: Boolean, isAmoled: Boolean) = Unit

@Composable
actual fun LockScreenOrientation(orientation: ScreenOrientation) = Unit

@Composable
actual fun HideSystemBars() = Unit

actual fun formatRelativeDays(daysAgo: Int): String {
    val formatter = RelativeDateTimeFormatter.getInstance()

    return when (daysAgo) {
        0 -> formatter.format(RelativeDateTimeFormatter.Direction.THIS, RelativeDateTimeFormatter.AbsoluteUnit.DAY)
        1 -> formatter.format(RelativeDateTimeFormatter.Direction.LAST, RelativeDateTimeFormatter.AbsoluteUnit.DAY)
        else -> formatter.format(daysAgo.toDouble(), RelativeDateTimeFormatter.Direction.LAST, RelativeDateTimeFormatter.RelativeUnit.DAYS)
    }
}

actual fun launchAuth(uriHandler: UriHandler) = uriHandler.openUri(ApiRoutes.authUri)
package org.application.shikiapp.shared.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navDeepLink
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import coil3.ComponentRegistry
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.Json
import okio.Path
import org.application.shikiapp.shared.di.AppConfig
import org.application.shikiapp.shared.di.PlatformContext
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.network.client.ImageInterceptor
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.utils.data.DataManager
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.ScreenOrientation
import org.application.shikiapp.shared.utils.permissions.PermissionState
import org.application.shikiapp.shared.utils.ui.IDomain
import org.application.shikiapp.shared.utils.ui.IToast
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@OptIn(ExperimentalCoilApi::class)
fun sharedImageLoader(
    context: coil3.PlatformContext,
    cacheDir: Path,
    components: ComponentRegistry.Builder.() -> Unit = {}
) = ImageLoader.Builder(context)
    .components {
        add(KtorNetworkFetcherFactory(Network.baseClient))
        add(ImageInterceptor)
        components()
    }
    .crossfade(200)
    .memoryCachePolicy(CachePolicy.ENABLED)
    .memoryCache {
        MemoryCache.Builder()
            .maxSizePercent(context, 0.25)
            .build()
    }
    .diskCachePolicy(CachePolicy.ENABLED)
    .diskCache {
        DiskCache.Builder()
            .maxSizeBytes(Preferences.cache.toLong() * 1024 * 1024L)
            .directory(cacheDir)
            .build()
    }
    .build()

inline fun <reified T> serializableNavType(
    serializer: KSerializer<T>,
    isNullableAllowed: Boolean = true,
    json: Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
) = object : NavType<T>(isNullableAllowed) {

    override fun put(bundle: SavedState, key: String, value: T) = bundle.write {
        putString(key, json.encodeToString(serializer, value))
    }

    override fun get(bundle: SavedState, key: String): T = bundle.read {
        getString(key).let { json.decodeFromString(serializer, it) }
    }

    override fun parseValue(value: String) = json.decodeFromString(serializer, value)

    override fun serializeAsValue(value: T) = json.encodeToString(serializer, value)
}

inline fun <reified T : Any> generateDeepLinks(
    vararg paths: String,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap()
) = (listOf(AppConfig.baseUrl) + AppConfig.urlMirrors).flatMap { domain ->
    paths.map { path ->
        navDeepLink<T>(
            basePath = "*",
            typeMap = typeMap,
            deepLinkBuilder = { uriPattern = "$domain$path" }
        )
    }
}

@Composable
inline fun <reified T : ViewModel> viewModel(crossinline factory: (SavedStateHandle) -> T) =
    viewModel { factory(createSavedStateHandle()) }

val linkedTypeMap = mapOf(
    typeOf<LinkedType?>() to serializableNavType(LinkedType.serializer().nullable)
)

expect object AppLocale {
    val current: String @Composable get

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}

expect fun fromHtml(text: String?): AnnotatedString

expect fun getDefaultLocale(context: PlatformContext): String

expect fun isDynamicColorAvailable(): Boolean

@Composable
expect fun rememberDataManager(): Pair<DataManager, PermissionState>

@Composable
expect fun rememberVerifiedDomain() : IDomain

@Composable
expect fun rememberToastState(): IToast

@Composable
expect fun platformColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme?

@Composable
expect fun EdgeToEdge(darkTheme: Boolean, isAmoled: Boolean)

@Composable
expect fun LockScreenOrientation(orientation: ScreenOrientation)

@Composable
expect fun HideSystemBars()
package org.application.shikiapp.shared.utils.data

import com.sun.jna.Library
import com.sun.jna.Native
import org.application.shikiapp.shared.network.YggbridgeNative
import java.io.File

internal object YggbridgeNativeLoader {
    val instance: YggbridgeNative by lazy {
        val library = nativeLibrary()

        Native.load(
            /* name = */ library.absolutePath,
            /* interfaceClass = */ YggbridgeNative::class.java,
            /* options = */ mapOf(Library.OPTION_STRING_ENCODING to "UTF-8")
        )
    }

    private fun nativeLibrary(): File {
        val os = System.getProperty("os.name").lowercase()
        val architecture = when (val arch = System.getProperty("os.arch").lowercase()) {
            "amd64", "x86_64" -> "x64"

            else -> error("Unsupported Yggdrasil architecture: $arch")
        }

        val platform = when {
            os.contains("win") -> "windows-$architecture"
            os.contains("linux") -> "linux-$architecture"

            else -> error("Unsupported Yggdrasil platform: $os")
        }

        val libraryName = when {
            os.contains("win") -> "yggbridge.dll"
            os.contains("linux") -> "libyggbridge.so"

            else -> error("Unsupported Yggdrasil platform: $os")
        }

        val resourcesDir = findResourcesDir()

        return resourcesDir.resolve("yggbridge/$platform/$libraryName")
    }

    private fun findResourcesDir(): File {
        val candidates = buildList {
            System.getProperty("compose.application.resources.dir")
                ?.takeIf(String::isNotBlank)
                ?.let { add(File(it)) }

            add(File("files"))
            add(File("desktopApp/files"))
        }

        return candidates.firstOrNull { it.isDirectory } ?: error("Resources directory not found")
    }
}
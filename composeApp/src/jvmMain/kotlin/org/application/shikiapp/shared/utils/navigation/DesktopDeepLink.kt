package org.application.shikiapp.shared.utils.navigation

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

// https://github.com/OpenHub-Store/GitHub-Store/blob/f6c83ddf49bf750e4c1491fb063c86ad2aee797c/composeApp/src/jvmMain/kotlin/zed/rainxch/githubstore/DesktopDeepLink.kt
object DesktopDeepLink {

    private const val PORT = 47632
    private const val SCHEME = "app"
    private const val DESKTOP_FILE_NAME = "shikiapp-deeplink"

    fun registerUriSchemeIfNeeded() {
        val system = System.getProperty("os.name").orEmpty().lowercase()

        if (system.contains("win")) registerWindows()
        else if (system.contains("linux")) registerLinux()
    }

    private fun registerWindows() {
        val checkResult = runCommand("reg", "query", "HKCU\\SOFTWARE\\Classes\\$SCHEME", "/ve")
        if (checkResult != null && checkResult.contains("URL:")) return

        val exePath = resolveExePath() ?: return

        // Стандарт
        runCommand("reg", "add", "HKCU\\SOFTWARE\\Classes\\$SCHEME", "/ve", "/d", "URL:ShikiApp", "/f")
        runCommand("reg", "add", "HKCU\\SOFTWARE\\Classes\\$SCHEME", "/v", "URL Protocol", "/d", "", "/f")
        runCommand("reg", "add", "HKCU\\SOFTWARE\\Classes\\$SCHEME\\DefaultIcon", "/ve", "/d", "\"$exePath\",1", "/f")

        // Без cmd путь не добавляется в реестр
        val commandKey = "HKCU\\SOFTWARE\\Classes\\$SCHEME\\shell\\open\\command"
        val commandValue = "\\\"$exePath\\\" \\\"%1\\\""

        runCommand("cmd.exe", "/c", "reg add \"$commandKey\" /ve /d \"$commandValue\" /f")
    }

    private fun registerLinux() {
        val appsDir = File(System.getProperty("user.home"), ".local/share/applications")
        val desktopFile = File(appsDir, "$DESKTOP_FILE_NAME.desktop")

        if (desktopFile.exists()) return

        val exePath = resolveExePath() ?: return

        appsDir.mkdirs()

        desktopFile.writeText(
            """
            [Desktop Entry]
            Type=Application
            Name=ShikiApp
            Exec="$exePath" %u
            Terminal=false
            MimeType=x-scheme-handler/$SCHEME;
            NoDisplay=true
            """.trimIndent()
        )

        runCommand("xdg-mime", "default", "$DESKTOP_FILE_NAME.desktop", "x-scheme-handler/$SCHEME")
    }

    fun tryForwardToRunningInstance(uri: String) = try {
        Socket("127.0.0.1", PORT).use { socket ->
            PrintWriter(socket.getOutputStream(), true).println(uri)
        }
        true
    } catch (_: Exception) {
        false
    }

    fun startInstanceListener(onUri: (String) -> Unit) {
        val thread = Thread({
            try {
                val server = ServerSocket(PORT, 50, InetAddress.getLoopbackAddress())
                while (true) {
                    val client = server.accept()
                    try {
                        val reader = BufferedReader(InputStreamReader(client.getInputStream()))
                        val uri = reader.readLine()
                        if (!uri.isNullOrBlank()) {
                            onUri(uri.trim())
                        }
                    } catch (_: Exception) {
                    } finally {
                        client.close()
                    }
                }
            } catch (_: Exception) {
            }
        }, "DeepLinkListener")

        thread.isDaemon = true
        thread.start()
    }

    private fun resolveExePath() = System.getProperty("jpackage.app-path") ?: try {
        ProcessHandle.current().info().command().orElse(null)
    } catch (_: Exception) {
        null
    }

    private fun runCommand(vararg cmd: String) = try {
        val process = ProcessBuilder(*cmd)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()
        output
    } catch (_: Exception) {
        null
    }
}
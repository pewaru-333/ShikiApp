package org.application.shikiapp.auth.webview

import java.io.File
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.channels.OverlappingFileLockException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.WRITE
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

internal class AuthBrowserProfile : AutoCloseable {
    val directory: File = Files.createTempDirectory(root.toPath(), "session-").toFile()
    private val ownerFile = File(directory, ".owner")
    private val owner = FileChannel.open(ownerFile.toPath(), CREATE, WRITE)
    private val lock = owner.lock()
    private val closed = AtomicBoolean(false)

    override fun close() {
        if (!closed.compareAndSet(false, true) || deleteProfile()) return

        val task = AtomicReference<ScheduledFuture<*>>()
        task.set(
            cleanup.scheduleWithFixedDelay(
                /* command = */ { if (deleteProfile()) task.get()?.cancel(false) },
                /* initialDelay = */ 2,
                /* delay = */ 2,
                /* unit = */ TimeUnit.SECONDS
            )
        )
    }

    private fun deleteProfile(): Boolean {
        try {
            if (!removeData(directory, ownerFile)) return false
            if (lock.isValid) lock.release()
            if (owner.isOpen) owner.close()
            return (!ownerFile.exists() || ownerFile.delete()) && (!directory.exists() || directory.delete())
        } catch (_: IOException) {
            return false
        }
    }

    companion object {
        private val root = Files.createDirectories(Path.of(System.getProperty("java.io.tmpdir"), "shikirip-auth-webview"))
            .toFile()
            .also(::clearAbandonedProfiles)

        private val cleanup = Executors.newSingleThreadScheduledExecutor { runnable ->
            Thread(runnable, "auth-profile-cleanup").apply {
                isDaemon = true
            }
        }

        private fun removeData(directory: File, ownerFile: File): Boolean {
            if (!directory.exists()) return true

            directory.walkBottomUp().forEach {
                if (it != directory && it != ownerFile) {
                    it.delete()
                }
            }

            return directory.listFiles()?.all { it == ownerFile } == true
        }

        private fun clearAbandonedProfiles(root: File) {
            val files = root.listFiles()
            if (files != null) {
                for (directory in files) {
                    if (!directory.isDirectory || !directory.name.startsWith("session-")) continue

                    val ownerFile = File(directory, ".owner")

                    try {
                        val cleared = FileChannel.open(ownerFile.toPath(), CREATE, WRITE).use { owner ->
                            owner.tryLock()?.use { removeData(directory, ownerFile) } ?: false
                        }
                        if (cleared) {
                            ownerFile.delete()
                            directory.delete()
                        }
                    } catch (_: IOException) {

                    } catch (_: OverlappingFileLockException) {

                    }
                }
            }
        }
    }
}

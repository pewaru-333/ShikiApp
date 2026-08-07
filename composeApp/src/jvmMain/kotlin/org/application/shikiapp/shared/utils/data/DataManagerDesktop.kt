package org.application.shikiapp.shared.utils.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeBytes

class DataManagerDesktop : IDataManager {
    override suspend fun saveImage(bytes: ByteArray, name: String, onUpdateUri: (String?) -> Unit) =
        withContext(Dispatchers.IO) {
            try {
                val extension = getImageExtension(bytes) ?: return@withContext false

                val fileName = if (name.endsWith(".$extension", ignoreCase = true)) {
                    name
                } else {
                    val baseName = name.substringBeforeLast('.')

                    "$baseName.$extension"
                }

                val picturesDir = Path(System.getProperty("user.home"), "Pictures", "ShikiApp")
                picturesDir.createDirectories()

                val file = picturesDir.resolve(fileName)
                file.writeBytes(bytes)

                onUpdateUri(file.toUri().toString())

                true
            } catch (_: Exception) {
                false
            }
        }

    override fun onDeleteDamagedFile(path: String?) {
        if (path.isNullOrBlank()) return

        runCatching { Path(path).deleteIfExists() }
    }

    private fun getImageExtension(bytes: ByteArray): String? {
        if (bytes.size < 12) return null

        return when {
            bytes[0] == 0xFF.toByte() &&
                    bytes[1] == 0xD8.toByte() &&
                    bytes[2] == 0xFF.toByte() -> "jpg"

            bytes[0] == 0x89.toByte() &&
                    bytes[1] == 0x50.toByte() &&
                    bytes[2] == 0x4E.toByte() &&
                    bytes[3] == 0x47.toByte() -> "png"

            bytes[0] == 'G'.code.toByte() &&
                    bytes[1] == 'I'.code.toByte() &&
                    bytes[2] == 'F'.code.toByte() -> "gif"

            bytes[0] == 'R'.code.toByte() &&
                    bytes[1] == 'I'.code.toByte() &&
                    bytes[2] == 'F'.code.toByte() &&
                    bytes[3] == 'F'.code.toByte() &&
                    bytes[8] == 'W'.code.toByte() &&
                    bytes[9] == 'E'.code.toByte() &&
                    bytes[10] == 'B'.code.toByte() &&
                    bytes[11] == 'P'.code.toByte() -> "webp"

            else -> null
        }
    }
}
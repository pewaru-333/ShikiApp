package org.application.shikiapp.shared.utils.data

import java.io.File

class DataManagerDesktop : IDataManager {
    override suspend fun saveImage(bytes: ByteArray, name: String, onUpdateUri: (String?) -> Unit): Boolean {
        return try {
            val picturesDir = System.getProperty("user.home") + "/Pictures/ShikiApp"
            val dir = File(picturesDir)

            if (!dir.exists()) {
                dir.mkdirs()
            }

            val fileName = if (name.endsWith(".jpg")) name else "$name.jpg"
            val file = File(dir, fileName)

            file.writeBytes(bytes)
            onUpdateUri(file.absolutePath)

            true

        } catch (_: Exception) {
            false
        }
    }

    override fun onDeleteDamagedFile(path: String?) {
        if (path == null) return

        try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        } catch (_: Exception) {

        }
    }
}
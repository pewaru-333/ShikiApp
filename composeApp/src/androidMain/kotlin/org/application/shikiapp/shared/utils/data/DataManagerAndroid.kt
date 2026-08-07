package org.application.shikiapp.shared.utils.data

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataManagerAndroid(private val context: Context) : IDataManager {
    override suspend fun saveImage(bytes: ByteArray, name: String, onUpdateUri: (String?) -> Unit) =
        withContext(Dispatchers.IO) {
            val extension = name.substringAfterLast('.')
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

            val fileName = if (name.endsWith(".$extension", ignoreCase = true)) {
                name
            } else {
                val baseName = name.substringBeforeLast('.')

                "$baseName.$extension"
            }

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/ShikiApp")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: return@withContext false

            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(bytes)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)
                }

                onUpdateUri(uri.toString())

                true
            } catch (_: Exception) {
                context.contentResolver.delete(uri, null, null)
                false
            }
        }

    override fun onDeleteDamagedFile(path: String?) {
        runCatching {
            path?.toUri()?.let {
                context.contentResolver.delete(it, null, null)
            }
        }
    }
}
package org.application.shikiapp.shared.utils.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri

class DataManagerAndroid(private val context: Context) : IDataManager {
    override suspend fun saveImage(bytes: ByteArray, name: String, onUpdateUri: (String?) -> Unit): Boolean {
        var uri: Uri?

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/ShikiApp")
                put(MediaStore.Audio.Media.IS_PENDING, 1)
            }
        }

        uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return false
        onUpdateUri(uri.path)

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(bytes)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, contentValues, null, null)
        }

        return true
    }

    override fun onDeleteDamagedFile(path: String?) {
        path?.toUri()?.let { context.contentResolver.delete(it, null, null) }
    }
}
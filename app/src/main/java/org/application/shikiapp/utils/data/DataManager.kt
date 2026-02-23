package org.application.shikiapp.utils.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.copyAndClose
import io.ktor.utils.io.streams.asByteWriteChannel
import org.application.shikiapp.network.client.Network

class DataManager(private val context: Context) {
    suspend fun downloadImage(url: String): Boolean {
        var uri: Uri? = null

        return try {
            Network.client.prepareGet(url).execute { response ->
                if (response.status != HttpStatusCode.OK) return@execute false

                val name = response.call.request.url.segments
                    .lastOrNull()
                    ?.takeIf(String::isNotBlank)
                    ?: "img_${System.currentTimeMillis()}.jpg"

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/ShikiApp")
                        put(MediaStore.Audio.Media.IS_PENDING, 1)
                    }
                }

                uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return@execute false

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    response.bodyAsChannel().copyAndClose(outputStream.asByteWriteChannel())
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)
                }

                true
            }
        } catch (_: Exception) {
            uri?.let { context.contentResolver.delete(it, null, null) }

            false
        }
    }
}
@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package org.application.shikiapp.shared.utils.data

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.dataWithBytes
import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIImage

class DataManagerIos : IDataManager {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun saveImage(bytes: ByteArray, name: String, onUpdateUri: (String?) -> Unit): Boolean {
        val data = bytes.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
        }

        val image = UIImage.imageWithData(data) ?: return false

        return suspendCancellableCoroutine { continuation ->
            PHPhotoLibrary.sharedPhotoLibrary().performChanges(
                changeBlock = {
                    PHAssetChangeRequest.creationRequestForAssetFromImage(image)
                },
                completionHandler = { success, _ ->
                    continuation.resume(success) { _, _, _ -> }
                }
            )
        }
    }

    override fun onDeleteDamagedFile(path: String?) {
        if (path == null) return

        try {
            val fileManager = NSFileManager.defaultManager
            if (fileManager.fileExistsAtPath(path)) {
                fileManager.removeItemAtPath(path, error = null)
            }
        } catch (_: Exception) {

        }
    }
}
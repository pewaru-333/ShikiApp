package org.application.shikiapp.shared.utils.data

interface IDataManager {
    suspend fun saveImage(bytes: ByteArray, name: String, onUpdateUri: (String?) -> Unit): Boolean
    fun onDeleteDamagedFile(path: String?)
}
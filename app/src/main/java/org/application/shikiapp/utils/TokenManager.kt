package org.application.shikiapp.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.User
import org.application.shikiapp.network.NetworkClient

object TokenManager {

    suspend fun getToken(code: String) {
        val token = NetworkClient.profile.getToken(code = code)
        Preferences.saveToken(token)
    }

    suspend fun getUser(): User {
        val whoAmI = NetworkClient.profile.whoAmI()
        val user = NetworkClient.user.getUser(whoAmI.id)
        Preferences.setUserId(whoAmI.id)

        return user
    }

    suspend fun refreshToken() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val token = NetworkClient.profile.getRefreshToken(refreshToken = Preferences.refreshToken())
            Preferences.saveToken(token)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
package org.application.shikiapp.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.application.shikiapp.models.data.NewRate
import org.application.shikiapp.models.data.UserRate

class UserRates(private val client: HttpClient) {
    suspend fun createRate(newRate: NewRate) = client.post("v2/user_rates") {
        setBody(newRate)
    }.body<UserRate>()

    suspend fun updateRate(id: Long, newRate: NewRate) = client.patch("v2/user_rates/$id") {
        setBody(newRate)
    }.body<UserRate>()

    suspend fun increment(id: Long) = client.post("v2/user_rates/$id/increment")
    suspend fun delete(id: Long) = client.delete("v2/user_rates/$id")
}
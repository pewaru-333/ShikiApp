package org.application.shikiapp.shared.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.application.shikiapp.shared.models.data.Calendar
import org.application.shikiapp.shared.models.data.Character
import org.application.shikiapp.shared.models.data.Person

class Content(private val client: HttpClient) {
    suspend fun getCalendar(): List<Calendar> = client.get("calendar").body()

    suspend fun getCharacter(id: Any): Character = client.get("characters/$id").body()

    suspend fun getPerson(id: Any): Person = client.get("people/$id").body()
}
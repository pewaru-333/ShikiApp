package org.application.shikiapp.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.application.shikiapp.models.data.Character
import org.application.shikiapp.models.data.Person

class Content(private val client: HttpClient) {
    //suspend fun getCalendar() = client.get("calendar").body<List<Calendar>>()
    suspend fun getCharacter(id: Any) = client.get("characters/$id").body<Character>()
    suspend fun getPerson(id: Any) = client.get("people/$id").body<Person>()
}
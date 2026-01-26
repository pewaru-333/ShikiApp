package org.application.shikiapp.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.application.shikiapp.models.data.Calendar
import org.application.shikiapp.models.data.Character
import org.application.shikiapp.models.data.Person
import org.application.shikiapp.utils.extensions.requestWithCache

class Content(private val client: HttpClient) {
    suspend fun getCalendar() = client.get("calendar").body<List<Calendar>>()

    suspend fun getCharacter(id: Any) = client.requestWithCache<Character>(
        cacheKey = "character:$id",
        url = "characters/$id"
    )

    suspend fun getPerson(id: Any) = client.requestWithCache<Person>(
        cacheKey = "person:$id",
        url = "people/$id"
    )
}
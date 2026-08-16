package org.application.shikiapp.shared.network.calls

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.application.shikiapp.shared.models.data.Calendar
import org.application.shikiapp.shared.models.data.Character
import org.application.shikiapp.shared.models.data.Person
import org.application.shikiapp.shared.utils.extensions.requestWithCache

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
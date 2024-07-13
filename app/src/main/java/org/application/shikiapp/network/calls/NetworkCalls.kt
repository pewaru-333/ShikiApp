package org.application.shikiapp.network.calls

import org.application.shikiapp.models.data.Calendar
import org.application.shikiapp.models.data.Character
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.News
import org.application.shikiapp.models.data.Person
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkCalls {

    @GET("calendar")
    suspend fun getCalendar(): List<Calendar>

    @GET("topics")
    suspend fun getNewsList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("forum") forum: String = "news"
    ): List<News>

    @GET("topics/{topicId}")
    suspend fun getTopicById(@Path(value = "topicId") topicId: Long): News

    @GET("comments")
    suspend fun getCommentsList(
        @Query("commentable_id") commentableId: Long,
        @Query("commentable_type") commentableType: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("desc") desc: Int = 1
    ): List<Comment>

    @GET("characters/{characterId}")
    suspend fun getCharacter(@Path(value = "characterId") characterId: Long): Character

    @GET("people/{personId}")
    suspend fun getPerson(@Path(value = "personId") personId: Long): Person
}
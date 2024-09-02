package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Favourites(
    @Json(name = "animes") val animes: List<Favourite>,
    @Json(name = "mangas") val mangas: List<Favourite>,
    @Json(name = "ranobe") val ranobe: List<Favourite>,
    @Json(name = "characters") val characters: List<Favourite>,
    @Json(name = "people") val people: List<Favourite>,
    @Json(name = "mangakas") val mangakas: List<Favourite>,
    @Json(name = "seyu") val seyu: List<Favourite>,
    @Json(name = "producers") val producers: List<Favourite>
)

@JsonClass(generateAdapter = true)
data class Favourite(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "russian") val russian: String,
    @Json(name = "image") val image: String,
    @Json(name = "url") val url: String?,
)
package org.application.shikiapp.models.data

import kotlinx.serialization.Serializable

@Serializable
data class Favourites(
    val animes: List<Favourite>,
    val mangas: List<Favourite>,
    val ranobe: List<Favourite>,
    val characters: List<Favourite>,
    val people: List<Favourite>,
    val mangakas: List<Favourite>,
    val seyu: List<Favourite>,
    val producers: List<Favourite>
)

@Serializable
data class Favourite(
    val id: Long,
    val name: String,
    val russian: String?,
    val image: String,
    val url: String?
)
package org.application.shikiapp.models.data

import kotlinx.serialization.Serializable
import org.application.shikiapp.utils.BLANK

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
    val id: Long = 0L,
    val name: String = BLANK,
    val russian: String? = null,
    val image: String = BLANK,
    val url: String? = null
)
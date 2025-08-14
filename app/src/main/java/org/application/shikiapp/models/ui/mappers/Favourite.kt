package org.application.shikiapp.models.ui.mappers

import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.utils.enums.FavouriteItem

fun Favourites.toBasicContentMap() = mapOf(
    FavouriteItem.ANIME to animes,
    FavouriteItem.MANGA to mangas,
    FavouriteItem.RANOBE to ranobe,
    FavouriteItem.CHARACTERS to characters,
    FavouriteItem.PEOPLE to people,
    FavouriteItem.MANGAKAS to mangakas,
    FavouriteItem.SEYU to seyu,
    FavouriteItem.OTHERS to producers
)
    .mapValues { (_, value) ->
        value.map {
            BasicContent(
                id = it.id.toString(),
                title = it.russian ?: it.name,
                poster = it.image
            )
        }
    }

package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.shared.models.data.Favourites
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.utils.enums.FavouriteItem

fun Favourites.toBasicContentMap() = buildMap(FavouriteItem.entries.size) {
    if (animes.isNotEmpty()) put(FavouriteItem.ANIME, animes.toBasicContent())
    if (mangas.isNotEmpty()) put(FavouriteItem.MANGA, mangas.toBasicContent())
    if (ranobe.isNotEmpty()) put(FavouriteItem.RANOBE, ranobe.toBasicContent())
    if (characters.isNotEmpty()) put(FavouriteItem.CHARACTERS, characters.toBasicContent())
    if (people.isNotEmpty()) put(FavouriteItem.PEOPLE, people.toBasicContent())
    if (mangakas.isNotEmpty()) put(FavouriteItem.MANGAKAS, mangakas.toBasicContent())
    if (seyu.isNotEmpty()) put(FavouriteItem.SEYU, seyu.toBasicContent())
    if (producers.isNotEmpty()) put(FavouriteItem.OTHERS, producers.toBasicContent())
}

private fun List<Favourites.Favourite>.toBasicContent() = map {
    BasicContent(
        id = it.id.toString(),
        title = it.russian.takeUnless(String?::isNullOrEmpty) ?: it.name,
        poster = it.image.replace("x64", "x96")
    )
}
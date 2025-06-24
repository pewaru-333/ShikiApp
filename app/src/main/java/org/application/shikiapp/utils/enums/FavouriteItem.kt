package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R
import org.application.shikiapp.models.data.Favourite
import org.application.shikiapp.models.data.Favourites

enum class FavouriteItem(
    @StringRes val title: Int,
    private val getFavourite: (Favourites) -> List<Favourite>
) {
    ANIME(R.string.text_anime, Favourites::animes),
    MANGA(R.string.text_manga, Favourites::mangas),
    RANOBE(R.string.text_ranobe, Favourites::ranobe),
    CHARACTERS(R.string.text_characters, Favourites::characters),
    PEOPLE(R.string.text_people, Favourites::people),
    MANGAKAS(R.string.text_mangakas, Favourites::mangakas),
    SEYU(R.string.text_seyu, Favourites::seyu),
    OTHERS(R.string.text_others, Favourites::producers);

    fun getFavouriteList(favourites: Favourites) = getFavourite(favourites)
}
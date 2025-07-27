package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R
import org.application.shikiapp.utils.navigation.Screen

enum class CatalogItem(@StringRes val title: Int, val icon: Int) {
    ANIME(R.string.text_anime, R.drawable.vector_anime) {
        override fun navigateTo(contentId: String) = Screen.Anime(contentId)
    },
    MANGA(R.string.text_manga, R.drawable.vector_manga) {
        override fun navigateTo(contentId: String) = Screen.Manga(contentId)
    },
    RANOBE(R.string.text_ranobe, R.drawable.vector_ranobe) {
        override fun navigateTo(contentId: String) = Screen.Manga(contentId)
    },
    CHARACTERS(R.string.text_characters, R.drawable.vector_character) {
        override fun navigateTo(contentId: String) = Screen.Character(contentId)
    },
    PEOPLE(R.string.text_people, R.drawable.vector_person) {
        override fun navigateTo(contentId: String) = Screen.Person(contentId.toLong())
    },
    USERS(R.string.text_users, R.drawable.vector_users) {
        override fun navigateTo(contentId: String) = Screen.User(contentId.toLong())
    },
    CLUBS(R.string.text_clubs, R.drawable.vector_clubs) {
        override fun navigateTo(contentId: String) = Screen.Club(contentId.toLong())
    };

    abstract fun navigateTo(contentId: String): Screen
}
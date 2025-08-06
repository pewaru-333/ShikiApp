package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R
import org.application.shikiapp.utils.navigation.Screen

enum class LinkedType(@StringRes val title: Int) {
    ANIME(R.string.text_anime) {
        override fun navigateTo(contentId: String) = Screen.Anime(contentId)
        override fun getWatchStatusTitle(status: WatchStatus) = status.titleAnime
    },
    MANGA(R.string.text_manga) {
        override fun navigateTo(contentId: String) = Screen.Manga(contentId)
        override fun getWatchStatusTitle(status: WatchStatus) = status.titleManga
    },
    RANOBE(R.string.text_ranobe) {
        override fun navigateTo(contentId: String) = Screen.Manga(contentId)
        override fun getWatchStatusTitle(status: WatchStatus) = status.titleManga
    },
    PERSON(R.string.blank) {
        override fun navigateTo(contentId: String) = Screen.Person(contentId.toLong())
        override fun getWatchStatusTitle(status: WatchStatus) = R.string.blank
    },
    CHARACTER(R.string.blank) {
        override fun navigateTo(contentId: String) = Screen.Character(contentId)
        override fun getWatchStatusTitle(status: WatchStatus) = R.string.blank
    };

    @StringRes
    abstract fun getWatchStatusTitle(status: WatchStatus): Int
    abstract fun navigateTo(contentId: String): Screen

    @StringRes
    fun getListTitle() = when (this) {
        ANIME -> R.string.text_anime_list
        MANGA -> R.string.text_manga_list
        else -> R.string.blank
    }
}
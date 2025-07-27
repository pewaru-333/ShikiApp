package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R
import org.application.shikiapp.utils.navigation.Screen

enum class LinkedType {
    ANIME {
        override fun navigateTo(contentId: String) = Screen.Anime(contentId)
        override fun getTitleResId(status: WatchStatus) = status.titleAnime
    },
    MANGA {
        override fun navigateTo(contentId: String) = Screen.Manga(contentId)
        override fun getTitleResId(status: WatchStatus) = status.titleManga
    },
    RANOBE {
        override fun navigateTo(contentId: String) = Screen.Manga(contentId)
        override fun getTitleResId(status: WatchStatus) = status.titleManga
    },
    PERSON {
        override fun navigateTo(contentId: String) = Screen.Person(contentId.toLong())
        override fun getTitleResId(status: WatchStatus) = R.string.blank
    },
    CHARACTER {
        override fun navigateTo(contentId: String) = Screen.Character(contentId)
        override fun getTitleResId(status: WatchStatus) = R.string.blank
    };

    @StringRes
    abstract fun getTitleResId(status: WatchStatus): Int
    abstract fun navigateTo(contentId: String): Screen

    @StringRes
    fun getListTitle() = when (this) {
        ANIME -> R.string.text_anime_list
        MANGA -> R.string.text_manga_list
        else -> R.string.blank
    }
}
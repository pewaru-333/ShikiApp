package org.application.shikiapp.shared.utils.enums

import kotlinx.serialization.Serializable
import org.application.shikiapp.shared.utils.navigation.Screen
import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.blank
import shikiapp.composeapp.generated.resources.text_anime
import shikiapp.composeapp.generated.resources.text_anime_list
import shikiapp.composeapp.generated.resources.text_manga
import shikiapp.composeapp.generated.resources.text_manga_list
import shikiapp.composeapp.generated.resources.text_ranobe

@Serializable
enum class LinkedType(val title: StringResource) {
    ANIME(Res.string.text_anime) {
        override fun navigateTo(contentId: String) = Screen.Anime(contentId)
        override fun getWatchStatusTitle(status: WatchStatus) = status.titleAnime
    },
    MANGA(Res.string.text_manga) {
        override fun navigateTo(contentId: String) = Screen.Manga(contentId)
        override fun getWatchStatusTitle(status: WatchStatus) = status.titleManga
    },
    RANOBE(Res.string.text_ranobe) {
        override fun navigateTo(contentId: String) = Screen.Manga(contentId)
        override fun getWatchStatusTitle(status: WatchStatus) = status.titleManga
    },
    PERSON(Res.string.blank) {
        override fun navigateTo(contentId: String) = Screen.Person(contentId.toLong())
        override fun getWatchStatusTitle(status: WatchStatus) = Res.string.blank
    },
    CHARACTER(Res.string.blank) {
        override fun navigateTo(contentId: String) = Screen.Character(contentId)
        override fun getWatchStatusTitle(status: WatchStatus) = Res.string.blank
    };


    abstract fun getWatchStatusTitle(status: WatchStatus): StringResource
    abstract fun navigateTo(contentId: String): Screen

    fun getListTitle() = when (this) {
        ANIME -> Res.string.text_anime_list
        MANGA -> Res.string.text_manga_list
        else -> Res.string.blank
    }
}
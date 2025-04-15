package org.application.shikiapp.utils.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data class Catalog(val showOngoing: Boolean = false) : Screen

    @Serializable
    object News : Screen

    @Serializable
    object Calendar : Screen

    @Serializable
    object Profile : Screen

    @Serializable
    object Settings : Screen

    @Serializable
    data class Login(val code: String? = null) : Screen

    @Serializable
    data class Anime(val id: String) : Screen

    @Serializable
    data class Manga(val id: String) : Screen

    @Serializable
    data class Character(val id: String) : Screen

    @Serializable
    data class Person(val id: Long) : Screen

    @Serializable
    data class User(val id: Long) : Screen

    @Serializable
    data class Club(val id: Long) : Screen

    @Serializable
    data class NewsDetail(val id: Long) : Screen

    @Serializable
    data class AnimeRates(val id: Long) : Screen

    @Serializable
    data class MangaRates(val id: Long) : Screen
}
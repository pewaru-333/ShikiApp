package org.application.shikiapp.utils.navigation

import kotlinx.serialization.Serializable
import org.application.shikiapp.utils.enums.LinkedType

sealed interface Screen {
    @Serializable
    data class Catalog(
        val studio: String? = null,
        val publisher: String? = null,
        val linkedType: LinkedType? = null,
        val showOngoing: Boolean? = null
    ) : Screen

    @Serializable
    object News : Screen

    @Serializable
    object Calendar : Screen

    @Serializable
    object Profile : Screen

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
    data class UserRates(
        val id: Long? = null,
        val type: LinkedType? = null,
        val editable: Boolean = false
    ) : Screen
}
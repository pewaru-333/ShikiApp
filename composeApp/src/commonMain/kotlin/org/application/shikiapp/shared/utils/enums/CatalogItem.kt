package org.application.shikiapp.shared.utils.enums

import org.application.shikiapp.shared.models.states.DialogFilters
import org.application.shikiapp.shared.utils.navigation.Screen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_anime
import shikiapp.composeapp.generated.resources.text_characters
import shikiapp.composeapp.generated.resources.text_clubs
import shikiapp.composeapp.generated.resources.text_manga
import shikiapp.composeapp.generated.resources.text_people
import shikiapp.composeapp.generated.resources.text_ranobe
import shikiapp.composeapp.generated.resources.text_users
import shikiapp.composeapp.generated.resources.vector_anime
import shikiapp.composeapp.generated.resources.vector_character
import shikiapp.composeapp.generated.resources.vector_clubs
import shikiapp.composeapp.generated.resources.vector_manga
import shikiapp.composeapp.generated.resources.vector_person
import shikiapp.composeapp.generated.resources.vector_ranobe
import shikiapp.composeapp.generated.resources.vector_users

enum class CatalogItem(val title: StringResource, val icon: DrawableResource) {
    ANIME(Res.string.text_anime, Res.drawable.vector_anime) {
        override val showFilter = true
        override val linkedType = LinkedType.ANIME
        override val dialogFilter = DialogFilters.Anime
        override fun navigateTo(contentId: String) = Screen.Anime(contentId)
    },
    MANGA(Res.string.text_manga, Res.drawable.vector_manga) {
        override val showFilter = true
        override val linkedType = LinkedType.MANGA
        override val dialogFilter = DialogFilters.Manga
        override fun navigateTo(contentId: String) = Screen.Manga(contentId)
    },
    RANOBE(Res.string.text_ranobe, Res.drawable.vector_ranobe) {
        override val showFilter = true
        override val linkedType = LinkedType.RANOBE
        override val dialogFilter = DialogFilters.Ranobe
        override fun navigateTo(contentId: String) = Screen.Manga(contentId)
    },
    CHARACTERS(Res.string.text_characters, Res.drawable.vector_character) {
        override val showFilter = false
        override val linkedType = null
        override val dialogFilter = null
        override fun navigateTo(contentId: String) = Screen.Character(contentId)
    },
    PEOPLE(Res.string.text_people, Res.drawable.vector_person) {
        override val showFilter = true
        override val linkedType = null
        override val dialogFilter = DialogFilters.People
        override fun navigateTo(contentId: String) = Screen.Person(contentId.toLong())
    },
    USERS(Res.string.text_users, Res.drawable.vector_users) {
        override val showFilter = false
        override val linkedType = null
        override val dialogFilter = null
        override fun navigateTo(contentId: String) = Screen.User(contentId.toLong())
    },
    CLUBS(Res.string.text_clubs, Res.drawable.vector_clubs) {
        override val showFilter = false
        override val linkedType = null
        override val dialogFilter = null
        override fun navigateTo(contentId: String) = Screen.Club(contentId.toLong())
    };

    abstract val showFilter: Boolean
    abstract val linkedType: LinkedType?
    abstract val dialogFilter: DialogFilters?
    abstract fun navigateTo(contentId: String): Screen
}
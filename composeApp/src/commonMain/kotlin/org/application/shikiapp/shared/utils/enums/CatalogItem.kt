package org.application.shikiapp.shared.utils.enums

import androidx.compose.ui.graphics.vector.ImageVector
import org.application.shikiapp.shared.models.states.DialogFilters
import org.application.shikiapp.shared.models.ui.list.ContentViewType
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.navigation.Screen
import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_anime
import shikiapp.composeapp.generated.resources.text_characters
import shikiapp.composeapp.generated.resources.text_clubs
import shikiapp.composeapp.generated.resources.text_manga
import shikiapp.composeapp.generated.resources.text_people
import shikiapp.composeapp.generated.resources.text_ranobe
import shikiapp.composeapp.generated.resources.text_users

enum class CatalogItem(val title: StringResource, val icon: ImageVector) {
    ANIME(Res.string.text_anime, Icons.Anime) {
        override val showFilter = true
        override val linkedType = LinkedType.ANIME
        override val dialogFilter = DialogFilters.Anime
        override val viewType = ContentViewType.ADAPTIVE_ITEM
        override fun navigateTo(contentId: String) = Screen.Anime(contentId)
    },
    MANGA(Res.string.text_manga, Icons.Manga) {
        override val showFilter = true
        override val linkedType = LinkedType.MANGA
        override val dialogFilter = DialogFilters.Manga
        override val viewType = ContentViewType.ADAPTIVE_ITEM
        override fun navigateTo(contentId: String) = Screen.Manga(contentId)
    },
    RANOBE(Res.string.text_ranobe, Icons.Ranobe) {
        override val showFilter = true
        override val linkedType = LinkedType.RANOBE
        override val dialogFilter = DialogFilters.Ranobe
        override val viewType = ContentViewType.ADAPTIVE_ITEM
        override fun navigateTo(contentId: String) = Screen.Manga(contentId)
    },
    CHARACTERS(Res.string.text_characters, Icons.Character) {
        override val showFilter = false
        override val linkedType = null
        override val dialogFilter = null
        override val viewType = ContentViewType.ADAPTIVE_ITEM
        override fun navigateTo(contentId: String) = Screen.Character(contentId)
    },
    PEOPLE(Res.string.text_people, Icons.Person) {
        override val showFilter = true
        override val linkedType = null
        override val dialogFilter = DialogFilters.People
        override val viewType = ContentViewType.ADAPTIVE_ITEM
        override fun navigateTo(contentId: String) = Screen.Person(contentId.toLong())
    },
    USERS(Res.string.text_users, Icons.Users) {
        override val showFilter = false
        override val linkedType = null
        override val dialogFilter = null
        override val viewType = ContentViewType.GRID_ITEM_SMALL
        override fun navigateTo(contentId: String) = Screen.User(contentId.toLong())
    },
    CLUBS(Res.string.text_clubs, Icons.Clubs) {
        override val showFilter = false
        override val linkedType = null
        override val dialogFilter = null
        override val viewType = ContentViewType.GRID_ITEM_SMALL
        override fun navigateTo(contentId: String) = Screen.Club(contentId.toLong())
    };

    abstract val showFilter: Boolean
    abstract val linkedType: LinkedType?
    abstract val dialogFilter: DialogFilters?
    abstract val viewType: ContentViewType
    abstract fun navigateTo(contentId: String): Screen
}
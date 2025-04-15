package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.application.shikiapp.utils.BLANK

@Serializable
class User : UserBrief() {
    @SerialName("last_online")
    val lastOnline: String? = null

    @SerialName("location")
    val location: String? = null

    @SerialName("banned")
    val banned: Boolean = false

    @SerialName("about")
    val about: String = BLANK

    @SerialName("about_html")
    val aboutHtml: String = BLANK

    @SerialName("common_info")
    val commonInfo: List<String> = emptyList()

    @SerialName("show_comments")
    val showComments: Boolean = false

    @SerialName("in_friends")
    val inFriends: Boolean? = null

    @SerialName("is_ignored")
    val isIgnored: Boolean = false

    @SerialName("stats")
    val stats: Stats = Stats()

    @SerialName("style_id")
    val styleId: Long? = null
}

@Serializable
open class UserBasic {
    @SerialName("id")
    val id: Long = 0L

    @SerialName("nickname")
    val nickname: String = BLANK

    @SerialName("avatar")
    val avatar: String = BLANK

    @SerialName("image")
    val image: UserImage = UserImage()

    @SerialName("last_online_at")
    val lastOnlineAt: String? = null

    @SerialName("url")
    val url: String? = null
}

@Serializable
open class UserBrief : UserBasic() {
    @SerialName("name")
    val name: String? = null

    @SerialName("sex")
    val sex: String? = null

    @SerialName("website")
    val website: String? = null

    @SerialName("birth_on")
    val birthOn: String? = null

    @SerialName("full_years")
    val fullYears: Int? = null

    @SerialName("locale")
    val locale: String? = null
}

@Serializable
data class UserImage(
    val x160: String = BLANK,
    val x148: String = BLANK,
    val x80: String = BLANK,
    val x64: String = BLANK,
    val x48: String = BLANK,
    val x32: String = BLANK,
    val x16: String = BLANK
)
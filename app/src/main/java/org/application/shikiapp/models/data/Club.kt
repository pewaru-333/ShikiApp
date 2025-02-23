package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.application.shikiapp.utils.BLANK


enum class ClubJoinPolicy {
    FREE, MEMBER_INVITE, ADMIN_INVITE, OWNER_INVITE
}

enum class ClubCommentPolicy {
    FREE, MEMBERS, ADMINS
}

@Serializable
class Club : ClubBasic() {
    @SerialName("description")
    val description: String? = null

    @SerialName("description_html")
    val descriptionHtml: String? = null

    @SerialName("thread_id")
    val threadId: Long = 0L

    @SerialName("topic_id")
    val topicId: Long = 0L

    @SerialName("user_role")
    val userRole: String? = null

    @SerialName("style_id")
    val styleId: Long? = null

    @SerialName("images")
    val images: List<ClubImages> = emptyList()

    @SerialName("members")
    val members: List<UserBasic> = emptyList()

    @SerialName("animes")
    val animes: List<AnimeBasic> = emptyList()

    @SerialName("mangas")
    val mangas: List<MangaBasic> = emptyList()

    @SerialName("characters")
    val characters: List<BasicInfo> = emptyList()
}

@Serializable
open class ClubBasic {
    @SerialName("id")
    val id: Long = 0L

    @SerialName("name")
    val name: String = BLANK

    @SerialName("logo")
    val logo: ClubImage = ClubImage()

    @SerialName("is_censored")
    val isCensored: Boolean = false

    @SerialName("join_policy")
    val joinPolicy: ClubJoinPolicy = ClubJoinPolicy.FREE

    @SerialName("comment_Policy")
    val commentPolicy: ClubCommentPolicy = ClubCommentPolicy.FREE
}

@Serializable
data class ClubImage(
    val original: String? = null,
    val main: String? = null,
    val x96: String? = null,
    val x73: String? = null,
    val x48: String? = null
)

@Serializable
data class ClubImages(
    @SerialName("id") val id: Long,
    @SerialName("original_url") val originalUrl: String?,
    @SerialName("main_url") val mainUrl: String?,
    @SerialName("preview_url") val previewUrl: String?,
    @SerialName("can_destroy") val canDestroy: Boolean?,
    @SerialName("user_id") val userId: Long
)
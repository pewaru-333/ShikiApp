package org.application.shikiapp.shared.models.data.dark

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewItem(
    val id: Long,
    @SerialName("user_id") val userId: Long,
    @SerialName("user_nickname") val userNickname: String,
    @SerialName("user_avatar") val userAvatar: String,
    val date: String,
    @SerialName("body_html") val bodyHtml: String,
    val opinion: String,
    @SerialName("anime_score") val animeScore: Int? = null,
    @SerialName("watch_status") val watchStatus: String? = null,
    @SerialName("rip_votes_for") val ripVotesFor: Int = 0,
    @SerialName("rip_votes_against") val ripVotesAgainst: Int = 0,
    @SerialName("shiki_votes_for") val shikiVotesFor: Int = 0,
    @SerialName("shiki_votes_against") val shikiVotesAgainst: Int = 0
)

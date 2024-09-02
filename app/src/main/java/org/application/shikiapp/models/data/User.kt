package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val id: Long,
    @Json(name = "nickname") val nickname: String,
    @Json(name = "avatar") val avatar: String,
    @Json(name = "image") val image: UserImage,
    @Json(name = "last_online_at") val lastOnlineAt: String,
    @Json(name = "url") val url: String,
    @Json(name = "name") val name: String?,
    @Json(name = "sex") val sex: String?,
    @Json(name = "full_years") val fullYears: Int?,
    @Json(name = "last_online") val lastOnline: String,
    @Json(name = "website") val website: String,
    @Json(name = "location") val location: String?,
    @Json(name = "banned") val banned: Boolean,
    @Json(name = "about") val about: String,
    @Json(name = "about_html") val aboutHtml: String,
    @Json(name = "common_info") val commonInfo: List<String>,
    @Json(name = "show_comments") val showComments: Boolean,
    @Json(name = "in_friends") val inFriends: Boolean?,
    @Json(name = "is_ignored") val isIgnored: Boolean,
    @Json(name = "stats") val stats: Stats,
    @Json(name = "style_id") val styleId: Long?
)

@JsonClass(generateAdapter = true)
data class UserShort(
    @Json(name = "id") val id: Long,
    @Json(name = "nickname") val nickname: String,
    @Json(name = "avatar") val avatar: String,
    @Json(name = "image") val image: UserImage,
    @Json(name = "last_online_at") val lastOnlineAt: String?,
    @Json(name = "url") val url: String?,
)

@JsonClass(generateAdapter = true)
data class UserBrief(
    @Json(name = "id") val id: Long,
    @Json(name = "nickname") val nickname: String,
    @Json(name = "avatar") val avatar: String,
    @Json(name = "image") val image: UserImage,
    @Json(name = "last_online_at") val lastOnlineAt: String,
    @Json(name = "url") val url: String,
    @Json(name = "name") val name: String?,
    @Json(name = "sex") val sex: String?,
    @Json(name = "website") val website: String?,
    @Json(name = "birth_on") val birthOn: String?,
    @Json(name = "full_years") val fullYears: Int?,
    @Json(name = "locale") val locale: String
)

@JsonClass(generateAdapter = true)
data class UserImage(
    @Json(name = "x160") val x160: String,
    @Json(name = "x148") val x148: String,
    @Json(name = "x80") val x80: String,
    @Json(name = "x64") val x64: String,
    @Json(name = "x48") val x48: String,
    @Json(name = "x32") val x32: String,
    @Json(name = "x16") val x16: String
)
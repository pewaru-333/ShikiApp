package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Franchise(
    @Json(name = "links") val links: List<Link>,
    @Json(name = "nodes") val nodes: List<Node>,
    @Json(name = "current_id") val currentId: Long
)

@JsonClass(generateAdapter = true)
data class Link(
    @Json(name = "id") val id: Long,
    @Json(name = "source_id") val sourceId: Long,
    @Json(name = "target_id") val targetId: Long,
    @Json(name = "source") val source: Long,
    @Json(name = "target") val target: Long,
    @Json(name = "weight") val weight: Int,
    @Json(name = "relation") val relation: String
)

@JsonClass(generateAdapter = true)
data class Node(
    @Json(name = "id") val id: Long,
    @Json(name = "date") val date: Long,
    @Json(name = "name") val name: String,
    @Json(name = "image_url") val imageUrl: String,
    @Json(name = "url") val url: String,
    @Json(name = "year") val year: Int?,
    @Json(name = "kind") val kind: String,
    @Json(name = "weight") val weight: Int
)
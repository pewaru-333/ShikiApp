package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Franchise(
    @SerialName("links") val links: List<Link>,
    @SerialName("nodes") val nodes: List<Node>,
    @SerialName("current_id") val currentId: Long
)

@Serializable
data class Link(
    @SerialName("id") val id: Long,
    @SerialName("source_id") val sourceId: Long,
    @SerialName("target_id") val targetId: Long,
    @SerialName("source") val source: Long,
    @SerialName("target") val target: Long,
    @SerialName("weight") val weight: Int,
    @SerialName("relation") val relation: String
)

@Serializable
data class Node(
    @SerialName("id") val id: Long,
    @SerialName("date") val date: Long,
    @SerialName("name") val name: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("url") val url: String,
    @SerialName("year") val year: Int?,
    @SerialName("kind") val kind: String,
    @SerialName("weight") val weight: Int
)
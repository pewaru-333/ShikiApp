package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Franchise(
    val links: List<Link>,
    val nodes: List<Node>,
    @SerialName("current_id") val currentId: Long
)

@Serializable
data class Link(
    val id: Long,
    @SerialName("source_id") val sourceId: Long,
    @SerialName("target_id") val targetId: Long,
    val source: Long,
    val target: Long,
    val weight: Int,
    val relation: String
)

@Serializable
data class Node(
    val id: Long,
    val date: Long,
    val name: String,
    @SerialName("image_url") val imageUrl: String,
    val url: String,
    val year: Int?,
    val kind: String?,
    val weight: Int
)
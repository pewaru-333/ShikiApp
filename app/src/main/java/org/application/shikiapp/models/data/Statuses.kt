package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Statuses(
    val anime: List<ShortInfo> = emptyList(),
    val manga: List<ShortInfo> = emptyList()
)

//@Serializable
//data class Scores(
//    val anime: List<RatesScores> = emptyList(),
//    val manga: List<RatesScores> = emptyList()
//)
//
//@Serializable
//data class Types(
//    val anime: List<RatesScores> = emptyList(),
//    val manga: List<RatesScores> = emptyList()
//)
//
//@Serializable
//data class Ratings(
//    val anime: List<RatesScores> = emptyList()
//)

@Serializable
data class ShortInfo(
    @SerialName("id") val id: Long,
    @SerialName("grouped_id") val groupedId: String,
    @SerialName("name") val name: String,
    @SerialName("size") val size: Long,
    @SerialName("type") val type: String
)
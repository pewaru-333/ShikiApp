package org.application.shikiapp.shared.models.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.time.LocalDate
import java.time.ZoneOffset

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
    @Serializable(with = NodeDateSerializer::class)
    val date: Long,
    val name: String,
    @SerialName("image_url") val imageUrl: String,
    val url: String,
    val year: Int?,
    val kind: String?,
    val weight: Int
)

object NodeDateSerializer : KSerializer<Long> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("NodeDate", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Long {
        val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException()

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> element.longOrNull ?: 0L
            is JsonObject -> {
                val year = element["year"]?.jsonPrimitive?.intOrNull ?: 1970
                val month = element["month"]?.jsonPrimitive?.intOrNull ?: 1
                val day = element["day"]?.jsonPrimitive?.intOrNull ?: 1

                try {
                    LocalDate.of(year, month, day)
                        .atStartOfDay()
                        .toInstant(ZoneOffset.UTC)
                        .toEpochMilli()
                } catch (_: Exception) {
                    0L
                }
            }
            else -> 0L
        }
    }

    override fun serialize(encoder: Encoder, value: Long) = encoder.encodeLong(value)
}
package org.application.shikiapp.models.data


import kotlinx.serialization.Serializable


@Serializable
class Anime : Content() {
//    @SerialName("kind")
//    override val kind: Kind = AnimeKind.TV
//
//    @SerialName("score")
//    val score: String = BLANK
//
//    @SerialName("status")
//    val status: String = BLANK
//
//    @SerialName("episodes")
//    val episodes: Int = 0
//
//    @SerialName("episodes_aired")
//    val episodesAired: Int = 0
//
//    @SerialName("rating")
//    val rating: String? = null
//
//    @SerialName("synonyms")
//    val synonyms: List<String?>? = null
//
//    @SerialName("duration")
//    val duration: Int? = null
//
//    @SerialName("updated_at")
//    val updatedAt: String? = null
//
//    @SerialName("next_episode_at")
//    val nextEpisodeAt: String? = null
//
//    @SerialName("fansubbers")
//    val fanSubbers: List<String> = emptyList()
//
//    @SerialName("fandubbers")
//    val fanDubbers: List<String> = emptyList()
//
//    @SerialName("studios")
//    val studios: List<Studio> = emptyList()
//
//    @SerialName("videos")
//    val videos: List<Video> = emptyList()
//
//    @SerialName("screenshots")
//    val screenshots: List<Screenshot> = emptyList()
//
//    @SerialName("user_rate")
//    val userRate: UserRate? = null
}

@Serializable
class AnimeBasic : BasicContent() {
    //@SerialName("episodes")
    val episodes: Int? = null

//    @SerialName("episodes_aired")
//    val episodesAired: Int? = null
}
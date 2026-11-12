package org.application.shikiapp.shared.models.ui.mappers.shiki

import com.fleeksoft.ksoup.Ksoup
import org.application.shikiapp.shared.models.ui.Review
import org.application.shikiapp.shared.utils.enums.OpinionType
import org.application.shikiapp.shared.utils.ui.Formatter
import org.application.shikiapp.shared.utils.ui.HtmlParser

internal fun parseReviews(dto: org.application.shikiapp.shared.models.data.Review) =
    Ksoup.parse(dto.content).select("article.b-review-topic").map { review ->
        val id = review.attr("id")
        val userId = review.attr("data-user_id").toLongOrNull() ?: 0L
        val nickname = review.selectFirst("meta[itemprop=author]")?.attr("content").orEmpty()

        val avatarImg = review.selectFirst("img")
        val avatarUrl = avatarImg?.attr("src").orEmpty()

        val body = review.selectFirst("div.body[itemprop=text]")?.html().orEmpty()
        val date = review.selectFirst("meta[itemprop=datePublished]")?.attr("content").orEmpty()

        val opinion = when {
            review.selectFirst("div.opinion.positive") != null -> OpinionType.POSITIVE
            review.selectFirst("div.opinion.negative") != null -> OpinionType.NEGATIVE
            review.selectFirst("div.opinion.neutral") != null -> OpinionType.NEUTRAL
            else -> OpinionType.UNKNOWN
        }

        val scoreClass = review.selectFirst("div.stars.score")?.attr("class").orEmpty()
        val animeScore = Regex("score-(\\d+)").find(scoreClass)?.groupValues?.get(1)?.toIntOrNull()

        val watchStatus = review.selectFirst("div.status-name")?.attr("data-text")?.takeIf(String::isNotBlank)

        val votesFor = review.selectFirst("span.votes-for")?.text().orEmpty()
        val votesAgainst = review.selectFirst("span.votes-against")?.text().orEmpty()

        Review(
            id = id,
            userId = userId,
            userNickname = nickname,
            userAvatar = avatarUrl,
            date = Formatter.convertDate(date),
            body = HtmlParser.parseComment(body),
            opinion = opinion,
            animeScore = animeScore,
            watchStatus = watchStatus,
            votesFor = votesFor,
            votesAgainst = votesAgainst
        )
    }

internal fun parseReviewsNextPage(postloader: String?): Int? {
    if (postloader.isNullOrBlank()) return null

    val nextLink = Ksoup.parse(postloader).selectFirst("a.next")?.attr("href") ?: return null
    return nextLink.substringAfterLast("page/").substringBefore(".").toIntOrNull()
}

package org.application.shikiapp.shared.models.ui.mappers.dark

import io.ktor.http.URLBuilder
import io.ktor.http.takeFrom
import org.application.shikiapp.shared.models.data.dark.ReviewItem
import org.application.shikiapp.shared.models.ui.Review
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.OpinionType
import org.application.shikiapp.shared.utils.enums.WatchStatus
import org.application.shikiapp.shared.utils.ui.Formatter
import org.application.shikiapp.shared.utils.ui.HtmlParser

suspend fun ReviewItem.mapper(): Review {
    val status = WatchStatus.entries.firstOrNull { it.name.equals(watchStatus, ignoreCase = true) }

    return Review(
        id = id.toString(),
        userId = userId,
        userNickname = userNickname,
        userAvatar = if (userAvatar.isBlank()) BLANK else URLBuilder(ApiRoutes.workingBaseUrl).takeFrom(userAvatar).buildString(),
        date = Formatter.convertDate(date),
        body = HtmlParser.parseComment(bodyHtml),
        opinion = OpinionType.entries.firstOrNull { it.name.equals(opinion, ignoreCase = true) } ?: OpinionType.UNKNOWN,
        animeScore = animeScore,
        watchStatus = if (status == null) null else ResourceText.StringResource(status.titleAnime).asString(),
        votesFor = (ripVotesFor.toLong() + shikiVotesFor).toString(),
        votesAgainst = (ripVotesAgainst.toLong() + shikiVotesAgainst).toString()
    )
}

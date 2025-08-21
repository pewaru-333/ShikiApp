package org.application.shikiapp.utils.extensions

import org.application.shikiapp.R
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.CommentContent
import org.application.shikiapp.utils.ResourceText

fun List<CommentContent>.getLastMessage() =
    when (val last = lastOrNull { it !is CommentContent.LineBreakContent }) {
        is CommentContent.ImageContent -> ResourceText.StringResource(R.string.text_image)
        is CommentContent.QuoteContent -> ResourceText.StringResource(R.string.text_quote)
        is CommentContent.SpoilerContent -> ResourceText.StringResource(R.string.text_spoiler_l)
        is CommentContent.TextContent -> ResourceText.StaticString(last.text.text)

        else -> ResourceText.StaticString(BLANK)
    }
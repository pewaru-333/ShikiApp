package org.application.shikiapp.shared.utils.extensions

import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.ui.CommentContent
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_image
import shikiapp.composeapp.generated.resources.text_quote
import shikiapp.composeapp.generated.resources.text_spoiler_l

fun List<CommentContent>.getLastMessage() = when (val last = lastOrNull()) {
    is CommentContent.ImageContent -> ResourceText.StringResource(Res.string.text_image)
    is CommentContent.QuoteContent -> ResourceText.StringResource(Res.string.text_quote)
    is CommentContent.SpoilerContent -> ResourceText.StringResource(Res.string.text_spoiler_l)
    is CommentContent.TextContent -> ResourceText.StaticString(last.text.text)

    else -> ResourceText.StaticString(BLANK)
}

fun List<CommentContent>.flattenImages() = asSequence()
    .flatMap(CommentContent::deepFlatten)
    .filterIsInstance<CommentContent.ImageContent>()
    .toList()

private fun CommentContent.deepFlatten(): Sequence<CommentContent> = sequence {
    yield(this@deepFlatten)
    items.forEach { yieldAll(it.deepFlatten()) }
}
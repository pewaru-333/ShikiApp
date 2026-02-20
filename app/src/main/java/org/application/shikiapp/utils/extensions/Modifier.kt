package org.application.shikiapp.utils.extensions

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.core.net.toUri

fun Modifier.clickableUrl(
    context: Context,
    string: AnnotatedString,
    onLayout: () -> TextLayoutResult?
) = pointerInput(string) {
    detectTapGestures { offset ->
        onLayout()?.let { layoutResult ->
            val position = layoutResult.getOffsetForPosition(offset)
            string.getStringAnnotations("URL", position, position).firstOrNull()
                ?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, annotation.item.toUri())
                    context.startActivity(intent)
                }
        }
    }
}
package org.application.shikiapp.shared.utils.extensions

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult

@Composable
fun Modifier.clickableUrl(string: AnnotatedString, onLayout: () -> TextLayoutResult?): Modifier {
    val uriHandler = LocalUriHandler.current

    return pointerInput(string) {
        detectTapGestures { offset ->
            onLayout()?.let { layoutResult ->
                val position = layoutResult.getOffsetForPosition(offset)

                string.getStringAnnotations("URL", position, position).firstOrNull()
                    ?.let { annotation ->
                        uriHandler.openUri(annotation.item)
                    }
            }
        }
    }
}
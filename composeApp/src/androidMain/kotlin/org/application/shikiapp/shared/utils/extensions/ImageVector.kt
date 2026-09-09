package org.application.shikiapp.shared.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.graphics.vector.*
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withScale

fun ImageVector.toBitmap(context: Context, size: Int = 24, tint: Color = Color.White): Bitmap {
    val density = context.resources.displayMetrics.density
    val sizePx = (size * density).toInt().coerceAtLeast(1)

    val bitmap = createBitmap(sizePx, sizePx)
    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = tint.toColorLong().toColorInt()
        style = Paint.Style.FILL
    }

    canvas.withScale(sizePx / viewportWidth, sizePx / viewportHeight) {
        val pathParser = PathParser()
        val stack = ArrayDeque<VectorNode>()
        stack.addLast(root)

        while (stack.isNotEmpty()) {
            when (val node = stack.removeLast()) {
                is VectorPath -> drawPath(pathParser.addPathNodes(node.pathData).toPath().asAndroidPath(), paint)
                is VectorGroup -> for (i in node.size - 1 downTo 0) {
                    stack.addLast(node[i])
                }
            }
        }
    }

    return bitmap
}

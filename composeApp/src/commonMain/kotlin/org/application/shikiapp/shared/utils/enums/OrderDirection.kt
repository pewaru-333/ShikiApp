package org.application.shikiapp.shared.utils.enums

import androidx.compose.ui.graphics.vector.ImageVector
import org.application.shikiapp.shared.ui.theme.Icons

enum class OrderDirection(val icon: ImageVector) {
    ASCENDING(Icons.ArrowUp),
    DESCENDING(Icons.ArrowDown)
}
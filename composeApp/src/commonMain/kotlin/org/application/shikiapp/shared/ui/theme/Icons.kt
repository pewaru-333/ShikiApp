package org.application.shikiapp.shared.ui.theme

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object Icons {
    private var _AppIcon: ImageVector? = null

    val AppIcon: ImageVector
        get() {
            if (_AppIcon != null) return _AppIcon!!
            _AppIcon = ImageVector.Builder(
                name = "_appIcon",
                defaultWidth = 108.0.dp,
                defaultHeight = 108.0.dp,
                viewportWidth = 108.0f,
                viewportHeight = 108.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFFFED2D5)),
                    strokeLineWidth = 1.0f,
                ) {
                    moveTo(0.0f, 0.0f)
                    lineTo(108.0f, 0.0f)
                    lineTo(108.0f, 108.0f)
                    lineTo(0.0f, 108.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 1.0f,
                ) {
                    moveTo(27.76f, 34.99f)
                    curveTo(27.85f, 35.0f, 27.93f, 35.0f, 28.01f, 35.05f)
                    curveTo(28.28f, 35.21f, 28.5f, 35.5f, 28.72f, 35.72f)
                    curveTo(28.94f, 35.94f, 29.18f, 36.15f, 29.42f, 36.35f)
                    curveTo(32.87f, 39.1f, 39.33f, 40.88f, 43.69f, 40.85f)
                    curveTo(45.4f, 40.83f, 47.07f, 40.49f, 48.76f, 40.3f)
                    curveTo(48.91f, 39.01f, 49.44f, 36.77f, 49.95f, 35.58f)
                    curveTo(50.01f, 35.45f, 50.04f, 35.37f, 50.18f, 35.32f)
                    curveTo(50.26f, 35.33f, 50.3f, 35.34f, 50.34f, 35.41f)
                    curveTo(50.54f, 35.69f, 50.13f, 36.34f, 50.07f, 36.65f)
                    curveTo(50.03f, 36.81f, 50.02f, 36.98f, 50.12f, 37.12f)
                    curveTo(50.16f, 37.17f, 50.22f, 37.21f, 50.28f, 37.22f)
                    curveTo(50.79f, 37.28f, 51.46f, 35.84f, 51.86f, 35.53f)
                    curveTo(51.94f, 35.47f, 51.99f, 35.46f, 52.08f, 35.47f)
                    curveTo(52.26f, 35.68f, 51.92f, 36.19f, 51.88f, 36.44f)
                    curveTo(51.86f, 36.54f, 51.87f, 36.65f, 51.88f, 36.75f)
                    curveTo(51.97f, 37.59f, 52.58f, 38.24f, 53.21f, 38.75f)
                    curveTo(53.83f, 39.24f, 54.5f, 39.52f, 55.26f, 39.73f)
                    curveTo(56.49f, 40.07f, 57.82f, 40.15f, 59.09f, 40.26f)
                    curveTo(61.8f, 40.49f, 64.52f, 40.7f, 67.21f, 40.22f)
                    curveTo(67.9f, 40.1f, 68.59f, 39.95f, 69.25f, 39.73f)
                    curveTo(69.66f, 39.59f, 70.76f, 39.09f, 71.11f, 39.07f)
                    curveTo(71.22f, 39.06f, 71.29f, 39.05f, 71.37f, 39.12f)
                    curveTo(71.33f, 39.43f, 70.42f, 39.78f, 70.17f, 40.07f)
                    lineTo(70.14f, 40.1f)
                    curveTo(70.58f, 40.56f, 71.23f, 40.78f, 71.77f, 41.1f)
                    curveTo(72.59f, 41.58f, 73.3f, 42.26f, 73.9f, 42.99f)
                    curveTo(74.91f, 44.23f, 76.3f, 46.46f, 76.49f, 48.05f)
                    curveTo(76.57f, 48.67f, 76.47f, 49.36f, 76.41f, 49.99f)
                    curveTo(76.55f, 50.03f, 76.68f, 50.08f, 76.83f, 50.08f)
                    curveTo(77.11f, 50.1f, 77.62f, 49.9f, 77.86f, 50.02f)
                    curveTo(77.95f, 50.06f, 77.95f, 50.08f, 77.97f, 50.16f)
                    curveTo(77.96f, 50.29f, 77.89f, 50.36f, 77.82f, 50.46f)
                    curveTo(78.03f, 50.61f, 78.22f, 50.77f, 78.42f, 50.93f)
                    curveTo(79.4f, 51.78f, 79.96f, 52.82f, 80.06f, 54.13f)
                    curveTo(80.17f, 55.65f, 79.42f, 57.27f, 78.1f, 58.08f)
                    curveTo(77.78f, 58.27f, 77.31f, 58.37f, 77.06f, 58.67f)
                    curveTo(76.62f, 59.23f, 77.71f, 60.01f, 77.69f, 60.56f)
                    curveTo(77.68f, 60.65f, 77.67f, 60.66f, 77.61f, 60.72f)
                    curveTo(77.3f, 60.78f, 76.98f, 60.31f, 76.75f, 60.13f)
                    curveTo(75.79f, 59.37f, 74.4f, 58.84f, 73.27f, 58.35f)
                    curveTo(70.84f, 57.32f, 68.28f, 56.77f, 65.68f, 56.39f)
                    curveTo(64.62f, 56.24f, 63.54f, 56.1f, 62.47f, 56.04f)
                    curveTo(63.76f, 57.34f, 64.99f, 58.69f, 66.09f, 60.17f)
                    curveTo(68.2f, 63.01f, 70.74f, 67.79f, 71.43f, 71.26f)
                    curveTo(71.96f, 73.94f, 71.58f, 76.71f, 70.03f, 79.01f)
                    curveTo(69.42f, 79.92f, 68.45f, 80.79f, 67.34f, 81.0f)
                    curveTo(65.4f, 81.35f, 64.91f, 79.47f, 63.62f, 78.57f)
                    curveTo(63.39f, 78.42f, 63.16f, 78.31f, 62.88f, 78.37f)
                    curveTo(62.33f, 78.48f, 62.08f, 79.05f, 61.8f, 79.47f)
                    curveTo(61.68f, 79.65f, 61.53f, 79.85f, 61.3f, 79.9f)
                    curveTo(61.22f, 79.89f, 61.17f, 79.86f, 61.12f, 79.8f)
                    curveTo(60.94f, 79.54f, 61.35f, 78.98f, 61.48f, 78.75f)
                    curveTo(61.71f, 78.36f, 61.87f, 77.94f, 62.05f, 77.52f)
                    curveTo(62.66f, 76.02f, 63.13f, 74.46f, 63.28f, 72.84f)
                    curveTo(63.35f, 71.98f, 63.36f, 71.12f, 63.34f, 70.25f)
                    curveTo(63.33f, 69.18f, 63.28f, 68.11f, 63.13f, 67.05f)
                    curveTo(63.04f, 66.38f, 62.9f, 65.72f, 62.75f, 65.07f)
                    curveTo(62.15f, 62.42f, 61.2f, 59.87f, 59.73f, 57.57f)
                    curveTo(59.37f, 56.99f, 59.0f, 56.4f, 58.56f, 55.87f)
                    lineTo(58.54f, 55.85f)
                    curveTo(57.81f, 55.75f, 56.7f, 55.81f, 55.95f, 55.82f)
                    curveTo(55.58f, 55.85f, 55.2f, 55.85f, 54.83f, 55.88f)
                    curveTo(55.23f, 56.88f, 55.65f, 57.86f, 56.02f, 58.87f)
                    curveTo(57.52f, 63.04f, 58.47f, 67.52f, 57.76f, 71.94f)
                    curveTo(57.59f, 72.99f, 57.38f, 74.02f, 57.01f, 75.01f)
                    curveTo(56.32f, 76.82f, 54.83f, 78.76f, 53.03f, 79.57f)
                    curveTo(52.14f, 79.97f, 51.17f, 79.93f, 50.24f, 79.75f)
                    curveTo(46.22f, 78.96f, 45.42f, 76.15f, 42.74f, 73.73f)
                    curveTo(42.27f, 73.29f, 41.67f, 72.82f, 41.07f, 72.6f)
                    lineTo(41.02f, 72.58f)
                    curveTo(40.64f, 72.96f, 40.36f, 73.4f, 40.04f, 73.83f)
                    curveTo(39.61f, 74.4f, 39.15f, 74.96f, 38.79f, 75.58f)
                    curveTo(38.57f, 75.96f, 38.4f, 76.35f, 38.32f, 76.78f)
                    curveTo(38.1f, 77.85f, 38.93f, 78.29f, 39.52f, 79.01f)
                    curveTo(39.61f, 79.12f, 39.76f, 79.3f, 39.78f, 79.44f)
                    curveTo(39.79f, 79.53f, 39.78f, 79.54f, 39.73f, 79.6f)
                    curveTo(39.64f, 79.64f, 39.56f, 79.65f, 39.46f, 79.63f)
                    curveTo(38.04f, 79.4f, 35.08f, 76.45f, 34.22f, 75.29f)
                    curveTo(32.47f, 72.91f, 31.5f, 70.02f, 31.95f, 67.06f)
                    curveTo(32.13f, 65.89f, 32.57f, 64.7f, 33.23f, 63.72f)
                    curveTo(33.86f, 62.79f, 34.76f, 62.08f, 35.58f, 61.32f)
                    curveTo(36.23f, 60.72f, 36.83f, 60.06f, 37.49f, 59.48f)
                    curveTo(38.09f, 58.96f, 38.75f, 58.5f, 39.39f, 58.03f)
                    curveTo(38.0f, 58.36f, 36.61f, 58.81f, 35.26f, 59.26f)
                    curveTo(34.64f, 59.47f, 34.02f, 59.7f, 33.39f, 59.89f)
                    curveTo(32.85f, 60.05f, 32.27f, 60.0f, 31.74f, 60.18f)
                    curveTo(31.51f, 60.25f, 31.33f, 60.35f, 31.17f, 60.54f)
                    curveTo(30.54f, 61.28f, 30.66f, 62.62f, 30.09f, 63.27f)
                    curveTo(30.02f, 63.35f, 29.97f, 63.36f, 29.87f, 63.37f)
                    curveTo(29.81f, 63.32f, 29.8f, 63.3f, 29.79f, 63.22f)
                    curveTo(29.75f, 62.96f, 29.85f, 62.59f, 29.91f, 62.33f)
                    curveTo(30.45f, 59.89f, 32.54f, 56.62f, 34.07f, 54.63f)
                    curveTo(33.54f, 54.46f, 33.01f, 54.38f, 32.53f, 54.1f)
                    curveTo(32.5f, 54.05f, 32.46f, 54.0f, 32.44f, 53.95f)
                    curveTo(32.4f, 53.85f, 32.49f, 53.73f, 32.53f, 53.65f)
                    curveTo(33.17f, 53.24f, 34.21f, 53.47f, 34.93f, 53.5f)
                    curveTo(36.29f, 51.76f, 37.91f, 49.64f, 39.73f, 48.36f)
                    curveTo(39.89f, 48.24f, 40.15f, 48.04f, 40.34f, 48.0f)
                    curveTo(40.43f, 47.98f, 40.46f, 47.99f, 40.52f, 48.03f)
                    lineTo(40.53f, 48.11f)
                    curveTo(40.4f, 48.46f, 39.99f, 48.75f, 39.75f, 49.04f)
                    curveTo(39.57f, 49.25f, 39.46f, 49.47f, 39.49f, 49.75f)
                    curveTo(39.53f, 50.15f, 39.81f, 50.58f, 40.13f, 50.82f)
                    curveTo(40.4f, 51.01f, 40.75f, 51.06f, 41.07f, 51.11f)
                    curveTo(41.76f, 51.21f, 42.48f, 51.19f, 43.18f, 51.23f)
                    curveTo(44.48f, 51.29f, 45.78f, 51.36f, 47.09f, 51.31f)
                    curveTo(48.21f, 51.26f, 49.33f, 51.14f, 50.44f, 51.0f)
                    curveTo(50.38f, 50.51f, 50.14f, 50.14f, 49.93f, 49.71f)
                    curveTo(49.78f, 49.4f, 49.64f, 49.08f, 49.54f, 48.74f)
                    curveTo(49.19f, 47.66f, 49.01f, 46.56f, 48.73f, 45.47f)
                    curveTo(47.62f, 45.61f, 46.29f, 46.07f, 45.16f, 46.33f)
                    curveTo(44.39f, 46.51f, 43.62f, 46.63f, 42.85f, 46.79f)
                    curveTo(39.4f, 47.46f, 34.49f, 48.26f, 31.21f, 46.71f)
                    curveTo(29.44f, 45.87f, 27.72f, 44.28f, 27.06f, 42.4f)
                    curveTo(26.54f, 40.93f, 26.55f, 39.33f, 27.09f, 37.87f)
                    curveTo(27.27f, 37.36f, 27.55f, 36.85f, 27.68f, 36.33f)
                    curveTo(27.78f, 35.92f, 27.58f, 35.37f, 27.76f, 34.99f)
                    close()
                    moveTo(39.82f, 57.95f)
                    curveTo(39.42f, 58.53f, 38.9f, 58.95f, 38.45f, 59.48f)
                    curveTo(37.29f, 60.87f, 37.07f, 62.82f, 37.4f, 64.55f)
                    curveTo(37.72f, 66.28f, 38.71f, 67.97f, 39.83f, 69.3f)
                    curveTo(40.17f, 69.7f, 40.64f, 70.07f, 40.91f, 70.52f)
                    lineTo(40.91f, 70.54f)
                    curveTo(41.1f, 71.19f, 40.89f, 71.35f, 41.58f, 71.74f)
                    curveTo(42.41f, 72.21f, 44.24f, 73.12f, 45.14f, 73.2f)
                    curveTo(45.49f, 73.23f, 45.84f, 73.15f, 46.17f, 73.06f)
                    curveTo(47.81f, 72.61f, 49.03f, 71.35f, 49.84f, 69.9f)
                    curveTo(50.4f, 68.89f, 50.81f, 67.79f, 51.15f, 66.68f)
                    curveTo(52.0f, 63.88f, 52.33f, 61.03f, 52.03f, 58.1f)
                    curveTo(51.96f, 57.51f, 51.92f, 56.86f, 51.76f, 56.28f)
                    curveTo(51.72f, 56.16f, 51.67f, 55.99f, 51.55f, 55.91f)
                    curveTo(51.44f, 55.9f, 51.33f, 55.91f, 51.21f, 55.92f)
                    curveTo(50.31f, 55.99f, 49.41f, 56.06f, 48.51f, 56.18f)
                    curveTo(46.55f, 56.44f, 44.62f, 56.83f, 42.69f, 57.26f)
                    curveTo(41.73f, 57.47f, 40.76f, 57.64f, 39.82f, 57.95f)
                    close()
                    moveTo(51.2f, 50.93f)
                    curveTo(52.35f, 50.99f, 53.78f, 50.74f, 54.94f, 50.61f)
                    curveTo(56.75f, 50.41f, 58.56f, 50.18f, 60.37f, 49.91f)
                    curveTo(62.01f, 49.67f, 63.7f, 49.44f, 65.26f, 48.9f)
                    curveTo(65.61f, 48.78f, 65.95f, 48.64f, 66.28f, 48.48f)
                    curveTo(66.5f, 48.36f, 67.05f, 48.0f, 67.23f, 47.96f)
                    curveTo(67.35f, 47.94f, 67.38f, 47.97f, 67.46f, 48.05f)
                    curveTo(67.49f, 48.13f, 67.49f, 48.17f, 67.45f, 48.25f)
                    curveTo(67.28f, 48.67f, 66.26f, 49.15f, 65.86f, 49.4f)
                    curveTo(65.6f, 49.54f, 64.94f, 49.87f, 64.85f, 50.17f)
                    curveTo(64.83f, 50.22f, 64.84f, 50.22f, 64.86f, 50.27f)
                    curveTo(65.08f, 50.38f, 66.75f, 50.05f, 67.16f, 50.01f)
                    curveTo(68.76f, 49.85f, 70.37f, 49.88f, 71.97f, 49.92f)
                    curveTo(72.72f, 49.94f, 73.49f, 49.99f, 74.25f, 49.96f)
                    curveTo(73.75f, 49.41f, 73.22f, 48.93f, 72.62f, 48.49f)
                    curveTo(70.18f, 46.75f, 66.64f, 45.64f, 63.72f, 45.12f)
                    curveTo(62.14f, 44.83f, 60.52f, 44.68f, 58.91f, 44.6f)
                    curveTo(57.97f, 44.56f, 57.04f, 44.59f, 56.1f, 44.58f)
                    curveTo(55.08f, 44.61f, 52.4f, 44.69f, 51.52f, 45.02f)
                    curveTo(51.37f, 45.08f, 51.24f, 45.17f, 51.13f, 45.29f)
                    curveTo(50.49f, 45.96f, 50.02f, 47.5f, 50.06f, 48.42f)
                    curveTo(50.09f, 49.07f, 50.57f, 50.38f, 51.06f, 50.81f)
                    curveTo(51.11f, 50.85f, 51.16f, 50.89f, 51.2f, 50.93f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 1.0f,
                ) {
                    moveTo(41.48f, 27.36f)
                    curveTo(42.54f, 27.28f, 43.63f, 27.4f, 44.68f, 27.51f)
                    curveTo(46.29f, 27.69f, 47.89f, 27.91f, 49.52f, 27.94f)
                    curveTo(50.63f, 27.96f, 51.75f, 27.85f, 52.86f, 27.82f)
                    curveTo(54.1f, 27.78f, 55.35f, 27.8f, 56.58f, 27.72f)
                    curveTo(57.72f, 27.64f, 58.85f, 27.49f, 59.98f, 27.42f)
                    curveTo(61.47f, 27.34f, 62.96f, 27.37f, 64.45f, 27.44f)
                    curveTo(65.48f, 27.49f, 66.48f, 27.54f, 67.49f, 27.82f)
                    curveTo(69.3f, 28.32f, 71.64f, 29.42f, 72.59f, 31.12f)
                    curveTo(73.36f, 32.49f, 73.66f, 34.51f, 73.32f, 36.06f)
                    curveTo(73.19f, 36.62f, 72.91f, 37.17f, 72.9f, 37.75f)
                    curveTo(72.89f, 38.17f, 73.25f, 39.35f, 73.13f, 39.63f)
                    curveTo(73.1f, 39.71f, 73.08f, 39.72f, 73.01f, 39.76f)
                    curveTo(72.71f, 39.68f, 72.48f, 39.05f, 72.34f, 38.79f)
                    curveTo(72.25f, 38.64f, 72.14f, 38.5f, 72.04f, 38.36f)
                    curveTo(71.6f, 37.77f, 71.09f, 37.28f, 70.53f, 36.8f)
                    curveTo(69.84f, 36.27f, 69.09f, 35.82f, 68.29f, 35.46f)
                    curveTo(63.3f, 33.24f, 56.25f, 33.96f, 50.98f, 34.73f)
                    curveTo(49.77f, 34.9f, 48.57f, 35.09f, 47.36f, 35.24f)
                    curveTo(46.35f, 35.37f, 45.35f, 35.52f, 44.33f, 35.43f)
                    curveTo(41.98f, 35.22f, 39.15f, 33.5f, 37.66f, 31.69f)
                    curveTo(37.28f, 31.24f, 36.28f, 29.86f, 36.37f, 29.29f)
                    curveTo(36.39f, 29.21f, 36.42f, 29.15f, 36.49f, 29.1f)
                    curveTo(36.64f, 29.02f, 36.85f, 29.13f, 36.99f, 29.19f)
                    curveTo(38.79f, 27.63f, 39.16f, 27.57f, 41.48f, 27.36f)
                    close()
                }
            }.build()
            return _AppIcon!!
        }

    private var _AppIconRip: ImageVector? = null

    val AppIconRip: ImageVector
        get() {
            if (_AppIconRip != null) return _AppIconRip!!

            return ImageVector.Builder(
                name = "_AppIconRip",
                defaultWidth = 108.0.dp,
                defaultHeight = 108.0.dp,
                viewportWidth = 108.0f,
                viewportHeight = 108.0f,
            ).apply {
                // <rect width="108" height="108" fill="#000" />
                path(
                    fill = SolidColor(Color(0xFF000000)),
                ) {
                    // M 0 0
                    moveTo(x = 0.0f, y = 0.0f)
                    // h 108
                    horizontalLineToRelative(dx = 108.0f)
                    // v 108
                    verticalLineToRelative(dy = 108.0f)
                    // h -108z
                    horizontalLineToRelative(dx = -108.0f)
                    close()
                }
                // m62.68 27.33 -.06 .06 -.46 .62 -.35 .5 h-.99 c-.15 0 -.15 .07 -.02 .32 l.1 .19 -.7 .05 a81 81 0 0 0 -7.16 .9 10 10 0 0 1 -1.48 .1 c-1.01 .02 -1.1 .04 -1.75 .41 q-.16 .1 -.28 .04 -.11 -.07 -.19 .03 c-.07 .08 -.31 .12 -2.09 .31 q-.73 .08 -1.46 .2 c-.72 .12 -.76 .12 -1.15 0 a17 17 0 0 0 -2.73 -.37 l-.6 -.05 -.36 .32 a5 5 0 0 0 -.63 .67 C40.12 31.91 40 32 39.88 32 c-.27 0 -2.73 .38 -4.51 .7 -1.92 .35 -2.65 .42 -3.21 .3 -.59 -.11 -.6 -.12 -.57 -.86 .03 -.63 .02 -.65 -.14 -.73 -.37 -.19 -.73 -.08 -1.2 .36 a.6 .6 0 0 1 -.45 .15 2 2 0 0 0 -.66 .17 l-.4 .18 .03 .35 c.01 .2 0 .41 -.05 .48 a3 3 0 0 0 -.04 1.03 4 4 0 0 1 -.09 1.33 l-.11 .43 .32 .46 c.65 .9 1.1 1.6 1.1 1.67 0 .04 .2 .26 .46 .48 1.11 .97 1.58 1.24 2.67 1.47 1.16 .25 1.35 .23 2.06 -.15 a5 5 0 0 0 .86 -.57 8 8 0 0 1 1.63 -.84 c1.48 -.66 1.67 -.74 1.67 -.65 l.2 2 c.24 2.25 .32 4.4 .22 6.06 -.08 1.3 -.14 1.47 -1.06 3.2 a37 37 0 0 1 -4.4 6.68 q-.42 .48 -.78 1 a12 12 0 0 1 -1.9 2.08 c-.3 .26 -.48 .48 -.46 .54 s.23 .16 .46 .24 a3 3 0 0 1 .5 .21 c.1 .11 .23 .07 .73 -.2 l.49 -.27 .19 .14 c.24 .15 .66 .17 1.02 .03 q.3 -.1 .63 -.13 a2 2 0 0 0 1.12 -.49 2 2 0 0 1 .47 -.26 22 22 0 0 0 2.79 -2.82 33 33 0 0 1 1.58 -1.66 9 9 0 0 1 1.66 -1.41 c.68 -.4 1.22 -.9 1.29 -1.18 a2 2 0 0 1 .2 -.47 3.5 3.5 0 0 0 .38 -1.5 3 3 0 0 0 -.22 -.5 c-.17 -.32 -.19 -.42 -.22 -1.28 a6 6 0 0 0 -1.07 -3.5 6 6 0 0 1 -.83 -1.82 9 9 0 0 1 -.36 -4.08 c.1 -.56 .16 -.63 .61 -.9 a5 5 0 0 0 .7 -.5 l.27 -.25 1.3 -.14 a33 33 0 0 0 1.79 -.25 l1.28 -.27 a17 17 0 0 0 1.42 -.36 q.5 -.17 1.03 -.3 a13 13 0 0 1 2.14 -.2 29 29 0 0 1 .03 2.13 c0 1.82 -.02 2.3 -.18 3.64 a49 49 0 0 0 -.38 5.54 c-.06 2.52 -.08 2.63 -.49 2.93 -.3 .21 -.6 .17 -1.5 -.22 l-2.08 -.86 a39 39 0 0 1 -2.2 -.97 c-.5 -.26 -.92 -.45 -.92 -.43 s.25 .35 .57 .74 .57 .71 .57 .73 -.06 .04 -.14 .04 c-.12 0 -.13 .01 -.04 .14 q.1 .14 .07 .37 c-.03 .18 -.01 .24 .12 .33 a1 1 0 0 1 .32 .37 2 2 0 0 0 .3 .4 2 2 0 0 1 .3 .35 c.06 .13 .3 .36 .5 .52 s.43 .37 .47 .46 .24 .25 .47 .36 a4 4 0 0 1 1.43 1.26 c.08 .19 .23 .34 .66 .62 a13 13 0 0 1 1.62 1.42 c1.22 1.2 1.29 1.25 2.28 1.19 .65 -.05 .97 -.17 1.36 -.52 q.19 -.18 .4 -.33 a2 2 0 0 0 .37 -.44 2 2 0 0 1 .42 -.48 c.28 -.17 .79 -.88 .79 -1.11 0 -.34 .13 -.55 .57 -.97 a2.3 2.3 0 0 0 .71 -1.08 8 8 0 0 0 .63 -2.68 19 19 0 0 0 -.03 -4.3 103 103 0 0 0 -1.64 -7.77 12 12 0 0 1 -.51 -2.22 c0 -.07 .14 -.08 .7 -.04 a6 6 0 0 0 1.47 -.07 5 5 0 0 1 1.65 -.04 6 6 0 0 1 .87 .1 84 84 0 0 1 1.27 4.1 c.36 1.41 .37 1.51 .44 5.05 l.08 4.88 a7 7 0 0 0 .2 2.17 6 6 0 0 0 .93 2 q.35 .52 .66 1.07 a5 5 0 0 0 1.34 1.57 11 11 0 0 0 1.9 1.24 3.4 3.4 0 0 1 1.39 1.22 c.17 .25 .42 .58 .55 .74 s.3 .43 .38 .58 c.13 .26 .17 .3 .9 .5 a6 6 0 0 0 1.78 .04 c.29 -.05 .4 -.11 .86 -.57 q.93 -.88 .92 -1.33 c0 -.47 .07 -.6 .34 -.72 q.27 -.1 .26 -.22 c0 -.43 -.44 -2.14 -.67 -2.58 -.15 -.3 -2.08 -2.9 -3.7 -5 -1.72 -2.24 -2.32 -3.2 -2.68 -4.25 a64 64 0 0 1 -.81 -4.7 18 18 0 0 0 -1.17 -4.22 4 4 0 0 1 -.3 -.93 c.15 0 3.62 .97 3.82 1.07 l1.16 .63 c.5 .3 .99 .52 1.08 .52 s.52 .24 1.02 .57 l.85 .58 .45 -.04 c.64 -.06 .76 -.09 1.03 -.27 .38 -.27 .6 -.3 .91 -.17 .59 .28 .99 .33 .63 .08 -.53 -.37 -.58 -.55 -.27 -1.02 .37 -.56 .4 -.6 .79 -.8 .61 -.3 .65 -.38 .85 -1.48 a8 8 0 0 1 .28 -1.17 c.15 -.33 .12 -.53 -.14 -.78 a5 5 0 0 1 -.5 -.64 c-.22 -.32 -.28 -.48 -.28 -.71 0 -.27 -.02 -.32 -.22 -.44 a3 3 0 0 0 -.47 -.24 c-.3 -.1 -.74 -.57 -.68 -.7 l.07 -.19 c.06 -.14 -.49 -.43 -1.03 -.56 -.33 -.08 -.44 -.1 -.47 -.14 l-2.7 -.3 q-.05 .02 -.24 .02 a13 13 0 0 1 -1.06 -.1 q-.58 -.08 -1.16 -.11 a254 254 0 0 0 -5.76 .1 l-.97 .04 -.16 -.33 a3 3 0 0 0 -.56 -.72 c-.28 -.29 -.37 -.36 -.43 -.33
                path(
                    fill = SolidColor(Color(0xFFFFFFFF)),
                ) {
                    // M 62.68 27.33
                    moveTo(x = 62.68f, y = 27.33f)
                    // l -0.06 0.06
                    lineToRelative(dx = -0.06f, dy = 0.06f)
                    // l -0.46 0.62
                    lineToRelative(dx = -0.46f, dy = 0.62f)
                    // l -0.35 0.5
                    lineToRelative(dx = -0.35f, dy = 0.5f)
                    // h -0.99
                    horizontalLineToRelative(dx = -0.99f)
                    // c -0.15 0 -0.15 0.07 -0.02 0.32
                    curveToRelative(
                        dx1 = -0.15f,
                        dy1 = 0.0f,
                        dx2 = -0.15f,
                        dy2 = 0.07f,
                        dx3 = -0.02f,
                        dy3 = 0.32f,
                    )
                    // l 0.1 0.19
                    lineToRelative(dx = 0.1f, dy = 0.19f)
                    // l -0.7 0.05
                    lineToRelative(dx = -0.7f, dy = 0.05f)
                    // a 81 81 0 0 0 -7.16 0.9
                    arcToRelative(
                        a = 81.0f,
                        b = 81.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -7.16f,
                        dy1 = 0.9f,
                    )
                    // a 10 10 0 0 1 -1.48 0.1
                    arcToRelative(
                        a = 10.0f,
                        b = 10.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -1.48f,
                        dy1 = 0.1f,
                    )
                    // c -1.01 0.02 -1.1 0.04 -1.75 0.41
                    curveToRelative(
                        dx1 = -1.01f,
                        dy1 = 0.02f,
                        dx2 = -1.1f,
                        dy2 = 0.04f,
                        dx3 = -1.75f,
                        dy3 = 0.41f,
                    )
                    // q -0.16 0.1 -0.28 0.04
                    quadToRelative(
                        dx1 = -0.16f,
                        dy1 = 0.1f,
                        dx2 = -0.28f,
                        dy2 = 0.04f,
                    )
                    // q -0.11 -0.07 -0.19 0.03
                    quadToRelative(
                        dx1 = -0.11f,
                        dy1 = -0.07f,
                        dx2 = -0.19f,
                        dy2 = 0.03f,
                    )
                    // c -0.07 0.08 -0.31 0.12 -2.09 0.31
                    curveToRelative(
                        dx1 = -0.07f,
                        dy1 = 0.08f,
                        dx2 = -0.31f,
                        dy2 = 0.12f,
                        dx3 = -2.09f,
                        dy3 = 0.31f,
                    )
                    // q -0.73 0.08 -1.46 0.2
                    quadToRelative(
                        dx1 = -0.73f,
                        dy1 = 0.08f,
                        dx2 = -1.46f,
                        dy2 = 0.2f,
                    )
                    // c -0.72 0.12 -0.76 0.12 -1.15 0
                    curveToRelative(
                        dx1 = -0.72f,
                        dy1 = 0.12f,
                        dx2 = -0.76f,
                        dy2 = 0.12f,
                        dx3 = -1.15f,
                        dy3 = 0.0f,
                    )
                    // a 17 17 0 0 0 -2.73 -0.37
                    arcToRelative(
                        a = 17.0f,
                        b = 17.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -2.73f,
                        dy1 = -0.37f,
                    )
                    // l -0.6 -0.05
                    lineToRelative(dx = -0.6f, dy = -0.05f)
                    // l -0.36 0.32
                    lineToRelative(dx = -0.36f, dy = 0.32f)
                    // a 5 5 0 0 0 -0.63 0.67
                    arcToRelative(
                        a = 5.0f,
                        b = 5.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.63f,
                        dy1 = 0.67f,
                    )
                    // C 40.12 31.91 40 32 39.88 32
                    curveTo(
                        x1 = 40.12f,
                        y1 = 31.91f,
                        x2 = 40.0f,
                        y2 = 32.0f,
                        x3 = 39.88f,
                        y3 = 32.0f,
                    )
                    // c -0.27 0 -2.73 0.38 -4.51 0.7
                    curveToRelative(
                        dx1 = -0.27f,
                        dy1 = 0.0f,
                        dx2 = -2.73f,
                        dy2 = 0.38f,
                        dx3 = -4.51f,
                        dy3 = 0.7f,
                    )
                    // c -1.92 0.35 -2.65 0.42 -3.21 0.3
                    curveToRelative(
                        dx1 = -1.92f,
                        dy1 = 0.35f,
                        dx2 = -2.65f,
                        dy2 = 0.42f,
                        dx3 = -3.21f,
                        dy3 = 0.3f,
                    )
                    // c -0.59 -0.11 -0.6 -0.12 -0.57 -0.86
                    curveToRelative(
                        dx1 = -0.59f,
                        dy1 = -0.11f,
                        dx2 = -0.6f,
                        dy2 = -0.12f,
                        dx3 = -0.57f,
                        dy3 = -0.86f,
                    )
                    // c 0.03 -0.63 0.02 -0.65 -0.14 -0.73
                    curveToRelative(
                        dx1 = 0.03f,
                        dy1 = -0.63f,
                        dx2 = 0.02f,
                        dy2 = -0.65f,
                        dx3 = -0.14f,
                        dy3 = -0.73f,
                    )
                    // c -0.37 -0.19 -0.73 -0.08 -1.2 0.36
                    curveToRelative(
                        dx1 = -0.37f,
                        dy1 = -0.19f,
                        dx2 = -0.73f,
                        dy2 = -0.08f,
                        dx3 = -1.2f,
                        dy3 = 0.36f,
                    )
                    // a 0.6 0.6 0 0 1 -0.45 0.15
                    arcToRelative(
                        a = 0.6f,
                        b = 0.6f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.45f,
                        dy1 = 0.15f,
                    )
                    // a 2 2 0 0 0 -0.66 0.17
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.66f,
                        dy1 = 0.17f,
                    )
                    // l -0.4 0.18
                    lineToRelative(dx = -0.4f, dy = 0.18f)
                    // l 0.03 0.35
                    lineToRelative(dx = 0.03f, dy = 0.35f)
                    // c 0.01 0.2 0 0.41 -0.05 0.48
                    curveToRelative(
                        dx1 = 0.01f,
                        dy1 = 0.2f,
                        dx2 = 0.0f,
                        dy2 = 0.41f,
                        dx3 = -0.05f,
                        dy3 = 0.48f,
                    )
                    // a 3 3 0 0 0 -0.04 1.03
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.04f,
                        dy1 = 1.03f,
                    )
                    // a 4 4 0 0 1 -0.09 1.33
                    arcToRelative(
                        a = 4.0f,
                        b = 4.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.09f,
                        dy1 = 1.33f,
                    )
                    // l -0.11 0.43
                    lineToRelative(dx = -0.11f, dy = 0.43f)
                    // l 0.32 0.46
                    lineToRelative(dx = 0.32f, dy = 0.46f)
                    // c 0.65 0.9 1.1 1.6 1.1 1.67
                    curveToRelative(
                        dx1 = 0.65f,
                        dy1 = 0.9f,
                        dx2 = 1.1f,
                        dy2 = 1.6f,
                        dx3 = 1.1f,
                        dy3 = 1.67f,
                    )
                    // c 0 0.04 0.2 0.26 0.46 0.48
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = 0.04f,
                        dx2 = 0.2f,
                        dy2 = 0.26f,
                        dx3 = 0.46f,
                        dy3 = 0.48f,
                    )
                    // c 1.11 0.97 1.58 1.24 2.67 1.47
                    curveToRelative(
                        dx1 = 1.11f,
                        dy1 = 0.97f,
                        dx2 = 1.58f,
                        dy2 = 1.24f,
                        dx3 = 2.67f,
                        dy3 = 1.47f,
                    )
                    // c 1.16 0.25 1.35 0.23 2.06 -0.15
                    curveToRelative(
                        dx1 = 1.16f,
                        dy1 = 0.25f,
                        dx2 = 1.35f,
                        dy2 = 0.23f,
                        dx3 = 2.06f,
                        dy3 = -0.15f,
                    )
                    // a 5 5 0 0 0 0.86 -0.57
                    arcToRelative(
                        a = 5.0f,
                        b = 5.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.86f,
                        dy1 = -0.57f,
                    )
                    // a 8 8 0 0 1 1.63 -0.84
                    arcToRelative(
                        a = 8.0f,
                        b = 8.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.63f,
                        dy1 = -0.84f,
                    )
                    // c 1.48 -0.66 1.67 -0.74 1.67 -0.65
                    curveToRelative(
                        dx1 = 1.48f,
                        dy1 = -0.66f,
                        dx2 = 1.67f,
                        dy2 = -0.74f,
                        dx3 = 1.67f,
                        dy3 = -0.65f,
                    )
                    // l 0.2 2
                    lineToRelative(dx = 0.2f, dy = 2.0f)
                    // c 0.24 2.25 0.32 4.4 0.22 6.06
                    curveToRelative(
                        dx1 = 0.24f,
                        dy1 = 2.25f,
                        dx2 = 0.32f,
                        dy2 = 4.4f,
                        dx3 = 0.22f,
                        dy3 = 6.06f,
                    )
                    // c -0.08 1.3 -0.14 1.47 -1.06 3.2
                    curveToRelative(
                        dx1 = -0.08f,
                        dy1 = 1.3f,
                        dx2 = -0.14f,
                        dy2 = 1.47f,
                        dx3 = -1.06f,
                        dy3 = 3.2f,
                    )
                    // a 37 37 0 0 1 -4.4 6.68
                    arcToRelative(
                        a = 37.0f,
                        b = 37.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -4.4f,
                        dy1 = 6.68f,
                    )
                    // q -0.42 0.48 -0.78 1
                    quadToRelative(
                        dx1 = -0.42f,
                        dy1 = 0.48f,
                        dx2 = -0.78f,
                        dy2 = 1.0f,
                    )
                    // a 12 12 0 0 1 -1.9 2.08
                    arcToRelative(
                        a = 12.0f,
                        b = 12.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -1.9f,
                        dy1 = 2.08f,
                    )
                    // c -0.3 0.26 -0.48 0.48 -0.46 0.54
                    curveToRelative(
                        dx1 = -0.3f,
                        dy1 = 0.26f,
                        dx2 = -0.48f,
                        dy2 = 0.48f,
                        dx3 = -0.46f,
                        dy3 = 0.54f,
                    )
                    // s 0.23 0.16 0.46 0.24
                    reflectiveCurveToRelative(
                        dx1 = 0.23f,
                        dy1 = 0.16f,
                        dx2 = 0.46f,
                        dy2 = 0.24f,
                    )
                    // a 3 3 0 0 1 0.5 0.21
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.5f,
                        dy1 = 0.21f,
                    )
                    // c 0.1 0.11 0.23 0.07 0.73 -0.2
                    curveToRelative(
                        dx1 = 0.1f,
                        dy1 = 0.11f,
                        dx2 = 0.23f,
                        dy2 = 0.07f,
                        dx3 = 0.73f,
                        dy3 = -0.2f,
                    )
                    // l 0.49 -0.27
                    lineToRelative(dx = 0.49f, dy = -0.27f)
                    // l 0.19 0.14
                    lineToRelative(dx = 0.19f, dy = 0.14f)
                    // c 0.24 0.15 0.66 0.17 1.02 0.03
                    curveToRelative(
                        dx1 = 0.24f,
                        dy1 = 0.15f,
                        dx2 = 0.66f,
                        dy2 = 0.17f,
                        dx3 = 1.02f,
                        dy3 = 0.03f,
                    )
                    // q 0.3 -0.1 0.63 -0.13
                    quadToRelative(
                        dx1 = 0.3f,
                        dy1 = -0.1f,
                        dx2 = 0.63f,
                        dy2 = -0.13f,
                    )
                    // a 2 2 0 0 0 1.12 -0.49
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 1.12f,
                        dy1 = -0.49f,
                    )
                    // a 2 2 0 0 1 0.47 -0.26
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.47f,
                        dy1 = -0.26f,
                    )
                    // a 22 22 0 0 0 2.79 -2.82
                    arcToRelative(
                        a = 22.0f,
                        b = 22.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 2.79f,
                        dy1 = -2.82f,
                    )
                    // a 33 33 0 0 1 1.58 -1.66
                    arcToRelative(
                        a = 33.0f,
                        b = 33.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.58f,
                        dy1 = -1.66f,
                    )
                    // a 9 9 0 0 1 1.66 -1.41
                    arcToRelative(
                        a = 9.0f,
                        b = 9.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.66f,
                        dy1 = -1.41f,
                    )
                    // c 0.68 -0.4 1.22 -0.9 1.29 -1.18
                    curveToRelative(
                        dx1 = 0.68f,
                        dy1 = -0.4f,
                        dx2 = 1.22f,
                        dy2 = -0.9f,
                        dx3 = 1.29f,
                        dy3 = -1.18f,
                    )
                    // a 2 2 0 0 1 0.2 -0.47
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.2f,
                        dy1 = -0.47f,
                    )
                    // a 3.5 3.5 0 0 0 0.38 -1.5
                    arcToRelative(
                        a = 3.5f,
                        b = 3.5f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.38f,
                        dy1 = -1.5f,
                    )
                    // a 3 3 0 0 0 -0.22 -0.5
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.22f,
                        dy1 = -0.5f,
                    )
                    // c -0.17 -0.32 -0.19 -0.42 -0.22 -1.28
                    curveToRelative(
                        dx1 = -0.17f,
                        dy1 = -0.32f,
                        dx2 = -0.19f,
                        dy2 = -0.42f,
                        dx3 = -0.22f,
                        dy3 = -1.28f,
                    )
                    // a 6 6 0 0 0 -1.07 -3.5
                    arcToRelative(
                        a = 6.0f,
                        b = 6.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -1.07f,
                        dy1 = -3.5f,
                    )
                    // a 6 6 0 0 1 -0.83 -1.82
                    arcToRelative(
                        a = 6.0f,
                        b = 6.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.83f,
                        dy1 = -1.82f,
                    )
                    // a 9 9 0 0 1 -0.36 -4.08
                    arcToRelative(
                        a = 9.0f,
                        b = 9.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.36f,
                        dy1 = -4.08f,
                    )
                    // c 0.1 -0.56 0.16 -0.63 0.61 -0.9
                    curveToRelative(
                        dx1 = 0.1f,
                        dy1 = -0.56f,
                        dx2 = 0.16f,
                        dy2 = -0.63f,
                        dx3 = 0.61f,
                        dy3 = -0.9f,
                    )
                    // a 5 5 0 0 0 0.7 -0.5
                    arcToRelative(
                        a = 5.0f,
                        b = 5.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.7f,
                        dy1 = -0.5f,
                    )
                    // l 0.27 -0.25
                    lineToRelative(dx = 0.27f, dy = -0.25f)
                    // l 1.3 -0.14
                    lineToRelative(dx = 1.3f, dy = -0.14f)
                    // a 33 33 0 0 0 1.79 -0.25
                    arcToRelative(
                        a = 33.0f,
                        b = 33.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 1.79f,
                        dy1 = -0.25f,
                    )
                    // l 1.28 -0.27
                    lineToRelative(dx = 1.28f, dy = -0.27f)
                    // a 17 17 0 0 0 1.42 -0.36
                    arcToRelative(
                        a = 17.0f,
                        b = 17.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 1.42f,
                        dy1 = -0.36f,
                    )
                    // q 0.5 -0.17 1.03 -0.3
                    quadToRelative(
                        dx1 = 0.5f,
                        dy1 = -0.17f,
                        dx2 = 1.03f,
                        dy2 = -0.3f,
                    )
                    // a 13 13 0 0 1 2.14 -0.2
                    arcToRelative(
                        a = 13.0f,
                        b = 13.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 2.14f,
                        dy1 = -0.2f,
                    )
                    // a 29 29 0 0 1 0.03 2.13
                    arcToRelative(
                        a = 29.0f,
                        b = 29.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.03f,
                        dy1 = 2.13f,
                    )
                    // c 0 1.82 -0.02 2.3 -0.18 3.64
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = 1.82f,
                        dx2 = -0.02f,
                        dy2 = 2.3f,
                        dx3 = -0.18f,
                        dy3 = 3.64f,
                    )
                    // a 49 49 0 0 0 -0.38 5.54
                    arcToRelative(
                        a = 49.0f,
                        b = 49.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.38f,
                        dy1 = 5.54f,
                    )
                    // c -0.06 2.52 -0.08 2.63 -0.49 2.93
                    curveToRelative(
                        dx1 = -0.06f,
                        dy1 = 2.52f,
                        dx2 = -0.08f,
                        dy2 = 2.63f,
                        dx3 = -0.49f,
                        dy3 = 2.93f,
                    )
                    // c -0.3 0.21 -0.6 0.17 -1.5 -0.22
                    curveToRelative(
                        dx1 = -0.3f,
                        dy1 = 0.21f,
                        dx2 = -0.6f,
                        dy2 = 0.17f,
                        dx3 = -1.5f,
                        dy3 = -0.22f,
                    )
                    // l -2.08 -0.86
                    lineToRelative(dx = -2.08f, dy = -0.86f)
                    // a 39 39 0 0 1 -2.2 -0.97
                    arcToRelative(
                        a = 39.0f,
                        b = 39.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -2.2f,
                        dy1 = -0.97f,
                    )
                    // c -0.5 -0.26 -0.92 -0.45 -0.92 -0.43
                    curveToRelative(
                        dx1 = -0.5f,
                        dy1 = -0.26f,
                        dx2 = -0.92f,
                        dy2 = -0.45f,
                        dx3 = -0.92f,
                        dy3 = -0.43f,
                    )
                    // s 0.25 0.35 0.57 0.74
                    reflectiveCurveToRelative(
                        dx1 = 0.25f,
                        dy1 = 0.35f,
                        dx2 = 0.57f,
                        dy2 = 0.74f,
                    )
                    // s 0.57 0.71 0.57 0.73
                    reflectiveCurveToRelative(
                        dx1 = 0.57f,
                        dy1 = 0.71f,
                        dx2 = 0.57f,
                        dy2 = 0.73f,
                    )
                    // s -0.06 0.04 -0.14 0.04
                    reflectiveCurveToRelative(
                        dx1 = -0.06f,
                        dy1 = 0.04f,
                        dx2 = -0.14f,
                        dy2 = 0.04f,
                    )
                    // c -0.12 0 -0.13 0.01 -0.04 0.14
                    curveToRelative(
                        dx1 = -0.12f,
                        dy1 = 0.0f,
                        dx2 = -0.13f,
                        dy2 = 0.01f,
                        dx3 = -0.04f,
                        dy3 = 0.14f,
                    )
                    // q 0.1 0.14 0.07 0.37
                    quadToRelative(
                        dx1 = 0.1f,
                        dy1 = 0.14f,
                        dx2 = 0.07f,
                        dy2 = 0.37f,
                    )
                    // c -0.03 0.18 -0.01 0.24 0.12 0.33
                    curveToRelative(
                        dx1 = -0.03f,
                        dy1 = 0.18f,
                        dx2 = -0.01f,
                        dy2 = 0.24f,
                        dx3 = 0.12f,
                        dy3 = 0.33f,
                    )
                    // a 1 1 0 0 1 0.32 0.37
                    arcToRelative(
                        a = 1.0f,
                        b = 1.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.32f,
                        dy1 = 0.37f,
                    )
                    // a 2 2 0 0 0 0.3 0.4
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.3f,
                        dy1 = 0.4f,
                    )
                    // a 2 2 0 0 1 0.3 0.35
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.3f,
                        dy1 = 0.35f,
                    )
                    // c 0.06 0.13 0.3 0.36 0.5 0.52
                    curveToRelative(
                        dx1 = 0.06f,
                        dy1 = 0.13f,
                        dx2 = 0.3f,
                        dy2 = 0.36f,
                        dx3 = 0.5f,
                        dy3 = 0.52f,
                    )
                    // s 0.43 0.37 0.47 0.46
                    reflectiveCurveToRelative(
                        dx1 = 0.43f,
                        dy1 = 0.37f,
                        dx2 = 0.47f,
                        dy2 = 0.46f,
                    )
                    // s 0.24 0.25 0.47 0.36
                    reflectiveCurveToRelative(
                        dx1 = 0.24f,
                        dy1 = 0.25f,
                        dx2 = 0.47f,
                        dy2 = 0.36f,
                    )
                    // a 4 4 0 0 1 1.43 1.26
                    arcToRelative(
                        a = 4.0f,
                        b = 4.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.43f,
                        dy1 = 1.26f,
                    )
                    // c 0.08 0.19 0.23 0.34 0.66 0.62
                    curveToRelative(
                        dx1 = 0.08f,
                        dy1 = 0.19f,
                        dx2 = 0.23f,
                        dy2 = 0.34f,
                        dx3 = 0.66f,
                        dy3 = 0.62f,
                    )
                    // a 13 13 0 0 1 1.62 1.42
                    arcToRelative(
                        a = 13.0f,
                        b = 13.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.62f,
                        dy1 = 1.42f,
                    )
                    // c 1.22 1.2 1.29 1.25 2.28 1.19
                    curveToRelative(
                        dx1 = 1.22f,
                        dy1 = 1.2f,
                        dx2 = 1.29f,
                        dy2 = 1.25f,
                        dx3 = 2.28f,
                        dy3 = 1.19f,
                    )
                    // c 0.65 -0.05 0.97 -0.17 1.36 -0.52
                    curveToRelative(
                        dx1 = 0.65f,
                        dy1 = -0.05f,
                        dx2 = 0.97f,
                        dy2 = -0.17f,
                        dx3 = 1.36f,
                        dy3 = -0.52f,
                    )
                    // q 0.19 -0.18 0.4 -0.33
                    quadToRelative(
                        dx1 = 0.19f,
                        dy1 = -0.18f,
                        dx2 = 0.4f,
                        dy2 = -0.33f,
                    )
                    // a 2 2 0 0 0 0.37 -0.44
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.37f,
                        dy1 = -0.44f,
                    )
                    // a 2 2 0 0 1 0.42 -0.48
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.42f,
                        dy1 = -0.48f,
                    )
                    // c 0.28 -0.17 0.79 -0.88 0.79 -1.11
                    curveToRelative(
                        dx1 = 0.28f,
                        dy1 = -0.17f,
                        dx2 = 0.79f,
                        dy2 = -0.88f,
                        dx3 = 0.79f,
                        dy3 = -1.11f,
                    )
                    // c 0 -0.34 0.13 -0.55 0.57 -0.97
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = -0.34f,
                        dx2 = 0.13f,
                        dy2 = -0.55f,
                        dx3 = 0.57f,
                        dy3 = -0.97f,
                    )
                    // a 2.3 2.3 0 0 0 0.71 -1.08
                    arcToRelative(
                        a = 2.3f,
                        b = 2.3f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.71f,
                        dy1 = -1.08f,
                    )
                    // a 8 8 0 0 0 0.63 -2.68
                    arcToRelative(
                        a = 8.0f,
                        b = 8.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.63f,
                        dy1 = -2.68f,
                    )
                    // a 19 19 0 0 0 -0.03 -4.3
                    arcToRelative(
                        a = 19.0f,
                        b = 19.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.03f,
                        dy1 = -4.3f,
                    )
                    // a 103 103 0 0 0 -1.64 -7.77
                    arcToRelative(
                        a = 103.0f,
                        b = 103.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -1.64f,
                        dy1 = -7.77f,
                    )
                    // a 12 12 0 0 1 -0.51 -2.22
                    arcToRelative(
                        a = 12.0f,
                        b = 12.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.51f,
                        dy1 = -2.22f,
                    )
                    // c 0 -0.07 0.14 -0.08 0.7 -0.04
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = -0.07f,
                        dx2 = 0.14f,
                        dy2 = -0.08f,
                        dx3 = 0.7f,
                        dy3 = -0.04f,
                    )
                    // a 6 6 0 0 0 1.47 -0.07
                    arcToRelative(
                        a = 6.0f,
                        b = 6.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 1.47f,
                        dy1 = -0.07f,
                    )
                    // a 5 5 0 0 1 1.65 -0.04
                    arcToRelative(
                        a = 5.0f,
                        b = 5.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.65f,
                        dy1 = -0.04f,
                    )
                    // a 6 6 0 0 1 0.87 0.1
                    arcToRelative(
                        a = 6.0f,
                        b = 6.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.87f,
                        dy1 = 0.1f,
                    )
                    // a 84 84 0 0 1 1.27 4.1
                    arcToRelative(
                        a = 84.0f,
                        b = 84.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.27f,
                        dy1 = 4.1f,
                    )
                    // c 0.36 1.41 0.37 1.51 0.44 5.05
                    curveToRelative(
                        dx1 = 0.36f,
                        dy1 = 1.41f,
                        dx2 = 0.37f,
                        dy2 = 1.51f,
                        dx3 = 0.44f,
                        dy3 = 5.05f,
                    )
                    // l 0.08 4.88
                    lineToRelative(dx = 0.08f, dy = 4.88f)
                    // a 7 7 0 0 0 0.2 2.17
                    arcToRelative(
                        a = 7.0f,
                        b = 7.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.2f,
                        dy1 = 2.17f,
                    )
                    // a 6 6 0 0 0 0.93 2
                    arcToRelative(
                        a = 6.0f,
                        b = 6.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.93f,
                        dy1 = 2.0f,
                    )
                    // q 0.35 0.52 0.66 1.07
                    quadToRelative(
                        dx1 = 0.35f,
                        dy1 = 0.52f,
                        dx2 = 0.66f,
                        dy2 = 1.07f,
                    )
                    // a 5 5 0 0 0 1.34 1.57
                    arcToRelative(
                        a = 5.0f,
                        b = 5.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 1.34f,
                        dy1 = 1.57f,
                    )
                    // a 11 11 0 0 0 1.9 1.24
                    arcToRelative(
                        a = 11.0f,
                        b = 11.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 1.9f,
                        dy1 = 1.24f,
                    )
                    // a 3.4 3.4 0 0 1 1.39 1.22
                    arcToRelative(
                        a = 3.4f,
                        b = 3.4f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.39f,
                        dy1 = 1.22f,
                    )
                    // c 0.17 0.25 0.42 0.58 0.55 0.74
                    curveToRelative(
                        dx1 = 0.17f,
                        dy1 = 0.25f,
                        dx2 = 0.42f,
                        dy2 = 0.58f,
                        dx3 = 0.55f,
                        dy3 = 0.74f,
                    )
                    // s 0.3 0.43 0.38 0.58
                    reflectiveCurveToRelative(
                        dx1 = 0.3f,
                        dy1 = 0.43f,
                        dx2 = 0.38f,
                        dy2 = 0.58f,
                    )
                    // c 0.13 0.26 0.17 0.3 0.9 0.5
                    curveToRelative(
                        dx1 = 0.13f,
                        dy1 = 0.26f,
                        dx2 = 0.17f,
                        dy2 = 0.3f,
                        dx3 = 0.9f,
                        dy3 = 0.5f,
                    )
                    // a 6 6 0 0 0 1.78 0.04
                    arcToRelative(
                        a = 6.0f,
                        b = 6.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 1.78f,
                        dy1 = 0.04f,
                    )
                    // c 0.29 -0.05 0.4 -0.11 0.86 -0.57
                    curveToRelative(
                        dx1 = 0.29f,
                        dy1 = -0.05f,
                        dx2 = 0.4f,
                        dy2 = -0.11f,
                        dx3 = 0.86f,
                        dy3 = -0.57f,
                    )
                    // q 0.93 -0.88 0.92 -1.33
                    quadToRelative(
                        dx1 = 0.93f,
                        dy1 = -0.88f,
                        dx2 = 0.92f,
                        dy2 = -1.33f,
                    )
                    // c 0 -0.47 0.07 -0.6 0.34 -0.72
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = -0.47f,
                        dx2 = 0.07f,
                        dy2 = -0.6f,
                        dx3 = 0.34f,
                        dy3 = -0.72f,
                    )
                    // q 0.27 -0.1 0.26 -0.22
                    quadToRelative(
                        dx1 = 0.27f,
                        dy1 = -0.1f,
                        dx2 = 0.26f,
                        dy2 = -0.22f,
                    )
                    // c 0 -0.43 -0.44 -2.14 -0.67 -2.58
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = -0.43f,
                        dx2 = -0.44f,
                        dy2 = -2.14f,
                        dx3 = -0.67f,
                        dy3 = -2.58f,
                    )
                    // c -0.15 -0.3 -2.08 -2.9 -3.7 -5
                    curveToRelative(
                        dx1 = -0.15f,
                        dy1 = -0.3f,
                        dx2 = -2.08f,
                        dy2 = -2.9f,
                        dx3 = -3.7f,
                        dy3 = -5.0f,
                    )
                    // c -1.72 -2.24 -2.32 -3.2 -2.68 -4.25
                    curveToRelative(
                        dx1 = -1.72f,
                        dy1 = -2.24f,
                        dx2 = -2.32f,
                        dy2 = -3.2f,
                        dx3 = -2.68f,
                        dy3 = -4.25f,
                    )
                    // a 64 64 0 0 1 -0.81 -4.7
                    arcToRelative(
                        a = 64.0f,
                        b = 64.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.81f,
                        dy1 = -4.7f,
                    )
                    // a 18 18 0 0 0 -1.17 -4.22
                    arcToRelative(
                        a = 18.0f,
                        b = 18.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -1.17f,
                        dy1 = -4.22f,
                    )
                    // a 4 4 0 0 1 -0.3 -0.93
                    arcToRelative(
                        a = 4.0f,
                        b = 4.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.3f,
                        dy1 = -0.93f,
                    )
                    // c 0.15 0 3.62 0.97 3.82 1.07
                    curveToRelative(
                        dx1 = 0.15f,
                        dy1 = 0.0f,
                        dx2 = 3.62f,
                        dy2 = 0.97f,
                        dx3 = 3.82f,
                        dy3 = 1.07f,
                    )
                    // l 1.16 0.63
                    lineToRelative(dx = 1.16f, dy = 0.63f)
                    // c 0.5 0.3 0.99 0.52 1.08 0.52
                    curveToRelative(
                        dx1 = 0.5f,
                        dy1 = 0.3f,
                        dx2 = 0.99f,
                        dy2 = 0.52f,
                        dx3 = 1.08f,
                        dy3 = 0.52f,
                    )
                    // s 0.52 0.24 1.02 0.57
                    reflectiveCurveToRelative(
                        dx1 = 0.52f,
                        dy1 = 0.24f,
                        dx2 = 1.02f,
                        dy2 = 0.57f,
                    )
                    // l 0.85 0.58
                    lineToRelative(dx = 0.85f, dy = 0.58f)
                    // l 0.45 -0.04
                    lineToRelative(dx = 0.45f, dy = -0.04f)
                    // c 0.64 -0.06 0.76 -0.09 1.03 -0.27
                    curveToRelative(
                        dx1 = 0.64f,
                        dy1 = -0.06f,
                        dx2 = 0.76f,
                        dy2 = -0.09f,
                        dx3 = 1.03f,
                        dy3 = -0.27f,
                    )
                    // c 0.38 -0.27 0.6 -0.3 0.91 -0.17
                    curveToRelative(
                        dx1 = 0.38f,
                        dy1 = -0.27f,
                        dx2 = 0.6f,
                        dy2 = -0.3f,
                        dx3 = 0.91f,
                        dy3 = -0.17f,
                    )
                    // c 0.59 0.28 0.99 0.33 0.63 0.08
                    curveToRelative(
                        dx1 = 0.59f,
                        dy1 = 0.28f,
                        dx2 = 0.99f,
                        dy2 = 0.33f,
                        dx3 = 0.63f,
                        dy3 = 0.08f,
                    )
                    // c -0.53 -0.37 -0.58 -0.55 -0.27 -1.02
                    curveToRelative(
                        dx1 = -0.53f,
                        dy1 = -0.37f,
                        dx2 = -0.58f,
                        dy2 = -0.55f,
                        dx3 = -0.27f,
                        dy3 = -1.02f,
                    )
                    // c 0.37 -0.56 0.4 -0.6 0.79 -0.8
                    curveToRelative(
                        dx1 = 0.37f,
                        dy1 = -0.56f,
                        dx2 = 0.4f,
                        dy2 = -0.6f,
                        dx3 = 0.79f,
                        dy3 = -0.8f,
                    )
                    // c 0.61 -0.3 0.65 -0.38 0.85 -1.48
                    curveToRelative(
                        dx1 = 0.61f,
                        dy1 = -0.3f,
                        dx2 = 0.65f,
                        dy2 = -0.38f,
                        dx3 = 0.85f,
                        dy3 = -1.48f,
                    )
                    // a 8 8 0 0 1 0.28 -1.17
                    arcToRelative(
                        a = 8.0f,
                        b = 8.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.28f,
                        dy1 = -1.17f,
                    )
                    // c 0.15 -0.33 0.12 -0.53 -0.14 -0.78
                    curveToRelative(
                        dx1 = 0.15f,
                        dy1 = -0.33f,
                        dx2 = 0.12f,
                        dy2 = -0.53f,
                        dx3 = -0.14f,
                        dy3 = -0.78f,
                    )
                    // a 5 5 0 0 1 -0.5 -0.64
                    arcToRelative(
                        a = 5.0f,
                        b = 5.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.5f,
                        dy1 = -0.64f,
                    )
                    // c -0.22 -0.32 -0.28 -0.48 -0.28 -0.71
                    curveToRelative(
                        dx1 = -0.22f,
                        dy1 = -0.32f,
                        dx2 = -0.28f,
                        dy2 = -0.48f,
                        dx3 = -0.28f,
                        dy3 = -0.71f,
                    )
                    // c 0 -0.27 -0.02 -0.32 -0.22 -0.44
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = -0.27f,
                        dx2 = -0.02f,
                        dy2 = -0.32f,
                        dx3 = -0.22f,
                        dy3 = -0.44f,
                    )
                    // a 3 3 0 0 0 -0.47 -0.24
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.47f,
                        dy1 = -0.24f,
                    )
                    // c -0.3 -0.1 -0.74 -0.57 -0.68 -0.7
                    curveToRelative(
                        dx1 = -0.3f,
                        dy1 = -0.1f,
                        dx2 = -0.74f,
                        dy2 = -0.57f,
                        dx3 = -0.68f,
                        dy3 = -0.7f,
                    )
                    // l 0.07 -0.19
                    lineToRelative(dx = 0.07f, dy = -0.19f)
                    // c 0.06 -0.14 -0.49 -0.43 -1.03 -0.56
                    curveToRelative(
                        dx1 = 0.06f,
                        dy1 = -0.14f,
                        dx2 = -0.49f,
                        dy2 = -0.43f,
                        dx3 = -1.03f,
                        dy3 = -0.56f,
                    )
                    // c -0.33 -0.08 -0.44 -0.1 -0.47 -0.14
                    curveToRelative(
                        dx1 = -0.33f,
                        dy1 = -0.08f,
                        dx2 = -0.44f,
                        dy2 = -0.1f,
                        dx3 = -0.47f,
                        dy3 = -0.14f,
                    )
                    // l -2.7 -0.3
                    lineToRelative(dx = -2.7f, dy = -0.3f)
                    // q -0.05 0.02 -0.24 0.02
                    quadToRelative(
                        dx1 = -0.05f,
                        dy1 = 0.02f,
                        dx2 = -0.24f,
                        dy2 = 0.02f,
                    )
                    // a 13 13 0 0 1 -1.06 -0.1
                    arcToRelative(
                        a = 13.0f,
                        b = 13.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -1.06f,
                        dy1 = -0.1f,
                    )
                    // q -0.58 -0.08 -1.16 -0.11
                    quadToRelative(
                        dx1 = -0.58f,
                        dy1 = -0.08f,
                        dx2 = -1.16f,
                        dy2 = -0.11f,
                    )
                    // a 254 254 0 0 0 -5.76 0.1
                    arcToRelative(
                        a = 254.0f,
                        b = 254.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -5.76f,
                        dy1 = 0.1f,
                    )
                    // l -0.97 0.04
                    lineToRelative(dx = -0.97f, dy = 0.04f)
                    // l -0.16 -0.33
                    lineToRelative(dx = -0.16f, dy = -0.33f)
                    // a 3 3 0 0 0 -0.56 -0.72
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.56f,
                        dy1 = -0.72f,
                    )
                    // c -0.28 -0.29 -0.37 -0.36 -0.43 -0.33
                    curveToRelative(
                        dx1 = -0.28f,
                        dy1 = -0.29f,
                        dx2 = -0.37f,
                        dy2 = -0.36f,
                        dx3 = -0.43f,
                        dy3 = -0.33f,
                    )
                }
                // M48.1 76.4 q-.02 -.25 -.23 -.27 c-.16 0 -.24 .05 -.37 .22 -.08 .1 -.07 .12 .25 .12 .2 0 .35 -.03 .35 -.06 m6.15 -3.74 a1.6 1.6 0 0 0 -.83 -.33 q-.38 -.06 -.07 .2 a1 1 0 0 0 .78 .3 l.32 -.01z m-18.5 -1.22 a2 2 0 0 0 0 -.49 1.3 1.3 0 0 1 .41 -1.17 10 10 0 0 1 2.21 -1.12 c1.8 -.63 6.56 -1.12 13.1 -1.35 4.05 -.14 5.86 -.24 6.53 -.37 a3 3 0 0 1 .69 -.05 q.2 .03 .26 -.04 t.58 -.03 c.38 .03 .53 .02 .53 -.04 q.02 -.06 .27 -.07 c.3 0 .42 -.11 .2 -.2 -.1 -.02 .15 -.1 .83 -.2 1.13 -.17 1.06 -.17 1.06 -.04 q0 .09 .07 .1 .05 0 .06 -.07 0 -.09 .1 0 c.12 .1 .3 .04 .3 -.1 0 -.13 .17 -.2 .78 -.3 l.62 -.1 q.13 -.02 .15 .03 -.01 .06 -.19 .07 c-.29 0 -.48 .07 -.48 .18 0 .08 .07 .1 .35 .05 q.95 -.13 .98 -.31 .02 -.12 -.1 -.12 -.14 0 -.02 -.07 a1 1 0 0 1 .23 -.07 .4 .4 0 0 0 .23 -.08 12 12 0 0 1 1.46 -.51 c3.49 -1.1 4.79 -1.77 6.07 -3.14 .41 -.44 .95 -1.16 .95 -1.27 s-.2 -.02 -.27 .12 a3 3 0 0 1 -.87 .82 2 2 0 0 0 -.45 .41 q-.16 .2 -.24 .22 a2 2 0 0 0 -.46 .31 6 6 0 0 1 -1.49 .82 8.75 8.75 0 0 0 1.69 -1.96 l.48 -.66 v.36 c0 .32 .01 .35 .1 .24 a5 5 0 0 0 .3 -.76 3 3 0 0 1 .52 -.97 q.48 -.59 .4 -.66 c-.07 -.06 -.52 .4 -.65 .66 q-.13 .28 -.32 .5 L72.5 60.3 v-.22 c0 -.29 -.08 -.27 -.8 .08 a6.4 6.4 0 0 1 -2.56 .83 9 9 0 0 0 -1.41 .22 23 23 0 0 1 -3.23 .56 8 8 0 0 1 -2 .03 7 7 0 0 0 -1.03 .02 20 20 0 0 1 -2.41 .1 85 85 0 0 1 3.97 -.3 q.24 .01 .13 -.1 c-.06 -.06 -.37 -.08 -1.57 -.05 q-1.5 .02 -1.74 -.04 a25 25 0 0 0 -3.08 -.28 6 6 0 0 1 -1.36 -.2 20 20 0 0 0 -2.37 -.48 c-.77 -.12 -.97 -.18 -1.24 -.39 a1 1 0 0 0 -.64 -.21 5 5 0 0 1 -.64 -.07 19 19 0 0 0 -2.36 .06 26 26 0 0 1 -2.52 .08 28 28 0 0 0 -4.29 .2 7 7 0 0 0 -1.73 .42 l-1.14 .42 a6 6 0 0 0 -2.54 1.67 c-.69 .73 -.82 1.02 -1.4 2.9 a7 7 0 0 0 -.5 2.49 c-.08 1.46 .03 1.81 .75 2.45 .19 .16 .33 .34 .33 .41 0 .37 .43 .75 .62 .56
                path(
                    fill = SolidColor(Color(0xFFFFFFFF)),
                ) {
                    // M 48.1 76.4
                    moveTo(x = 48.1f, y = 76.4f)
                    // q -0.02 -0.25 -0.23 -0.27
                    quadToRelative(
                        dx1 = -0.02f,
                        dy1 = -0.25f,
                        dx2 = -0.23f,
                        dy2 = -0.27f,
                    )
                    // c -0.16 0 -0.24 0.05 -0.37 0.22
                    curveToRelative(
                        dx1 = -0.16f,
                        dy1 = 0.0f,
                        dx2 = -0.24f,
                        dy2 = 0.05f,
                        dx3 = -0.37f,
                        dy3 = 0.22f,
                    )
                    // c -0.08 0.1 -0.07 0.12 0.25 0.12
                    curveToRelative(
                        dx1 = -0.08f,
                        dy1 = 0.1f,
                        dx2 = -0.07f,
                        dy2 = 0.12f,
                        dx3 = 0.25f,
                        dy3 = 0.12f,
                    )
                    // c 0.2 0 0.35 -0.03 0.35 -0.06
                    curveToRelative(
                        dx1 = 0.2f,
                        dy1 = 0.0f,
                        dx2 = 0.35f,
                        dy2 = -0.03f,
                        dx3 = 0.35f,
                        dy3 = -0.06f,
                    )
                    // m 6.15 -3.74
                    moveToRelative(dx = 6.15f, dy = -3.74f)
                    // a 1.6 1.6 0 0 0 -0.83 -0.33
                    arcToRelative(
                        a = 1.6f,
                        b = 1.6f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.83f,
                        dy1 = -0.33f,
                    )
                    // q -0.38 -0.06 -0.07 0.2
                    quadToRelative(
                        dx1 = -0.38f,
                        dy1 = -0.06f,
                        dx2 = -0.07f,
                        dy2 = 0.2f,
                    )
                    // a 1 1 0 0 0 0.78 0.3
                    arcToRelative(
                        a = 1.0f,
                        b = 1.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.78f,
                        dy1 = 0.3f,
                    )
                    // l 0.32 -0.01z
                    lineToRelative(dx = 0.32f, dy = -0.01f)
                    close()
                    // m -18.5 -1.22
                    moveToRelative(dx = -18.5f, dy = -1.22f)
                    // a 2 2 0 0 0 0 -0.49
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.0f,
                        dy1 = -0.49f,
                    )
                    // a 1.3 1.3 0 0 1 0.41 -1.17
                    arcToRelative(
                        a = 1.3f,
                        b = 1.3f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.41f,
                        dy1 = -1.17f,
                    )
                    // a 10 10 0 0 1 2.21 -1.12
                    arcToRelative(
                        a = 10.0f,
                        b = 10.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 2.21f,
                        dy1 = -1.12f,
                    )
                    // c 1.8 -0.63 6.56 -1.12 13.1 -1.35
                    curveToRelative(
                        dx1 = 1.8f,
                        dy1 = -0.63f,
                        dx2 = 6.56f,
                        dy2 = -1.12f,
                        dx3 = 13.1f,
                        dy3 = -1.35f,
                    )
                    // c 4.05 -0.14 5.86 -0.24 6.53 -0.37
                    curveToRelative(
                        dx1 = 4.05f,
                        dy1 = -0.14f,
                        dx2 = 5.86f,
                        dy2 = -0.24f,
                        dx3 = 6.53f,
                        dy3 = -0.37f,
                    )
                    // a 3 3 0 0 1 0.69 -0.05
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.69f,
                        dy1 = -0.05f,
                    )
                    // q 0.2 0.03 0.26 -0.04
                    quadToRelative(
                        dx1 = 0.2f,
                        dy1 = 0.03f,
                        dx2 = 0.26f,
                        dy2 = -0.04f,
                    )
                    // t 0.58 -0.03
                    reflectiveQuadToRelative(
                        dx1 = 0.58f,
                        dy1 = -0.03f,
                    )
                    // c 0.38 0.03 0.53 0.02 0.53 -0.04
                    curveToRelative(
                        dx1 = 0.38f,
                        dy1 = 0.03f,
                        dx2 = 0.53f,
                        dy2 = 0.02f,
                        dx3 = 0.53f,
                        dy3 = -0.04f,
                    )
                    // q 0.02 -0.06 0.27 -0.07
                    quadToRelative(
                        dx1 = 0.02f,
                        dy1 = -0.06f,
                        dx2 = 0.27f,
                        dy2 = -0.07f,
                    )
                    // c 0.3 0 0.42 -0.11 0.2 -0.2
                    curveToRelative(
                        dx1 = 0.3f,
                        dy1 = 0.0f,
                        dx2 = 0.42f,
                        dy2 = -0.11f,
                        dx3 = 0.2f,
                        dy3 = -0.2f,
                    )
                    // c -0.1 -0.02 0.15 -0.1 0.83 -0.2
                    curveToRelative(
                        dx1 = -0.1f,
                        dy1 = -0.02f,
                        dx2 = 0.15f,
                        dy2 = -0.1f,
                        dx3 = 0.83f,
                        dy3 = -0.2f,
                    )
                    // c 1.13 -0.17 1.06 -0.17 1.06 -0.04
                    curveToRelative(
                        dx1 = 1.13f,
                        dy1 = -0.17f,
                        dx2 = 1.06f,
                        dy2 = -0.17f,
                        dx3 = 1.06f,
                        dy3 = -0.04f,
                    )
                    // q 0 0.09 0.07 0.1
                    quadToRelative(
                        dx1 = 0.0f,
                        dy1 = 0.09f,
                        dx2 = 0.07f,
                        dy2 = 0.1f,
                    )
                    // q 0.05 0 0.06 -0.07
                    quadToRelative(
                        dx1 = 0.05f,
                        dy1 = 0.0f,
                        dx2 = 0.06f,
                        dy2 = -0.07f,
                    )
                    // q 0 -0.09 0.1 0
                    quadToRelative(
                        dx1 = 0.0f,
                        dy1 = -0.09f,
                        dx2 = 0.1f,
                        dy2 = 0.0f,
                    )
                    // c 0.12 0.1 0.3 0.04 0.3 -0.1
                    curveToRelative(
                        dx1 = 0.12f,
                        dy1 = 0.1f,
                        dx2 = 0.3f,
                        dy2 = 0.04f,
                        dx3 = 0.3f,
                        dy3 = -0.1f,
                    )
                    // c 0 -0.13 0.17 -0.2 0.78 -0.3
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = -0.13f,
                        dx2 = 0.17f,
                        dy2 = -0.2f,
                        dx3 = 0.78f,
                        dy3 = -0.3f,
                    )
                    // l 0.62 -0.1
                    lineToRelative(dx = 0.62f, dy = -0.1f)
                    // q 0.13 -0.02 0.15 0.03
                    quadToRelative(
                        dx1 = 0.13f,
                        dy1 = -0.02f,
                        dx2 = 0.15f,
                        dy2 = 0.03f,
                    )
                    // q -0.01 0.06 -0.19 0.07
                    quadToRelative(
                        dx1 = -0.01f,
                        dy1 = 0.06f,
                        dx2 = -0.19f,
                        dy2 = 0.07f,
                    )
                    // c -0.29 0 -0.48 0.07 -0.48 0.18
                    curveToRelative(
                        dx1 = -0.29f,
                        dy1 = 0.0f,
                        dx2 = -0.48f,
                        dy2 = 0.07f,
                        dx3 = -0.48f,
                        dy3 = 0.18f,
                    )
                    // c 0 0.08 0.07 0.1 0.35 0.05
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = 0.08f,
                        dx2 = 0.07f,
                        dy2 = 0.1f,
                        dx3 = 0.35f,
                        dy3 = 0.05f,
                    )
                    // q 0.95 -0.13 0.98 -0.31
                    quadToRelative(
                        dx1 = 0.95f,
                        dy1 = -0.13f,
                        dx2 = 0.98f,
                        dy2 = -0.31f,
                    )
                    // q 0.02 -0.12 -0.1 -0.12
                    quadToRelative(
                        dx1 = 0.02f,
                        dy1 = -0.12f,
                        dx2 = -0.1f,
                        dy2 = -0.12f,
                    )
                    // q -0.14 0 -0.02 -0.07
                    quadToRelative(
                        dx1 = -0.14f,
                        dy1 = 0.0f,
                        dx2 = -0.02f,
                        dy2 = -0.07f,
                    )
                    // a 1 1 0 0 1 0.23 -0.07
                    arcToRelative(
                        a = 1.0f,
                        b = 1.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.23f,
                        dy1 = -0.07f,
                    )
                    // a 0.4 0.4 0 0 0 0.23 -0.08
                    arcToRelative(
                        a = 0.4f,
                        b = 0.4f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.23f,
                        dy1 = -0.08f,
                    )
                    // a 12 12 0 0 1 1.46 -0.51
                    arcToRelative(
                        a = 12.0f,
                        b = 12.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.46f,
                        dy1 = -0.51f,
                    )
                    // c 3.49 -1.1 4.79 -1.77 6.07 -3.14
                    curveToRelative(
                        dx1 = 3.49f,
                        dy1 = -1.1f,
                        dx2 = 4.79f,
                        dy2 = -1.77f,
                        dx3 = 6.07f,
                        dy3 = -3.14f,
                    )
                    // c 0.41 -0.44 0.95 -1.16 0.95 -1.27
                    curveToRelative(
                        dx1 = 0.41f,
                        dy1 = -0.44f,
                        dx2 = 0.95f,
                        dy2 = -1.16f,
                        dx3 = 0.95f,
                        dy3 = -1.27f,
                    )
                    // s -0.2 -0.02 -0.27 0.12
                    reflectiveCurveToRelative(
                        dx1 = -0.2f,
                        dy1 = -0.02f,
                        dx2 = -0.27f,
                        dy2 = 0.12f,
                    )
                    // a 3 3 0 0 1 -0.87 0.82
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.87f,
                        dy1 = 0.82f,
                    )
                    // a 2 2 0 0 0 -0.45 0.41
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.45f,
                        dy1 = 0.41f,
                    )
                    // q -0.16 0.2 -0.24 0.22
                    quadToRelative(
                        dx1 = -0.16f,
                        dy1 = 0.2f,
                        dx2 = -0.24f,
                        dy2 = 0.22f,
                    )
                    // a 2 2 0 0 0 -0.46 0.31
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.46f,
                        dy1 = 0.31f,
                    )
                    // a 6 6 0 0 1 -1.49 0.82
                    arcToRelative(
                        a = 6.0f,
                        b = 6.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -1.49f,
                        dy1 = 0.82f,
                    )
                    // a 8.75 8.75 0 0 0 1.69 -1.96
                    arcToRelative(
                        a = 8.75f,
                        b = 8.75f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 1.69f,
                        dy1 = -1.96f,
                    )
                    // l 0.48 -0.66
                    lineToRelative(dx = 0.48f, dy = -0.66f)
                    // v 0.36
                    verticalLineToRelative(dy = 0.36f)
                    // c 0 0.32 0.01 0.35 0.1 0.24
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = 0.32f,
                        dx2 = 0.01f,
                        dy2 = 0.35f,
                        dx3 = 0.1f,
                        dy3 = 0.24f,
                    )
                    // a 5 5 0 0 0 0.3 -0.76
                    arcToRelative(
                        a = 5.0f,
                        b = 5.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.3f,
                        dy1 = -0.76f,
                    )
                    // a 3 3 0 0 1 0.52 -0.97
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.52f,
                        dy1 = -0.97f,
                    )
                    // q 0.48 -0.59 0.4 -0.66
                    quadToRelative(
                        dx1 = 0.48f,
                        dy1 = -0.59f,
                        dx2 = 0.4f,
                        dy2 = -0.66f,
                    )
                    // c -0.07 -0.06 -0.52 0.4 -0.65 0.66
                    curveToRelative(
                        dx1 = -0.07f,
                        dy1 = -0.06f,
                        dx2 = -0.52f,
                        dy2 = 0.4f,
                        dx3 = -0.65f,
                        dy3 = 0.66f,
                    )
                    // q -0.13 0.28 -0.32 0.5
                    quadToRelative(
                        dx1 = -0.13f,
                        dy1 = 0.28f,
                        dx2 = -0.32f,
                        dy2 = 0.5f,
                    )
                    // L 72.5 60.3
                    lineTo(x = 72.5f, y = 60.3f)
                    // v -0.22
                    verticalLineToRelative(dy = -0.22f)
                    // c 0 -0.29 -0.08 -0.27 -0.8 0.08
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = -0.29f,
                        dx2 = -0.08f,
                        dy2 = -0.27f,
                        dx3 = -0.8f,
                        dy3 = 0.08f,
                    )
                    // a 6.4 6.4 0 0 1 -2.56 0.83
                    arcToRelative(
                        a = 6.4f,
                        b = 6.4f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -2.56f,
                        dy1 = 0.83f,
                    )
                    // a 9 9 0 0 0 -1.41 0.22
                    arcToRelative(
                        a = 9.0f,
                        b = 9.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -1.41f,
                        dy1 = 0.22f,
                    )
                    // a 23 23 0 0 1 -3.23 0.56
                    arcToRelative(
                        a = 23.0f,
                        b = 23.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -3.23f,
                        dy1 = 0.56f,
                    )
                    // a 8 8 0 0 1 -2 0.03
                    arcToRelative(
                        a = 8.0f,
                        b = 8.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -2.0f,
                        dy1 = 0.03f,
                    )
                    // a 7 7 0 0 0 -1.03 0.02
                    arcToRelative(
                        a = 7.0f,
                        b = 7.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -1.03f,
                        dy1 = 0.02f,
                    )
                    // a 20 20 0 0 1 -2.41 0.1
                    arcToRelative(
                        a = 20.0f,
                        b = 20.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -2.41f,
                        dy1 = 0.1f,
                    )
                    // a 85 85 0 0 1 3.97 -0.3
                    arcToRelative(
                        a = 85.0f,
                        b = 85.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 3.97f,
                        dy1 = -0.3f,
                    )
                    // q 0.24 0.01 0.13 -0.1
                    quadToRelative(
                        dx1 = 0.24f,
                        dy1 = 0.01f,
                        dx2 = 0.13f,
                        dy2 = -0.1f,
                    )
                    // c -0.06 -0.06 -0.37 -0.08 -1.57 -0.05
                    curveToRelative(
                        dx1 = -0.06f,
                        dy1 = -0.06f,
                        dx2 = -0.37f,
                        dy2 = -0.08f,
                        dx3 = -1.57f,
                        dy3 = -0.05f,
                    )
                    // q -1.5 0.02 -1.74 -0.04
                    quadToRelative(
                        dx1 = -1.5f,
                        dy1 = 0.02f,
                        dx2 = -1.74f,
                        dy2 = -0.04f,
                    )
                    // a 25 25 0 0 0 -3.08 -0.28
                    arcToRelative(
                        a = 25.0f,
                        b = 25.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -3.08f,
                        dy1 = -0.28f,
                    )
                    // a 6 6 0 0 1 -1.36 -0.2
                    arcToRelative(
                        a = 6.0f,
                        b = 6.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -1.36f,
                        dy1 = -0.2f,
                    )
                    // a 20 20 0 0 0 -2.37 -0.48
                    arcToRelative(
                        a = 20.0f,
                        b = 20.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -2.37f,
                        dy1 = -0.48f,
                    )
                    // c -0.77 -0.12 -0.97 -0.18 -1.24 -0.39
                    curveToRelative(
                        dx1 = -0.77f,
                        dy1 = -0.12f,
                        dx2 = -0.97f,
                        dy2 = -0.18f,
                        dx3 = -1.24f,
                        dy3 = -0.39f,
                    )
                    // a 1 1 0 0 0 -0.64 -0.21
                    arcToRelative(
                        a = 1.0f,
                        b = 1.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.64f,
                        dy1 = -0.21f,
                    )
                    // a 5 5 0 0 1 -0.64 -0.07
                    arcToRelative(
                        a = 5.0f,
                        b = 5.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.64f,
                        dy1 = -0.07f,
                    )
                    // a 19 19 0 0 0 -2.36 0.06
                    arcToRelative(
                        a = 19.0f,
                        b = 19.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -2.36f,
                        dy1 = 0.06f,
                    )
                    // a 26 26 0 0 1 -2.52 0.08
                    arcToRelative(
                        a = 26.0f,
                        b = 26.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -2.52f,
                        dy1 = 0.08f,
                    )
                    // a 28 28 0 0 0 -4.29 0.2
                    arcToRelative(
                        a = 28.0f,
                        b = 28.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -4.29f,
                        dy1 = 0.2f,
                    )
                    // a 7 7 0 0 0 -1.73 0.42
                    arcToRelative(
                        a = 7.0f,
                        b = 7.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -1.73f,
                        dy1 = 0.42f,
                    )
                    // l -1.14 0.42
                    lineToRelative(dx = -1.14f, dy = 0.42f)
                    // a 6 6 0 0 0 -2.54 1.67
                    arcToRelative(
                        a = 6.0f,
                        b = 6.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -2.54f,
                        dy1 = 1.67f,
                    )
                    // c -0.69 0.73 -0.82 1.02 -1.4 2.9
                    curveToRelative(
                        dx1 = -0.69f,
                        dy1 = 0.73f,
                        dx2 = -0.82f,
                        dy2 = 1.02f,
                        dx3 = -1.4f,
                        dy3 = 2.9f,
                    )
                    // a 7 7 0 0 0 -0.5 2.49
                    arcToRelative(
                        a = 7.0f,
                        b = 7.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.5f,
                        dy1 = 2.49f,
                    )
                    // c -0.08 1.46 0.03 1.81 0.75 2.45
                    curveToRelative(
                        dx1 = -0.08f,
                        dy1 = 1.46f,
                        dx2 = 0.03f,
                        dy2 = 1.81f,
                        dx3 = 0.75f,
                        dy3 = 2.45f,
                    )
                    // c 0.19 0.16 0.33 0.34 0.33 0.41
                    curveToRelative(
                        dx1 = 0.19f,
                        dy1 = 0.16f,
                        dx2 = 0.33f,
                        dy2 = 0.34f,
                        dx3 = 0.33f,
                        dy3 = 0.41f,
                    )
                    // c 0 0.37 0.43 0.75 0.62 0.56
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = 0.37f,
                        dx2 = 0.43f,
                        dy2 = 0.75f,
                        dx3 = 0.62f,
                        dy3 = 0.56f,
                    )
                }
                // M72.26 65.87 c1.36 -.09 2.63 .9 3.51 2.77 a5.5 5.5 0 0 1 .62 2.83 9 9 0 0 1 -.14 2.22 c-.33 1.89 -.42 2.1 -1.11 2.79 a10 10 0 0 1 -4.48 2.16 16 16 0 0 1 -3.26 .22 c-1.85 .03 -2 .02 -2.26 -.1 a4 4 0 0 0 -1.07 -.2 8 8 0 0 1 -1.04 -.13 3 3 0 0 0 -.6 -.07 c-.22 0 -.43 -.04 -.47 -.08 -.16 -.16 -.92 -.33 -1.66 -.37 a5 5 0 0 1 -.86 -.1 c-.22 -.12 -.11 -.18 .25 -.14 .2 .02 .41 0 .49 -.04 .1 -.06 .07 -.07 -.18 -.08 l-.74 -.04 c-.36 -.04 -.4 -.06 -.3 -.13 .25 -.18 .03 -.23 -1.08 -.23 -1 0 -1.3 -.05 -1 -.16 a3 3 0 0 1 .55 -.05 q.4 -.02 .25 -.03 l-.55 -.1 a30 30 0 0 0 -2.62 -.1 l-2.36 -.03 q-.1 0 -.05 -.14 .05 -.13 -.06 -.13 c-.05 0 -.18 -.1 -.28 -.24 -.2 -.26 -.26 -.24 -.34 .1 l-.06 .22 -.49 .02 c-.38 .01 -.51 0 -.64 -.1 a1 1 0 0 0 -.4 -.17 l-.25 -.05 .04 .4 c.04 .31 .03 .37 -.05 .34 l-.48 -.12 -.38 -.07 .14 -.2 a1 1 0 0 1 .43 -.25 l.26 -.07 -.4 -.01 h-.66 c-.14 0 -.25 -.02 -.25 -.06 s.04 -.08 .08 -.08 l.33 -.02 a2 2 0 0 0 .47 -.16 1 1 0 0 1 .35 -.15 q.1 0 .12 -.07 -.01 -.08 -.56 -.05 c-.54 .02 -.55 .02 -.57 .2 -.03 .2 -.03 .2 -.18 .15 a.1 .1 0 0 1 -.06 -.13 c.04 -.1 -.08 -.2 -.18 -.13 l-.35 .06 -2.6 .21 q-.34 .04 -.34 .14 -.02 .16 -.18 .02 c-.06 -.06 -.32 -.05 -.96 0 -.78 .08 -.88 .1 -.88 .22 q0 .12 -.15 .12 l-.62 .06 a2 2 0 0 1 -.55 .02 2 2 0 0 1 -.33 -.45 c-.27 -.43 -.49 -.7 -.58 -.7 q-.05 0 -.01 .1 .02 .09 -.04 .1 -.07 .01 -.06 .1 .02 .12 -.2 .17 c-.25 .05 -.36 .13 -.2 .13 q.1 .01 .12 .08 c.02 .07 -.4 .22 -.98 .4 -.61 .22 -.97 .36 -.97 .42 0 .07 .2 .02 .79 -.17 1.37 -.47 1.97 -.57 1.97 -.34 0 .1 -.11 .15 -.63 .27 l-.71 .14 a6.02 6.02 0 0 1 -1.52 .67 A6 6 0 0 0 36.8 78 a2 2 0 0 1 -.42 .25 4.5 4.5 0 0 0 -1.49 1.34 c-.43 .58 -.57 .76 -.52 .82 l-2.71 .31 q.02 -.02 -.03 -.09 -.07 -.08 0 -.66 c.2 -1.84 .94 -3.13 2.19 -3.78 a3 3 0 0 0 .52 -.32 c.13 -.2 1.71 -.8 2.59 -.99 q.6 -.12 1.2 -.3 .67 -.17 1.35 -.26 c1 -.1 2.35 -.32 2.8 -.46 a13 13 0 0 1 1.16 -.27 4 4 0 0 0 .8 -.2 q.07 -.05 .42 0 a5 5 0 0 0 1.24 0 33 33 0 0 1 2.63 -.07 c1.47 .01 1.88 -.01 2.63 -.13 a11 11 0 0 1 1.45 -.14 c.74 0 2.92 -.23 2.53 -.26 L54.7 72.7 c-.31 -.07 -.33 -.08 -.27 -.22 s.1 -.15 1.05 -.14 c.8 0 .99 .02 1.05 .1 .07 .1 .14 .1 .43 .05 .32 -.07 .34 -.08 .24 -.16 s-.11 -.1 -.02 -.16 a1 1 0 0 1 .2 -.07 l6.04 -.6 a3 3 0 0 0 .67 -.1 q.02 -.04 -.08 -.04 T63.9 71.3 c0 -.1 .34 -.13 .81 -.06 .33 .05 .5 .05 .61 0 .13 -.06 .1 -.06 -.2 -.04 -.64 .07 -.37 -.08 .3 -.16 l.84 -.11 a1 1 0 0 0 .3 -.1 14 14 0 0 1 1.64 -.29 20 20 0 0 0 2.09 -.37 7 7 0 0 0 2.67 -1.33 l.3 -.27 -.11 -.26 a2 2 0 0 0 -1 -1.08 c-.7 -.34 -1.02 -.39 -2.37 -.43 -1.23 -.03 -1.52 -.08 -1.38 -.24 a5 5 0 0 1 1.52 -.24 8 8 0 0 0 1.75 -.35 3 3 0 0 1 .59 -.1 m-31.78 8.54 a1 1 0 0 0 -.36 .07 1 1 0 0 1 -.5 .07 q-.1 -.03 -.17 .04 c-.03 .06 .07 .07 .4 .04 q.45 -.05 .45 .02 -.01 .06 -.19 .1 c-.49 .11 -.57 .16 -.15 .12 q.4 -.04 .81 -.05 c.35 0 .37 0 .4 .18 .01 .15 .06 .18 .39 .25 .45 .09 .39 .1 .72 -.06 a8 8 0 0 1 1.8 -.44 c.3 -.04 .46 -.08 .35 -.1 -.1 0 -.73 .08 -1.37 .17 -1.2 .17 -1.54 .2 -1.46 .13 a12 12 0 0 1 1.17 -.26 l1.13 -.23 h-.66 c-.58 -.01 -.68 .01 -.86 .14 q-.2 .15 -.27 .07 -.08 -.06 -.57 -.04 c-.42 .04 -.53 .02 -.77 -.1 a1 1 0 0 0 -.3 -.12 m9.54 1.13 q-.05 0 -.08 .02 -.04 .04 .05 .1 c.13 .08 .18 .08 .24 0 .03 -.05 -.11 -.12 -.21 -.12 m-10.94 .2 L39 75.83 a.3 .3 0 0 1 -.22 .1 q-.15 0 -.03 .08 l.14 .06 q.2 -.03 .2 -.22 0 -.1 -.02 -.1 m-4.9 4.4 q-.1 -.01 -.26 .1 -.22 .15 -.03 .12 c.19 0 .4 -.17 .28 -.21
                path(
                    fill = SolidColor(Color(0xFFFFFFFF)),
                ) {
                    // M 72.26 65.87
                    moveTo(x = 72.26f, y = 65.87f)
                    // c 1.36 -0.09 2.63 0.9 3.51 2.77
                    curveToRelative(
                        dx1 = 1.36f,
                        dy1 = -0.09f,
                        dx2 = 2.63f,
                        dy2 = 0.9f,
                        dx3 = 3.51f,
                        dy3 = 2.77f,
                    )
                    // a 5.5 5.5 0 0 1 0.62 2.83
                    arcToRelative(
                        a = 5.5f,
                        b = 5.5f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.62f,
                        dy1 = 2.83f,
                    )
                    // a 9 9 0 0 1 -0.14 2.22
                    arcToRelative(
                        a = 9.0f,
                        b = 9.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.14f,
                        dy1 = 2.22f,
                    )
                    // c -0.33 1.89 -0.42 2.1 -1.11 2.79
                    curveToRelative(
                        dx1 = -0.33f,
                        dy1 = 1.89f,
                        dx2 = -0.42f,
                        dy2 = 2.1f,
                        dx3 = -1.11f,
                        dy3 = 2.79f,
                    )
                    // a 10 10 0 0 1 -4.48 2.16
                    arcToRelative(
                        a = 10.0f,
                        b = 10.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -4.48f,
                        dy1 = 2.16f,
                    )
                    // a 16 16 0 0 1 -3.26 0.22
                    arcToRelative(
                        a = 16.0f,
                        b = 16.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -3.26f,
                        dy1 = 0.22f,
                    )
                    // c -1.85 0.03 -2 0.02 -2.26 -0.1
                    curveToRelative(
                        dx1 = -1.85f,
                        dy1 = 0.03f,
                        dx2 = -2.0f,
                        dy2 = 0.02f,
                        dx3 = -2.26f,
                        dy3 = -0.1f,
                    )
                    // a 4 4 0 0 0 -1.07 -0.2
                    arcToRelative(
                        a = 4.0f,
                        b = 4.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -1.07f,
                        dy1 = -0.2f,
                    )
                    // a 8 8 0 0 1 -1.04 -0.13
                    arcToRelative(
                        a = 8.0f,
                        b = 8.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -1.04f,
                        dy1 = -0.13f,
                    )
                    // a 3 3 0 0 0 -0.6 -0.07
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.6f,
                        dy1 = -0.07f,
                    )
                    // c -0.22 0 -0.43 -0.04 -0.47 -0.08
                    curveToRelative(
                        dx1 = -0.22f,
                        dy1 = 0.0f,
                        dx2 = -0.43f,
                        dy2 = -0.04f,
                        dx3 = -0.47f,
                        dy3 = -0.08f,
                    )
                    // c -0.16 -0.16 -0.92 -0.33 -1.66 -0.37
                    curveToRelative(
                        dx1 = -0.16f,
                        dy1 = -0.16f,
                        dx2 = -0.92f,
                        dy2 = -0.33f,
                        dx3 = -1.66f,
                        dy3 = -0.37f,
                    )
                    // a 5 5 0 0 1 -0.86 -0.1
                    arcToRelative(
                        a = 5.0f,
                        b = 5.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.86f,
                        dy1 = -0.1f,
                    )
                    // c -0.22 -0.12 -0.11 -0.18 0.25 -0.14
                    curveToRelative(
                        dx1 = -0.22f,
                        dy1 = -0.12f,
                        dx2 = -0.11f,
                        dy2 = -0.18f,
                        dx3 = 0.25f,
                        dy3 = -0.14f,
                    )
                    // c 0.2 0.02 0.41 0 0.49 -0.04
                    curveToRelative(
                        dx1 = 0.2f,
                        dy1 = 0.02f,
                        dx2 = 0.41f,
                        dy2 = 0.0f,
                        dx3 = 0.49f,
                        dy3 = -0.04f,
                    )
                    // c 0.1 -0.06 0.07 -0.07 -0.18 -0.08
                    curveToRelative(
                        dx1 = 0.1f,
                        dy1 = -0.06f,
                        dx2 = 0.07f,
                        dy2 = -0.07f,
                        dx3 = -0.18f,
                        dy3 = -0.08f,
                    )
                    // l -0.74 -0.04
                    lineToRelative(dx = -0.74f, dy = -0.04f)
                    // c -0.36 -0.04 -0.4 -0.06 -0.3 -0.13
                    curveToRelative(
                        dx1 = -0.36f,
                        dy1 = -0.04f,
                        dx2 = -0.4f,
                        dy2 = -0.06f,
                        dx3 = -0.3f,
                        dy3 = -0.13f,
                    )
                    // c 0.25 -0.18 0.03 -0.23 -1.08 -0.23
                    curveToRelative(
                        dx1 = 0.25f,
                        dy1 = -0.18f,
                        dx2 = 0.03f,
                        dy2 = -0.23f,
                        dx3 = -1.08f,
                        dy3 = -0.23f,
                    )
                    // c -1 0 -1.3 -0.05 -1 -0.16
                    curveToRelative(
                        dx1 = -1.0f,
                        dy1 = 0.0f,
                        dx2 = -1.3f,
                        dy2 = -0.05f,
                        dx3 = -1.0f,
                        dy3 = -0.16f,
                    )
                    // a 3 3 0 0 1 0.55 -0.05
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.55f,
                        dy1 = -0.05f,
                    )
                    // q 0.4 -0.02 0.25 -0.03
                    quadToRelative(
                        dx1 = 0.4f,
                        dy1 = -0.02f,
                        dx2 = 0.25f,
                        dy2 = -0.03f,
                    )
                    // l -0.55 -0.1
                    lineToRelative(dx = -0.55f, dy = -0.1f)
                    // a 30 30 0 0 0 -2.62 -0.1
                    arcToRelative(
                        a = 30.0f,
                        b = 30.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -2.62f,
                        dy1 = -0.1f,
                    )
                    // l -2.36 -0.03
                    lineToRelative(dx = -2.36f, dy = -0.03f)
                    // q -0.1 0 -0.05 -0.14
                    quadToRelative(
                        dx1 = -0.1f,
                        dy1 = 0.0f,
                        dx2 = -0.05f,
                        dy2 = -0.14f,
                    )
                    // q 0.05 -0.13 -0.06 -0.13
                    quadToRelative(
                        dx1 = 0.05f,
                        dy1 = -0.13f,
                        dx2 = -0.06f,
                        dy2 = -0.13f,
                    )
                    // c -0.05 0 -0.18 -0.1 -0.28 -0.24
                    curveToRelative(
                        dx1 = -0.05f,
                        dy1 = 0.0f,
                        dx2 = -0.18f,
                        dy2 = -0.1f,
                        dx3 = -0.28f,
                        dy3 = -0.24f,
                    )
                    // c -0.2 -0.26 -0.26 -0.24 -0.34 0.1
                    curveToRelative(
                        dx1 = -0.2f,
                        dy1 = -0.26f,
                        dx2 = -0.26f,
                        dy2 = -0.24f,
                        dx3 = -0.34f,
                        dy3 = 0.1f,
                    )
                    // l -0.06 0.22
                    lineToRelative(dx = -0.06f, dy = 0.22f)
                    // l -0.49 0.02
                    lineToRelative(dx = -0.49f, dy = 0.02f)
                    // c -0.38 0.01 -0.51 0 -0.64 -0.1
                    curveToRelative(
                        dx1 = -0.38f,
                        dy1 = 0.01f,
                        dx2 = -0.51f,
                        dy2 = 0.0f,
                        dx3 = -0.64f,
                        dy3 = -0.1f,
                    )
                    // a 1 1 0 0 0 -0.4 -0.17
                    arcToRelative(
                        a = 1.0f,
                        b = 1.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.4f,
                        dy1 = -0.17f,
                    )
                    // l -0.25 -0.05
                    lineToRelative(dx = -0.25f, dy = -0.05f)
                    // l 0.04 0.4
                    lineToRelative(dx = 0.04f, dy = 0.4f)
                    // c 0.04 0.31 0.03 0.37 -0.05 0.34
                    curveToRelative(
                        dx1 = 0.04f,
                        dy1 = 0.31f,
                        dx2 = 0.03f,
                        dy2 = 0.37f,
                        dx3 = -0.05f,
                        dy3 = 0.34f,
                    )
                    // l -0.48 -0.12
                    lineToRelative(dx = -0.48f, dy = -0.12f)
                    // l -0.38 -0.07
                    lineToRelative(dx = -0.38f, dy = -0.07f)
                    // l 0.14 -0.2
                    lineToRelative(dx = 0.14f, dy = -0.2f)
                    // a 1 1 0 0 1 0.43 -0.25
                    arcToRelative(
                        a = 1.0f,
                        b = 1.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.43f,
                        dy1 = -0.25f,
                    )
                    // l 0.26 -0.07
                    lineToRelative(dx = 0.26f, dy = -0.07f)
                    // l -0.4 -0.01
                    lineToRelative(dx = -0.4f, dy = -0.01f)
                    // h -0.66
                    horizontalLineToRelative(dx = -0.66f)
                    // c -0.14 0 -0.25 -0.02 -0.25 -0.06
                    curveToRelative(
                        dx1 = -0.14f,
                        dy1 = 0.0f,
                        dx2 = -0.25f,
                        dy2 = -0.02f,
                        dx3 = -0.25f,
                        dy3 = -0.06f,
                    )
                    // s 0.04 -0.08 0.08 -0.08
                    reflectiveCurveToRelative(
                        dx1 = 0.04f,
                        dy1 = -0.08f,
                        dx2 = 0.08f,
                        dy2 = -0.08f,
                    )
                    // l 0.33 -0.02
                    lineToRelative(dx = 0.33f, dy = -0.02f)
                    // a 2 2 0 0 0 0.47 -0.16
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.47f,
                        dy1 = -0.16f,
                    )
                    // a 1 1 0 0 1 0.35 -0.15
                    arcToRelative(
                        a = 1.0f,
                        b = 1.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.35f,
                        dy1 = -0.15f,
                    )
                    // q 0.1 0 0.12 -0.07
                    quadToRelative(
                        dx1 = 0.1f,
                        dy1 = 0.0f,
                        dx2 = 0.12f,
                        dy2 = -0.07f,
                    )
                    // q -0.01 -0.08 -0.56 -0.05
                    quadToRelative(
                        dx1 = -0.01f,
                        dy1 = -0.08f,
                        dx2 = -0.56f,
                        dy2 = -0.05f,
                    )
                    // c -0.54 0.02 -0.55 0.02 -0.57 0.2
                    curveToRelative(
                        dx1 = -0.54f,
                        dy1 = 0.02f,
                        dx2 = -0.55f,
                        dy2 = 0.02f,
                        dx3 = -0.57f,
                        dy3 = 0.2f,
                    )
                    // c -0.03 0.2 -0.03 0.2 -0.18 0.15
                    curveToRelative(
                        dx1 = -0.03f,
                        dy1 = 0.2f,
                        dx2 = -0.03f,
                        dy2 = 0.2f,
                        dx3 = -0.18f,
                        dy3 = 0.15f,
                    )
                    // a 0.1 0.1 0 0 1 -0.06 -0.13
                    arcToRelative(
                        a = 0.1f,
                        b = 0.1f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.06f,
                        dy1 = -0.13f,
                    )
                    // c 0.04 -0.1 -0.08 -0.2 -0.18 -0.13
                    curveToRelative(
                        dx1 = 0.04f,
                        dy1 = -0.1f,
                        dx2 = -0.08f,
                        dy2 = -0.2f,
                        dx3 = -0.18f,
                        dy3 = -0.13f,
                    )
                    // l -0.35 0.06
                    lineToRelative(dx = -0.35f, dy = 0.06f)
                    // l -2.6 0.21
                    lineToRelative(dx = -2.6f, dy = 0.21f)
                    // q -0.34 0.04 -0.34 0.14
                    quadToRelative(
                        dx1 = -0.34f,
                        dy1 = 0.04f,
                        dx2 = -0.34f,
                        dy2 = 0.14f,
                    )
                    // q -0.02 0.16 -0.18 0.02
                    quadToRelative(
                        dx1 = -0.02f,
                        dy1 = 0.16f,
                        dx2 = -0.18f,
                        dy2 = 0.02f,
                    )
                    // c -0.06 -0.06 -0.32 -0.05 -0.96 0
                    curveToRelative(
                        dx1 = -0.06f,
                        dy1 = -0.06f,
                        dx2 = -0.32f,
                        dy2 = -0.05f,
                        dx3 = -0.96f,
                        dy3 = 0.0f,
                    )
                    // c -0.78 0.08 -0.88 0.1 -0.88 0.22
                    curveToRelative(
                        dx1 = -0.78f,
                        dy1 = 0.08f,
                        dx2 = -0.88f,
                        dy2 = 0.1f,
                        dx3 = -0.88f,
                        dy3 = 0.22f,
                    )
                    // q 0 0.12 -0.15 0.12
                    quadToRelative(
                        dx1 = 0.0f,
                        dy1 = 0.12f,
                        dx2 = -0.15f,
                        dy2 = 0.12f,
                    )
                    // l -0.62 0.06
                    lineToRelative(dx = -0.62f, dy = 0.06f)
                    // a 2 2 0 0 1 -0.55 0.02
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.55f,
                        dy1 = 0.02f,
                    )
                    // a 2 2 0 0 1 -0.33 -0.45
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.33f,
                        dy1 = -0.45f,
                    )
                    // c -0.27 -0.43 -0.49 -0.7 -0.58 -0.7
                    curveToRelative(
                        dx1 = -0.27f,
                        dy1 = -0.43f,
                        dx2 = -0.49f,
                        dy2 = -0.7f,
                        dx3 = -0.58f,
                        dy3 = -0.7f,
                    )
                    // q -0.05 0 -0.01 0.1
                    quadToRelative(
                        dx1 = -0.05f,
                        dy1 = 0.0f,
                        dx2 = -0.01f,
                        dy2 = 0.1f,
                    )
                    // q 0.02 0.09 -0.04 0.1
                    quadToRelative(
                        dx1 = 0.02f,
                        dy1 = 0.09f,
                        dx2 = -0.04f,
                        dy2 = 0.1f,
                    )
                    // q -0.07 0.01 -0.06 0.1
                    quadToRelative(
                        dx1 = -0.07f,
                        dy1 = 0.01f,
                        dx2 = -0.06f,
                        dy2 = 0.1f,
                    )
                    // q 0.02 0.12 -0.2 0.17
                    quadToRelative(
                        dx1 = 0.02f,
                        dy1 = 0.12f,
                        dx2 = -0.2f,
                        dy2 = 0.17f,
                    )
                    // c -0.25 0.05 -0.36 0.13 -0.2 0.13
                    curveToRelative(
                        dx1 = -0.25f,
                        dy1 = 0.05f,
                        dx2 = -0.36f,
                        dy2 = 0.13f,
                        dx3 = -0.2f,
                        dy3 = 0.13f,
                    )
                    // q 0.1 0.01 0.12 0.08
                    quadToRelative(
                        dx1 = 0.1f,
                        dy1 = 0.01f,
                        dx2 = 0.12f,
                        dy2 = 0.08f,
                    )
                    // c 0.02 0.07 -0.4 0.22 -0.98 0.4
                    curveToRelative(
                        dx1 = 0.02f,
                        dy1 = 0.07f,
                        dx2 = -0.4f,
                        dy2 = 0.22f,
                        dx3 = -0.98f,
                        dy3 = 0.4f,
                    )
                    // c -0.61 0.22 -0.97 0.36 -0.97 0.42
                    curveToRelative(
                        dx1 = -0.61f,
                        dy1 = 0.22f,
                        dx2 = -0.97f,
                        dy2 = 0.36f,
                        dx3 = -0.97f,
                        dy3 = 0.42f,
                    )
                    // c 0 0.07 0.2 0.02 0.79 -0.17
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = 0.07f,
                        dx2 = 0.2f,
                        dy2 = 0.02f,
                        dx3 = 0.79f,
                        dy3 = -0.17f,
                    )
                    // c 1.37 -0.47 1.97 -0.57 1.97 -0.34
                    curveToRelative(
                        dx1 = 1.37f,
                        dy1 = -0.47f,
                        dx2 = 1.97f,
                        dy2 = -0.57f,
                        dx3 = 1.97f,
                        dy3 = -0.34f,
                    )
                    // c 0 0.1 -0.11 0.15 -0.63 0.27
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = 0.1f,
                        dx2 = -0.11f,
                        dy2 = 0.15f,
                        dx3 = -0.63f,
                        dy3 = 0.27f,
                    )
                    // l -0.71 0.14
                    lineToRelative(dx = -0.71f, dy = 0.14f)
                    // a 6.02 6.02 0 0 1 -1.52 0.67
                    arcToRelative(
                        a = 6.02f,
                        b = 6.02f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -1.52f,
                        dy1 = 0.67f,
                    )
                    // A 6 6 0 0 0 36.8 78
                    arcTo(
                        horizontalEllipseRadius = 6.0f,
                        verticalEllipseRadius = 6.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 36.8f,
                        y1 = 78.0f,
                    )
                    // a 2 2 0 0 1 -0.42 0.25
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.42f,
                        dy1 = 0.25f,
                    )
                    // a 4.5 4.5 0 0 0 -1.49 1.34
                    arcToRelative(
                        a = 4.5f,
                        b = 4.5f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -1.49f,
                        dy1 = 1.34f,
                    )
                    // c -0.43 0.58 -0.57 0.76 -0.52 0.82
                    curveToRelative(
                        dx1 = -0.43f,
                        dy1 = 0.58f,
                        dx2 = -0.57f,
                        dy2 = 0.76f,
                        dx3 = -0.52f,
                        dy3 = 0.82f,
                    )
                    // l -2.71 0.31
                    lineToRelative(dx = -2.71f, dy = 0.31f)
                    // q 0.02 -0.02 -0.03 -0.09
                    quadToRelative(
                        dx1 = 0.02f,
                        dy1 = -0.02f,
                        dx2 = -0.03f,
                        dy2 = -0.09f,
                    )
                    // q -0.07 -0.08 0 -0.66
                    quadToRelative(
                        dx1 = -0.07f,
                        dy1 = -0.08f,
                        dx2 = 0.0f,
                        dy2 = -0.66f,
                    )
                    // c 0.2 -1.84 0.94 -3.13 2.19 -3.78
                    curveToRelative(
                        dx1 = 0.2f,
                        dy1 = -1.84f,
                        dx2 = 0.94f,
                        dy2 = -3.13f,
                        dx3 = 2.19f,
                        dy3 = -3.78f,
                    )
                    // a 3 3 0 0 0 0.52 -0.32
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.52f,
                        dy1 = -0.32f,
                    )
                    // c 0.13 -0.2 1.71 -0.8 2.59 -0.99
                    curveToRelative(
                        dx1 = 0.13f,
                        dy1 = -0.2f,
                        dx2 = 1.71f,
                        dy2 = -0.8f,
                        dx3 = 2.59f,
                        dy3 = -0.99f,
                    )
                    // q 0.6 -0.12 1.2 -0.3
                    quadToRelative(
                        dx1 = 0.6f,
                        dy1 = -0.12f,
                        dx2 = 1.2f,
                        dy2 = -0.3f,
                    )
                    // q 0.67 -0.17 1.35 -0.26
                    quadToRelative(
                        dx1 = 0.67f,
                        dy1 = -0.17f,
                        dx2 = 1.35f,
                        dy2 = -0.26f,
                    )
                    // c 1 -0.1 2.35 -0.32 2.8 -0.46
                    curveToRelative(
                        dx1 = 1.0f,
                        dy1 = -0.1f,
                        dx2 = 2.35f,
                        dy2 = -0.32f,
                        dx3 = 2.8f,
                        dy3 = -0.46f,
                    )
                    // a 13 13 0 0 1 1.16 -0.27
                    arcToRelative(
                        a = 13.0f,
                        b = 13.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.16f,
                        dy1 = -0.27f,
                    )
                    // a 4 4 0 0 0 0.8 -0.2
                    arcToRelative(
                        a = 4.0f,
                        b = 4.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.8f,
                        dy1 = -0.2f,
                    )
                    // q 0.07 -0.05 0.42 0
                    quadToRelative(
                        dx1 = 0.07f,
                        dy1 = -0.05f,
                        dx2 = 0.42f,
                        dy2 = 0.0f,
                    )
                    // a 5 5 0 0 0 1.24 0
                    arcToRelative(
                        a = 5.0f,
                        b = 5.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 1.24f,
                        dy1 = 0.0f,
                    )
                    // a 33 33 0 0 1 2.63 -0.07
                    arcToRelative(
                        a = 33.0f,
                        b = 33.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 2.63f,
                        dy1 = -0.07f,
                    )
                    // c 1.47 0.01 1.88 -0.01 2.63 -0.13
                    curveToRelative(
                        dx1 = 1.47f,
                        dy1 = 0.01f,
                        dx2 = 1.88f,
                        dy2 = -0.01f,
                        dx3 = 2.63f,
                        dy3 = -0.13f,
                    )
                    // a 11 11 0 0 1 1.45 -0.14
                    arcToRelative(
                        a = 11.0f,
                        b = 11.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.45f,
                        dy1 = -0.14f,
                    )
                    // c 0.74 0 2.92 -0.23 2.53 -0.26
                    curveToRelative(
                        dx1 = 0.74f,
                        dy1 = 0.0f,
                        dx2 = 2.92f,
                        dy2 = -0.23f,
                        dx3 = 2.53f,
                        dy3 = -0.26f,
                    )
                    // L 54.7 72.7
                    lineTo(x = 54.7f, y = 72.7f)
                    // c -0.31 -0.07 -0.33 -0.08 -0.27 -0.22
                    curveToRelative(
                        dx1 = -0.31f,
                        dy1 = -0.07f,
                        dx2 = -0.33f,
                        dy2 = -0.08f,
                        dx3 = -0.27f,
                        dy3 = -0.22f,
                    )
                    // s 0.1 -0.15 1.05 -0.14
                    reflectiveCurveToRelative(
                        dx1 = 0.1f,
                        dy1 = -0.15f,
                        dx2 = 1.05f,
                        dy2 = -0.14f,
                    )
                    // c 0.8 0 0.99 0.02 1.05 0.1
                    curveToRelative(
                        dx1 = 0.8f,
                        dy1 = 0.0f,
                        dx2 = 0.99f,
                        dy2 = 0.02f,
                        dx3 = 1.05f,
                        dy3 = 0.1f,
                    )
                    // c 0.07 0.1 0.14 0.1 0.43 0.05
                    curveToRelative(
                        dx1 = 0.07f,
                        dy1 = 0.1f,
                        dx2 = 0.14f,
                        dy2 = 0.1f,
                        dx3 = 0.43f,
                        dy3 = 0.05f,
                    )
                    // c 0.32 -0.07 0.34 -0.08 0.24 -0.16
                    curveToRelative(
                        dx1 = 0.32f,
                        dy1 = -0.07f,
                        dx2 = 0.34f,
                        dy2 = -0.08f,
                        dx3 = 0.24f,
                        dy3 = -0.16f,
                    )
                    // s -0.11 -0.1 -0.02 -0.16
                    reflectiveCurveToRelative(
                        dx1 = -0.11f,
                        dy1 = -0.1f,
                        dx2 = -0.02f,
                        dy2 = -0.16f,
                    )
                    // a 1 1 0 0 1 0.2 -0.07
                    arcToRelative(
                        a = 1.0f,
                        b = 1.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.2f,
                        dy1 = -0.07f,
                    )
                    // l 6.04 -0.6
                    lineToRelative(dx = 6.04f, dy = -0.6f)
                    // a 3 3 0 0 0 0.67 -0.1
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.67f,
                        dy1 = -0.1f,
                    )
                    // q 0.02 -0.04 -0.08 -0.04
                    quadToRelative(
                        dx1 = 0.02f,
                        dy1 = -0.04f,
                        dx2 = -0.08f,
                        dy2 = -0.04f,
                    )
                    // T 63.9 71.3
                    reflectiveQuadTo(
                        x1 = 63.9f,
                        y1 = 71.3f,
                    )
                    // c 0 -0.1 0.34 -0.13 0.81 -0.06
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = -0.1f,
                        dx2 = 0.34f,
                        dy2 = -0.13f,
                        dx3 = 0.81f,
                        dy3 = -0.06f,
                    )
                    // c 0.33 0.05 0.5 0.05 0.61 0
                    curveToRelative(
                        dx1 = 0.33f,
                        dy1 = 0.05f,
                        dx2 = 0.5f,
                        dy2 = 0.05f,
                        dx3 = 0.61f,
                        dy3 = 0.0f,
                    )
                    // c 0.13 -0.06 0.1 -0.06 -0.2 -0.04
                    curveToRelative(
                        dx1 = 0.13f,
                        dy1 = -0.06f,
                        dx2 = 0.1f,
                        dy2 = -0.06f,
                        dx3 = -0.2f,
                        dy3 = -0.04f,
                    )
                    // c -0.64 0.07 -0.37 -0.08 0.3 -0.16
                    curveToRelative(
                        dx1 = -0.64f,
                        dy1 = 0.07f,
                        dx2 = -0.37f,
                        dy2 = -0.08f,
                        dx3 = 0.3f,
                        dy3 = -0.16f,
                    )
                    // l 0.84 -0.11
                    lineToRelative(dx = 0.84f, dy = -0.11f)
                    // a 1 1 0 0 0 0.3 -0.1
                    arcToRelative(
                        a = 1.0f,
                        b = 1.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.3f,
                        dy1 = -0.1f,
                    )
                    // a 14 14 0 0 1 1.64 -0.29
                    arcToRelative(
                        a = 14.0f,
                        b = 14.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.64f,
                        dy1 = -0.29f,
                    )
                    // a 20 20 0 0 0 2.09 -0.37
                    arcToRelative(
                        a = 20.0f,
                        b = 20.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 2.09f,
                        dy1 = -0.37f,
                    )
                    // a 7 7 0 0 0 2.67 -1.33
                    arcToRelative(
                        a = 7.0f,
                        b = 7.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 2.67f,
                        dy1 = -1.33f,
                    )
                    // l 0.3 -0.27
                    lineToRelative(dx = 0.3f, dy = -0.27f)
                    // l -0.11 -0.26
                    lineToRelative(dx = -0.11f, dy = -0.26f)
                    // a 2 2 0 0 0 -1 -1.08
                    arcToRelative(
                        a = 2.0f,
                        b = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -1.0f,
                        dy1 = -1.08f,
                    )
                    // c -0.7 -0.34 -1.02 -0.39 -2.37 -0.43
                    curveToRelative(
                        dx1 = -0.7f,
                        dy1 = -0.34f,
                        dx2 = -1.02f,
                        dy2 = -0.39f,
                        dx3 = -2.37f,
                        dy3 = -0.43f,
                    )
                    // c -1.23 -0.03 -1.52 -0.08 -1.38 -0.24
                    curveToRelative(
                        dx1 = -1.23f,
                        dy1 = -0.03f,
                        dx2 = -1.52f,
                        dy2 = -0.08f,
                        dx3 = -1.38f,
                        dy3 = -0.24f,
                    )
                    // a 5 5 0 0 1 1.52 -0.24
                    arcToRelative(
                        a = 5.0f,
                        b = 5.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.52f,
                        dy1 = -0.24f,
                    )
                    // a 8 8 0 0 0 1.75 -0.35
                    arcToRelative(
                        a = 8.0f,
                        b = 8.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 1.75f,
                        dy1 = -0.35f,
                    )
                    // a 3 3 0 0 1 0.59 -0.1
                    arcToRelative(
                        a = 3.0f,
                        b = 3.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.59f,
                        dy1 = -0.1f,
                    )
                    // m -31.78 8.54
                    moveToRelative(dx = -31.78f, dy = 8.54f)
                    // a 1 1 0 0 0 -0.36 0.07
                    arcToRelative(
                        a = 1.0f,
                        b = 1.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.36f,
                        dy1 = 0.07f,
                    )
                    // a 1 1 0 0 1 -0.5 0.07
                    arcToRelative(
                        a = 1.0f,
                        b = 1.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.5f,
                        dy1 = 0.07f,
                    )
                    // q -0.1 -0.03 -0.17 0.04
                    quadToRelative(
                        dx1 = -0.1f,
                        dy1 = -0.03f,
                        dx2 = -0.17f,
                        dy2 = 0.04f,
                    )
                    // c -0.03 0.06 0.07 0.07 0.4 0.04
                    curveToRelative(
                        dx1 = -0.03f,
                        dy1 = 0.06f,
                        dx2 = 0.07f,
                        dy2 = 0.07f,
                        dx3 = 0.4f,
                        dy3 = 0.04f,
                    )
                    // q 0.45 -0.05 0.45 0.02
                    quadToRelative(
                        dx1 = 0.45f,
                        dy1 = -0.05f,
                        dx2 = 0.45f,
                        dy2 = 0.02f,
                    )
                    // q -0.01 0.06 -0.19 0.1
                    quadToRelative(
                        dx1 = -0.01f,
                        dy1 = 0.06f,
                        dx2 = -0.19f,
                        dy2 = 0.1f,
                    )
                    // c -0.49 0.11 -0.57 0.16 -0.15 0.12
                    curveToRelative(
                        dx1 = -0.49f,
                        dy1 = 0.11f,
                        dx2 = -0.57f,
                        dy2 = 0.16f,
                        dx3 = -0.15f,
                        dy3 = 0.12f,
                    )
                    // q 0.4 -0.04 0.81 -0.05
                    quadToRelative(
                        dx1 = 0.4f,
                        dy1 = -0.04f,
                        dx2 = 0.81f,
                        dy2 = -0.05f,
                    )
                    // c 0.35 0 0.37 0 0.4 0.18
                    curveToRelative(
                        dx1 = 0.35f,
                        dy1 = 0.0f,
                        dx2 = 0.37f,
                        dy2 = 0.0f,
                        dx3 = 0.4f,
                        dy3 = 0.18f,
                    )
                    // c 0.01 0.15 0.06 0.18 0.39 0.25
                    curveToRelative(
                        dx1 = 0.01f,
                        dy1 = 0.15f,
                        dx2 = 0.06f,
                        dy2 = 0.18f,
                        dx3 = 0.39f,
                        dy3 = 0.25f,
                    )
                    // c 0.45 0.09 0.39 0.1 0.72 -0.06
                    curveToRelative(
                        dx1 = 0.45f,
                        dy1 = 0.09f,
                        dx2 = 0.39f,
                        dy2 = 0.1f,
                        dx3 = 0.72f,
                        dy3 = -0.06f,
                    )
                    // a 8 8 0 0 1 1.8 -0.44
                    arcToRelative(
                        a = 8.0f,
                        b = 8.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.8f,
                        dy1 = -0.44f,
                    )
                    // c 0.3 -0.04 0.46 -0.08 0.35 -0.1
                    curveToRelative(
                        dx1 = 0.3f,
                        dy1 = -0.04f,
                        dx2 = 0.46f,
                        dy2 = -0.08f,
                        dx3 = 0.35f,
                        dy3 = -0.1f,
                    )
                    // c -0.1 0 -0.73 0.08 -1.37 0.17
                    curveToRelative(
                        dx1 = -0.1f,
                        dy1 = 0.0f,
                        dx2 = -0.73f,
                        dy2 = 0.08f,
                        dx3 = -1.37f,
                        dy3 = 0.17f,
                    )
                    // c -1.2 0.17 -1.54 0.2 -1.46 0.13
                    curveToRelative(
                        dx1 = -1.2f,
                        dy1 = 0.17f,
                        dx2 = -1.54f,
                        dy2 = 0.2f,
                        dx3 = -1.46f,
                        dy3 = 0.13f,
                    )
                    // a 12 12 0 0 1 1.17 -0.26
                    arcToRelative(
                        a = 12.0f,
                        b = 12.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.17f,
                        dy1 = -0.26f,
                    )
                    // l 1.13 -0.23
                    lineToRelative(dx = 1.13f, dy = -0.23f)
                    // h -0.66
                    horizontalLineToRelative(dx = -0.66f)
                    // c -0.58 -0.01 -0.68 0.01 -0.86 0.14
                    curveToRelative(
                        dx1 = -0.58f,
                        dy1 = -0.01f,
                        dx2 = -0.68f,
                        dy2 = 0.01f,
                        dx3 = -0.86f,
                        dy3 = 0.14f,
                    )
                    // q -0.2 0.15 -0.27 0.07
                    quadToRelative(
                        dx1 = -0.2f,
                        dy1 = 0.15f,
                        dx2 = -0.27f,
                        dy2 = 0.07f,
                    )
                    // q -0.08 -0.06 -0.57 -0.04
                    quadToRelative(
                        dx1 = -0.08f,
                        dy1 = -0.06f,
                        dx2 = -0.57f,
                        dy2 = -0.04f,
                    )
                    // c -0.42 0.04 -0.53 0.02 -0.77 -0.1
                    curveToRelative(
                        dx1 = -0.42f,
                        dy1 = 0.04f,
                        dx2 = -0.53f,
                        dy2 = 0.02f,
                        dx3 = -0.77f,
                        dy3 = -0.1f,
                    )
                    // a 1 1 0 0 0 -0.3 -0.12
                    arcToRelative(
                        a = 1.0f,
                        b = 1.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.3f,
                        dy1 = -0.12f,
                    )
                    // m 9.54 1.13
                    moveToRelative(dx = 9.54f, dy = 1.13f)
                    // q -0.05 0 -0.08 0.02
                    quadToRelative(
                        dx1 = -0.05f,
                        dy1 = 0.0f,
                        dx2 = -0.08f,
                        dy2 = 0.02f,
                    )
                    // q -0.04 0.04 0.05 0.1
                    quadToRelative(
                        dx1 = -0.04f,
                        dy1 = 0.04f,
                        dx2 = 0.05f,
                        dy2 = 0.1f,
                    )
                    // c 0.13 0.08 0.18 0.08 0.24 0
                    curveToRelative(
                        dx1 = 0.13f,
                        dy1 = 0.08f,
                        dx2 = 0.18f,
                        dy2 = 0.08f,
                        dx3 = 0.24f,
                        dy3 = 0.0f,
                    )
                    // c 0.03 -0.05 -0.11 -0.12 -0.21 -0.12
                    curveToRelative(
                        dx1 = 0.03f,
                        dy1 = -0.05f,
                        dx2 = -0.11f,
                        dy2 = -0.12f,
                        dx3 = -0.21f,
                        dy3 = -0.12f,
                    )
                    // m -10.94 0.2
                    moveToRelative(dx = -10.94f, dy = 0.2f)
                    // L 39 75.83
                    lineTo(x = 39.0f, y = 75.83f)
                    // a 0.3 0.3 0 0 1 -0.22 0.1
                    arcToRelative(
                        a = 0.3f,
                        b = 0.3f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.22f,
                        dy1 = 0.1f,
                    )
                    // q -0.15 0 -0.03 0.08
                    quadToRelative(
                        dx1 = -0.15f,
                        dy1 = 0.0f,
                        dx2 = -0.03f,
                        dy2 = 0.08f,
                    )
                    // l 0.14 0.06
                    lineToRelative(dx = 0.14f, dy = 0.06f)
                    // q 0.2 -0.03 0.2 -0.22
                    quadToRelative(
                        dx1 = 0.2f,
                        dy1 = -0.03f,
                        dx2 = 0.2f,
                        dy2 = -0.22f,
                    )
                    // q 0 -0.1 -0.02 -0.1
                    quadToRelative(
                        dx1 = 0.0f,
                        dy1 = -0.1f,
                        dx2 = -0.02f,
                        dy2 = -0.1f,
                    )
                    // m -4.9 4.4
                    moveToRelative(dx = -4.9f, dy = 4.4f)
                    // q -0.1 -0.01 -0.26 0.1
                    quadToRelative(
                        dx1 = -0.1f,
                        dy1 = -0.01f,
                        dx2 = -0.26f,
                        dy2 = 0.1f,
                    )
                    // q -0.22 0.15 -0.03 0.12
                    quadToRelative(
                        dx1 = -0.22f,
                        dy1 = 0.15f,
                        dx2 = -0.03f,
                        dy2 = 0.12f,
                    )
                    // c 0.19 0 0.4 -0.17 0.28 -0.21
                    curveToRelative(
                        dx1 = 0.19f,
                        dy1 = 0.0f,
                        dx2 = 0.4f,
                        dy2 = -0.17f,
                        dx3 = 0.28f,
                        dy3 = -0.21f,
                    )
                }
            }.build().also { _AppIconRip = it }
        }

    private var _add: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Add: ImageVector
        get() {
            if (_add != null) {
                return _add!!
            }
            _add =
                ImageVector.Builder(
                    name = "_add",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(11f, 13f)
                            horizontalLineTo(6f)
                            quadTo(5.58f, 13f, 5.29f, 12.71f)
                            quadTo(5f, 12.43f, 5f, 12f)
                            reflectiveQuadTo(5.29f, 11.29f)
                            reflectiveQuadTo(6f, 11f)
                            horizontalLineToRelative(5f)
                            verticalLineTo(6f)
                            quadTo(11f, 5.57f, 11.29f, 5.29f)
                            reflectiveQuadTo(12f, 5f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(13f, 6f)
                            verticalLineToRelative(5f)
                            horizontalLineToRelative(5f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(19f, 12f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(18f, 13f)
                            horizontalLineTo(13f)
                            verticalLineToRelative(5f)
                            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                            reflectiveQuadTo(12f, 19f)
                            reflectiveQuadTo(11.29f, 18.71f)
                            quadTo(11f, 18.43f, 11f, 18f)
                            verticalLineTo(13f)
                            close()
                        }
                    }
                    .build()
            return _add!!
        }

    private var _addFriend: ImageVector? = null

    @Suppress("CheckReturnValue")
    val AddFriend: ImageVector
        get() {
            if (_addFriend != null) {
                return _addFriend!!
            }
            _addFriend =
                ImageVector.Builder(
                    name = "_add_friend",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(18f, 11f)
                            horizontalLineTo(16f)
                            quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
                            quadTo(15f, 10.43f, 15f, 10f)
                            quadTo(15f, 9.57f, 15.29f, 9.29f)
                            reflectiveQuadTo(16f, 9f)
                            horizontalLineToRelative(2f)
                            verticalLineTo(7f)
                            quadTo(18f, 6.57f, 18.29f, 6.29f)
                            reflectiveQuadTo(19f, 6f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(20f, 7f)
                            verticalLineTo(9f)
                            horizontalLineToRelative(2f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(23f, 10f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(22f, 11f)
                            horizontalLineTo(20f)
                            verticalLineToRelative(2f)
                            quadToRelative(0f, 0.42f, -0.29f, 0.71f)
                            reflectiveQuadTo(19f, 14f)
                            reflectiveQuadTo(18.29f, 13.71f)
                            quadTo(18f, 13.43f, 18f, 13f)
                            verticalLineTo(11f)
                            close()
                            moveTo(6.18f, 10.83f)
                            quadTo(5f, 9.65f, 5f, 8f)
                            reflectiveQuadTo(6.18f, 5.18f)
                            reflectiveQuadTo(9f, 4f)
                            reflectiveQuadToRelative(2.83f, 1.18f)
                            reflectiveQuadTo(13f, 8f)
                            reflectiveQuadToRelative(-1.17f, 2.82f)
                            reflectiveQuadTo(9f, 12f)
                            reflectiveQuadTo(6.18f, 10.83f)
                            close()
                            moveTo(1f, 18f)
                            verticalLineTo(17.2f)
                            quadTo(1f, 16.35f, 1.44f, 15.64f)
                            quadTo(1.88f, 14.93f, 2.6f, 14.55f)
                            quadTo(4.15f, 13.77f, 5.75f, 13.39f)
                            reflectiveQuadTo(9f, 13f)
                            reflectiveQuadToRelative(3.25f, 0.39f)
                            reflectiveQuadToRelative(3.15f, 1.16f)
                            quadToRelative(0.72f, 0.38f, 1.16f, 1.09f)
                            reflectiveQuadTo(17f, 17.2f)
                            verticalLineTo(18f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(15f, 20f)
                            horizontalLineTo(3f)
                            quadTo(2.18f, 20f, 1.59f, 19.41f)
                            reflectiveQuadTo(1f, 18f)
                            close()
                            moveToRelative(2f, 0f)
                            horizontalLineTo(15f)
                            verticalLineTo(17.2f)
                            quadToRelative(0f, -0.27f, -0.14f, -0.5f)
                            quadTo(14.73f, 16.48f, 14.5f, 16.35f)
                            quadTo(13.15f, 15.68f, 11.78f, 15.34f)
                            reflectiveQuadTo(9f, 15f)
                            reflectiveQuadTo(6.23f, 15.34f)
                            reflectiveQuadTo(3.5f, 16.35f)
                            quadTo(3.28f, 16.48f, 3.14f, 16.7f)
                            quadTo(3f, 16.93f, 3f, 17.2f)
                            verticalLineTo(18f)
                            close()
                            moveTo(10.41f, 9.41f)
                            quadTo(11f, 8.82f, 11f, 8f)
                            reflectiveQuadTo(10.41f, 6.59f)
                            reflectiveQuadTo(9f, 6f)
                            quadTo(8.18f, 6f, 7.59f, 6.59f)
                            quadTo(7f, 7.18f, 7f, 8f)
                            reflectiveQuadTo(7.59f, 9.41f)
                            reflectiveQuadTo(9f, 10f)
                            quadToRelative(0.83f, 0f, 1.41f, -0.59f)
                            close()
                            moveTo(9f, 8f)
                            close()
                            moveTo(9f, 18f)
                            close()
                        }
                    }
                    .build()
            return _addFriend!!
        }

    private var _anime: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Anime: ImageVector
        get() {
            if (_anime != null) {
                return _anime!!
            }
            _anime =
                ImageVector.Builder(
                    name = "_anime",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(4f, 4f)
                            lineTo(5.63f, 7.25f)
                            quadTo(5.8f, 7.6f, 6.13f, 7.8f)
                            reflectiveQuadTo(6.83f, 8f)
                            quadTo(7.58f, 8f, 7.98f, 7.36f)
                            quadTo(8.38f, 6.72f, 8.03f, 6.05f)
                            lineTo(7f, 4f)
                            horizontalLineTo(9f)
                            lineToRelative(1.63f, 3.25f)
                            quadTo(10.8f, 7.6f, 11.13f, 7.8f)
                            reflectiveQuadTo(11.83f, 8f)
                            quadToRelative(0.75f, 0f, 1.15f, -0.64f)
                            quadToRelative(0.4f, -0.64f, 0.05f, -1.31f)
                            lineTo(12f, 4f)
                            horizontalLineToRelative(2f)
                            lineToRelative(1.63f, 3.25f)
                            quadTo(15.8f, 7.6f, 16.13f, 7.8f)
                            reflectiveQuadTo(16.83f, 8f)
                            quadToRelative(0.75f, 0f, 1.15f, -0.64f)
                            quadToRelative(0.4f, -0.64f, 0.05f, -1.31f)
                            lineTo(17f, 4f)
                            horizontalLineToRelative(3f)
                            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                            quadTo(22f, 5.18f, 22f, 6f)
                            verticalLineTo(18f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(20f, 20f)
                            horizontalLineTo(4f)
                            quadTo(3.18f, 20f, 2.59f, 19.41f)
                            reflectiveQuadTo(2f, 18f)
                            verticalLineTo(6f)
                            quadTo(2f, 5.18f, 2.59f, 4.59f)
                            reflectiveQuadTo(4f, 4f)
                            close()
                            moveToRelative(0f, 6f)
                            verticalLineToRelative(8f)
                            horizontalLineTo(20f)
                            verticalLineTo(10f)
                            horizontalLineTo(4f)
                            close()
                            moveToRelative(0f, 0f)
                            verticalLineToRelative(8f)
                            verticalLineTo(10f)
                            close()
                        }
                    }
                    .build()
            return _anime!!
        }

    private var _arrowBack: ImageVector? = null

    @Suppress("CheckReturnValue")
    val ArrowBack: ImageVector
        get() {
            if (_arrowBack != null) {
                return _arrowBack!!
            }
            _arrowBack =
                ImageVector.Builder(
                    name = "_arrow_back",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(7.83f, 13f)
                            lineToRelative(4.9f, 4.9f)
                            quadToRelative(0.3f, 0.3f, 0.29f, 0.7f)
                            reflectiveQuadTo(12.7f, 19.3f)
                            quadTo(12.4f, 19.58f, 12f, 19.59f)
                            reflectiveQuadTo(11.3f, 19.3f)
                            lineTo(4.7f, 12.7f)
                            quadTo(4.55f, 12.55f, 4.49f, 12.38f)
                            reflectiveQuadTo(4.43f, 12f)
                            reflectiveQuadTo(4.49f, 11.63f)
                            reflectiveQuadTo(4.7f, 11.3f)
                            lineTo(11.3f, 4.7f)
                            quadTo(11.58f, 4.42f, 11.99f, 4.42f)
                            reflectiveQuadTo(12.7f, 4.7f)
                            quadTo(13f, 5f, 13f, 5.41f)
                            reflectiveQuadTo(12.7f, 6.13f)
                            lineTo(7.83f, 11f)
                            horizontalLineTo(19f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(20f, 12f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(19f, 13f)
                            horizontalLineTo(7.83f)
                            close()
                        }
                    }
                    .build()
            return _arrowBack!!
        }

    private var _arrowDown: ImageVector? = null

    @Suppress("CheckReturnValue")
    val ArrowDown: ImageVector
        get() {
            if (_arrowDown != null) {
                return _arrowDown!!
            }
            _arrowDown =
                ImageVector.Builder(
                    name = "_arrow_down",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(11f, 16.18f)
                            verticalLineTo(5f)
                            quadTo(11f, 4.57f, 11.29f, 4.29f)
                            reflectiveQuadTo(12f, 4f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(13f, 5f)
                            verticalLineTo(16.18f)
                            lineToRelative(4.9f, -4.9f)
                            quadToRelative(0.3f, -0.3f, 0.7f, -0.29f)
                            reflectiveQuadToRelative(0.7f, 0.31f)
                            quadToRelative(0.28f, 0.3f, 0.29f, 0.7f)
                            reflectiveQuadTo(19.3f, 12.7f)
                            lineToRelative(-6.6f, 6.6f)
                            quadToRelative(-0.15f, 0.15f, -0.33f, 0.21f)
                            reflectiveQuadTo(12f, 19.58f)
                            reflectiveQuadTo(11.63f, 19.51f)
                            reflectiveQuadTo(11.3f, 19.3f)
                            lineTo(4.7f, 12.7f)
                            quadTo(4.43f, 12.43f, 4.43f, 12.01f)
                            reflectiveQuadTo(4.7f, 11.3f)
                            quadTo(5f, 11f, 5.41f, 11f)
                            reflectiveQuadToRelative(0.71f, 0.3f)
                            lineTo(11f, 16.18f)
                            close()
                        }
                    }
                    .build()
            return _arrowDown!!
        }

    private var _arrowForward: ImageVector? = null

    @Suppress("CheckReturnValue")
    val ArrowForward: ImageVector
        get() {
            if (_arrowForward != null) {
                return _arrowForward!!
            }
            _arrowForward =
                ImageVector.Builder(
                    name = "_arrow_forward",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(16.18f, 13f)
                            horizontalLineTo(5f)
                            quadTo(4.58f, 13f, 4.29f, 12.71f)
                            quadTo(4f, 12.43f, 4f, 12f)
                            reflectiveQuadTo(4.29f, 11.29f)
                            reflectiveQuadTo(5f, 11f)
                            horizontalLineTo(16.18f)
                            lineTo(11.28f, 6.1f)
                            quadTo(10.98f, 5.8f, 10.99f, 5.4f)
                            reflectiveQuadTo(11.3f, 4.7f)
                            quadTo(11.6f, 4.42f, 12f, 4.41f)
                            reflectiveQuadTo(12.7f, 4.7f)
                            lineToRelative(6.6f, 6.6f)
                            quadToRelative(0.15f, 0.15f, 0.21f, 0.33f)
                            reflectiveQuadTo(19.58f, 12f)
                            reflectiveQuadToRelative(-0.06f, 0.38f)
                            reflectiveQuadTo(19.3f, 12.7f)
                            lineToRelative(-6.6f, 6.6f)
                            quadToRelative(-0.28f, 0.27f, -0.69f, 0.27f)
                            reflectiveQuadTo(11.3f, 19.3f)
                            quadTo(11f, 19f, 11f, 18.59f)
                            quadToRelative(0f, -0.41f, 0.3f, -0.71f)
                            lineTo(16.18f, 13f)
                            close()
                        }
                    }
                    .build()
            return _arrowForward!!
        }

    private var _arrowUp: ImageVector? = null

    @Suppress("CheckReturnValue")
    val ArrowUp: ImageVector
        get() {
            if (_arrowUp != null) {
                return _arrowUp!!
            }
            _arrowUp =
                ImageVector.Builder(
                    name = "_arrow_up",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(11f, 7.82f)
                            lineToRelative(-4.9f, 4.9f)
                            quadToRelative(-0.3f, 0.3f, -0.7f, 0.29f)
                            reflectiveQuadTo(4.7f, 12.7f)
                            quadTo(4.43f, 12.4f, 4.41f, 12f)
                            reflectiveQuadTo(4.7f, 11.3f)
                            lineTo(11.3f, 4.7f)
                            quadTo(11.45f, 4.55f, 11.63f, 4.49f)
                            reflectiveQuadTo(12f, 4.42f)
                            reflectiveQuadToRelative(0.38f, 0.06f)
                            reflectiveQuadTo(12.7f, 4.7f)
                            lineToRelative(6.6f, 6.6f)
                            quadToRelative(0.28f, 0.28f, 0.28f, 0.69f)
                            reflectiveQuadTo(19.3f, 12.7f)
                            quadTo(19f, 13f, 18.59f, 13f)
                            reflectiveQuadTo(17.88f, 12.7f)
                            lineTo(13f, 7.82f)
                            verticalLineTo(19f)
                            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                            reflectiveQuadTo(12f, 20f)
                            reflectiveQuadTo(11.29f, 19.71f)
                            quadTo(11f, 19.43f, 11f, 19f)
                            verticalLineTo(7.82f)
                            close()
                        }
                    }
                    .build()
            return _arrowUp!!
        }

    private var _smileBad: ImageVector? = null

    @Suppress("CheckReturnValue")
    val SmileBad: ImageVector
        get() {
            if (_smileBad != null) {
                return _smileBad!!
            }
            _smileBad =
                ImageVector.Builder(
                    name = "_offline_pin_off",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(9f, 17f)
                            quadTo(8.58f, 17f, 8.29f, 16.71f)
                            quadTo(8f, 16.43f, 8f, 16f)
                            reflectiveQuadTo(8.29f, 15.29f)
                            quadTo(8.58f, 15f, 9f, 15f)
                            horizontalLineToRelative(6f)
                            lineToRelative(2f, 2f)
                            horizontalLineTo(9f)
                            close()
                            moveToRelative(3f, 5f)
                            quadTo(9.93f, 22f, 8.1f, 21.21f)
                            quadTo(6.28f, 20.43f, 4.93f, 19.08f)
                            quadTo(3.58f, 17.73f, 2.79f, 15.9f)
                            reflectiveQuadTo(2f, 12f)
                            quadTo(2f, 10.52f, 2.43f, 9.13f)
                            quadTo(2.85f, 7.72f, 3.65f, 6.5f)
                            lineTo(2.08f, 4.93f)
                            quadTo(1.78f, 4.63f, 1.78f, 4.21f)
                            reflectiveQuadTo(2.08f, 3.5f)
                            reflectiveQuadTo(2.79f, 3.2f)
                            reflectiveQuadTo(3.5f, 3.5f)
                            lineToRelative(17f, 17f)
                            quadToRelative(0.3f, 0.3f, 0.3f, 0.7f)
                            reflectiveQuadToRelative(-0.3f, 0.7f)
                            reflectiveQuadToRelative(-0.71f, 0.3f)
                            reflectiveQuadTo(19.08f, 21.9f)
                            lineTo(17.5f, 20.35f)
                            quadToRelative(-1.22f, 0.8f, -2.63f, 1.22f)
                            quadTo(13.48f, 22f, 12f, 22f)
                            close()
                            moveToRelative(4.05f, -3.1f)
                            lineTo(5.1f, 7.95f)
                            quadTo(4.55f, 8.88f, 4.28f, 9.9f)
                            quadTo(4f, 10.93f, 4f, 12f)
                            quadToRelative(0f, 3.35f, 2.33f, 5.68f)
                            reflectiveQuadTo(12f, 20f)
                            quadToRelative(1.08f, 0f, 2.1f, -0.27f)
                            reflectiveQuadTo(16.05f, 18.9f)
                            close()
                            moveTo(13.98f, 10.02f)
                            close()
                            moveToRelative(-3.95f, 3.95f)
                            close()
                            moveTo(16.18f, 8.35f)
                            quadToRelative(0f, 0.42f, -0.3f, 0.72f)
                            lineTo(14.6f, 10.35f)
                            quadToRelative(-0.28f, 0.28f, -0.7f, 0.28f)
                            reflectiveQuadTo(13.2f, 10.35f)
                            quadTo(12.93f, 10.07f, 12.93f, 9.65f)
                            quadToRelative(0f, -0.42f, 0.28f, -0.7f)
                            lineToRelative(1.3f, -1.3f)
                            quadTo(14.78f, 7.38f, 15.18f, 7.38f)
                            reflectiveQuadToRelative(0.7f, 0.27f)
                            quadToRelative(0.3f, 0.28f, 0.3f, 0.7f)
                            close()
                            moveTo(7.85f, 2.9f)
                            quadTo(8.83f, 2.45f, 9.86f, 2.22f)
                            reflectiveQuadTo(12f, 2f)
                            quadToRelative(2.08f, 0f, 3.9f, 0.79f)
                            reflectiveQuadToRelative(3.17f, 2.14f)
                            quadToRelative(1.35f, 1.35f, 2.14f, 3.17f)
                            quadTo(22f, 9.92f, 22f, 12f)
                            quadToRelative(0f, 1.1f, -0.22f, 2.14f)
                            reflectiveQuadTo(21.1f, 16.15f)
                            quadToRelative(-0.18f, 0.38f, -0.57f, 0.49f)
                            reflectiveQuadTo(19.78f, 16.55f)
                            reflectiveQuadTo(19.3f, 15.95f)
                            reflectiveQuadToRelative(0.05f, -0.8f)
                            quadTo(19.68f, 14.4f, 19.84f, 13.6f)
                            reflectiveQuadTo(20f, 12f)
                            quadTo(20f, 8.65f, 17.68f, 6.32f)
                            reflectiveQuadTo(12f, 4f)
                            quadTo(11.2f, 4f, 10.4f, 4.16f)
                            reflectiveQuadTo(8.85f, 4.65f)
                            quadTo(8.45f, 4.82f, 8.05f, 4.7f)
                            reflectiveQuadTo(7.45f, 4.22f)
                            quadTo(7.25f, 3.88f, 7.36f, 3.47f)
                            reflectiveQuadTo(7.85f, 2.9f)
                            close()
                        }
                    }
                    .build()
            return _smileBad!!
        }

    private var _bookmark: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Bookmark: ImageVector
        get() {
            if (_bookmark != null) {
                return _bookmark!!
            }
            _bookmark =
                ImageVector.Builder(
                    name = "_bookmark",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(12f, 18f)
                            lineTo(7.8f, 19.8f)
                            quadToRelative(-1f, 0.43f, -1.9f, -0.16f)
                            reflectiveQuadTo(5f, 17.98f)
                            verticalLineTo(5f)
                            quadTo(5f, 4.17f, 5.59f, 3.59f)
                            reflectiveQuadTo(7f, 3f)
                            horizontalLineTo(17f)
                            quadToRelative(0.82f, 0f, 1.41f, 0.59f)
                            reflectiveQuadTo(19f, 5f)
                            verticalLineTo(17.98f)
                            quadToRelative(0f, 1.07f, -0.9f, 1.66f)
                            quadToRelative(-0.9f, 0.59f, -1.9f, 0.16f)
                            lineTo(12f, 18f)
                            close()
                            moveToRelative(0f, -2.2f)
                            lineToRelative(5f, 2.15f)
                            verticalLineTo(5f)
                            horizontalLineTo(7f)
                            verticalLineTo(17.95f)
                            lineTo(12f, 15.8f)
                            close()
                            moveTo(12f, 5f)
                            horizontalLineTo(7f)
                            horizontalLineTo(17f)
                            horizontalLineTo(12f)
                            close()
                        }
                    }
                    .build()
            return _bookmark!!
        }

    private var _calendar: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Calendar: ImageVector
        get() {
            if (_calendar != null) {
                return _calendar!!
            }
            _calendar =
                ImageVector.Builder(
                    name = "_calendar",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(7.29f, 13.71f)
                            quadTo(7f, 13.43f, 7f, 13f)
                            reflectiveQuadTo(7.29f, 12.29f)
                            reflectiveQuadTo(8f, 12f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(9f, 13f)
                            reflectiveQuadTo(8.71f, 13.71f)
                            reflectiveQuadTo(8f, 14f)
                            quadTo(7.58f, 14f, 7.29f, 13.71f)
                            close()
                            moveToRelative(4f, 0f)
                            quadTo(11f, 13.43f, 11f, 13f)
                            reflectiveQuadToRelative(0.29f, -0.71f)
                            reflectiveQuadTo(12f, 12f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(13f, 13f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(12f, 14f)
                            reflectiveQuadTo(11.29f, 13.71f)
                            close()
                            moveToRelative(4f, 0f)
                            quadTo(15f, 13.43f, 15f, 13f)
                            reflectiveQuadToRelative(0.29f, -0.71f)
                            reflectiveQuadTo(16f, 12f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(17f, 13f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(16f, 14f)
                            reflectiveQuadTo(15.29f, 13.71f)
                            close()
                            moveTo(5f, 22f)
                            quadTo(4.18f, 22f, 3.59f, 21.41f)
                            reflectiveQuadTo(3f, 20f)
                            verticalLineTo(6f)
                            quadTo(3f, 5.18f, 3.59f, 4.59f)
                            reflectiveQuadTo(5f, 4f)
                            horizontalLineTo(6f)
                            verticalLineTo(3f)
                            quadTo(6f, 2.57f, 6.29f, 2.29f)
                            reflectiveQuadTo(7f, 2f)
                            reflectiveQuadTo(7.71f, 2.29f)
                            reflectiveQuadTo(8f, 3f)
                            verticalLineTo(4f)
                            horizontalLineToRelative(8f)
                            verticalLineTo(3f)
                            quadTo(16f, 2.57f, 16.29f, 2.29f)
                            reflectiveQuadTo(17f, 2f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(18f, 3f)
                            verticalLineTo(4f)
                            horizontalLineToRelative(1f)
                            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                            quadTo(21f, 5.18f, 21f, 6f)
                            verticalLineTo(20f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(19f, 22f)
                            horizontalLineTo(5f)
                            close()
                            moveTo(5f, 20f)
                            horizontalLineTo(19f)
                            verticalLineTo(10f)
                            horizontalLineTo(5f)
                            verticalLineTo(20f)
                            close()
                            moveTo(5f, 8f)
                            horizontalLineTo(19f)
                            verticalLineTo(6f)
                            horizontalLineTo(5f)
                            verticalLineTo(8f)
                            close()
                            moveTo(5f, 8f)
                            verticalLineTo(6f)
                            verticalLineTo(8f)
                            close()
                        }
                    }
                    .build()
            return _calendar!!
        }

    private var _character: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Character: ImageVector
        get() {
            if (_character != null) {
                return _character!!
            }
            _character =
                ImageVector.Builder(
                    name = "face",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(8.11f, 13.89f)
                            quadTo(7.75f, 13.52f, 7.75f, 13f)
                            reflectiveQuadTo(8.11f, 12.11f)
                            reflectiveQuadTo(9f, 11.75f)
                            reflectiveQuadToRelative(0.89f, 0.36f)
                            quadToRelative(0.36f, 0.36f, 0.36f, 0.89f)
                            quadToRelative(0f, 0.52f, -0.36f, 0.89f)
                            reflectiveQuadTo(9f, 14.25f)
                            reflectiveQuadTo(8.11f, 13.89f)
                            close()
                            moveToRelative(6f, 0f)
                            quadTo(13.75f, 13.52f, 13.75f, 13f)
                            reflectiveQuadToRelative(0.36f, -0.89f)
                            reflectiveQuadTo(15f, 11.75f)
                            quadToRelative(0.53f, 0f, 0.89f, 0.36f)
                            quadToRelative(0.36f, 0.36f, 0.36f, 0.89f)
                            quadToRelative(0f, 0.52f, -0.36f, 0.89f)
                            reflectiveQuadTo(15f, 14.25f)
                            reflectiveQuadTo(14.11f, 13.89f)
                            close()
                            moveTo(12f, 20f)
                            quadToRelative(3.35f, 0f, 5.68f, -2.32f)
                            reflectiveQuadTo(20f, 12f)
                            quadToRelative(0f, -0.6f, -0.07f, -1.16f)
                            reflectiveQuadTo(19.65f, 9.75f)
                            quadTo(19.13f, 9.88f, 18.6f, 9.94f)
                            reflectiveQuadTo(17.5f, 10f)
                            quadTo(15.23f, 10f, 13.2f, 9.02f)
                            reflectiveQuadTo(9.75f, 6.3f)
                            quadTo(8.95f, 8.25f, 7.46f, 9.69f)
                            reflectiveQuadTo(4f, 11.85f)
                            quadToRelative(0f, 0.05f, 0f, 0.07f)
                            reflectiveQuadTo(4f, 12f)
                            quadToRelative(0f, 3.35f, 2.33f, 5.68f)
                            reflectiveQuadTo(12f, 20f)
                            close()
                            moveToRelative(0f, 2f)
                            quadTo(9.93f, 22f, 8.1f, 21.21f)
                            quadTo(6.28f, 20.43f, 4.93f, 19.08f)
                            quadTo(3.58f, 17.73f, 2.79f, 15.9f)
                            reflectiveQuadTo(2f, 12f)
                            quadTo(2f, 9.92f, 2.79f, 8.1f)
                            quadTo(3.58f, 6.27f, 4.93f, 4.93f)
                            quadTo(6.28f, 3.57f, 8.1f, 2.79f)
                            quadTo(9.93f, 2f, 12f, 2f)
                            reflectiveQuadToRelative(3.9f, 0.79f)
                            reflectiveQuadToRelative(3.17f, 2.14f)
                            quadToRelative(1.35f, 1.35f, 2.14f, 3.17f)
                            quadTo(22f, 9.92f, 22f, 12f)
                            reflectiveQuadToRelative(-0.79f, 3.9f)
                            reflectiveQuadToRelative(-2.14f, 3.17f)
                            quadToRelative(-1.35f, 1.35f, -3.17f, 2.14f)
                            reflectiveQuadTo(12f, 22f)
                            close()
                            moveTo(10.65f, 4.13f)
                            quadTo(11.7f, 5.88f, 13.5f, 6.94f)
                            reflectiveQuadTo(17.5f, 8f)
                            quadToRelative(0.35f, 0f, 0.68f, -0.04f)
                            quadTo(18.5f, 7.93f, 18.85f, 7.88f)
                            quadTo(17.8f, 6.13f, 16f, 5.06f)
                            reflectiveQuadTo(12f, 4f)
                            quadTo(11.65f, 4f, 11.33f, 4.04f)
                            reflectiveQuadTo(10.65f, 4.13f)
                            close()
                            moveTo(4.43f, 9.48f)
                            quadTo(5.7f, 8.75f, 6.65f, 7.6f)
                            reflectiveQuadTo(8.08f, 5.02f)
                            quadTo(6.8f, 5.75f, 5.85f, 6.9f)
                            reflectiveQuadTo(4.43f, 9.48f)
                            close()
                            moveTo(10.65f, 4.13f)
                            close()
                            moveTo(8.08f, 5.02f)
                            close()
                        }
                    }
                    .build()
            return _character!!
        }

    private var _check: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Check: ImageVector
        get() {
            if (_check != null) {
                return _check!!
            }
            _check =
                ImageVector.Builder(
                    name = "_check",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(9.55f, 15.15f)
                            lineTo(18.03f, 6.68f)
                            quadToRelative(0.3f, -0.3f, 0.7f, -0.3f)
                            reflectiveQuadToRelative(0.7f, 0.3f)
                            quadToRelative(0.3f, 0.3f, 0.3f, 0.71f)
                            reflectiveQuadTo(19.43f, 8.1f)
                            lineToRelative(-9.18f, 9.2f)
                            quadToRelative(-0.3f, 0.3f, -0.7f, 0.3f)
                            reflectiveQuadTo(8.85f, 17.3f)
                            lineTo(4.55f, 13f)
                            quadTo(4.25f, 12.7f, 4.26f, 12.29f)
                            reflectiveQuadTo(4.58f, 11.58f)
                            reflectiveQuadToRelative(0.71f, -0.3f)
                            reflectiveQuadTo(6f, 11.58f)
                            lineToRelative(3.55f, 3.58f)
                            close()
                        }
                    }
                    .build()
            return _check!!
        }

    private var _close: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Close: ImageVector
        get() {
            if (_close != null) {
                return _close!!
            }
            _close =
                ImageVector.Builder(
                    name = "_close",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(12f, 13.4f)
                            lineTo(7.1f, 18.3f)
                            quadTo(6.83f, 18.58f, 6.4f, 18.58f)
                            reflectiveQuadTo(5.7f, 18.3f)
                            quadTo(5.43f, 18.02f, 5.43f, 17.6f)
                            reflectiveQuadTo(5.7f, 16.9f)
                            lineTo(10.6f, 12f)
                            lineTo(5.7f, 7.1f)
                            quadTo(5.43f, 6.82f, 5.43f, 6.4f)
                            reflectiveQuadTo(5.7f, 5.7f)
                            reflectiveQuadTo(6.4f, 5.43f)
                            reflectiveQuadTo(7.1f, 5.7f)
                            lineTo(12f, 10.6f)
                            lineTo(16.9f, 5.7f)
                            quadTo(17.18f, 5.43f, 17.6f, 5.43f)
                            reflectiveQuadTo(18.3f, 5.7f)
                            reflectiveQuadToRelative(0.27f, 0.7f)
                            reflectiveQuadTo(18.3f, 7.1f)
                            lineTo(13.4f, 12f)
                            lineToRelative(4.9f, 4.9f)
                            quadToRelative(0.27f, 0.28f, 0.27f, 0.7f)
                            quadToRelative(0f, 0.42f, -0.27f, 0.7f)
                            reflectiveQuadToRelative(-0.7f, 0.27f)
                            reflectiveQuadTo(16.9f, 18.3f)
                            lineTo(12f, 13.4f)
                            close()
                        }
                    }
                    .build()
            return _close!!
        }

    private var _clubs: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Clubs: ImageVector
        get() {
            if (_clubs != null) {
                return _clubs!!
            }
            _clubs =
                ImageVector.Builder(
                    name = "_clubs",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(1f, 18f)
                            quadTo(0.58f, 18f, 0.29f, 17.71f)
                            quadTo(0f, 17.43f, 0f, 17f)
                            verticalLineTo(16.43f)
                            quadTo(0f, 15.35f, 1.1f, 14.68f)
                            reflectiveQuadTo(4f, 14f)
                            quadToRelative(0.33f, 0f, 0.63f, 0.01f)
                            reflectiveQuadTo(5.2f, 14.08f)
                            quadTo(4.85f, 14.6f, 4.68f, 15.18f)
                            reflectiveQuadTo(4.5f, 16.38f)
                            verticalLineTo(18f)
                            horizontalLineTo(1f)
                            close()
                            moveToRelative(6f, 0f)
                            quadTo(6.58f, 18f, 6.29f, 17.71f)
                            quadTo(6f, 17.43f, 6f, 17f)
                            verticalLineTo(16.38f)
                            quadToRelative(0f, -0.8f, 0.44f, -1.46f)
                            reflectiveQuadTo(7.68f, 13.75f)
                            reflectiveQuadTo(9.59f, 13f)
                            reflectiveQuadTo(12f, 12.75f)
                            quadToRelative(1.33f, 0f, 2.44f, 0.25f)
                            reflectiveQuadToRelative(1.91f, 0.75f)
                            reflectiveQuadToRelative(1.22f, 1.16f)
                            reflectiveQuadTo(18f, 16.38f)
                            verticalLineTo(17f)
                            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                            reflectiveQuadTo(17f, 18f)
                            horizontalLineTo(7f)
                            close()
                            moveToRelative(12.5f, 0f)
                            verticalLineTo(16.38f)
                            quadToRelative(0f, -0.65f, -0.16f, -1.22f)
                            reflectiveQuadTo(18.85f, 14.08f)
                            quadToRelative(0.27f, -0.05f, 0.56f, -0.06f)
                            reflectiveQuadTo(20f, 14f)
                            quadToRelative(1.8f, 0f, 2.9f, 0.66f)
                            reflectiveQuadTo(24f, 16.43f)
                            verticalLineTo(17f)
                            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                            reflectiveQuadTo(23f, 18f)
                            horizontalLineTo(19.5f)
                            close()
                            moveTo(8.13f, 16f)
                            horizontalLineTo(15.9f)
                            quadTo(15.65f, 15.5f, 14.51f, 15.13f)
                            reflectiveQuadTo(12f, 14.75f)
                            reflectiveQuadTo(9.49f, 15.13f)
                            reflectiveQuadTo(8.13f, 16f)
                            close()
                            moveTo(4f, 13f)
                            quadTo(3.18f, 13f, 2.59f, 12.41f)
                            reflectiveQuadTo(2f, 11f)
                            quadTo(2f, 10.15f, 2.59f, 9.57f)
                            reflectiveQuadTo(4f, 9f)
                            quadTo(4.85f, 9f, 5.43f, 9.57f)
                            reflectiveQuadTo(6f, 11f)
                            quadToRelative(0f, 0.82f, -0.57f, 1.41f)
                            reflectiveQuadTo(4f, 13f)
                            close()
                            moveToRelative(16f, 0f)
                            quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
                            reflectiveQuadTo(18f, 11f)
                            quadToRelative(0f, -0.85f, 0.59f, -1.43f)
                            reflectiveQuadTo(20f, 9f)
                            quadToRelative(0.85f, 0f, 1.43f, 0.57f)
                            reflectiveQuadTo(22f, 11f)
                            quadToRelative(0f, 0.82f, -0.57f, 1.41f)
                            reflectiveQuadTo(20f, 13f)
                            close()
                            moveTo(12f, 12f)
                            quadTo(10.75f, 12f, 9.88f, 11.13f)
                            reflectiveQuadTo(9f, 9f)
                            quadTo(9f, 7.72f, 9.88f, 6.86f)
                            reflectiveQuadTo(12f, 6f)
                            quadToRelative(1.28f, 0f, 2.14f, 0.86f)
                            quadTo(15f, 7.72f, 15f, 9f)
                            quadToRelative(0f, 1.25f, -0.86f, 2.13f)
                            reflectiveQuadTo(12f, 12f)
                            close()
                            moveToRelative(0f, -2f)
                            quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                            reflectiveQuadTo(13f, 9f)
                            quadTo(13f, 8.57f, 12.71f, 8.29f)
                            reflectiveQuadTo(12f, 8f)
                            reflectiveQuadTo(11.29f, 8.29f)
                            reflectiveQuadTo(11f, 9f)
                            quadToRelative(0f, 0.42f, 0.29f, 0.71f)
                            reflectiveQuadTo(12f, 10f)
                            close()
                            moveToRelative(0.03f, 6f)
                            close()
                            moveTo(12f, 9f)
                            close()
                        }
                    }
                    .build()
            return _clubs!!
        }

    private var _comments: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Comments: ImageVector
        get() {
            if (_comments != null) {
                return _comments!!
            }
            _comments =
                ImageVector.Builder(
                    name = "_comments",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(7f, 14f)
                            horizontalLineTo(17f)
                            quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                            quadTo(18f, 13.43f, 18f, 13f)
                            reflectiveQuadTo(17.71f, 12.29f)
                            reflectiveQuadTo(17f, 12f)
                            horizontalLineTo(7f)
                            quadTo(6.58f, 12f, 6.29f, 12.29f)
                            reflectiveQuadTo(6f, 13f)
                            reflectiveQuadToRelative(0.29f, 0.71f)
                            reflectiveQuadTo(7f, 14f)
                            close()
                            moveTo(7f, 11f)
                            horizontalLineTo(17f)
                            quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                            quadTo(18f, 10.43f, 18f, 10f)
                            quadTo(18f, 9.57f, 17.71f, 9.29f)
                            reflectiveQuadTo(17f, 9f)
                            horizontalLineTo(7f)
                            quadTo(6.58f, 9f, 6.29f, 9.29f)
                            reflectiveQuadTo(6f, 10f)
                            reflectiveQuadToRelative(0.29f, 0.71f)
                            reflectiveQuadTo(7f, 11f)
                            close()
                            moveTo(7f, 8f)
                            horizontalLineTo(17f)
                            quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                            quadTo(18f, 7.43f, 18f, 7f)
                            reflectiveQuadTo(17.71f, 6.29f)
                            reflectiveQuadTo(17f, 6f)
                            horizontalLineTo(7f)
                            quadTo(6.58f, 6f, 6.29f, 6.29f)
                            reflectiveQuadTo(6f, 7f)
                            reflectiveQuadTo(6.29f, 7.71f)
                            reflectiveQuadTo(7f, 8f)
                            close()
                            moveTo(4f, 18f)
                            quadTo(3.18f, 18f, 2.59f, 17.41f)
                            reflectiveQuadTo(2f, 16f)
                            verticalLineTo(4f)
                            quadTo(2f, 3.17f, 2.59f, 2.59f)
                            reflectiveQuadTo(4f, 2f)
                            horizontalLineTo(20f)
                            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                            reflectiveQuadTo(22f, 4f)
                            verticalLineTo(19.58f)
                            quadToRelative(0f, 0.68f, -0.61f, 0.94f)
                            reflectiveQuadTo(20.3f, 20.3f)
                            lineTo(18f, 18f)
                            horizontalLineTo(4f)
                            close()
                            moveTo(18.85f, 16f)
                            lineTo(20f, 17.13f)
                            verticalLineTo(4f)
                            horizontalLineTo(4f)
                            verticalLineTo(16f)
                            horizontalLineTo(18.85f)
                            close()
                            moveTo(4f, 16f)
                            verticalLineTo(4f)
                            verticalLineTo(16f)
                            close()
                        }
                    }
                    .build()
            return _comments!!
        }

    private var _compass: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Compass: ImageVector
        get() {
            if (_compass != null) {
                return _compass!!
            }
            _compass =
                ImageVector.Builder(
                    name = "_explore",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(8.38f, 16.25f)
                            lineTo(13.43f, 14.8f)
                            quadToRelative(0.5f, -0.15f, 0.86f, -0.51f)
                            reflectiveQuadTo(14.8f, 13.43f)
                            lineTo(16.25f, 8.38f)
                            quadTo(16.33f, 8.1f, 16.11f, 7.89f)
                            quadTo(15.9f, 7.68f, 15.63f, 7.75f)
                            lineTo(10.58f, 9.2f)
                            quadTo(10.08f, 9.35f, 9.71f, 9.71f)
                            reflectiveQuadTo(9.2f, 10.58f)
                            lineTo(7.75f, 15.63f)
                            quadTo(7.68f, 15.9f, 7.89f, 16.11f)
                            reflectiveQuadToRelative(0.49f, 0.14f)
                            close()
                            moveTo(12f, 13.5f)
                            quadToRelative(-0.63f, 0f, -1.06f, -0.44f)
                            reflectiveQuadTo(10.5f, 12f)
                            reflectiveQuadToRelative(0.44f, -1.06f)
                            reflectiveQuadTo(12f, 10.5f)
                            reflectiveQuadToRelative(1.06f, 0.44f)
                            reflectiveQuadTo(13.5f, 12f)
                            reflectiveQuadToRelative(-0.44f, 1.06f)
                            reflectiveQuadTo(12f, 13.5f)
                            close()
                            moveTo(12f, 22f)
                            quadTo(9.93f, 22f, 8.1f, 21.21f)
                            quadTo(6.28f, 20.43f, 4.93f, 19.08f)
                            quadTo(3.58f, 17.73f, 2.79f, 15.9f)
                            reflectiveQuadTo(2f, 12f)
                            quadTo(2f, 9.92f, 2.79f, 8.1f)
                            quadTo(3.58f, 6.27f, 4.93f, 4.93f)
                            quadTo(6.28f, 3.57f, 8.1f, 2.79f)
                            quadTo(9.93f, 2f, 12f, 2f)
                            reflectiveQuadToRelative(3.9f, 0.79f)
                            reflectiveQuadToRelative(3.17f, 2.14f)
                            quadToRelative(1.35f, 1.35f, 2.14f, 3.17f)
                            quadTo(22f, 9.92f, 22f, 12f)
                            reflectiveQuadToRelative(-0.79f, 3.9f)
                            reflectiveQuadToRelative(-2.14f, 3.17f)
                            quadToRelative(-1.35f, 1.35f, -3.17f, 2.14f)
                            reflectiveQuadTo(12f, 22f)
                            close()
                            moveToRelative(0f, -2f)
                            quadToRelative(3.33f, 0f, 5.66f, -2.34f)
                            reflectiveQuadTo(20f, 12f)
                            quadTo(20f, 8.67f, 17.66f, 6.34f)
                            reflectiveQuadTo(12f, 4f)
                            quadTo(8.68f, 4f, 6.34f, 6.34f)
                            reflectiveQuadTo(4f, 12f)
                            reflectiveQuadToRelative(2.34f, 5.66f)
                            reflectiveQuadTo(12f, 20f)
                            close()
                            moveToRelative(0f, -8f)
                            close()
                        }
                    }
                    .build()
            return _compass!!
        }

    private var _copy: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Copy: ImageVector
        get() {
            if (_copy != null) {
                return _copy!!
            }
            _copy =
                ImageVector.Builder(
                    name = "_content_copy",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(9f, 18f)
                            quadTo(8.18f, 18f, 7.59f, 17.41f)
                            reflectiveQuadTo(7f, 16f)
                            verticalLineTo(4f)
                            quadTo(7f, 3.17f, 7.59f, 2.59f)
                            reflectiveQuadTo(9f, 2f)
                            horizontalLineToRelative(9f)
                            quadToRelative(0.82f, 0f, 1.41f, 0.59f)
                            reflectiveQuadTo(20f, 4f)
                            verticalLineTo(16f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(18f, 18f)
                            horizontalLineTo(9f)
                            close()
                            moveTo(9f, 16f)
                            horizontalLineToRelative(9f)
                            verticalLineTo(4f)
                            horizontalLineTo(9f)
                            verticalLineTo(16f)
                            close()
                            moveTo(5f, 22f)
                            quadTo(4.18f, 22f, 3.59f, 21.41f)
                            reflectiveQuadTo(3f, 20f)
                            verticalLineTo(7f)
                            quadTo(3f, 6.57f, 3.29f, 6.29f)
                            reflectiveQuadTo(4f, 6f)
                            reflectiveQuadTo(4.71f, 6.29f)
                            reflectiveQuadTo(5f, 7f)
                            verticalLineTo(20f)
                            horizontalLineTo(15f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(16f, 21f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(15f, 22f)
                            horizontalLineTo(5f)
                            close()
                            moveTo(9f, 16f)
                            verticalLineTo(4f)
                            verticalLineTo(16f)
                            close()
                        }
                    }
                    .build()
            return _copy!!
        }

    private var _download: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Download: ImageVector
        get() {
            if (_download != null) {
                return _download!!
            }
            _download =
                ImageVector.Builder(
                    name = "_download",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(11f, 12.15f)
                            verticalLineTo(6.1f)
                            quadTo(9.1f, 6.45f, 8.05f, 7.94f)
                            quadTo(7f, 9.42f, 7f, 11f)
                            horizontalLineTo(6.5f)
                            quadTo(5.05f, 11f, 4.03f, 12.02f)
                            reflectiveQuadTo(3f, 14.5f)
                            reflectiveQuadToRelative(1.03f, 2.48f)
                            reflectiveQuadTo(6.5f, 18f)
                            horizontalLineToRelative(12f)
                            quadToRelative(1.05f, 0f, 1.78f, -0.73f)
                            reflectiveQuadTo(21f, 15.5f)
                            reflectiveQuadTo(20.28f, 13.73f)
                            reflectiveQuadTo(18.5f, 13f)
                            horizontalLineTo(17f)
                            verticalLineTo(11f)
                            quadTo(17f, 9.8f, 16.45f, 8.76f)
                            quadTo(15.9f, 7.72f, 15f, 7f)
                            verticalLineTo(4.67f)
                            quadToRelative(1.85f, 0.88f, 2.93f, 2.59f)
                            quadTo(19f, 8.98f, 19f, 11f)
                            quadToRelative(1.73f, 0.2f, 2.86f, 1.49f)
                            reflectiveQuadTo(23f, 15.5f)
                            quadToRelative(0f, 1.88f, -1.31f, 3.19f)
                            reflectiveQuadTo(18.5f, 20f)
                            horizontalLineTo(6.5f)
                            quadTo(4.23f, 20f, 2.61f, 18.43f)
                            reflectiveQuadTo(1f, 14.58f)
                            quadTo(1f, 12.63f, 2.18f, 11.1f)
                            reflectiveQuadTo(5.25f, 9.15f)
                            quadTo(5.68f, 7.35f, 7.38f, 5.72f)
                            quadTo(9.08f, 4.1f, 11f, 4.1f)
                            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                            quadTo(13f, 5.27f, 13f, 6.1f)
                            verticalLineToRelative(6.05f)
                            lineToRelative(0.9f, -0.88f)
                            quadTo(14.18f, 11f, 14.59f, 11f)
                            reflectiveQuadToRelative(0.71f, 0.3f)
                            quadToRelative(0.28f, 0.28f, 0.28f, 0.7f)
                            reflectiveQuadTo(15.3f, 12.7f)
                            lineToRelative(-2.6f, 2.6f)
                            quadTo(12.4f, 15.6f, 12f, 15.6f)
                            reflectiveQuadTo(11.3f, 15.3f)
                            lineTo(8.7f, 12.7f)
                            quadTo(8.43f, 12.43f, 8.41f, 12.01f)
                            reflectiveQuadTo(8.7f, 11.3f)
                            quadTo(8.98f, 11.02f, 9.39f, 11.01f)
                            reflectiveQuadToRelative(0.71f, 0.26f)
                            lineTo(11f, 12.15f)
                            close()
                            moveToRelative(1f, -1.1f)
                            close()
                        }
                    }
                    .build()
            return _download!!
        }

    private var _edit: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Edit: ImageVector
        get() {
            if (_edit != null) {
                return _edit!!
            }
            _edit =
                ImageVector.Builder(
                    name = "_edit",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(5f, 19f)
                            horizontalLineTo(6.43f)
                            lineTo(16.2f, 9.23f)
                            lineTo(14.78f, 7.8f)
                            lineTo(5f, 17.58f)
                            verticalLineTo(19f)
                            close()
                            moveTo(4f, 21f)
                            quadTo(3.58f, 21f, 3.29f, 20.71f)
                            quadTo(3f, 20.43f, 3f, 20f)
                            verticalLineTo(17.58f)
                            quadToRelative(0f, -0.4f, 0.15f, -0.76f)
                            reflectiveQuadTo(3.58f, 16.18f)
                            lineTo(16.2f, 3.57f)
                            quadTo(16.5f, 3.3f, 16.86f, 3.15f)
                            reflectiveQuadTo(17.63f, 3f)
                            quadToRelative(0.4f, 0f, 0.78f, 0.15f)
                            reflectiveQuadTo(19.05f, 3.6f)
                            lineTo(20.43f, 5f)
                            quadToRelative(0.3f, 0.27f, 0.44f, 0.65f)
                            reflectiveQuadTo(21f, 6.4f)
                            quadToRelative(0f, 0.4f, -0.14f, 0.76f)
                            reflectiveQuadTo(20.43f, 7.82f)
                            lineTo(7.83f, 20.43f)
                            quadTo(7.55f, 20.7f, 7.19f, 20.85f)
                            quadTo(6.83f, 21f, 6.43f, 21f)
                            horizontalLineTo(4f)
                            close()
                            moveTo(19f, 6.4f)
                            lineTo(17.6f, 5f)
                            lineTo(19f, 6.4f)
                            close()
                            moveTo(15.48f, 8.52f)
                            lineTo(14.78f, 7.8f)
                            lineTo(16.2f, 9.23f)
                            lineTo(15.48f, 8.52f)
                            close()
                        }
                    }
                    .build()
            return _edit!!
        }

    private var _episodePlay: ImageVector? = null

    @Suppress("CheckReturnValue")
    val EpisodePlay: ImageVector
        get() {
            if (_episodePlay != null) {
                return _episodePlay!!
            }
            _episodePlay =
                ImageVector.Builder(
                    name = "_live_tv",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(11.05f, 14.5f)
                            lineTo(15.2f, 11.85f)
                            quadTo(15.65f, 11.55f, 15.65f, 11f)
                            reflectiveQuadTo(15.2f, 10.15f)
                            lineTo(11.05f, 7.5f)
                            quadTo(10.55f, 7.18f, 10.03f, 7.45f)
                            reflectiveQuadTo(9.5f, 8.32f)
                            verticalLineToRelative(5.35f)
                            quadToRelative(0f, 0.6f, 0.53f, 0.88f)
                            reflectiveQuadTo(11.05f, 14.5f)
                            close()
                            moveTo(4f, 19f)
                            quadTo(3.18f, 19f, 2.59f, 18.41f)
                            reflectiveQuadTo(2f, 17f)
                            verticalLineTo(5f)
                            quadTo(2f, 4.17f, 2.59f, 3.59f)
                            reflectiveQuadTo(4f, 3f)
                            horizontalLineTo(20f)
                            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                            reflectiveQuadTo(22f, 5f)
                            verticalLineTo(17f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(20f, 19f)
                            horizontalLineTo(16f)
                            verticalLineToRelative(1f)
                            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                            reflectiveQuadTo(15f, 21f)
                            horizontalLineTo(9f)
                            quadTo(8.58f, 21f, 8.29f, 20.71f)
                            quadTo(8f, 20.43f, 8f, 20f)
                            verticalLineTo(19f)
                            horizontalLineTo(4f)
                            close()
                            moveTo(4f, 17f)
                            horizontalLineTo(20f)
                            verticalLineTo(5f)
                            horizontalLineTo(4f)
                            verticalLineTo(17f)
                            close()
                            moveToRelative(0f, 0f)
                            verticalLineTo(5f)
                            verticalLineTo(17f)
                            close()
                        }
                    }
                    .build()
            return _episodePlay!!
        }

    private var _exitApp: ImageVector? = null

    @Suppress("CheckReturnValue")
    val ExitApp: ImageVector
        get() {
            if (_exitApp != null) {
                return _exitApp!!
            }
            _exitApp =
                ImageVector.Builder(
                    name = "_exit_to_app",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(5f, 21f)
                            quadTo(4.18f, 21f, 3.59f, 20.41f)
                            reflectiveQuadTo(3f, 19f)
                            verticalLineTo(16f)
                            quadTo(3f, 15.58f, 3.29f, 15.29f)
                            reflectiveQuadTo(4f, 15f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(5f, 16f)
                            verticalLineToRelative(3f)
                            horizontalLineTo(19f)
                            verticalLineTo(5f)
                            horizontalLineTo(5f)
                            verticalLineTo(8f)
                            quadTo(5f, 8.42f, 4.71f, 8.71f)
                            reflectiveQuadTo(4f, 9f)
                            reflectiveQuadTo(3.29f, 8.71f)
                            reflectiveQuadTo(3f, 8f)
                            verticalLineTo(5f)
                            quadTo(3f, 4.17f, 3.59f, 3.59f)
                            reflectiveQuadTo(5f, 3f)
                            horizontalLineTo(19f)
                            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                            reflectiveQuadTo(21f, 5f)
                            verticalLineTo(19f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(19f, 21f)
                            horizontalLineTo(5f)
                            close()
                            moveToRelative(6.65f, -8f)
                            horizontalLineTo(4f)
                            quadTo(3.58f, 13f, 3.29f, 12.71f)
                            quadTo(3f, 12.43f, 3f, 12f)
                            reflectiveQuadTo(3.29f, 11.29f)
                            reflectiveQuadTo(4f, 11f)
                            horizontalLineToRelative(7.65f)
                            lineTo(9.8f, 9.15f)
                            quadTo(9.5f, 8.85f, 9.51f, 8.45f)
                            reflectiveQuadTo(9.8f, 7.75f)
                            quadToRelative(0.3f, -0.3f, 0.71f, -0.31f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            lineTo(14.8f, 11.3f)
                            quadToRelative(0.15f, 0.15f, 0.21f, 0.33f)
                            reflectiveQuadTo(15.08f, 12f)
                            reflectiveQuadToRelative(-0.06f, 0.38f)
                            reflectiveQuadTo(14.8f, 12.7f)
                            lineToRelative(-3.57f, 3.57f)
                            quadToRelative(-0.3f, 0.3f, -0.71f, 0.29f)
                            reflectiveQuadTo(9.8f, 16.25f)
                            quadTo(9.53f, 15.95f, 9.51f, 15.55f)
                            reflectiveQuadTo(9.8f, 14.85f)
                            lineTo(11.65f, 13f)
                            close()
                        }
                    }
                    .build()
            return _exitApp!!
        }

    private var _favorite: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Favorite: ImageVector
        get() {
            if (_favorite != null) {
                return _favorite!!
            }
            _favorite =
                ImageVector.Builder(
                    name = "_favorite",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(11.29f, 20.2f)
                            quadTo(10.93f, 20.08f, 10.65f, 19.8f)
                            lineTo(8.93f, 18.23f)
                            quadTo(6.28f, 15.8f, 4.14f, 13.41f)
                            quadTo(2f, 11.02f, 2f, 8.15f)
                            quadTo(2f, 5.8f, 3.58f, 4.22f)
                            reflectiveQuadTo(7.5f, 2.65f)
                            quadToRelative(1.33f, 0f, 2.5f, 0.56f)
                            reflectiveQuadToRelative(2f, 1.54f)
                            quadTo(12.83f, 3.77f, 14f, 3.21f)
                            reflectiveQuadTo(16.5f, 2.65f)
                            quadToRelative(2.35f, 0f, 3.93f, 1.57f)
                            reflectiveQuadTo(22f, 8.15f)
                            quadToRelative(0f, 2.88f, -2.13f, 5.28f)
                            reflectiveQuadToRelative(-4.82f, 4.83f)
                            lineToRelative(-1.7f, 1.55f)
                            quadToRelative(-0.28f, 0.27f, -0.64f, 0.4f)
                            reflectiveQuadTo(12f, 20.33f)
                            reflectiveQuadTo(11.29f, 20.2f)
                            close()
                        }
                    }
                    .build()
            return _favorite!!
        }

    private var _filter: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Filter: ImageVector
        get() {
            if (_filter != null) {
                return _filter!!
            }
            _filter =
                ImageVector.Builder(
                    name = "_filter",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(11f, 20f)
                            quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
                            quadTo(10f, 19.43f, 10f, 19f)
                            verticalLineTo(13f)
                            lineTo(4.2f, 5.6f)
                            quadTo(3.83f, 5.1f, 4.09f, 4.55f)
                            reflectiveQuadTo(5f, 4f)
                            horizontalLineTo(19f)
                            quadToRelative(0.65f, 0f, 0.91f, 0.55f)
                            reflectiveQuadTo(19.8f, 5.6f)
                            lineTo(14f, 13f)
                            verticalLineToRelative(6f)
                            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                            reflectiveQuadTo(13f, 20f)
                            horizontalLineTo(11f)
                            close()
                            moveToRelative(1f, -7.7f)
                            lineTo(16.95f, 6f)
                            horizontalLineTo(7.05f)
                            lineTo(12f, 12.3f)
                            close()
                            moveToRelative(0f, 0f)
                            close()
                        }
                    }
                    .build()
            return _filter!!
        }

    private var _fullscreen: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Fullscreen: ImageVector
        get() {
            if (_fullscreen != null) {
                return _fullscreen!!
            }
            _fullscreen =
                ImageVector.Builder(
                    name = "_fullscreen",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(5f, 19f)
                            horizontalLineTo(7f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(8f, 20f)
                            reflectiveQuadTo(7.71f, 20.71f)
                            reflectiveQuadTo(7f, 21f)
                            horizontalLineTo(4f)
                            quadTo(3.58f, 21f, 3.29f, 20.71f)
                            quadTo(3f, 20.43f, 3f, 20f)
                            verticalLineTo(17f)
                            quadTo(3f, 16.58f, 3.29f, 16.29f)
                            reflectiveQuadTo(4f, 16f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(5f, 17f)
                            verticalLineToRelative(2f)
                            close()
                            moveToRelative(14f, 0f)
                            verticalLineTo(17f)
                            quadToRelative(0f, -0.43f, 0.29f, -0.71f)
                            reflectiveQuadTo(20f, 16f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(21f, 17f)
                            verticalLineToRelative(3f)
                            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                            reflectiveQuadTo(20f, 21f)
                            horizontalLineTo(17f)
                            quadToRelative(-0.43f, 0f, -0.71f, -0.29f)
                            quadTo(16f, 20.43f, 16f, 20f)
                            reflectiveQuadToRelative(0.29f, -0.71f)
                            reflectiveQuadTo(17f, 19f)
                            horizontalLineToRelative(2f)
                            close()
                            moveTo(5f, 5f)
                            verticalLineTo(7f)
                            quadTo(5f, 7.43f, 4.71f, 7.71f)
                            reflectiveQuadTo(4f, 8f)
                            reflectiveQuadTo(3.29f, 7.71f)
                            quadTo(3f, 7.43f, 3f, 7f)
                            verticalLineTo(4f)
                            quadTo(3f, 3.57f, 3.29f, 3.29f)
                            reflectiveQuadTo(4f, 3f)
                            horizontalLineTo(7f)
                            quadTo(7.43f, 3f, 7.71f, 3.29f)
                            reflectiveQuadTo(8f, 4f)
                            quadTo(8f, 4.42f, 7.71f, 4.71f)
                            reflectiveQuadTo(7f, 5f)
                            horizontalLineTo(5f)
                            close()
                            moveTo(19f, 5f)
                            horizontalLineTo(17f)
                            quadTo(16.58f, 5f, 16.29f, 4.71f)
                            reflectiveQuadTo(16f, 4f)
                            quadTo(16f, 3.57f, 16.29f, 3.29f)
                            reflectiveQuadTo(17f, 3f)
                            horizontalLineToRelative(3f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(21f, 4f)
                            verticalLineTo(7f)
                            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                            reflectiveQuadTo(20f, 8f)
                            reflectiveQuadTo(19.29f, 7.71f)
                            quadTo(19f, 7.43f, 19f, 7f)
                            verticalLineTo(5f)
                            close()
                        }
                    }
                    .build()
            return _fullscreen!!
        }

    private var _fullscreenExit: ImageVector? = null

    @Suppress("CheckReturnValue")
    val FullscreenExit: ImageVector
        get() {
            if (_fullscreenExit != null) {
                return _fullscreenExit!!
            }
            _fullscreenExit =
                ImageVector.Builder(
                    name = "_fullscreen_exit",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(6f, 18f)
                            horizontalLineTo(4f)
                            quadTo(3.58f, 18f, 3.29f, 17.71f)
                            quadTo(3f, 17.43f, 3f, 17f)
                            reflectiveQuadTo(3.29f, 16.29f)
                            reflectiveQuadTo(4f, 16f)
                            horizontalLineTo(7f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(8f, 17f)
                            verticalLineToRelative(3f)
                            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                            reflectiveQuadTo(7f, 21f)
                            quadTo(6.58f, 21f, 6.29f, 20.71f)
                            quadTo(6f, 20.43f, 6f, 20f)
                            verticalLineTo(18f)
                            close()
                            moveToRelative(12f, 0f)
                            verticalLineToRelative(2f)
                            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                            reflectiveQuadTo(17f, 21f)
                            reflectiveQuadTo(16.29f, 20.71f)
                            quadTo(16f, 20.43f, 16f, 20f)
                            verticalLineTo(17f)
                            quadToRelative(0f, -0.43f, 0.29f, -0.71f)
                            reflectiveQuadTo(17f, 16f)
                            horizontalLineToRelative(3f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(21f, 17f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(20f, 18f)
                            horizontalLineTo(18f)
                            close()
                            moveTo(6f, 6f)
                            verticalLineTo(4f)
                            quadTo(6f, 3.57f, 6.29f, 3.29f)
                            reflectiveQuadTo(7f, 3f)
                            reflectiveQuadTo(7.71f, 3.29f)
                            reflectiveQuadTo(8f, 4f)
                            verticalLineTo(7f)
                            quadTo(8f, 7.43f, 7.71f, 7.71f)
                            reflectiveQuadTo(7f, 8f)
                            horizontalLineTo(4f)
                            quadTo(3.58f, 8f, 3.29f, 7.71f)
                            quadTo(3f, 7.43f, 3f, 7f)
                            reflectiveQuadTo(3.29f, 6.29f)
                            reflectiveQuadTo(4f, 6f)
                            horizontalLineTo(6f)
                            close()
                            moveTo(18f, 6f)
                            horizontalLineToRelative(2f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(21f, 7f)
                            reflectiveQuadTo(20.71f, 7.71f)
                            reflectiveQuadTo(20f, 8f)
                            horizontalLineTo(17f)
                            quadTo(16.58f, 8f, 16.29f, 7.71f)
                            quadTo(16f, 7.43f, 16f, 7f)
                            verticalLineTo(4f)
                            quadTo(16f, 3.57f, 16.29f, 3.29f)
                            reflectiveQuadTo(17f, 3f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(18f, 4f)
                            verticalLineTo(6f)
                            close()
                        }
                    }
                    .build()
            return _fullscreenExit!!
        }

    private var _keyboardArrowDown: ImageVector? = null

    @Suppress("CheckReturnValue")
    val KeyboardArrowDown: ImageVector
        get() {
            if (_keyboardArrowDown != null) {
                return _keyboardArrowDown!!
            }
            _keyboardArrowDown =
                ImageVector.Builder(
                    name = "_keyboard_arrow_down",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(11.63f, 14.91f)
                            quadTo(11.45f, 14.85f, 11.3f, 14.7f)
                            lineTo(6.7f, 10.1f)
                            quadTo(6.43f, 9.82f, 6.43f, 9.4f)
                            quadTo(6.43f, 8.98f, 6.7f, 8.7f)
                            reflectiveQuadTo(7.4f, 8.42f)
                            reflectiveQuadTo(8.1f, 8.7f)
                            lineTo(12f, 12.6f)
                            lineTo(15.9f, 8.7f)
                            quadTo(16.18f, 8.42f, 16.6f, 8.42f)
                            reflectiveQuadTo(17.3f, 8.7f)
                            reflectiveQuadToRelative(0.27f, 0.7f)
                            reflectiveQuadTo(17.3f, 10.1f)
                            lineToRelative(-4.6f, 4.6f)
                            quadToRelative(-0.15f, 0.15f, -0.33f, 0.21f)
                            reflectiveQuadTo(12f, 14.98f)
                            reflectiveQuadTo(11.63f, 14.91f)
                            close()
                        }
                    }
                    .build()
            return _keyboardArrowDown!!
        }

    private var _keyboardArrowRight: ImageVector? = null

    @Suppress("CheckReturnValue")
    val KeyboardArrowRight: ImageVector
        get() {
            if (_keyboardArrowRight != null) {
                return _keyboardArrowRight!!
            }
            _keyboardArrowRight =
                ImageVector.Builder(
                    name = "_keyboard_arrow_right",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(12.6f, 12f)
                            lineTo(8.7f, 8.1f)
                            quadTo(8.43f, 7.82f, 8.43f, 7.4f)
                            reflectiveQuadTo(8.7f, 6.7f)
                            reflectiveQuadTo(9.4f, 6.43f)
                            reflectiveQuadTo(10.1f, 6.7f)
                            lineToRelative(4.6f, 4.6f)
                            quadToRelative(0.15f, 0.15f, 0.21f, 0.33f)
                            reflectiveQuadTo(14.98f, 12f)
                            reflectiveQuadToRelative(-0.06f, 0.38f)
                            reflectiveQuadTo(14.7f, 12.7f)
                            lineToRelative(-4.6f, 4.6f)
                            quadTo(9.83f, 17.58f, 9.4f, 17.58f)
                            reflectiveQuadTo(8.7f, 17.3f)
                            quadTo(8.43f, 17.02f, 8.43f, 16.6f)
                            reflectiveQuadTo(8.7f, 15.9f)
                            lineTo(12.6f, 12f)
                            close()
                        }
                    }
                    .build()
            return _keyboardArrowRight!!
        }

    private var _keyboardArrowUp: ImageVector? = null

    @Suppress("CheckReturnValue")
    val KeyboardArrowUp: ImageVector
        get() {
            if (_keyboardArrowUp != null) {
                return _keyboardArrowUp!!
            }
            _keyboardArrowUp =
                ImageVector.Builder(
                    name = "_keyboard_arrow_up",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(12f, 10.8f)
                            lineTo(8.1f, 14.7f)
                            quadTo(7.83f, 14.98f, 7.4f, 14.98f)
                            reflectiveQuadTo(6.7f, 14.7f)
                            reflectiveQuadTo(6.43f, 14f)
                            reflectiveQuadTo(6.7f, 13.3f)
                            lineTo(11.3f, 8.7f)
                            quadTo(11.6f, 8.4f, 12f, 8.4f)
                            reflectiveQuadToRelative(0.7f, 0.3f)
                            lineToRelative(4.6f, 4.6f)
                            quadToRelative(0.27f, 0.28f, 0.27f, 0.7f)
                            reflectiveQuadTo(17.3f, 14.7f)
                            reflectiveQuadToRelative(-0.7f, 0.28f)
                            reflectiveQuadTo(15.9f, 14.7f)
                            lineTo(12f, 10.8f)
                            close()
                        }
                    }
                    .build()
            return _keyboardArrowUp!!
        }

    private var _list: ImageVector? = null

    @Suppress("CheckReturnValue")
    val List: ImageVector
        get() {
            if (_list != null) {
                return _list!!
            }
            _list =
                ImageVector.Builder(
                    name = "_list",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(8f, 9f)
                            quadTo(7.58f, 9f, 7.29f, 8.71f)
                            reflectiveQuadTo(7f, 8f)
                            quadTo(7f, 7.57f, 7.29f, 7.29f)
                            reflectiveQuadTo(8f, 7f)
                            horizontalLineTo(20f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(21f, 8f)
                            quadToRelative(0f, 0.42f, -0.29f, 0.71f)
                            reflectiveQuadTo(20f, 9f)
                            horizontalLineTo(8f)
                            close()
                            moveToRelative(0f, 4f)
                            quadTo(7.58f, 13f, 7.29f, 12.71f)
                            quadTo(7f, 12.43f, 7f, 12f)
                            reflectiveQuadTo(7.29f, 11.29f)
                            reflectiveQuadTo(8f, 11f)
                            horizontalLineTo(20f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(21f, 12f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(20f, 13f)
                            horizontalLineTo(8f)
                            close()
                            moveToRelative(0f, 4f)
                            quadTo(7.58f, 17f, 7.29f, 16.71f)
                            quadTo(7f, 16.43f, 7f, 16f)
                            reflectiveQuadTo(7.29f, 15.29f)
                            reflectiveQuadTo(8f, 15f)
                            horizontalLineTo(20f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(21f, 16f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(20f, 17f)
                            horizontalLineTo(8f)
                            close()
                            moveTo(4f, 9f)
                            quadTo(3.58f, 9f, 3.29f, 8.71f)
                            reflectiveQuadTo(3f, 8f)
                            quadTo(3f, 7.57f, 3.29f, 7.29f)
                            reflectiveQuadTo(4f, 7f)
                            reflectiveQuadTo(4.71f, 7.29f)
                            reflectiveQuadTo(5f, 8f)
                            quadTo(5f, 8.42f, 4.71f, 8.71f)
                            reflectiveQuadTo(4f, 9f)
                            close()
                            moveToRelative(0f, 4f)
                            quadTo(3.58f, 13f, 3.29f, 12.71f)
                            quadTo(3f, 12.43f, 3f, 12f)
                            reflectiveQuadTo(3.29f, 11.29f)
                            reflectiveQuadTo(4f, 11f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(5f, 12f)
                            reflectiveQuadTo(4.71f, 12.71f)
                            reflectiveQuadTo(4f, 13f)
                            close()
                            moveToRelative(0f, 4f)
                            quadTo(3.58f, 17f, 3.29f, 16.71f)
                            quadTo(3f, 16.43f, 3f, 16f)
                            reflectiveQuadTo(3.29f, 15.29f)
                            reflectiveQuadTo(4f, 15f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(5f, 16f)
                            reflectiveQuadTo(4.71f, 16.71f)
                            reflectiveQuadTo(4f, 17f)
                            close()
                        }
                    }
                    .build()
            return _list!!
        }

    private var _mail: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Mail: ImageVector
        get() {
            if (_mail != null) {
                return _mail!!
            }
            _mail =
                ImageVector.Builder(
                    name = "_mail",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(4f, 20f)
                            quadTo(3.18f, 20f, 2.59f, 19.41f)
                            reflectiveQuadTo(2f, 18f)
                            verticalLineTo(6f)
                            quadTo(2f, 5.18f, 2.59f, 4.59f)
                            reflectiveQuadTo(4f, 4f)
                            horizontalLineTo(20f)
                            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                            quadTo(22f, 5.18f, 22f, 6f)
                            verticalLineTo(18f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(20f, 20f)
                            horizontalLineTo(4f)
                            close()
                            moveTo(20f, 8f)
                            lineToRelative(-7.48f, 4.67f)
                            quadToRelative(-0.13f, 0.08f, -0.26f, 0.11f)
                            reflectiveQuadTo(12f, 12.83f)
                            reflectiveQuadTo(11.74f, 12.79f)
                            reflectiveQuadTo(11.48f, 12.68f)
                            lineTo(4f, 8f)
                            verticalLineTo(18f)
                            horizontalLineTo(20f)
                            verticalLineTo(8f)
                            close()
                            moveToRelative(-8f, 3f)
                            lineTo(20f, 6f)
                            horizontalLineTo(4f)
                            lineToRelative(8f, 5f)
                            close()
                            moveTo(4f, 8f)
                            verticalLineTo(8.25f)
                            quadTo(4f, 8.13f, 4f, 7.94f)
                            reflectiveQuadTo(4f, 7.52f)
                            quadTo(4f, 7.02f, 4f, 6.77f)
                            reflectiveQuadTo(4f, 6.8f)
                            verticalLineTo(6f)
                            verticalLineTo(6.8f)
                            quadTo(4f, 6.52f, 4f, 6.79f)
                            reflectiveQuadTo(4f, 7.52f)
                            quadTo(4f, 7.77f, 4f, 7.96f)
                            reflectiveQuadTo(4f, 8.25f)
                            verticalLineTo(8f)
                            verticalLineTo(18f)
                            verticalLineTo(8f)
                            close()
                        }
                    }
                    .build()
            return _mail!!
        }

    private var _manga: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Manga: ImageVector
        get() {
            if (_manga != null) {
                return _manga!!
            }
            _manga =
                ImageVector.Builder(
                    name = "_manga",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(6.5f, 16f)
                            quadToRelative(1.18f, 0f, 2.29f, 0.26f)
                            reflectiveQuadTo(11f, 17.05f)
                            verticalLineTo(7.2f)
                            quadTo(9.98f, 6.6f, 8.83f, 6.3f)
                            reflectiveQuadTo(6.5f, 6f)
                            quadTo(5.6f, 6f, 4.71f, 6.18f)
                            reflectiveQuadTo(3f, 6.7f)
                            verticalLineToRelative(9.9f)
                            quadTo(3.88f, 16.3f, 4.74f, 16.15f)
                            reflectiveQuadTo(6.5f, 16f)
                            close()
                            moveTo(13f, 17.05f)
                            quadToRelative(1.1f, -0.53f, 2.21f, -0.79f)
                            reflectiveQuadTo(17.5f, 16f)
                            quadToRelative(0.9f, 0f, 1.76f, 0.15f)
                            reflectiveQuadTo(21f, 16.6f)
                            verticalLineTo(6.7f)
                            quadTo(20.18f, 6.35f, 19.29f, 6.18f)
                            reflectiveQuadTo(17.5f, 6f)
                            quadTo(16.33f, 6f, 15.18f, 6.3f)
                            reflectiveQuadTo(13f, 7.2f)
                            verticalLineToRelative(9.85f)
                            close()
                            moveToRelative(-1.66f, 2.34f)
                            quadTo(11.03f, 19.3f, 10.75f, 19.15f)
                            quadTo(9.78f, 18.58f, 8.7f, 18.29f)
                            reflectiveQuadTo(6.5f, 18f)
                            quadTo(5.45f, 18f, 4.44f, 18.27f)
                            reflectiveQuadTo(2.5f, 19.05f)
                            quadTo(1.98f, 19.33f, 1.49f, 19.02f)
                            quadTo(1f, 18.73f, 1f, 18.15f)
                            verticalLineTo(6.1f)
                            quadTo(1f, 5.82f, 1.14f, 5.57f)
                            quadTo(1.28f, 5.32f, 1.55f, 5.2f)
                            quadTo(2.7f, 4.6f, 3.95f, 4.3f)
                            reflectiveQuadTo(6.5f, 4f)
                            quadTo(7.95f, 4f, 9.34f, 4.38f)
                            reflectiveQuadTo(12f, 5.5f)
                            quadTo(13.28f, 4.75f, 14.66f, 4.38f)
                            reflectiveQuadTo(17.5f, 4f)
                            quadToRelative(1.3f, 0f, 2.55f, 0.3f)
                            reflectiveQuadToRelative(2.4f, 0.9f)
                            quadToRelative(0.27f, 0.13f, 0.41f, 0.38f)
                            reflectiveQuadTo(23f, 6.1f)
                            verticalLineTo(18.15f)
                            quadToRelative(0f, 0.58f, -0.49f, 0.88f)
                            quadToRelative(-0.49f, 0.3f, -1.01f, 0.03f)
                            quadToRelative(-0.92f, -0.5f, -1.94f, -0.78f)
                            reflectiveQuadTo(17.5f, 18f)
                            quadToRelative(-1.13f, 0f, -2.2f, 0.29f)
                            reflectiveQuadToRelative(-2.05f, 0.86f)
                            quadToRelative(-0.27f, 0.15f, -0.59f, 0.24f)
                            quadTo(12.35f, 19.48f, 12f, 19.48f)
                            reflectiveQuadTo(11.34f, 19.39f)
                            close()
                            moveTo(7f, 11.65f)
                            close()
                            moveTo(14f, 8.77f)
                            quadTo(14f, 8.55f, 14.16f, 8.31f)
                            quadTo(14.33f, 8.07f, 14.53f, 8f)
                            quadTo(15.25f, 7.75f, 15.98f, 7.63f)
                            reflectiveQuadTo(17.5f, 7.5f)
                            quadToRelative(0.5f, 0f, 0.99f, 0.06f)
                            reflectiveQuadToRelative(0.96f, 0.16f)
                            quadToRelative(0.22f, 0.05f, 0.39f, 0.25f)
                            reflectiveQuadTo(20f, 8.42f)
                            quadToRelative(0f, 0.43f, -0.27f, 0.63f)
                            reflectiveQuadToRelative(-0.7f, 0.1f)
                            quadTo(18.68f, 9.07f, 18.29f, 9.04f)
                            reflectiveQuadTo(17.5f, 9f)
                            quadTo(16.85f, 9f, 16.23f, 9.13f)
                            reflectiveQuadToRelative(-1.2f, 0.32f)
                            quadTo(14.58f, 9.63f, 14.29f, 9.42f)
                            quadTo(14f, 9.23f, 14f, 8.77f)
                            close()
                            moveToRelative(0f, 5.5f)
                            quadToRelative(0f, -0.22f, 0.16f, -0.46f)
                            reflectiveQuadTo(14.53f, 13.5f)
                            quadToRelative(0.72f, -0.25f, 1.45f, -0.38f)
                            reflectiveQuadTo(17.5f, 13f)
                            quadToRelative(0.5f, 0f, 0.99f, 0.06f)
                            reflectiveQuadToRelative(0.96f, 0.16f)
                            quadToRelative(0.22f, 0.05f, 0.39f, 0.25f)
                            reflectiveQuadTo(20f, 13.93f)
                            quadToRelative(0f, 0.43f, -0.27f, 0.63f)
                            reflectiveQuadToRelative(-0.7f, 0.1f)
                            quadTo(18.68f, 14.58f, 18.29f, 14.54f)
                            reflectiveQuadTo(17.5f, 14.5f)
                            quadToRelative(-0.65f, 0f, -1.27f, 0.11f)
                            quadToRelative(-0.63f, 0.11f, -1.2f, 0.31f)
                            quadTo(14.58f, 15.1f, 14.29f, 14.91f)
                            reflectiveQuadTo(14f, 14.27f)
                            close()
                            moveToRelative(0f, -2.75f)
                            quadToRelative(0f, -0.22f, 0.16f, -0.46f)
                            reflectiveQuadToRelative(0.36f, -0.31f)
                            quadToRelative(0.72f, -0.25f, 1.45f, -0.38f)
                            reflectiveQuadTo(17.5f, 10.25f)
                            quadToRelative(0.5f, 0f, 0.99f, 0.06f)
                            reflectiveQuadToRelative(0.96f, 0.16f)
                            quadToRelative(0.22f, 0.05f, 0.39f, 0.25f)
                            reflectiveQuadTo(20f, 11.18f)
                            quadToRelative(0f, 0.43f, -0.27f, 0.63f)
                            reflectiveQuadToRelative(-0.7f, 0.1f)
                            quadTo(18.68f, 11.83f, 18.29f, 11.79f)
                            reflectiveQuadTo(17.5f, 11.75f)
                            quadToRelative(-0.65f, 0f, -1.27f, 0.13f)
                            reflectiveQuadToRelative(-1.2f, 0.32f)
                            quadToRelative(-0.45f, 0.18f, -0.74f, -0.03f)
                            quadTo(14f, 11.98f, 14f, 11.52f)
                            close()
                        }
                    }
                    .build()
            return _manga!!
        }

    private var _menu: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Menu: ImageVector
        get() {
            if (_menu != null) {
                return _menu!!
            }
            _menu =
                ImageVector.Builder(
                    name = "_menu",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(4f, 18f)
                            quadTo(3.58f, 18f, 3.29f, 17.71f)
                            quadTo(3f, 17.43f, 3f, 17f)
                            reflectiveQuadTo(3.29f, 16.29f)
                            reflectiveQuadTo(4f, 16f)
                            horizontalLineTo(20f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(21f, 17f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(20f, 18f)
                            horizontalLineTo(4f)
                            close()
                            moveTo(4f, 13f)
                            quadTo(3.58f, 13f, 3.29f, 12.71f)
                            quadTo(3f, 12.43f, 3f, 12f)
                            reflectiveQuadTo(3.29f, 11.29f)
                            reflectiveQuadTo(4f, 11f)
                            horizontalLineTo(20f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(21f, 12f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(20f, 13f)
                            horizontalLineTo(4f)
                            close()
                            moveTo(4f, 8f)
                            quadTo(3.58f, 8f, 3.29f, 7.71f)
                            quadTo(3f, 7.43f, 3f, 7f)
                            reflectiveQuadTo(3.29f, 6.29f)
                            reflectiveQuadTo(4f, 6f)
                            horizontalLineTo(20f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(21f, 7f)
                            reflectiveQuadTo(20.71f, 7.71f)
                            reflectiveQuadTo(20f, 8f)
                            horizontalLineTo(4f)
                            close()
                        }
                    }
                    .build()
            return _menu!!
        }

    private var _moreVert: ImageVector? = null

    @Suppress("CheckReturnValue")
    val MoreVertical: ImageVector
        get() {
            if (_moreVert != null) {
                return _moreVert!!
            }
            _moreVert =
                ImageVector.Builder(
                    name = "_more_vert",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(12f, 20f)
                            quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
                            reflectiveQuadTo(10f, 18f)
                            reflectiveQuadToRelative(0.59f, -1.41f)
                            reflectiveQuadTo(12f, 16f)
                            reflectiveQuadToRelative(1.41f, 0.59f)
                            quadTo(14f, 17.18f, 14f, 18f)
                            reflectiveQuadToRelative(-0.59f, 1.41f)
                            reflectiveQuadTo(12f, 20f)
                            close()
                            moveToRelative(0f, -6f)
                            quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
                            reflectiveQuadTo(10f, 12f)
                            reflectiveQuadToRelative(0.59f, -1.41f)
                            reflectiveQuadTo(12f, 10f)
                            reflectiveQuadToRelative(1.41f, 0.59f)
                            quadTo(14f, 11.18f, 14f, 12f)
                            reflectiveQuadToRelative(-0.59f, 1.41f)
                            reflectiveQuadTo(12f, 14f)
                            close()
                            moveTo(12f, 8f)
                            quadTo(11.18f, 8f, 10.59f, 7.41f)
                            reflectiveQuadTo(10f, 6f)
                            reflectiveQuadTo(10.59f, 4.59f)
                            reflectiveQuadTo(12f, 4f)
                            reflectiveQuadToRelative(1.41f, 0.59f)
                            quadTo(14f, 5.18f, 14f, 6f)
                            reflectiveQuadTo(13.41f, 7.41f)
                            reflectiveQuadTo(12f, 8f)
                            close()
                        }
                    }
                    .build()
            return _moreVert!!
        }

    private var _news: ImageVector? = null

    @Suppress("CheckReturnValue")
    val News: ImageVector
        get() {
            if (_news != null) {
                return _news!!
            }
            _news =
                ImageVector.Builder(
                    name = "_newspaper",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(4f, 21f)
                            quadTo(3.18f, 21f, 2.59f, 20.41f)
                            reflectiveQuadTo(2f, 19f)
                            verticalLineTo(3.6f)
                            quadTo(2f, 3.42f, 2.15f, 3.36f)
                            reflectiveQuadTo(2.43f, 3.42f)
                            lineTo(3.68f, 4.67f)
                            lineTo(4.98f, 3.35f)
                            quadTo(5.13f, 3.2f, 5.33f, 3.2f)
                            reflectiveQuadTo(5.68f, 3.35f)
                            lineTo(7f, 4.67f)
                            lineTo(8.33f, 3.35f)
                            quadTo(8.48f, 3.2f, 8.68f, 3.2f)
                            reflectiveQuadTo(9.03f, 3.35f)
                            lineToRelative(1.3f, 1.32f)
                            lineTo(11.65f, 3.35f)
                            quadTo(11.8f, 3.2f, 12f, 3.2f)
                            reflectiveQuadToRelative(0.35f, 0.15f)
                            lineToRelative(1.32f, 1.32f)
                            lineToRelative(1.3f, -1.32f)
                            quadTo(15.13f, 3.2f, 15.33f, 3.2f)
                            reflectiveQuadToRelative(0.35f, 0.15f)
                            lineTo(17f, 4.67f)
                            lineTo(18.33f, 3.35f)
                            quadTo(18.48f, 3.2f, 18.68f, 3.2f)
                            reflectiveQuadToRelative(0.35f, 0.15f)
                            lineToRelative(1.3f, 1.32f)
                            lineTo(21.58f, 3.42f)
                            quadTo(21.7f, 3.3f, 21.85f, 3.36f)
                            reflectiveQuadTo(22f, 3.6f)
                            verticalLineTo(19f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(20f, 21f)
                            horizontalLineTo(4f)
                            close()
                            moveTo(4f, 19f)
                            horizontalLineToRelative(7f)
                            verticalLineTo(13f)
                            horizontalLineTo(4f)
                            verticalLineToRelative(6f)
                            close()
                            moveToRelative(9f, 0f)
                            horizontalLineToRelative(7f)
                            verticalLineTo(17f)
                            horizontalLineTo(13f)
                            verticalLineToRelative(2f)
                            close()
                            moveToRelative(0f, -4f)
                            horizontalLineToRelative(7f)
                            verticalLineTo(13f)
                            horizontalLineTo(13f)
                            verticalLineToRelative(2f)
                            close()
                            moveTo(4f, 11f)
                            horizontalLineTo(20f)
                            verticalLineTo(8f)
                            horizontalLineTo(4f)
                            verticalLineToRelative(3f)
                            close()
                        }
                    }
                    .build()
            return _news!!
        }

    private var _profileOff: ImageVector? = null

    @Suppress("CheckReturnValue")
    val ProfileOff: ImageVector
        get() {
            if (_profileOff != null) {
                return _profileOff!!
            }
            _profileOff =
                ImageVector.Builder(
                    name = "_account_circle_off",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(10.6f, 13.4f)
                            close()
                            moveToRelative(3.85f, 6.21f)
                            quadToRelative(1.17f, -0.39f, 2.2f, -1.11f)
                            quadToRelative(-1.03f, -0.73f, -2.2f, -1.11f)
                            reflectiveQuadTo(12f, 17f)
                            reflectiveQuadTo(9.55f, 17.39f)
                            reflectiveQuadTo(7.35f, 18.5f)
                            quadToRelative(1.02f, 0.73f, 2.2f, 1.11f)
                            reflectiveQuadTo(12f, 20f)
                            quadToRelative(1.28f, 0f, 2.45f, -0.39f)
                            close()
                            moveTo(14.48f, 7.02f)
                            quadToRelative(0.77f, 0.78f, 0.97f, 1.85f)
                            reflectiveQuadTo(15.2f, 10.95f)
                            quadTo(15f, 11.38f, 14.61f, 11.54f)
                            reflectiveQuadTo(13.83f, 11.5f)
                            quadTo(13.45f, 11.33f, 13.3f, 10.94f)
                            reflectiveQuadToRelative(0.05f, -0.76f)
                            quadToRelative(0.2f, -0.42f, 0.13f, -0.9f)
                            reflectiveQuadTo(13.05f, 8.45f)
                            quadTo(12.73f, 8.13f, 12.29f, 8.04f)
                            reflectiveQuadTo(11.43f, 8.13f)
                            quadTo(11.08f, 8.25f, 10.73f, 8.09f)
                            quadTo(10.38f, 7.93f, 10.25f, 7.52f)
                            quadTo(10.1f, 7.1f, 10.31f, 6.71f)
                            quadTo(10.53f, 6.32f, 10.95f, 6.15f)
                            quadTo(11.83f, 5.8f, 12.79f, 6.05f)
                            reflectiveQuadToRelative(1.69f, 0.97f)
                            close()
                            moveTo(12f, 22f)
                            quadTo(9.93f, 22f, 8.1f, 21.21f)
                            quadTo(6.28f, 20.43f, 4.93f, 19.08f)
                            quadTo(3.58f, 17.73f, 2.79f, 15.9f)
                            reflectiveQuadTo(2f, 12f)
                            quadTo(2f, 10.52f, 2.41f, 9.13f)
                            quadTo(2.83f, 7.72f, 3.63f, 6.47f)
                            lineTo(1.38f, 4.2f)
                            quadTo(1.08f, 3.9f, 1.09f, 3.49f)
                            reflectiveQuadTo(1.4f, 2.77f)
                            reflectiveQuadTo(2.11f, 2.47f)
                            reflectiveQuadToRelative(0.71f, 0.3f)
                            lineTo(21.2f, 21.18f)
                            quadToRelative(0.3f, 0.3f, 0.29f, 0.71f)
                            reflectiveQuadTo(21.18f, 22.6f)
                            reflectiveQuadToRelative(-0.7f, 0.3f)
                            reflectiveQuadToRelative(-0.7f, -0.3f)
                            lineTo(5.1f, 7.95f)
                            quadTo(4.55f, 8.88f, 4.28f, 9.9f)
                            quadTo(4f, 10.93f, 4f, 12f)
                            quadToRelative(0f, 1.42f, 0.48f, 2.72f)
                            reflectiveQuadTo(5.85f, 17.1f)
                            quadTo(7.2f, 16.08f, 8.76f, 15.54f)
                            reflectiveQuadTo(12f, 15f)
                            quadToRelative(0.95f, 0f, 1.9f, 0.2f)
                            reflectiveQuadToRelative(1.85f, 0.55f)
                            lineToRelative(3.32f, 3.32f)
                            quadToRelative(-1.42f, 1.43f, -3.25f, 2.18f)
                            reflectiveQuadTo(12f, 22f)
                            close()
                            moveTo(13.98f, 10.02f)
                            close()
                            moveTo(7.85f, 2.9f)
                            quadTo(8.83f, 2.45f, 9.88f, 2.22f)
                            reflectiveQuadTo(12f, 2f)
                            quadToRelative(2f, 0f, 3.83f, 0.75f)
                            reflectiveQuadToRelative(3.25f, 2.18f)
                            reflectiveQuadToRelative(2.18f, 3.25f)
                            reflectiveQuadTo(22f, 12f)
                            quadToRelative(0f, 1.07f, -0.22f, 2.13f)
                            reflectiveQuadTo(21.1f, 16.15f)
                            quadToRelative(-0.18f, 0.38f, -0.57f, 0.49f)
                            reflectiveQuadTo(19.78f, 16.55f)
                            reflectiveQuadTo(19.3f, 15.95f)
                            reflectiveQuadToRelative(0.05f, -0.8f)
                            quadTo(19.68f, 14.4f, 19.84f, 13.6f)
                            reflectiveQuadTo(20f, 12f)
                            quadTo(20f, 8.65f, 17.68f, 6.32f)
                            reflectiveQuadTo(12f, 4f)
                            quadTo(11.2f, 4f, 10.4f, 4.16f)
                            reflectiveQuadTo(8.85f, 4.65f)
                            quadTo(8.45f, 4.82f, 8.05f, 4.7f)
                            reflectiveQuadTo(7.45f, 4.22f)
                            quadTo(7.25f, 3.88f, 7.36f, 3.47f)
                            reflectiveQuadTo(7.85f, 2.9f)
                            close()
                        }
                    }
                    .build()
            return _profileOff!!
        }

    private var _openInBrowser: ImageVector? = null

    @Suppress("CheckReturnValue")
    val OpenInBrowser: ImageVector
        get() {
            if (_openInBrowser != null) {
                return _openInBrowser!!
            }
            _openInBrowser =
                ImageVector.Builder(
                    name = "_open_in_browser",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(5f, 21f)
                            quadTo(4.18f, 21f, 3.59f, 20.41f)
                            reflectiveQuadTo(3f, 19f)
                            verticalLineTo(5f)
                            quadTo(3f, 4.17f, 3.59f, 3.59f)
                            reflectiveQuadTo(5f, 3f)
                            horizontalLineTo(19f)
                            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                            reflectiveQuadTo(21f, 5f)
                            verticalLineTo(19f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(19f, 21f)
                            horizontalLineTo(16f)
                            quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
                            quadTo(15f, 20.43f, 15f, 20f)
                            reflectiveQuadToRelative(0.29f, -0.71f)
                            reflectiveQuadTo(16f, 19f)
                            horizontalLineToRelative(3f)
                            verticalLineTo(7f)
                            horizontalLineTo(5f)
                            verticalLineTo(19f)
                            horizontalLineTo(8f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(9f, 20f)
                            reflectiveQuadTo(8.71f, 20.71f)
                            reflectiveQuadTo(8f, 21f)
                            horizontalLineTo(5f)
                            close()
                            moveToRelative(6f, -1f)
                            verticalLineTo(14.85f)
                            lineToRelative(-0.88f, 0.88f)
                            quadToRelative(-0.3f, 0.3f, -0.71f, 0.29f)
                            reflectiveQuadTo(8.7f, 15.7f)
                            quadTo(8.43f, 15.4f, 8.41f, 15f)
                            reflectiveQuadTo(8.7f, 14.3f)
                            lineToRelative(2.6f, -2.6f)
                            quadToRelative(0.15f, -0.15f, 0.32f, -0.21f)
                            reflectiveQuadTo(12f, 11.43f)
                            reflectiveQuadToRelative(0.38f, 0.06f)
                            reflectiveQuadTo(12.7f, 11.7f)
                            lineToRelative(2.6f, 2.6f)
                            quadToRelative(0.3f, 0.3f, 0.29f, 0.7f)
                            reflectiveQuadTo(15.3f, 15.7f)
                            quadTo(15f, 16f, 14.59f, 16.01f)
                            reflectiveQuadTo(13.88f, 15.73f)
                            lineTo(13f, 14.85f)
                            verticalLineTo(20f)
                            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                            reflectiveQuadTo(12f, 21f)
                            reflectiveQuadTo(11.29f, 20.71f)
                            quadTo(11f, 20.43f, 11f, 20f)
                            close()
                        }
                    }
                    .build()
            return _openInBrowser!!
        }

    private var _pauseCircle: ImageVector? = null

    @Suppress("CheckReturnValue")
    val PauseCircle: ImageVector
        get() {
            if (_pauseCircle != null) {
                return _pauseCircle!!
            }
            _pauseCircle =
                ImageVector.Builder(
                    name = "_pause_circle",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(10.71f, 15.71f)
                            quadTo(11f, 15.43f, 11f, 15f)
                            verticalLineTo(9f)
                            quadTo(11f, 8.57f, 10.71f, 8.29f)
                            reflectiveQuadTo(10f, 8f)
                            quadTo(9.58f, 8f, 9.29f, 8.29f)
                            reflectiveQuadTo(9f, 9f)
                            verticalLineToRelative(6f)
                            quadToRelative(0f, 0.42f, 0.29f, 0.71f)
                            quadTo(9.58f, 16f, 10f, 16f)
                            reflectiveQuadToRelative(0.71f, -0.29f)
                            close()
                            moveToRelative(4f, 0f)
                            quadTo(15f, 15.43f, 15f, 15f)
                            verticalLineTo(9f)
                            quadTo(15f, 8.57f, 14.71f, 8.29f)
                            reflectiveQuadTo(14f, 8f)
                            reflectiveQuadTo(13.29f, 8.29f)
                            reflectiveQuadTo(13f, 9f)
                            verticalLineToRelative(6f)
                            quadToRelative(0f, 0.42f, 0.29f, 0.71f)
                            reflectiveQuadTo(14f, 16f)
                            reflectiveQuadToRelative(0.71f, -0.29f)
                            close()
                            moveTo(12f, 22f)
                            quadTo(9.93f, 22f, 8.1f, 21.21f)
                            quadTo(6.28f, 20.43f, 4.93f, 19.08f)
                            quadTo(3.58f, 17.73f, 2.79f, 15.9f)
                            reflectiveQuadTo(2f, 12f)
                            quadTo(2f, 9.92f, 2.79f, 8.1f)
                            quadTo(3.58f, 6.27f, 4.93f, 4.93f)
                            quadTo(6.28f, 3.57f, 8.1f, 2.79f)
                            quadTo(9.93f, 2f, 12f, 2f)
                            reflectiveQuadToRelative(3.9f, 0.79f)
                            reflectiveQuadToRelative(3.17f, 2.14f)
                            quadToRelative(1.35f, 1.35f, 2.14f, 3.17f)
                            quadTo(22f, 9.92f, 22f, 12f)
                            reflectiveQuadToRelative(-0.79f, 3.9f)
                            reflectiveQuadToRelative(-2.14f, 3.17f)
                            quadToRelative(-1.35f, 1.35f, -3.17f, 2.14f)
                            reflectiveQuadTo(12f, 22f)
                            close()
                            moveToRelative(0f, -2f)
                            quadToRelative(3.35f, 0f, 5.68f, -2.32f)
                            reflectiveQuadTo(20f, 12f)
                            reflectiveQuadTo(17.68f, 6.32f)
                            reflectiveQuadTo(12f, 4f)
                            reflectiveQuadTo(6.33f, 6.32f)
                            reflectiveQuadTo(4f, 12f)
                            reflectiveQuadToRelative(2.33f, 5.68f)
                            reflectiveQuadTo(12f, 20f)
                            close()
                            moveToRelative(0f, -8f)
                            close()
                        }
                    }
                    .build()
            return _pauseCircle!!
        }

    private var _person: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Person: ImageVector
        get() {
            if (_person != null) {
                return _person!!
            }
            _person =
                ImageVector.Builder(
                    name = "_person",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(9.18f, 10.83f)
                            quadTo(8f, 9.65f, 8f, 8f)
                            reflectiveQuadTo(9.18f, 5.18f)
                            reflectiveQuadTo(12f, 4f)
                            reflectiveQuadToRelative(2.83f, 1.18f)
                            reflectiveQuadTo(16f, 8f)
                            reflectiveQuadToRelative(-1.17f, 2.82f)
                            reflectiveQuadTo(12f, 12f)
                            reflectiveQuadTo(9.18f, 10.83f)
                            close()
                            moveTo(4f, 18f)
                            verticalLineTo(17.2f)
                            quadTo(4f, 16.35f, 4.44f, 15.64f)
                            quadTo(4.88f, 14.93f, 5.6f, 14.55f)
                            quadTo(7.15f, 13.77f, 8.75f, 13.39f)
                            reflectiveQuadTo(12f, 13f)
                            reflectiveQuadToRelative(3.25f, 0.39f)
                            reflectiveQuadToRelative(3.15f, 1.16f)
                            quadToRelative(0.72f, 0.38f, 1.16f, 1.09f)
                            reflectiveQuadTo(20f, 17.2f)
                            verticalLineTo(18f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(18f, 20f)
                            horizontalLineTo(6f)
                            quadTo(5.18f, 20f, 4.59f, 19.41f)
                            reflectiveQuadTo(4f, 18f)
                            close()
                            moveToRelative(2f, 0f)
                            horizontalLineTo(18f)
                            verticalLineTo(17.2f)
                            quadToRelative(0f, -0.27f, -0.14f, -0.5f)
                            quadTo(17.73f, 16.48f, 17.5f, 16.35f)
                            quadTo(16.15f, 15.68f, 14.78f, 15.34f)
                            reflectiveQuadTo(12f, 15f)
                            reflectiveQuadTo(9.23f, 15.34f)
                            reflectiveQuadTo(6.5f, 16.35f)
                            quadTo(6.28f, 16.48f, 6.14f, 16.7f)
                            quadTo(6f, 16.93f, 6f, 17.2f)
                            verticalLineTo(18f)
                            close()
                            moveTo(13.41f, 9.41f)
                            quadTo(14f, 8.82f, 14f, 8f)
                            reflectiveQuadTo(13.41f, 6.59f)
                            reflectiveQuadTo(12f, 6f)
                            reflectiveQuadTo(10.59f, 6.59f)
                            quadTo(10f, 7.18f, 10f, 8f)
                            reflectiveQuadToRelative(0.59f, 1.41f)
                            reflectiveQuadTo(12f, 10f)
                            reflectiveQuadTo(13.41f, 9.41f)
                            close()
                            moveTo(12f, 8f)
                            close()
                            moveToRelative(0f, 10f)
                            close()
                        }
                    }
                    .build()
            return _person!!
        }

    private var _playCircle: ImageVector? = null

    @Suppress("CheckReturnValue")
    val PlayCircle: ImageVector
        get() {
            if (_playCircle != null) {
                return _playCircle!!
            }
            _playCircle =
                ImageVector.Builder(
                    name = "_play_circle",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(10.65f, 15.75f)
                            lineToRelative(4.88f, -3.13f)
                            quadTo(15.88f, 12.4f, 15.88f, 12f)
                            reflectiveQuadTo(15.53f, 11.38f)
                            lineTo(10.65f, 8.25f)
                            quadTo(10.28f, 8f, 9.89f, 8.21f)
                            reflectiveQuadTo(9.5f, 8.88f)
                            verticalLineToRelative(6.25f)
                            quadToRelative(0f, 0.45f, 0.39f, 0.66f)
                            reflectiveQuadToRelative(0.76f, -0.04f)
                            close()
                            moveTo(12f, 22f)
                            quadTo(9.93f, 22f, 8.1f, 21.21f)
                            quadTo(6.28f, 20.43f, 4.93f, 19.08f)
                            quadTo(3.58f, 17.73f, 2.79f, 15.9f)
                            reflectiveQuadTo(2f, 12f)
                            quadTo(2f, 9.92f, 2.79f, 8.1f)
                            quadTo(3.58f, 6.27f, 4.93f, 4.93f)
                            quadTo(6.28f, 3.57f, 8.1f, 2.79f)
                            quadTo(9.93f, 2f, 12f, 2f)
                            reflectiveQuadToRelative(3.9f, 0.79f)
                            reflectiveQuadToRelative(3.17f, 2.14f)
                            quadToRelative(1.35f, 1.35f, 2.14f, 3.17f)
                            quadTo(22f, 9.92f, 22f, 12f)
                            reflectiveQuadToRelative(-0.79f, 3.9f)
                            reflectiveQuadToRelative(-2.14f, 3.17f)
                            quadToRelative(-1.35f, 1.35f, -3.17f, 2.14f)
                            reflectiveQuadTo(12f, 22f)
                            close()
                            moveToRelative(0f, -2f)
                            quadToRelative(3.35f, 0f, 5.68f, -2.32f)
                            reflectiveQuadTo(20f, 12f)
                            reflectiveQuadTo(17.68f, 6.32f)
                            reflectiveQuadTo(12f, 4f)
                            reflectiveQuadTo(6.33f, 6.32f)
                            reflectiveQuadTo(4f, 12f)
                            reflectiveQuadToRelative(2.33f, 5.68f)
                            reflectiveQuadTo(12f, 20f)
                            close()
                            moveToRelative(0f, -8f)
                            close()
                        }
                    }
                    .build()
            return _playCircle!!
        }

    private var _ranobe: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Ranobe: ImageVector
        get() {
            if (_ranobe != null) {
                return _ranobe!!
            }
            _ranobe =
                ImageVector.Builder(
                    name = "_ranobe",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(7.5f, 22f)
                            quadTo(6.05f, 22f, 5.03f, 20.98f)
                            reflectiveQuadTo(4f, 18.5f)
                            verticalLineTo(5.5f)
                            quadTo(4f, 4.05f, 5.03f, 3.02f)
                            reflectiveQuadTo(7.5f, 2f)
                            horizontalLineTo(18f)
                            quadToRelative(0.82f, 0f, 1.41f, 0.59f)
                            reflectiveQuadTo(20f, 4f)
                            verticalLineTo(16.02f)
                            quadToRelative(0f, 0.4f, -0.2f, 0.73f)
                            reflectiveQuadToRelative(-0.55f, 0.5f)
                            reflectiveQuadTo(18.7f, 17.76f)
                            reflectiveQuadTo(18.5f, 18.5f)
                            reflectiveQuadToRelative(0.2f, 0.75f)
                            reflectiveQuadToRelative(0.55f, 0.5f)
                            quadToRelative(0.33f, 0.13f, 0.54f, 0.38f)
                            reflectiveQuadTo(20f, 20.73f)
                            verticalLineToRelative(0.25f)
                            quadToRelative(0f, 0.42f, -0.29f, 0.72f)
                            reflectiveQuadTo(19f, 22f)
                            horizontalLineTo(7.5f)
                            close()
                            moveTo(6f, 15.33f)
                            quadTo(6.35f, 15.15f, 6.73f, 15.08f)
                            reflectiveQuadTo(7.5f, 15f)
                            horizontalLineTo(18f)
                            verticalLineTo(4f)
                            horizontalLineTo(7.5f)
                            quadTo(6.88f, 4f, 6.44f, 4.44f)
                            reflectiveQuadTo(6f, 5.5f)
                            verticalLineToRelative(9.82f)
                            close()
                            moveToRelative(4.58f, -4.1f)
                            horizontalLineTo(13.4f)
                            lineToRelative(0.5f, 1.4f)
                            quadToRelative(0.05f, 0.17f, 0.19f, 0.27f)
                            reflectiveQuadTo(14.43f, 13f)
                            quadToRelative(0.3f, 0f, 0.47f, -0.25f)
                            reflectiveQuadToRelative(0.05f, -0.53f)
                            lineTo(12.75f, 6.38f)
                            quadTo(12.7f, 6.2f, 12.55f, 6.1f)
                            reflectiveQuadTo(12.2f, 6f)
                            horizontalLineTo(11.75f)
                            quadTo(11.55f, 6f, 11.4f, 6.1f)
                            reflectiveQuadTo(11.2f, 6.38f)
                            lineTo(9f, 12.25f)
                            quadToRelative(-0.13f, 0.27f, 0.06f, 0.51f)
                            reflectiveQuadTo(9.55f, 13f)
                            quadToRelative(0.2f, 0f, 0.34f, -0.1f)
                            reflectiveQuadToRelative(0.19f, -0.27f)
                            lineToRelative(0.5f, -1.4f)
                            close()
                            moveTo(10.93f, 10.2f)
                            lineTo(11.95f, 7.3f)
                            horizontalLineToRelative(0.07f)
                            lineToRelative(1.03f, 2.9f)
                            horizontalLineTo(10.93f)
                            close()
                            moveTo(6f, 15.33f)
                            verticalLineTo(4f)
                            verticalLineTo(15.33f)
                            close()
                            moveTo(7.5f, 20f)
                            horizontalLineToRelative(9.32f)
                            quadTo(16.68f, 19.65f, 16.59f, 19.29f)
                            reflectiveQuadTo(16.5f, 18.5f)
                            quadToRelative(0f, -0.4f, 0.07f, -0.77f)
                            reflectiveQuadTo(16.83f, 17f)
                            horizontalLineTo(7.5f)
                            quadTo(6.85f, 17f, 6.43f, 17.44f)
                            reflectiveQuadTo(6f, 18.5f)
                            quadToRelative(0f, 0.65f, 0.43f, 1.07f)
                            reflectiveQuadTo(7.5f, 20f)
                            close()
                        }
                    }
                    .build()
            return _ranobe!!
        }

    private var _refresh: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Refresh: ImageVector
        get() {
            if (_refresh != null) {
                return _refresh!!
            }
            _refresh =
                ImageVector.Builder(
                    name = "_refresh",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(12f, 20f)
                            quadTo(8.65f, 20f, 6.33f, 17.68f)
                            reflectiveQuadTo(4f, 12f)
                            reflectiveQuadTo(6.33f, 6.32f)
                            reflectiveQuadTo(12f, 4f)
                            quadToRelative(1.73f, 0f, 3.3f, 0.71f)
                            quadTo(16.88f, 5.43f, 18f, 6.75f)
                            verticalLineTo(5f)
                            quadTo(18f, 4.57f, 18.29f, 4.29f)
                            reflectiveQuadTo(19f, 4f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(20f, 5f)
                            verticalLineToRelative(5f)
                            quadToRelative(0f, 0.42f, -0.29f, 0.71f)
                            reflectiveQuadTo(19f, 11f)
                            horizontalLineTo(14f)
                            quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
                            quadTo(13f, 10.43f, 13f, 10f)
                            quadTo(13f, 9.57f, 13.29f, 9.29f)
                            reflectiveQuadTo(14f, 9f)
                            horizontalLineToRelative(3.2f)
                            quadTo(16.4f, 7.6f, 15.01f, 6.8f)
                            reflectiveQuadTo(12f, 6f)
                            quadTo(9.5f, 6f, 7.75f, 7.75f)
                            reflectiveQuadTo(6f, 12f)
                            reflectiveQuadToRelative(1.75f, 4.25f)
                            reflectiveQuadTo(12f, 18f)
                            quadToRelative(1.7f, 0f, 3.11f, -0.86f)
                            quadToRelative(1.41f, -0.86f, 2.19f, -2.31f)
                            quadToRelative(0.2f, -0.35f, 0.56f, -0.49f)
                            reflectiveQuadTo(18.6f, 14.33f)
                            quadToRelative(0.4f, 0.13f, 0.57f, 0.53f)
                            reflectiveQuadTo(19.15f, 15.6f)
                            quadToRelative(-1.03f, 2f, -2.93f, 3.2f)
                            reflectiveQuadTo(12f, 20f)
                            close()
                        }
                    }
                    .build()
            return _refresh!!
        }

    private var _remove_friend: ImageVector? = null

    @Suppress("CheckReturnValue")
    val RemoveFriend: ImageVector
        get() {
            if (_remove_friend != null) {
                return _remove_friend!!
            }
            _remove_friend =
                ImageVector.Builder(
                    name = "_remove_friend",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(17f, 9f)
                            horizontalLineToRelative(4f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(22f, 10f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(21f, 11f)
                            horizontalLineTo(17f)
                            quadToRelative(-0.43f, 0f, -0.71f, -0.29f)
                            quadTo(16f, 10.43f, 16f, 10f)
                            quadTo(16f, 9.57f, 16.29f, 9.29f)
                            reflectiveQuadTo(17f, 9f)
                            close()
                            moveTo(6.18f, 10.83f)
                            quadTo(5f, 9.65f, 5f, 8f)
                            reflectiveQuadTo(6.18f, 5.18f)
                            reflectiveQuadTo(9f, 4f)
                            reflectiveQuadToRelative(2.83f, 1.18f)
                            reflectiveQuadTo(13f, 8f)
                            reflectiveQuadToRelative(-1.17f, 2.82f)
                            reflectiveQuadTo(9f, 12f)
                            reflectiveQuadTo(6.18f, 10.83f)
                            close()
                            moveTo(1f, 18f)
                            verticalLineTo(17.2f)
                            quadTo(1f, 16.35f, 1.44f, 15.64f)
                            quadTo(1.88f, 14.93f, 2.6f, 14.55f)
                            quadTo(4.15f, 13.77f, 5.75f, 13.39f)
                            reflectiveQuadTo(9f, 13f)
                            reflectiveQuadToRelative(3.25f, 0.39f)
                            reflectiveQuadToRelative(3.15f, 1.16f)
                            quadToRelative(0.72f, 0.38f, 1.16f, 1.09f)
                            reflectiveQuadTo(17f, 17.2f)
                            verticalLineTo(18f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(15f, 20f)
                            horizontalLineTo(3f)
                            quadTo(2.18f, 20f, 1.59f, 19.41f)
                            reflectiveQuadTo(1f, 18f)
                            close()
                            moveToRelative(2f, 0f)
                            horizontalLineTo(15f)
                            verticalLineTo(17.2f)
                            quadToRelative(0f, -0.27f, -0.14f, -0.5f)
                            quadTo(14.73f, 16.48f, 14.5f, 16.35f)
                            quadTo(13.15f, 15.68f, 11.78f, 15.34f)
                            reflectiveQuadTo(9f, 15f)
                            reflectiveQuadTo(6.23f, 15.34f)
                            reflectiveQuadTo(3.5f, 16.35f)
                            quadTo(3.28f, 16.48f, 3.14f, 16.7f)
                            quadTo(3f, 16.93f, 3f, 17.2f)
                            verticalLineTo(18f)
                            close()
                            moveTo(10.41f, 9.41f)
                            quadTo(11f, 8.82f, 11f, 8f)
                            reflectiveQuadTo(10.41f, 6.59f)
                            reflectiveQuadTo(9f, 6f)
                            quadTo(8.18f, 6f, 7.59f, 6.59f)
                            quadTo(7f, 7.18f, 7f, 8f)
                            reflectiveQuadTo(7.59f, 9.41f)
                            reflectiveQuadTo(9f, 10f)
                            quadToRelative(0.83f, 0f, 1.41f, -0.59f)
                            close()
                            moveTo(9f, 8f)
                            close()
                            moveTo(9f, 18f)
                            close()
                        }
                    }
                    .build()
            return _remove_friend!!
        }

    private var _search: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Search: ImageVector
        get() {
            if (_search != null) {
                return _search!!
            }
            _search =
                ImageVector.Builder(
                    name = "_search",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(9.5f, 16f)
                            quadTo(6.78f, 16f, 4.89f, 14.11f)
                            quadTo(3f, 12.23f, 3f, 9.5f)
                            quadTo(3f, 6.77f, 4.89f, 4.89f)
                            reflectiveQuadTo(9.5f, 3f)
                            reflectiveQuadToRelative(4.61f, 1.89f)
                            reflectiveQuadTo(16f, 9.5f)
                            quadToRelative(0f, 1.1f, -0.35f, 2.07f)
                            reflectiveQuadTo(14.7f, 13.3f)
                            lineToRelative(5.6f, 5.6f)
                            quadToRelative(0.28f, 0.28f, 0.28f, 0.7f)
                            quadToRelative(0f, 0.42f, -0.28f, 0.7f)
                            quadToRelative(-0.27f, 0.27f, -0.7f, 0.27f)
                            reflectiveQuadTo(18.9f, 20.3f)
                            lineTo(13.3f, 14.7f)
                            quadToRelative(-0.75f, 0.6f, -1.72f, 0.95f)
                            reflectiveQuadTo(9.5f, 16f)
                            close()
                            moveToRelative(0f, -2f)
                            quadToRelative(1.88f, 0f, 3.19f, -1.31f)
                            reflectiveQuadTo(14f, 9.5f)
                            reflectiveQuadTo(12.69f, 6.31f)
                            reflectiveQuadTo(9.5f, 5f)
                            reflectiveQuadTo(6.31f, 6.31f)
                            reflectiveQuadTo(5f, 9.5f)
                            reflectiveQuadToRelative(1.31f, 3.19f)
                            reflectiveQuadTo(9.5f, 14f)
                            close()
                        }
                    }
                    .build()
            return _search!!
        }

    private var _send: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Send: ImageVector
        get() {
            if (_send != null) {
                return _send!!
            }
            _send =
                ImageVector.Builder(
                    name = "_send",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(19.8f, 12.93f)
                            lineTo(4.4f, 19.43f)
                            quadTo(3.9f, 19.63f, 3.45f, 19.34f)
                            reflectiveQuadTo(3f, 18.5f)
                            verticalLineTo(5.5f)
                            quadTo(3f, 4.95f, 3.45f, 4.66f)
                            quadTo(3.9f, 4.38f, 4.4f, 4.57f)
                            lineToRelative(15.4f, 6.5f)
                            quadToRelative(0.63f, 0.28f, 0.63f, 0.93f)
                            reflectiveQuadTo(19.8f, 12.93f)
                            close()
                            moveTo(5f, 17f)
                            lineTo(16.85f, 12f)
                            lineTo(5f, 7f)
                            verticalLineToRelative(3.5f)
                            lineTo(11f, 12f)
                            lineTo(5f, 13.5f)
                            verticalLineTo(17f)
                            close()
                            moveToRelative(0f, 0f)
                            verticalLineTo(12f)
                            verticalLineTo(7f)
                            verticalLineToRelative(3.5f)
                            verticalLineToRelative(3f)
                            verticalLineTo(17f)
                            close()
                        }
                    }
                    .build()
            return _send!!
        }

    private var _settings: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Settings: ImageVector
        get() {
            if (_settings != null) {
                return _settings!!
            }
            _settings =
                ImageVector.Builder(
                    name = "_settings",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(10.83f, 22f)
                            quadTo(10.15f, 22f, 9.66f, 21.55f)
                            reflectiveQuadTo(9.08f, 20.45f)
                            lineTo(8.85f, 18.8f)
                            quadTo(8.53f, 18.68f, 8.24f, 18.5f)
                            reflectiveQuadTo(7.68f, 18.13f)
                            lineTo(6.13f, 18.77f)
                            quadTo(5.5f, 19.05f, 4.88f, 18.83f)
                            reflectiveQuadTo(3.9f, 18.02f)
                            lineTo(2.73f, 15.98f)
                            quadTo(2.38f, 15.4f, 2.53f, 14.75f)
                            reflectiveQuadTo(3.2f, 13.68f)
                            lineToRelative(1.33f, -1f)
                            quadTo(4.5f, 12.5f, 4.5f, 12.34f)
                            quadToRelative(0f, -0.16f, 0f, -0.34f)
                            reflectiveQuadToRelative(0f, -0.34f)
                            reflectiveQuadTo(4.53f, 11.33f)
                            lineToRelative(-1.33f, -1f)
                            quadTo(2.68f, 9.9f, 2.53f, 9.25f)
                            reflectiveQuadTo(2.73f, 8.02f)
                            lineTo(3.9f, 5.97f)
                            quadTo(4.25f, 5.4f, 4.88f, 5.18f)
                            reflectiveQuadTo(6.13f, 5.22f)
                            lineTo(7.68f, 5.88f)
                            quadTo(7.95f, 5.68f, 8.25f, 5.5f)
                            reflectiveQuadTo(8.85f, 5.2f)
                            lineTo(9.08f, 3.55f)
                            quadTo(9.18f, 2.9f, 9.66f, 2.45f)
                            reflectiveQuadTo(10.83f, 2f)
                            horizontalLineToRelative(2.35f)
                            quadToRelative(0.68f, 0f, 1.16f, 0.45f)
                            reflectiveQuadToRelative(0.59f, 1.1f)
                            lineTo(15.15f, 5.2f)
                            quadToRelative(0.33f, 0.13f, 0.61f, 0.3f)
                            reflectiveQuadToRelative(0.56f, 0.38f)
                            lineTo(17.88f, 5.22f)
                            quadTo(18.5f, 4.95f, 19.13f, 5.18f)
                            reflectiveQuadToRelative(0.98f, 0.8f)
                            lineToRelative(1.18f, 2.05f)
                            quadToRelative(0.35f, 0.58f, 0.2f, 1.23f)
                            reflectiveQuadTo(20.8f, 10.33f)
                            lineToRelative(-1.32f, 1f)
                            quadToRelative(0.02f, 0.18f, 0.02f, 0.34f)
                            reflectiveQuadToRelative(0f, 0.34f)
                            reflectiveQuadToRelative(0f, 0.34f)
                            reflectiveQuadToRelative(-0.05f, 0.34f)
                            lineToRelative(1.32f, 1f)
                            quadToRelative(0.52f, 0.43f, 0.68f, 1.08f)
                            reflectiveQuadToRelative(-0.2f, 1.22f)
                            lineToRelative(-1.2f, 2.05f)
                            quadToRelative(-0.35f, 0.58f, -0.98f, 0.8f)
                            reflectiveQuadTo(17.83f, 18.77f)
                            lineToRelative(-1.5f, -0.65f)
                            quadToRelative(-0.27f, 0.2f, -0.57f, 0.38f)
                            reflectiveQuadToRelative(-0.6f, 0.3f)
                            lineToRelative(-0.22f, 1.65f)
                            quadToRelative(-0.1f, 0.65f, -0.59f, 1.1f)
                            reflectiveQuadTo(13.18f, 22f)
                            horizontalLineTo(10.83f)
                            close()
                            moveTo(11f, 20f)
                            horizontalLineToRelative(1.98f)
                            lineToRelative(0.35f, -2.65f)
                            quadToRelative(0.78f, -0.2f, 1.44f, -0.59f)
                            reflectiveQuadToRelative(1.21f, -0.94f)
                            lineToRelative(2.47f, 1.03f)
                            lineToRelative(0.98f, -1.7f)
                            lineTo(17.28f, 13.52f)
                            quadToRelative(0.13f, -0.35f, 0.17f, -0.74f)
                            reflectiveQuadTo(17.5f, 12f)
                            reflectiveQuadTo(17.45f, 11.21f)
                            quadTo(17.4f, 10.83f, 17.28f, 10.48f)
                            lineTo(19.43f, 8.85f)
                            lineTo(18.45f, 7.15f)
                            lineTo(15.98f, 8.2f)
                            quadTo(15.43f, 7.63f, 14.76f, 7.24f)
                            reflectiveQuadTo(13.33f, 6.65f)
                            lineTo(13f, 4f)
                            horizontalLineTo(11.03f)
                            lineTo(10.68f, 6.65f)
                            quadTo(9.9f, 6.85f, 9.24f, 7.24f)
                            reflectiveQuadTo(8.03f, 8.17f)
                            lineTo(5.55f, 7.15f)
                            lineTo(4.58f, 8.85f)
                            lineToRelative(2.15f, 1.6f)
                            quadTo(6.6f, 10.83f, 6.55f, 11.2f)
                            reflectiveQuadTo(6.5f, 12f)
                            quadToRelative(0f, 0.4f, 0.05f, 0.77f)
                            reflectiveQuadToRelative(0.17f, 0.75f)
                            lineTo(4.58f, 15.15f)
                            lineToRelative(0.98f, 1.7f)
                            lineTo(8.03f, 15.8f)
                            quadToRelative(0.55f, 0.58f, 1.21f, 0.96f)
                            reflectiveQuadToRelative(1.44f, 0.59f)
                            lineTo(11f, 20f)
                            close()
                            moveToRelative(1.05f, -4.5f)
                            quadToRelative(1.45f, 0f, 2.47f, -1.03f)
                            reflectiveQuadTo(15.55f, 12f)
                            reflectiveQuadTo(14.53f, 9.52f)
                            reflectiveQuadTo(12.05f, 8.5f)
                            quadToRelative(-1.47f, 0f, -2.49f, 1.02f)
                            reflectiveQuadTo(8.55f, 12f)
                            reflectiveQuadToRelative(1.01f, 2.47f)
                            reflectiveQuadToRelative(2.49f, 1.03f)
                            close()
                            moveTo(12f, 12f)
                            close()
                        }
                    }
                    .build()
            return _settings!!
        }

    private var _similar: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Similar: ImageVector
        get() {
            if (_similar != null) {
                return _similar!!
            }
            _similar =
                ImageVector.Builder(
                    name = "_similar",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(16f, 21f)
                            quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
                            reflectiveQuadTo(14f, 19f)
                            verticalLineTo(15f)
                            quadToRelative(0f, -0.83f, 0.59f, -1.41f)
                            reflectiveQuadTo(16f, 13f)
                            horizontalLineToRelative(4f)
                            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                            quadTo(22f, 14.18f, 22f, 15f)
                            verticalLineToRelative(4f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(20f, 21f)
                            horizontalLineTo(16f)
                            close()
                            moveToRelative(0f, -2f)
                            horizontalLineToRelative(4f)
                            verticalLineTo(15f)
                            horizontalLineTo(16f)
                            verticalLineToRelative(4f)
                            close()
                            moveTo(3f, 18f)
                            quadTo(2.58f, 18f, 2.29f, 17.71f)
                            quadTo(2f, 17.43f, 2f, 17f)
                            reflectiveQuadTo(2.29f, 16.29f)
                            reflectiveQuadTo(3f, 16f)
                            horizontalLineToRelative(7f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(11f, 17f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(10f, 18f)
                            horizontalLineTo(3f)
                            close()
                            moveTo(16f, 11f)
                            quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
                            reflectiveQuadTo(14f, 9f)
                            verticalLineTo(5f)
                            quadTo(14f, 4.17f, 14.59f, 3.59f)
                            reflectiveQuadTo(16f, 3f)
                            horizontalLineToRelative(4f)
                            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                            reflectiveQuadTo(22f, 5f)
                            verticalLineTo(9f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(20f, 11f)
                            horizontalLineTo(16f)
                            close()
                            moveTo(16f, 9f)
                            horizontalLineToRelative(4f)
                            verticalLineTo(5f)
                            horizontalLineTo(16f)
                            verticalLineTo(9f)
                            close()
                            moveTo(3f, 8f)
                            quadTo(2.58f, 8f, 2.29f, 7.71f)
                            quadTo(2f, 7.43f, 2f, 7f)
                            reflectiveQuadTo(2.29f, 6.29f)
                            reflectiveQuadTo(3f, 6f)
                            horizontalLineToRelative(7f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(11f, 7f)
                            reflectiveQuadTo(10.71f, 7.71f)
                            reflectiveQuadTo(10f, 8f)
                            horizontalLineTo(3f)
                            close()
                            moveToRelative(15f, 9f)
                            close()
                            moveTo(18f, 7f)
                            close()
                        }
                    }
                    .build()
            return _similar!!
        }

    private var _star: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Star: ImageVector
        get() {
            if (_star != null) {
                return _star!!
            }
            _star =
                ImageVector.Builder(
                    name = "_star",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(12f, 17.27f)
                            lineToRelative(-4.15f, 2.5f)
                            quadTo(7.58f, 19.95f, 7.28f, 19.93f)
                            reflectiveQuadTo(6.75f, 19.73f)
                            reflectiveQuadTo(6.4f, 19.29f)
                            quadTo(6.28f, 19.02f, 6.35f, 18.7f)
                            lineToRelative(1.1f, -4.72f)
                            lineTo(3.78f, 10.8f)
                            quadTo(3.53f, 10.58f, 3.46f, 10.29f)
                            reflectiveQuadTo(3.5f, 9.73f)
                            reflectiveQuadTo(3.8f, 9.27f)
                            reflectiveQuadTo(4.35f, 9.05f)
                            lineTo(9.2f, 8.63f)
                            lineTo(11.08f, 4.17f)
                            quadTo(11.2f, 3.88f, 11.46f, 3.72f)
                            reflectiveQuadTo(12f, 3.57f)
                            quadToRelative(0.28f, 0f, 0.54f, 0.15f)
                            quadToRelative(0.26f, 0.15f, 0.39f, 0.45f)
                            lineTo(14.8f, 8.63f)
                            lineToRelative(4.85f, 0.42f)
                            quadTo(20f, 9.1f, 20.2f, 9.27f)
                            reflectiveQuadToRelative(0.3f, 0.45f)
                            reflectiveQuadToRelative(0.04f, 0.56f)
                            reflectiveQuadTo(20.23f, 10.8f)
                            lineToRelative(-3.68f, 3.18f)
                            lineToRelative(1.1f, 4.72f)
                            quadToRelative(0.07f, 0.32f, -0.05f, 0.59f)
                            reflectiveQuadToRelative(-0.35f, 0.44f)
                            quadToRelative(-0.22f, 0.17f, -0.52f, 0.2f)
                            reflectiveQuadTo(16.15f, 19.77f)
                            lineTo(12f, 17.27f)
                            close()
                        }
                    }
                    .build()
            return _star!!
        }

    private var _statistics: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Statistics: ImageVector
        get() {
            if (_statistics != null) {
                return _statistics!!
            }
            _statistics =
                ImageVector.Builder(
                    name = "_statistics",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(3f, 21f)
                            quadTo(2.58f, 21f, 2.29f, 20.71f)
                            quadTo(2f, 20.43f, 2f, 20f)
                            reflectiveQuadTo(2.29f, 19.29f)
                            reflectiveQuadTo(3f, 19f)
                            horizontalLineTo(21f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(22f, 20f)
                            reflectiveQuadToRelative(-0.29f, 0.71f)
                            reflectiveQuadTo(21f, 21f)
                            horizontalLineTo(3f)
                            close()
                            moveTo(3.44f, 17.56f)
                            quadTo(3f, 17.13f, 3f, 16.5f)
                            verticalLineToRelative(-4f)
                            quadTo(3f, 11.88f, 3.44f, 11.44f)
                            reflectiveQuadTo(4.5f, 11f)
                            reflectiveQuadToRelative(1.06f, 0.44f)
                            reflectiveQuadTo(6f, 12.5f)
                            verticalLineToRelative(4f)
                            quadToRelative(0f, 0.63f, -0.44f, 1.06f)
                            reflectiveQuadTo(4.5f, 18f)
                            reflectiveQuadTo(3.44f, 17.56f)
                            close()
                            moveToRelative(5f, 0f)
                            quadTo(8f, 17.13f, 8f, 16.5f)
                            verticalLineToRelative(-9f)
                            quadTo(8f, 6.88f, 8.44f, 6.44f)
                            reflectiveQuadTo(9.5f, 6f)
                            reflectiveQuadToRelative(1.06f, 0.44f)
                            reflectiveQuadTo(11f, 7.5f)
                            verticalLineToRelative(9f)
                            quadToRelative(0f, 0.63f, -0.44f, 1.06f)
                            reflectiveQuadTo(9.5f, 18f)
                            reflectiveQuadTo(8.44f, 17.56f)
                            close()
                            moveToRelative(5f, 0f)
                            quadTo(13f, 17.13f, 13f, 16.5f)
                            verticalLineToRelative(-6f)
                            quadTo(13f, 9.88f, 13.44f, 9.44f)
                            reflectiveQuadTo(14.5f, 9f)
                            reflectiveQuadToRelative(1.06f, 0.44f)
                            reflectiveQuadTo(16f, 10.5f)
                            verticalLineToRelative(6f)
                            quadToRelative(0f, 0.63f, -0.44f, 1.06f)
                            reflectiveQuadTo(14.5f, 18f)
                            reflectiveQuadTo(13.44f, 17.56f)
                            close()
                            moveToRelative(5f, 0f)
                            quadTo(18f, 17.13f, 18f, 16.5f)
                            verticalLineTo(4.5f)
                            quadTo(18f, 3.88f, 18.44f, 3.44f)
                            reflectiveQuadTo(19.5f, 3f)
                            reflectiveQuadToRelative(1.06f, 0.44f)
                            reflectiveQuadTo(21f, 4.5f)
                            verticalLineToRelative(12f)
                            quadToRelative(0f, 0.63f, -0.44f, 1.06f)
                            reflectiveQuadTo(19.5f, 18f)
                            reflectiveQuadTo(18.44f, 17.56f)
                            close()
                        }
                    }
                    .build()
            return _statistics!!
        }

    private var _subtitles: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Subtitles: ImageVector
        get() {
            if (_subtitles != null) {
                return _subtitles!!
            }
            _subtitles =
                ImageVector.Builder(
                    name = "_subtitles",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(4f, 20f)
                            quadTo(3.18f, 20f, 2.59f, 19.41f)
                            reflectiveQuadTo(2f, 18f)
                            verticalLineTo(6f)
                            quadTo(2f, 5.18f, 2.59f, 4.59f)
                            reflectiveQuadTo(4f, 4f)
                            horizontalLineTo(20f)
                            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                            quadTo(22f, 5.18f, 22f, 6f)
                            verticalLineTo(18f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(20f, 20f)
                            horizontalLineTo(4f)
                            close()
                            moveTo(4f, 18f)
                            horizontalLineTo(20f)
                            verticalLineTo(6f)
                            horizontalLineTo(4f)
                            verticalLineTo(18f)
                            close()
                            moveToRelative(0f, 0f)
                            verticalLineTo(6f)
                            verticalLineTo(18f)
                            close()
                            moveTo(7f, 16f)
                            horizontalLineToRelative(6f)
                            quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                            reflectiveQuadTo(14f, 15f)
                            reflectiveQuadTo(13.71f, 14.29f)
                            reflectiveQuadTo(13f, 14f)
                            horizontalLineTo(7f)
                            quadTo(6.58f, 14f, 6.29f, 14.29f)
                            reflectiveQuadTo(6f, 15f)
                            reflectiveQuadToRelative(0.29f, 0.71f)
                            reflectiveQuadTo(7f, 16f)
                            close()
                            moveToRelative(4f, -4f)
                            horizontalLineToRelative(6f)
                            quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                            quadTo(18f, 11.43f, 18f, 11f)
                            reflectiveQuadTo(17.71f, 10.29f)
                            reflectiveQuadTo(17f, 10f)
                            horizontalLineTo(11f)
                            quadToRelative(-0.42f, 0f, -0.71f, 0.29f)
                            reflectiveQuadTo(10f, 11f)
                            reflectiveQuadToRelative(0.29f, 0.71f)
                            reflectiveQuadTo(11f, 12f)
                            close()
                            moveTo(7.71f, 11.71f)
                            quadTo(8f, 11.43f, 8f, 11f)
                            reflectiveQuadTo(7.71f, 10.29f)
                            reflectiveQuadTo(7f, 10f)
                            quadTo(6.58f, 10f, 6.29f, 10.29f)
                            reflectiveQuadTo(6f, 11f)
                            reflectiveQuadToRelative(0.29f, 0.71f)
                            reflectiveQuadTo(7f, 12f)
                            reflectiveQuadTo(7.71f, 11.71f)
                            close()
                            moveToRelative(10f, 4f)
                            quadTo(18f, 15.43f, 18f, 15f)
                            reflectiveQuadTo(17.71f, 14.29f)
                            reflectiveQuadTo(17f, 14f)
                            reflectiveQuadToRelative(-0.71f, 0.29f)
                            reflectiveQuadTo(16f, 15f)
                            reflectiveQuadToRelative(0.29f, 0.71f)
                            reflectiveQuadTo(17f, 16f)
                            reflectiveQuadToRelative(0.71f, -0.29f)
                            close()
                        }
                    }
                    .build()
            return _subtitles!!
        }

    private var _ten_seconds_left: ImageVector? = null

    @Suppress("CheckReturnValue")
    val TenSecondsLeft: ImageVector
        get() {
            if (_ten_seconds_left != null) {
                return _ten_seconds_left!!
            }
            _ten_seconds_left =
                ImageVector.Builder(
                    name = "_replay_10",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(9f, 11.5f)
                            horizontalLineTo(8.25f)
                            quadToRelative(-0.32f, 0f, -0.54f, -0.21f)
                            reflectiveQuadTo(7.5f, 10.75f)
                            reflectiveQuadTo(7.71f, 10.21f)
                            reflectiveQuadTo(8.25f, 10f)
                            horizontalLineToRelative(1.5f)
                            quadToRelative(0.33f, 0f, 0.54f, 0.21f)
                            quadToRelative(0.21f, 0.21f, 0.21f, 0.54f)
                            verticalLineToRelative(4.5f)
                            quadToRelative(0f, 0.32f, -0.21f, 0.54f)
                            reflectiveQuadTo(9.75f, 16f)
                            quadTo(9.43f, 16f, 9.21f, 15.79f)
                            reflectiveQuadTo(9f, 15.25f)
                            verticalLineTo(11.5f)
                            close()
                            moveTo(12.5f, 16f)
                            quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
                            reflectiveQuadTo(11.5f, 15f)
                            verticalLineTo(11f)
                            quadToRelative(0f, -0.43f, 0.29f, -0.71f)
                            reflectiveQuadTo(12.5f, 10f)
                            horizontalLineToRelative(2f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(15.5f, 11f)
                            verticalLineToRelative(4f)
                            quadToRelative(0f, 0.42f, -0.29f, 0.71f)
                            reflectiveQuadTo(14.5f, 16f)
                            horizontalLineToRelative(-2f)
                            close()
                            moveTo(13f, 14.5f)
                            horizontalLineToRelative(1f)
                            verticalLineToRelative(-3f)
                            horizontalLineTo(13f)
                            verticalLineToRelative(3f)
                            close()
                            moveTo(8.49f, 21.29f)
                            quadTo(6.85f, 20.58f, 5.64f, 19.36f)
                            reflectiveQuadTo(3.71f, 16.51f)
                            reflectiveQuadTo(3f, 13f)
                            quadTo(3f, 12.58f, 3.29f, 12.29f)
                            reflectiveQuadTo(4f, 12f)
                            reflectiveQuadToRelative(0.71f, 0.29f)
                            reflectiveQuadTo(5f, 13f)
                            quadToRelative(0f, 2.92f, 2.04f, 4.96f)
                            reflectiveQuadTo(12f, 20f)
                            reflectiveQuadToRelative(4.96f, -2.04f)
                            quadTo(19f, 15.93f, 19f, 13f)
                            quadTo(19f, 10.07f, 16.96f, 8.04f)
                            reflectiveQuadTo(12f, 6f)
                            horizontalLineTo(11.85f)
                            lineTo(12.7f, 6.85f)
                            quadToRelative(0.3f, 0.3f, 0.29f, 0.7f)
                            reflectiveQuadTo(12.7f, 8.25f)
                            quadToRelative(-0.3f, 0.3f, -0.71f, 0.31f)
                            quadTo(11.58f, 8.57f, 11.28f, 8.27f)
                            lineTo(8.7f, 5.7f)
                            quadTo(8.4f, 5.4f, 8.4f, 5f)
                            reflectiveQuadTo(8.7f, 4.3f)
                            lineTo(11.28f, 1.72f)
                            quadToRelative(0.3f, -0.3f, 0.71f, -0.29f)
                            reflectiveQuadTo(12.7f, 1.75f)
                            quadToRelative(0.28f, 0.3f, 0.29f, 0.7f)
                            reflectiveQuadTo(12.7f, 3.15f)
                            lineTo(11.85f, 4f)
                            horizontalLineTo(12f)
                            quadToRelative(1.88f, 0f, 3.51f, 0.71f)
                            quadToRelative(1.64f, 0.71f, 2.85f, 1.93f)
                            reflectiveQuadToRelative(1.93f, 2.85f)
                            reflectiveQuadTo(21f, 13f)
                            reflectiveQuadToRelative(-0.71f, 3.51f)
                            reflectiveQuadToRelative(-1.93f, 2.85f)
                            reflectiveQuadToRelative(-2.85f, 1.93f)
                            reflectiveQuadTo(12f, 22f)
                            reflectiveQuadTo(8.49f, 21.29f)
                            close()
                        }
                    }
                    .build()
            return _ten_seconds_left!!
        }

    private var _ten_seconds_right: ImageVector? = null

    @Suppress("CheckReturnValue")
    val TenSecondsRight: ImageVector
        get() {
            if (_ten_seconds_right != null) {
                return _ten_seconds_right!!
            }
            _ten_seconds_right =
                ImageVector.Builder(
                    name = "_forward_10",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(8.49f, 21.29f)
                            quadTo(6.85f, 20.58f, 5.64f, 19.36f)
                            reflectiveQuadTo(3.71f, 16.51f)
                            reflectiveQuadTo(3f, 13f)
                            reflectiveQuadTo(3.71f, 9.49f)
                            reflectiveQuadTo(5.64f, 6.64f)
                            reflectiveQuadTo(8.49f, 4.71f)
                            reflectiveQuadTo(12f, 4f)
                            horizontalLineToRelative(0.15f)
                            lineTo(11.3f, 3.15f)
                            quadTo(11f, 2.85f, 11.01f, 2.45f)
                            reflectiveQuadTo(11.3f, 1.75f)
                            quadToRelative(0.3f, -0.3f, 0.71f, -0.31f)
                            quadToRelative(0.41f, -0.01f, 0.71f, 0.29f)
                            lineTo(15.3f, 4.3f)
                            quadTo(15.6f, 4.6f, 15.6f, 5f)
                            reflectiveQuadTo(15.3f, 5.7f)
                            lineTo(12.73f, 8.27f)
                            quadToRelative(-0.3f, 0.3f, -0.71f, 0.29f)
                            reflectiveQuadTo(11.3f, 8.25f)
                            quadTo(11.03f, 7.95f, 11.01f, 7.55f)
                            reflectiveQuadTo(11.3f, 6.85f)
                            lineTo(12.15f, 6f)
                            horizontalLineTo(12f)
                            quadTo(9.08f, 6f, 7.04f, 8.04f)
                            reflectiveQuadTo(5f, 13f)
                            reflectiveQuadToRelative(2.04f, 4.96f)
                            reflectiveQuadTo(12f, 20f)
                            reflectiveQuadToRelative(4.96f, -2.04f)
                            quadTo(19f, 15.93f, 19f, 13f)
                            quadToRelative(0f, -0.43f, 0.29f, -0.71f)
                            reflectiveQuadTo(20f, 12f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(21f, 13f)
                            quadToRelative(0f, 1.88f, -0.71f, 3.51f)
                            reflectiveQuadToRelative(-1.93f, 2.85f)
                            reflectiveQuadToRelative(-2.85f, 1.93f)
                            reflectiveQuadTo(12f, 22f)
                            reflectiveQuadTo(8.49f, 21.29f)
                            close()
                            moveTo(9f, 11.5f)
                            horizontalLineTo(8.25f)
                            quadToRelative(-0.32f, 0f, -0.54f, -0.21f)
                            reflectiveQuadTo(7.5f, 10.75f)
                            reflectiveQuadTo(7.71f, 10.21f)
                            reflectiveQuadTo(8.25f, 10f)
                            horizontalLineToRelative(1.5f)
                            quadToRelative(0.33f, 0f, 0.54f, 0.21f)
                            quadToRelative(0.21f, 0.21f, 0.21f, 0.54f)
                            verticalLineToRelative(4.5f)
                            quadToRelative(0f, 0.32f, -0.21f, 0.54f)
                            reflectiveQuadTo(9.75f, 16f)
                            quadTo(9.43f, 16f, 9.21f, 15.79f)
                            reflectiveQuadTo(9f, 15.25f)
                            verticalLineTo(11.5f)
                            close()
                            moveTo(12.5f, 16f)
                            quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
                            reflectiveQuadTo(11.5f, 15f)
                            verticalLineTo(11f)
                            quadToRelative(0f, -0.43f, 0.29f, -0.71f)
                            reflectiveQuadTo(12.5f, 10f)
                            horizontalLineToRelative(2f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(15.5f, 11f)
                            verticalLineToRelative(4f)
                            quadToRelative(0f, 0.42f, -0.29f, 0.71f)
                            reflectiveQuadTo(14.5f, 16f)
                            horizontalLineToRelative(-2f)
                            close()
                            moveTo(13f, 14.5f)
                            horizontalLineToRelative(1f)
                            verticalLineToRelative(-3f)
                            horizontalLineTo(13f)
                            verticalLineToRelative(3f)
                            close()
                        }
                    }
                    .build()
            return _ten_seconds_right!!
        }

    private var _thumbDown: ImageVector? = null

    @Suppress("CheckReturnValue")
    val ThumbDown: ImageVector
        get() {
            if (_thumbDown != null) {
                return _thumbDown!!
            }
            _thumbDown =
                ImageVector.Builder(
                    name = "_thumb_down",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(3f, 16f)
                            quadTo(2.2f, 16f, 1.6f, 15.4f)
                            reflectiveQuadTo(1f, 14f)
                            verticalLineTo(12f)
                            quadTo(1f, 11.83f, 1.05f, 11.63f)
                            reflectiveQuadToRelative(0.1f, -0.38f)
                            lineToRelative(3f, -7.05f)
                            quadTo(4.38f, 3.7f, 4.9f, 3.35f)
                            reflectiveQuadTo(6f, 3f)
                            horizontalLineTo(17f)
                            verticalLineTo(16f)
                            lineToRelative(-6f, 5.95f)
                            quadToRelative(-0.38f, 0.38f, -0.89f, 0.44f)
                            reflectiveQuadTo(9.13f, 22.2f)
                            reflectiveQuadTo(8.43f, 21.5f)
                            reflectiveQuadTo(8.33f, 20.58f)
                            lineTo(9.45f, 16f)
                            horizontalLineTo(3f)
                            close()
                            moveTo(15f, 15.15f)
                            verticalLineTo(5f)
                            horizontalLineTo(6f)
                            lineTo(3f, 12f)
                            verticalLineToRelative(2f)
                            horizontalLineToRelative(9f)
                            lineToRelative(-1.35f, 5.5f)
                            lineTo(15f, 15.15f)
                            close()
                            moveTo(20f, 3f)
                            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                            reflectiveQuadTo(22f, 5f)
                            verticalLineToRelative(9f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(20f, 16f)
                            horizontalLineTo(17f)
                            verticalLineTo(14f)
                            horizontalLineToRelative(3f)
                            verticalLineTo(5f)
                            horizontalLineTo(17f)
                            verticalLineTo(3f)
                            horizontalLineToRelative(3f)
                            close()
                            moveTo(15f, 5f)
                            verticalLineTo(15.15f)
                            verticalLineTo(14f)
                            verticalLineTo(12f)
                            verticalLineTo(5f)
                            close()
                        }
                    }
                    .build()
            return _thumbDown!!
        }

    private var _thumbUp: ImageVector? = null

    @Suppress("CheckReturnValue")
    val ThumbUp: ImageVector
        get() {
            if (_thumbUp != null) {
                return _thumbUp!!
            }
            _thumbUp =
                ImageVector.Builder(
                    name = "_thumb_up",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(21f, 8f)
                            quadToRelative(0.8f, 0f, 1.4f, 0.6f)
                            reflectiveQuadTo(23f, 10f)
                            verticalLineToRelative(2f)
                            quadToRelative(0f, 0.17f, -0.05f, 0.38f)
                            reflectiveQuadToRelative(-0.1f, 0.38f)
                            lineToRelative(-3f, 7.05f)
                            quadToRelative(-0.23f, 0.5f, -0.75f, 0.85f)
                            reflectiveQuadTo(18f, 21f)
                            horizontalLineTo(7f)
                            verticalLineTo(8f)
                            lineTo(13f, 2.05f)
                            quadTo(13.38f, 1.67f, 13.89f, 1.61f)
                            reflectiveQuadTo(14.88f, 1.8f)
                            reflectiveQuadToRelative(0.7f, 0.7f)
                            reflectiveQuadToRelative(0.1f, 0.92f)
                            lineTo(14.55f, 8f)
                            horizontalLineTo(21f)
                            close()
                            moveTo(9f, 8.85f)
                            verticalLineTo(19f)
                            horizontalLineToRelative(9f)
                            lineToRelative(3f, -7f)
                            verticalLineTo(10f)
                            horizontalLineTo(12f)
                            lineTo(13.35f, 4.5f)
                            lineTo(9f, 8.85f)
                            close()
                            moveTo(4f, 21f)
                            quadTo(3.18f, 21f, 2.59f, 20.41f)
                            reflectiveQuadTo(2f, 19f)
                            verticalLineTo(10f)
                            quadTo(2f, 9.17f, 2.59f, 8.59f)
                            reflectiveQuadTo(4f, 8f)
                            horizontalLineTo(7f)
                            verticalLineToRelative(2f)
                            horizontalLineTo(4f)
                            verticalLineToRelative(9f)
                            horizontalLineTo(7f)
                            verticalLineToRelative(2f)
                            horizontalLineTo(4f)
                            close()
                            moveTo(9f, 19f)
                            verticalLineTo(8.85f)
                            verticalLineTo(10f)
                            verticalLineToRelative(2f)
                            verticalLineToRelative(7f)
                            close()
                        }
                    }
                    .build()
            return _thumbUp!!
        }

    private var _timer: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Timer: ImageVector
        get() {
            if (_timer != null) {
                return _timer!!
            }
            _timer =
                ImageVector.Builder(
                    name = "_timer",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(10f, 3f)
                            quadTo(9.58f, 3f, 9.29f, 2.71f)
                            reflectiveQuadTo(9f, 2f)
                            quadTo(9f, 1.57f, 9.29f, 1.29f)
                            quadTo(9.58f, 1f, 10f, 1f)
                            horizontalLineToRelative(4f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(15f, 2f)
                            quadToRelative(0f, 0.42f, -0.29f, 0.71f)
                            reflectiveQuadTo(14f, 3f)
                            horizontalLineTo(10f)
                            close()
                            moveToRelative(2.71f, 10.71f)
                            quadTo(13f, 13.43f, 13f, 13f)
                            verticalLineTo(9f)
                            quadTo(13f, 8.57f, 12.71f, 8.29f)
                            reflectiveQuadTo(12f, 8f)
                            reflectiveQuadTo(11.29f, 8.29f)
                            reflectiveQuadTo(11f, 9f)
                            verticalLineToRelative(4f)
                            quadToRelative(0f, 0.42f, 0.29f, 0.71f)
                            reflectiveQuadTo(12f, 14f)
                            reflectiveQuadToRelative(0.71f, -0.29f)
                            close()
                            moveToRelative(-4.2f, 7.58f)
                            quadTo(6.88f, 20.58f, 5.65f, 19.35f)
                            reflectiveQuadTo(3.71f, 16.49f)
                            reflectiveQuadTo(3f, 13f)
                            reflectiveQuadTo(3.71f, 9.51f)
                            reflectiveQuadTo(5.65f, 6.65f)
                            quadTo(6.88f, 5.43f, 8.51f, 4.71f)
                            reflectiveQuadTo(12f, 4f)
                            quadToRelative(1.55f, 0f, 2.98f, 0.5f)
                            reflectiveQuadToRelative(2.68f, 1.45f)
                            lineToRelative(0.7f, -0.7f)
                            quadToRelative(0.27f, -0.28f, 0.7f, -0.28f)
                            reflectiveQuadToRelative(0.7f, 0.28f)
                            quadToRelative(0.28f, 0.27f, 0.28f, 0.7f)
                            reflectiveQuadToRelative(-0.28f, 0.7f)
                            lineToRelative(-0.7f, 0.7f)
                            quadTo(20f, 8.6f, 20.5f, 10.02f)
                            reflectiveQuadTo(21f, 13f)
                            quadToRelative(0f, 1.85f, -0.71f, 3.49f)
                            reflectiveQuadToRelative(-1.94f, 2.86f)
                            reflectiveQuadToRelative(-2.86f, 1.94f)
                            reflectiveQuadTo(12f, 22f)
                            reflectiveQuadTo(8.51f, 21.29f)
                            close()
                            moveToRelative(8.44f, -3.34f)
                            quadTo(19f, 15.9f, 19f, 13f)
                            reflectiveQuadTo(16.95f, 8.05f)
                            reflectiveQuadTo(12f, 6f)
                            reflectiveQuadTo(7.05f, 8.05f)
                            reflectiveQuadTo(5f, 13f)
                            reflectiveQuadToRelative(2.05f, 4.95f)
                            reflectiveQuadTo(12f, 20f)
                            reflectiveQuadToRelative(4.95f, -2.05f)
                            close()
                            moveTo(12f, 13f)
                            close()
                        }
                    }
                    .build()
            return _timer!!
        }

    private var _trash: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Trash: ImageVector
        get() {
            if (_trash != null) {
                return _trash!!
            }
            _trash =
                ImageVector.Builder(
                    name = "_trash",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(7f, 21f)
                            quadTo(6.18f, 21f, 5.59f, 20.41f)
                            reflectiveQuadTo(5f, 19f)
                            verticalLineTo(6f)
                            quadTo(4.58f, 6f, 4.29f, 5.71f)
                            quadTo(4f, 5.43f, 4f, 5f)
                            reflectiveQuadTo(4.29f, 4.29f)
                            reflectiveQuadTo(5f, 4f)
                            horizontalLineTo(9f)
                            quadTo(9f, 3.57f, 9.29f, 3.29f)
                            quadTo(9.58f, 3f, 10f, 3f)
                            horizontalLineToRelative(4f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(15f, 4f)
                            horizontalLineToRelative(4f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(20f, 5f)
                            reflectiveQuadTo(19.71f, 5.71f)
                            reflectiveQuadTo(19f, 6f)
                            verticalLineTo(19f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(17f, 21f)
                            horizontalLineTo(7f)
                            close()
                            moveTo(17f, 6f)
                            horizontalLineTo(7f)
                            verticalLineTo(19f)
                            horizontalLineTo(17f)
                            verticalLineTo(6f)
                            close()
                            moveTo(10.71f, 16.71f)
                            quadTo(11f, 16.43f, 11f, 16f)
                            verticalLineTo(9f)
                            quadTo(11f, 8.57f, 10.71f, 8.29f)
                            reflectiveQuadTo(10f, 8f)
                            quadTo(9.58f, 8f, 9.29f, 8.29f)
                            reflectiveQuadTo(9f, 9f)
                            verticalLineToRelative(7f)
                            quadToRelative(0f, 0.43f, 0.29f, 0.71f)
                            quadTo(9.58f, 17f, 10f, 17f)
                            reflectiveQuadToRelative(0.71f, -0.29f)
                            close()
                            moveToRelative(4f, 0f)
                            quadTo(15f, 16.43f, 15f, 16f)
                            verticalLineTo(9f)
                            quadTo(15f, 8.57f, 14.71f, 8.29f)
                            reflectiveQuadTo(14f, 8f)
                            reflectiveQuadTo(13.29f, 8.29f)
                            reflectiveQuadTo(13f, 9f)
                            verticalLineToRelative(7f)
                            quadToRelative(0f, 0.43f, 0.29f, 0.71f)
                            reflectiveQuadTo(14f, 17f)
                            reflectiveQuadToRelative(0.71f, -0.29f)
                            close()
                            moveTo(7f, 6f)
                            verticalLineTo(19f)
                            verticalLineTo(6f)
                            close()
                        }
                    }
                    .build()
            return _trash!!
        }

    private var _users: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Users: ImageVector
        get() {
            if (_users != null) {
                return _users!!
            }
            _users =
                ImageVector.Builder(
                    name = "_users",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(1f, 17.2f)
                            quadTo(1f, 16.35f, 1.44f, 15.64f)
                            quadTo(1.88f, 14.93f, 2.6f, 14.55f)
                            quadTo(4.15f, 13.77f, 5.75f, 13.39f)
                            reflectiveQuadTo(9f, 13f)
                            reflectiveQuadToRelative(3.25f, 0.39f)
                            reflectiveQuadToRelative(3.15f, 1.16f)
                            quadToRelative(0.72f, 0.38f, 1.16f, 1.09f)
                            reflectiveQuadTo(17f, 17.2f)
                            verticalLineTo(18f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(15f, 20f)
                            horizontalLineTo(3f)
                            quadTo(2.18f, 20f, 1.59f, 19.41f)
                            reflectiveQuadTo(1f, 18f)
                            verticalLineTo(17.2f)
                            close()
                            moveTo(21f, 20f)
                            horizontalLineTo(18.45f)
                            quadToRelative(0.28f, -0.45f, 0.41f, -0.96f)
                            quadTo(19f, 18.52f, 19f, 18f)
                            verticalLineTo(17f)
                            quadToRelative(0f, -1.1f, -0.61f, -2.11f)
                            quadTo(17.78f, 13.88f, 16.65f, 13.15f)
                            quadToRelative(1.27f, 0.15f, 2.4f, 0.51f)
                            quadToRelative(1.13f, 0.36f, 2.1f, 0.89f)
                            quadToRelative(0.9f, 0.5f, 1.38f, 1.11f)
                            reflectiveQuadTo(23f, 17f)
                            verticalLineToRelative(1f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(21f, 20f)
                            close()
                            moveTo(6.18f, 10.83f)
                            quadTo(5f, 9.65f, 5f, 8f)
                            reflectiveQuadTo(6.18f, 5.18f)
                            reflectiveQuadTo(9f, 4f)
                            reflectiveQuadToRelative(2.83f, 1.18f)
                            reflectiveQuadTo(13f, 8f)
                            reflectiveQuadToRelative(-1.17f, 2.82f)
                            reflectiveQuadTo(9f, 12f)
                            reflectiveQuadTo(6.18f, 10.83f)
                            close()
                            moveToRelative(11.65f, 0f)
                            quadTo(16.65f, 12f, 15f, 12f)
                            quadToRelative(-0.27f, 0f, -0.7f, -0.06f)
                            reflectiveQuadTo(13.6f, 11.8f)
                            quadTo(14.28f, 11f, 14.64f, 10.02f)
                            reflectiveQuadTo(15f, 8f)
                            reflectiveQuadTo(14.64f, 5.97f)
                            reflectiveQuadTo(13.6f, 4.2f)
                            quadTo(13.95f, 4.07f, 14.3f, 4.04f)
                            reflectiveQuadTo(15f, 4f)
                            quadToRelative(1.65f, 0f, 2.82f, 1.18f)
                            reflectiveQuadTo(19f, 8f)
                            reflectiveQuadToRelative(-1.18f, 2.82f)
                            close()
                            moveTo(3f, 18f)
                            horizontalLineTo(15f)
                            verticalLineTo(17.2f)
                            quadToRelative(0f, -0.27f, -0.14f, -0.5f)
                            quadTo(14.73f, 16.48f, 14.5f, 16.35f)
                            quadTo(13.15f, 15.68f, 11.78f, 15.34f)
                            reflectiveQuadTo(9f, 15f)
                            reflectiveQuadTo(6.23f, 15.34f)
                            reflectiveQuadTo(3.5f, 16.35f)
                            quadTo(3.28f, 16.48f, 3.14f, 16.7f)
                            quadTo(3f, 16.93f, 3f, 17.2f)
                            verticalLineTo(18f)
                            close()
                            moveTo(10.41f, 9.41f)
                            quadTo(11f, 8.82f, 11f, 8f)
                            reflectiveQuadTo(10.41f, 6.59f)
                            reflectiveQuadTo(9f, 6f)
                            quadTo(8.18f, 6f, 7.59f, 6.59f)
                            quadTo(7f, 7.18f, 7f, 8f)
                            reflectiveQuadTo(7.59f, 9.41f)
                            reflectiveQuadTo(9f, 10f)
                            quadToRelative(0.83f, 0f, 1.41f, -0.59f)
                            close()
                            moveTo(9f, 18f)
                            close()
                            moveTo(9f, 8f)
                            close()
                        }
                    }
                    .build()
            return _users!!
        }

    private var _voiceActors: ImageVector? = null

    @Suppress("CheckReturnValue")
    val VoiceActors: ImageVector
        get() {
            if (_voiceActors != null) {
                return _voiceActors!!
            }
            _voiceActors =
                ImageVector.Builder(
                    name = "_adaptive_audio_mic",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(17.78f, 12f)
                            close()
                            moveTo(19f, 14f)
                            quadToRelative(-0.43f, 0f, -0.71f, -0.29f)
                            quadTo(18f, 13.43f, 18f, 13f)
                            reflectiveQuadToRelative(0.29f, -0.71f)
                            reflectiveQuadTo(19f, 12f)
                            horizontalLineToRelative(1.93f)
                            quadTo(20.83f, 11.83f, 20.7f, 11.7f)
                            reflectiveQuadTo(20.4f, 11.48f)
                            quadTo(19.75f, 11.1f, 18.91f, 10.93f)
                            reflectiveQuadTo(17f, 10.75f)
                            quadToRelative(-0.13f, 0f, -0.26f, 0f)
                            reflectiveQuadToRelative(-0.26f, 0f)
                            quadToRelative(-0.43f, 0f, -0.7f, -0.28f)
                            reflectiveQuadTo(15.5f, 9.77f)
                            reflectiveQuadTo(15.74f, 9.09f)
                            reflectiveQuadTo(16.4f, 8.77f)
                            quadTo(16.53f, 8.75f, 16.7f, 8.75f)
                            quadToRelative(0.18f, 0f, 0.3f, 0f)
                            quadToRelative(1.32f, 0f, 2.48f, 0.27f)
                            reflectiveQuadToRelative(2.15f, 0.8f)
                            quadToRelative(0.65f, 0.35f, 1.01f, 1.04f)
                            reflectiveQuadTo(23f, 12.43f)
                            verticalLineTo(13f)
                            quadToRelative(0f, 0.42f, -0.29f, 0.71f)
                            reflectiveQuadTo(22f, 14f)
                            horizontalLineTo(19f)
                            close()
                            moveTo(17f, 8f)
                            quadTo(15.75f, 8f, 14.88f, 7.13f)
                            reflectiveQuadTo(14f, 5f)
                            reflectiveQuadTo(14.88f, 2.88f)
                            reflectiveQuadTo(17f, 2f)
                            reflectiveQuadToRelative(2.13f, 0.88f)
                            reflectiveQuadTo(20f, 5f)
                            reflectiveQuadTo(19.13f, 7.13f)
                            reflectiveQuadTo(17f, 8f)
                            close()
                            moveTo(17f, 6f)
                            quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                            quadTo(18f, 5.43f, 18f, 5f)
                            reflectiveQuadTo(17.71f, 4.29f)
                            reflectiveQuadTo(17f, 4f)
                            reflectiveQuadTo(16.29f, 4.29f)
                            reflectiveQuadTo(16f, 5f)
                            reflectiveQuadToRelative(0.29f, 0.71f)
                            reflectiveQuadTo(17f, 6f)
                            close()
                            moveTo(17f, 5f)
                            close()
                            moveTo(6.23f, 12f)
                            close()
                            moveTo(2f, 14f)
                            quadTo(1.58f, 14f, 1.29f, 13.71f)
                            quadTo(1f, 13.43f, 1f, 13f)
                            verticalLineTo(12.43f)
                            quadTo(1f, 11.55f, 1.36f, 10.86f)
                            reflectiveQuadTo(2.38f, 9.82f)
                            quadToRelative(1f, -0.53f, 2.15f, -0.8f)
                            reflectiveQuadTo(7f, 8.75f)
                            quadToRelative(0.13f, 0f, 0.3f, 0f)
                            reflectiveQuadTo(7.6f, 8.77f)
                            quadTo(8.03f, 8.82f, 8.26f, 9.09f)
                            reflectiveQuadTo(8.5f, 9.77f)
                            reflectiveQuadToRelative(-0.28f, 0.7f)
                            reflectiveQuadToRelative(-0.7f, 0.28f)
                            quadToRelative(-0.13f, 0f, -0.26f, 0f)
                            reflectiveQuadTo(7f, 10.75f)
                            quadToRelative(-1.07f, 0f, -1.91f, 0.17f)
                            reflectiveQuadTo(3.6f, 11.48f)
                            quadTo(3.43f, 11.58f, 3.3f, 11.7f)
                            reflectiveQuadTo(3.08f, 12f)
                            horizontalLineTo(5f)
                            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                            reflectiveQuadTo(6f, 13f)
                            reflectiveQuadTo(5.71f, 13.71f)
                            reflectiveQuadTo(5f, 14f)
                            horizontalLineTo(2f)
                            close()
                            moveTo(7f, 8f)
                            quadTo(5.75f, 8f, 4.88f, 7.13f)
                            reflectiveQuadTo(4f, 5f)
                            reflectiveQuadTo(4.88f, 2.88f)
                            reflectiveQuadTo(7f, 2f)
                            reflectiveQuadTo(9.13f, 2.88f)
                            reflectiveQuadTo(10f, 5f)
                            reflectiveQuadTo(9.13f, 7.13f)
                            reflectiveQuadTo(7f, 8f)
                            close()
                            moveTo(7f, 6f)
                            quadTo(7.43f, 6f, 7.71f, 5.71f)
                            quadTo(8f, 5.43f, 8f, 5f)
                            reflectiveQuadTo(7.71f, 4.29f)
                            reflectiveQuadTo(7f, 4f)
                            quadTo(6.58f, 4f, 6.29f, 4.29f)
                            reflectiveQuadTo(6f, 5f)
                            reflectiveQuadTo(6.29f, 5.71f)
                            reflectiveQuadTo(7f, 6f)
                            close()
                            moveTo(7f, 5f)
                            close()
                            moveToRelative(5f, 12f)
                            quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
                            reflectiveQuadTo(10f, 15f)
                            verticalLineTo(12f)
                            quadToRelative(0f, -0.83f, 0.59f, -1.41f)
                            reflectiveQuadTo(12f, 10f)
                            reflectiveQuadToRelative(1.41f, 0.59f)
                            quadTo(14f, 11.18f, 14f, 12f)
                            verticalLineToRelative(3f)
                            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                            reflectiveQuadTo(12f, 17f)
                            close()
                            moveToRelative(-0.54f, 4.79f)
                            quadTo(11.25f, 21.58f, 11.25f, 21.25f)
                            verticalLineToRelative(-1.3f)
                            quadTo(9.63f, 19.7f, 8.46f, 18.55f)
                            reflectiveQuadTo(7.08f, 15.88f)
                            quadTo(7.03f, 15.53f, 7.25f, 15.26f)
                            reflectiveQuadTo(7.83f, 15f)
                            quadTo(8.1f, 15f, 8.3f, 15.16f)
                            reflectiveQuadTo(8.55f, 15.6f)
                            quadToRelative(0.28f, 1.3f, 1.22f, 2.1f)
                            reflectiveQuadTo(12f, 18.5f)
                            quadToRelative(1.28f, 0f, 2.23f, -0.8f)
                            reflectiveQuadToRelative(1.22f, -2.1f)
                            quadTo(15.5f, 15.33f, 15.7f, 15.16f)
                            reflectiveQuadTo(16.18f, 15f)
                            quadToRelative(0.35f, 0f, 0.57f, 0.26f)
                            quadToRelative(0.23f, 0.26f, 0.18f, 0.61f)
                            quadTo(16.7f, 17.4f, 15.54f, 18.55f)
                            reflectiveQuadToRelative(-2.79f, 1.4f)
                            verticalLineToRelative(1.3f)
                            quadToRelative(0f, 0.32f, -0.21f, 0.54f)
                            reflectiveQuadTo(12f, 22f)
                            reflectiveQuadTo(11.46f, 21.79f)
                            close()
                        }
                    }
                    .build()
            return _voiceActors!!
        }

    private var _volumeOff: ImageVector? = null

    @Suppress("CheckReturnValue")
    val VolumeOff: ImageVector
        get() {
            if (_volumeOff != null) {
                return _volumeOff!!
            }
            _volumeOff =
                ImageVector.Builder(
                    name = "_volume_off",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(16.78f, 19.58f)
                            quadTo(16.5f, 19.75f, 16.23f, 19.9f)
                            reflectiveQuadToRelative(-0.58f, 0.28f)
                            quadToRelative(-0.38f, 0.18f, -0.76f, 0f)
                            reflectiveQuadTo(14.35f, 19.6f)
                            quadTo(14.2f, 19.23f, 14.39f, 18.86f)
                            reflectiveQuadToRelative(0.56f, -0.54f)
                            quadToRelative(0.1f, -0.05f, 0.19f, -0.1f)
                            reflectiveQuadToRelative(0.19f, -0.1f)
                            lineTo(12f, 14.8f)
                            verticalLineToRelative(2.78f)
                            quadToRelative(0f, 0.68f, -0.61f, 0.94f)
                            reflectiveQuadTo(10.3f, 18.3f)
                            lineTo(7f, 15f)
                            horizontalLineTo(4f)
                            quadTo(3.58f, 15f, 3.29f, 14.71f)
                            reflectiveQuadTo(3f, 14f)
                            verticalLineTo(10f)
                            quadTo(3f, 9.57f, 3.29f, 9.29f)
                            reflectiveQuadTo(4f, 9f)
                            horizontalLineTo(6.2f)
                            lineTo(2.1f, 4.9f)
                            quadTo(1.83f, 4.63f, 1.83f, 4.2f)
                            reflectiveQuadTo(2.1f, 3.5f)
                            quadTo(2.38f, 3.22f, 2.8f, 3.22f)
                            reflectiveQuadTo(3.5f, 3.5f)
                            lineToRelative(17f, 17f)
                            quadToRelative(0.28f, 0.27f, 0.28f, 0.7f)
                            reflectiveQuadTo(20.5f, 21.9f)
                            quadToRelative(-0.27f, 0.28f, -0.7f, 0.28f)
                            reflectiveQuadTo(19.1f, 21.9f)
                            lineTo(16.78f, 19.58f)
                            close()
                            moveTo(19f, 11.98f)
                            quadTo(19f, 9.9f, 17.9f, 8.19f)
                            quadTo(16.8f, 6.47f, 14.95f, 5.63f)
                            quadTo(14.58f, 5.45f, 14.4f, 5.09f)
                            reflectiveQuadTo(14.35f, 4.35f)
                            quadTo(14.5f, 3.95f, 14.89f, 3.77f)
                            reflectiveQuadToRelative(0.79f, 0f)
                            quadToRelative(2.43f, 1.07f, 3.88f, 3.28f)
                            reflectiveQuadTo(21f, 11.98f)
                            quadToRelative(0f, 0.82f, -0.15f, 1.64f)
                            reflectiveQuadToRelative(-0.43f, 1.56f)
                            quadToRelative(-0.2f, 0.55f, -0.61f, 0.69f)
                            reflectiveQuadToRelative(-0.76f, 0.01f)
                            reflectiveQuadTo(18.49f, 15.43f)
                            reflectiveQuadTo(18.48f, 14.68f)
                            quadToRelative(0.27f, -0.65f, 0.4f, -1.31f)
                            reflectiveQuadTo(19f, 11.98f)
                            close()
                            moveTo(14.78f, 8.42f)
                            quadTo(15.6f, 8.95f, 16.05f, 10f)
                            reflectiveQuadToRelative(0.45f, 2f)
                            quadToRelative(0f, 0.13f, 0f, 0.25f)
                            reflectiveQuadTo(16.48f, 12.5f)
                            quadToRelative(-0.05f, 0.32f, -0.35f, 0.42f)
                            reflectiveQuadTo(15.58f, 12.77f)
                            lineTo(14.3f, 11.5f)
                            quadTo(14.15f, 11.35f, 14.08f, 11.16f)
                            reflectiveQuadTo(14f, 10.77f)
                            verticalLineTo(8.85f)
                            quadToRelative(0f, -0.3f, 0.26f, -0.44f)
                            quadToRelative(0.26f, -0.14f, 0.51f, 0.01f)
                            close()
                            moveTo(9.75f, 6.95f)
                            quadTo(9.6f, 6.8f, 9.6f, 6.6f)
                            reflectiveQuadTo(9.75f, 6.25f)
                            lineTo(10.3f, 5.7f)
                            quadTo(10.78f, 5.22f, 11.39f, 5.49f)
                            reflectiveQuadTo(12f, 6.43f)
                            verticalLineTo(8f)
                            quadToRelative(0f, 0.35f, -0.3f, 0.47f)
                            reflectiveQuadTo(11.15f, 8.35f)
                            lineTo(9.75f, 6.95f)
                            close()
                            moveTo(10f, 15.15f)
                            verticalLineTo(12.8f)
                            lineTo(8.2f, 11f)
                            horizontalLineTo(5f)
                            verticalLineToRelative(2f)
                            horizontalLineTo(7.85f)
                            lineTo(10f, 15.15f)
                            close()
                            moveTo(9.1f, 11.9f)
                            close()
                        }
                    }
                    .build()
            return _volumeOff!!
        }

    private var _volumeUp: ImageVector? = null

    @Suppress("CheckReturnValue")
    val VolumeUp: ImageVector
        get() {
            if (_volumeUp != null) {
                return _volumeUp!!
            }
            _volumeUp =
                ImageVector.Builder(
                    name = "_volume_up",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(19f, 11.98f)
                            quadTo(19f, 9.9f, 17.9f, 8.19f)
                            quadTo(16.8f, 6.47f, 14.95f, 5.63f)
                            quadTo(14.58f, 5.45f, 14.4f, 5.09f)
                            reflectiveQuadTo(14.35f, 4.35f)
                            quadTo(14.5f, 3.95f, 14.89f, 3.77f)
                            reflectiveQuadToRelative(0.79f, 0f)
                            quadToRelative(2.43f, 1.07f, 3.88f, 3.29f)
                            quadTo(21f, 9.27f, 21f, 11.98f)
                            reflectiveQuadToRelative(-1.45f, 4.91f)
                            reflectiveQuadToRelative(-3.88f, 3.29f)
                            quadToRelative(-0.4f, 0.18f, -0.79f, 0f)
                            reflectiveQuadTo(14.35f, 19.6f)
                            quadTo(14.23f, 19.23f, 14.4f, 18.86f)
                            reflectiveQuadToRelative(0.55f, -0.54f)
                            quadTo(16.8f, 17.48f, 17.9f, 15.76f)
                            reflectiveQuadTo(19f, 11.98f)
                            close()
                            moveTo(7f, 15f)
                            horizontalLineTo(4f)
                            quadTo(3.58f, 15f, 3.29f, 14.71f)
                            reflectiveQuadTo(3f, 14f)
                            verticalLineTo(10f)
                            quadTo(3f, 9.57f, 3.29f, 9.29f)
                            reflectiveQuadTo(4f, 9f)
                            horizontalLineTo(7f)
                            lineTo(10.3f, 5.7f)
                            quadTo(10.78f, 5.22f, 11.39f, 5.49f)
                            reflectiveQuadTo(12f, 6.43f)
                            verticalLineTo(17.58f)
                            quadToRelative(0f, 0.68f, -0.61f, 0.94f)
                            reflectiveQuadTo(10.3f, 18.3f)
                            lineTo(7f, 15f)
                            close()
                            moveToRelative(9.5f, -3f)
                            quadToRelative(0f, 1.05f, -0.47f, 1.99f)
                            reflectiveQuadToRelative(-1.25f, 1.54f)
                            quadToRelative(-0.25f, 0.15f, -0.51f, 0.01f)
                            reflectiveQuadTo(14f, 15.1f)
                            verticalLineTo(8.85f)
                            quadToRelative(0f, -0.3f, 0.26f, -0.44f)
                            quadToRelative(0.26f, -0.14f, 0.51f, 0.01f)
                            quadTo(15.55f, 9.05f, 16.03f, 10f)
                            reflectiveQuadToRelative(0.47f, 2f)
                            close()
                            moveTo(10f, 8.85f)
                            lineTo(7.85f, 11f)
                            horizontalLineTo(5f)
                            verticalLineToRelative(2f)
                            horizontalLineTo(7.85f)
                            lineTo(10f, 15.15f)
                            verticalLineTo(8.85f)
                            close()
                            moveTo(7.5f, 12f)
                            close()
                        }
                    }
                    .build()
            return _volumeUp!!
        }

    private var _website: ImageVector? = null

    @Suppress("CheckReturnValue")
    val Website: ImageVector
        get() {
            if (_website != null) {
                return _website!!
            }
            _website =
                ImageVector.Builder(
                    name = "_website",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.NonZero,
                        ) {
                            moveTo(12f, 22f)
                            quadTo(9.93f, 22f, 8.1f, 21.21f)
                            quadTo(6.28f, 20.43f, 4.93f, 19.08f)
                            quadTo(3.58f, 17.73f, 2.79f, 15.9f)
                            reflectiveQuadTo(2f, 12f)
                            quadTo(2f, 9.92f, 2.79f, 8.1f)
                            quadTo(3.58f, 6.27f, 4.93f, 4.93f)
                            quadTo(6.28f, 3.57f, 8.1f, 2.79f)
                            quadTo(9.93f, 2f, 12f, 2f)
                            reflectiveQuadToRelative(3.9f, 0.79f)
                            reflectiveQuadToRelative(3.17f, 2.14f)
                            quadToRelative(1.35f, 1.35f, 2.14f, 3.17f)
                            quadTo(22f, 9.92f, 22f, 12f)
                            reflectiveQuadToRelative(-0.79f, 3.9f)
                            reflectiveQuadToRelative(-2.14f, 3.17f)
                            quadToRelative(-1.35f, 1.35f, -3.17f, 2.14f)
                            reflectiveQuadTo(12f, 22f)
                            close()
                            moveToRelative(0f, -2f)
                            quadToRelative(3.35f, 0f, 5.68f, -2.32f)
                            reflectiveQuadTo(20f, 12f)
                            quadToRelative(0f, -0.18f, -0.01f, -0.36f)
                            reflectiveQuadTo(19.98f, 11.33f)
                            quadToRelative(-0.13f, 0.72f, -0.68f, 1.2f)
                            reflectiveQuadTo(18f, 13f)
                            horizontalLineTo(16f)
                            quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
                            reflectiveQuadTo(14f, 11f)
                            verticalLineTo(10f)
                            horizontalLineTo(10f)
                            verticalLineTo(8f)
                            quadTo(10f, 7.18f, 10.59f, 6.59f)
                            reflectiveQuadTo(12f, 6f)
                            horizontalLineToRelative(1f)
                            quadTo(13f, 5.43f, 13.31f, 4.99f)
                            reflectiveQuadTo(14.08f, 4.27f)
                            quadTo(13.58f, 4.15f, 13.06f, 4.07f)
                            reflectiveQuadTo(12f, 4f)
                            quadTo(8.65f, 4f, 6.33f, 6.32f)
                            reflectiveQuadTo(4f, 12f)
                            horizontalLineTo(9f)
                            quadToRelative(1.65f, 0f, 2.83f, 1.17f)
                            reflectiveQuadTo(13f, 16f)
                            verticalLineToRelative(1f)
                            horizontalLineTo(10f)
                            verticalLineToRelative(2.75f)
                            quadToRelative(0.5f, 0.13f, 0.99f, 0.19f)
                            reflectiveQuadTo(12f, 20f)
                            close()
                        }
                    }
                    .build()
            return _website!!
        }
}
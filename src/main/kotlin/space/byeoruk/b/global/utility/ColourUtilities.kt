package space.byeoruk.b.global.utility

import kotlin.random.Random

object ColourUtilities {
    private val colours = listOf(
        0xFFF44336, // Red
        0xFFE91E63, // Pink
        0xFF9C27B0, // Purple
        0xFF673AB7, // Deep Purple
        0xFF3F51B5, // Indigo
        0xFF2196F3, // Blue
        0xFF03A9F4, // Light Blue
        0xFF00BCD4, // Cyan
        0xFF009688, // Teal
        0xFF4CAF50, // Green
        0xFF8BC34A, // Light Green
        0xFFCDDC39, // Lime
        0xFFFFEB3B, // Yellow
        0xFFFFC107, // Amber
        0xFFFF9800, // Orange
        0xFFFF5722, // Deep Orange
        0xFF795548, // Brown
        0xFF607D8B  // Blue Grey
    )

    /**
     * 무작위로 새상 추출
     *
     * @return 색상 RGB 코드
     */
    fun random(): String {
        val colour = colours.random(Random)

        val r = (colour shr 16) and 0xFF
        val g = (colour shr 8) and 0xFF
        val b = colour and 0xFF

        return String.format("#%02X%02X%02X", r, g, b)
    }
}
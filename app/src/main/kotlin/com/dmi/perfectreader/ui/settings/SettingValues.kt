package com.dmi.perfectreader.ui.settings

import com.dmi.util.collection.binarySearchGreater
import com.dmi.util.collection.binarySearchLower
import java.lang.Math.max
import java.lang.Math.min

// todo compute values by formula instead hardcode
object SettingValues {
    val FONT_SIZE_DIP = floatArrayOf(
            4F,
            4.5F, 5F, 5.5F, 6F, 6.5F, 7F, 7.5F, 8F,
            9F, 10F, 11F, 12F, 13F, 14F, 15F, 16F,
            18F, 20F, 22F, 24F, 26F, 28F, 30F, 32F,
            36F, 40F, 44F, 48F, 52F, 56F, 60F, 64F,
            72F, 80F, 88F, 96F, 104F, 112F, 120F, 128F
    )

    val FONT_SKEW = floatArrayOf(
            -1.5F, -1.4F, -1.3F, -1.2F, -1.1F,
            -1F, -0.9F, -0.8F, -0.7F, -0.6F, -0.5F, -0.4F, -0.3F, -0.2F, -0.1F,
            0F,
            0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F, 1F,
            1.1F, 1.2F, 1.3F, 1.4F, 1.5F
    )

    val FONT_WIDTH = floatArrayOf(
            0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F, 1F,
            1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F, 1.8F, 1.9F, 2F,
            2.2F, 2.4F, 2.6F, 2.8F, 3F
    )

    val FONT_BOLDNESS_EM = floatArrayOf(
            0F,
            0.05F, 0.1F, 0.15F, 0.2F, 0.25F, 0.3F, 0.35F, 0.4F, 0.45F, 0.5F,
            0.6F, 0.7F, 0.8F, 0.9F, 1.0F,
            1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F, 1.8F, 1.9F, 2.0F
    )

    val FONT_LETTER_SPACING_EM = floatArrayOf(
            -0.4F, -0.35F, -0.3F, -0.25F, -0.2F, -0.15F, -0.1F, -0.05F, 0F,
            0.05F, 0.1F, 0.15F, 0.2F, 0.25F, 0.3F, 0.35F, 0.4F, 0.45F, 0.5F,
            0.6F, 0.7F, 0.8F, 0.9F, 1.0F
    )

    val FORMAT_PADDING = floatArrayOf(
            0F, 1F, 2F, 3F, 4F, 5F, 6F, 7F, 8F,
            10F, 12F, 16F, 18F, 20F, 22F, 24F, 26F, 28F, 30F, 32F
    )

    val FORMAT_PARAGRAPH_VERTICAL_MARGIN_EM = floatArrayOf(
            0F,
            0.05F, 0.1F, 0.15F, 0.2F, 0.25F, 0.3F, 0.35F, 0.4F, 0.45F, 0.5F,
            0.6F, 0.7F, 0.8F, 0.9F, 1.0F,
            1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F, 1.8F, 1.9F, 2.0F
    )

    val FORMAT_LINE_HEIGHT_MULTIPLIER = floatArrayOf(
            0.3F, 0.35F, 0.4F, 0.45F, 0.5F,
            0.55F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 0.95F, 1.0F,
            1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F, 1.8F, 1.9F, 2.0F,
            2.2F, 2.4F, 2.6F, 2.8F, 3.0F, 3.2F, 3.4F, 3.6F, 3.8F, 4.0F
    )

    val FORMAT_FIRST_LINE_INDENT_EM = floatArrayOf(
            0F,
            0.05F, 0.1F, 0.15F, 0.2F, 0.25F, 0.3F, 0.35F, 0.4F, 0.45F, 0.5F,
            0.6F, 0.7F, 0.8F, 0.9F, 1.0F,
            1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F, 1.8F, 1.9F, 2.0F,
            2.2F, 2.4F, 2.6F, 2.8F, 3.0F, 3.2F, 3.4F, 3.6F, 3.8F, 4F
    )

    val THEME_GAMMA_CORRECTION = floatArrayOf(
            0.4F, 0.45F, 0.5F, 0.55F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 0.95F,
            1.0F, 1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F, 1.8F, 1.9F, 2.0F,
            2.1F, 2.2F, 2.3F, 2.4F
    )

    val THEME_TEXT_SHADOW_ANGLE = (-180..180 step 5).map { it.toFloat() }.toFloatArray()
    val THEME_TEXT_SHADOW_OFFSET = (0..25).map { it.toFloat() / 100 }.toFloatArray()
    val THEME_TEXT_SHADOW_SIZE = (0..25).map { it.toFloat() / 100 }.toFloatArray()
    val THEME_TEXT_SHADOW_BLUR = (0..25).map { it.toFloat() / 100 }.toFloatArray()
    val THEME_TEXT_SHADOW_OPACITY = (0..25).map { it.toFloat() / 25 }.toFloatArray()

    val SCREEN_BRIGHTNESS = (0..100).map { it.toFloat() / 100 }.toFloatArray()
}

fun chooseSettingValue(values: FloatArray, value: Float, indexOffset: Int): Float = when {
    indexOffset < 0 -> {
        val lowerIndex = values.binarySearchLower(value)
        if (lowerIndex >= 0) {
            val offsetIndex = max(0, lowerIndex + indexOffset + 1)
            values[offsetIndex]
        } else {
            value
        }
    }
    indexOffset > 0 -> {
        val greaterIndex = values.binarySearchGreater(value)
        if (greaterIndex <= values.size - 1) {
            val offsetIndex = min(values.size - 1, greaterIndex + indexOffset - 1)
            values[offsetIndex]
        } else {
            value
        }
    }
    else -> value
}
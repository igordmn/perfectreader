package com.dmi.perfectreader.settingschange

import com.dmi.util.collection.binarySearchGreater
import com.dmi.util.collection.binarySearchLower
import java.lang.Math.max
import java.lang.Math.min

object SettingValues {
    val TEXT_SIZE = floatArrayOf(
            4F,
            4.5F, 5F, 5.5F, 6F, 6.5F, 7F, 7.5F, 8F,
            9F, 10F, 11F, 12F, 13F, 14F, 15F, 16F,
            18F, 20F, 22F, 24F, 26F, 28F, 30F, 32F,
            36F, 40F, 44F, 48F, 52F, 56F, 60F, 64F,
            72F, 80F, 88F, 96F, 104F, 112F, 120F, 128F
    )

    val TEXT_SKEW_X = floatArrayOf(
            -1.5F, -1.4F, -1.3F, -1.2F, -1.1F,
            -1F, -0.9F, -0.8F, -0.7F, -0.6F, -0.5F, -0.4F, -0.3F, -0.2F, -0.1F,
            0F,
            0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F, 1F,
            1.1F, 1.2F, 1.3F, 1.4F, 1.5F
    )

    val TEXT_SCALE_X = floatArrayOf(
            0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F, 1F,
            1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F, 1.8F, 1.9F, 2F,
            2.2F, 2.4F, 2.6F, 2.8F, 3F
    )

    val LINE_HEIGHT_MULTIPLIER = floatArrayOf(
            0.3F, 0.35F, 0.4F, 0.45F, 0.5F,
            0.55F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 0.95F, 1.0F,
            1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F, 1.8F, 1.9F, 2.0F,
            2.2F, 2.4F, 2.6F, 2.8F, 3.0F, 3.2F, 3.4F, 3.6F, 3.8F, 4.0F
    )

    val GAMMA = floatArrayOf(
            0.4F, 0.45F, 0.5F, 0.55F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 0.95F,
            1.0F, 1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F, 1.8F, 1.9F, 2.0F,
            2.1F, 2.2F, 2.3F, 2.4F
    )

    val TEXT_STROKE_WIDTH = floatArrayOf(
            0F,
            0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F,
            2.0F, 2.1F, 2.2F, 2.3F, 2.4F, 2.5F, 2.6F, 2.7F, 2.8F, 2.9F, 3.0F,
            3.1F, 3.2F, 3.3F, 3.4F, 3.5F, 3.6F, 3.7F, 3.8F, 3.9F, 4.0F
    )

    val TEXT_LETTER_SPACING = floatArrayOf(
            -0.4F, -0.35F, -0.3F, -0.25F, -0.2F, -0.15F, -0.1F, -0.05F, 0F,
            0.05F, 0.1F, 0.15F, 0.2F, 0.25F, 0.3F, 0.35F, 0.4F, 0.45F, 0.5F,
            0.6F, 0.7F, 0.8F, 0.9F, 1.0F
    )
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
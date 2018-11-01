package com.dmi.perfectreader.book.content.obj.common

import com.dmi.perfectreader.book.content.configure.common.ConfiguredLength
import java.io.Serializable

interface ContentLength : Serializable {
    fun configure(config: ContentConfig, style: ContentStyle): ConfiguredLength

    operator fun times(multiplier: Float): ContentLength

    object Zero : ContentLength {
        override fun configure(config: ContentConfig, style: ContentStyle) = ConfiguredLength.Zero
        override operator fun times(multiplier: Float) = ContentLength.Zero
    }

    data class Dip(val value: Float) : ContentLength {
        init {
            require(value >= 0F)
        }

        override fun configure(config: ContentConfig, style: ContentStyle) = ConfiguredLength.Absolute(value * config.density)
        override operator fun times(multiplier: Float) = ContentLength.Dip(value * multiplier)
    }

    data class Em(val value: Float) : ContentLength {
        init {
            require(value >= 0F)
        }

        override fun configure(config: ContentConfig, style: ContentStyle) = ConfiguredLength.Absolute(value * style.textSizeDip * config.density)
        override operator fun times(multiplier: Float) = ContentLength.Em(value * multiplier)
    }

    data class Percent(val percent: Float) : ContentLength {
        init {
            require(percent >= 0F)
        }

        override fun configure(config: ContentConfig, style: ContentStyle) = ConfiguredLength.Percent(percent)
        override operator fun times(multiplier: Float) = ContentLength.Percent(percent * multiplier)
    }
}
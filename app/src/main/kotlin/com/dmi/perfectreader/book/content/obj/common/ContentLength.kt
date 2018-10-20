package com.dmi.perfectreader.book.content.obj.common

import com.dmi.perfectreader.book.content.configure.common.ConfiguredLength
import java.io.Serializable

interface ContentLength : Serializable {
    fun configure(config: ContentConfig): ConfiguredLength

    object Zero : ContentLength {
        override fun configure(config: ContentConfig) = ConfiguredLength.Absolute(0F)
    }

    data class Dip(val value: Float) : ContentLength {
        init {
            require(value >= 0F)
        }

        override fun configure(config: ContentConfig) = ConfiguredLength.Absolute(value * config.density)
    }

    data class Em(val value: Float) : ContentLength {
        init {
            require(value >= 0F)
        }

        override fun configure(config: ContentConfig) = ConfiguredLength.Absolute(value * config.style.textSizeDip * config.density)
    }

    data class Percent(val percent: Float) : ContentLength {
        init {
            require(percent >= 0F)
        }

        override fun configure(config: ContentConfig) = ConfiguredLength.Percent(percent)
    }
}
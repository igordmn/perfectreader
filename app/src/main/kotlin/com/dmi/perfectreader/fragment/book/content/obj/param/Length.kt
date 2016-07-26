package com.dmi.perfectreader.fragment.book.content.obj.param

import java.io.Serializable

interface Length : Serializable {
    fun compute(percentBase: Float): Float

    fun configure(config: LayoutConfig) = when (this) {
        is Length.Absolute -> Length.Absolute(value * config.density)
        else -> this
    }

    data class Absolute(val value: Float) : Length {
        init {
            require(value >= 0F)
        }

        override fun compute(percentBase: Float) = value
    }

    data class Percent(val percent: Float) : Length {
        init {
            require(percent >= 0F)
        }

        override fun compute(percentBase: Float) = percent * percentBase
    }

    data class Multiplier(val other: Length, val value: Float) : Length {
        init {
            require(value >= 0F)
        }

        override fun compute(percentBase: Float) = other.compute(percentBase) * value
    }
}
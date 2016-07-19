package com.dmi.perfectreader.fragment.book.content.obj.param

import java.io.Serializable

interface Length : Serializable {
    fun configure(percentBase: Float): Float

    data class Absolute(val value: Float) : Length {
        init {
            require(value >= 0F)
        }

        override fun configure(percentBase: Float) = value
    }

    data class Percent(val percent: Float) : Length {
        init {
            require(percent >= 0F)
        }

        override fun configure(percentBase: Float) = percent * percentBase
    }

    data class Multiplier(val other: Length, val value: Float) : Length {
        init {
            require(value >= 0F)
        }

        override fun configure(percentBase: Float) = other.configure(percentBase) * value
    }
}
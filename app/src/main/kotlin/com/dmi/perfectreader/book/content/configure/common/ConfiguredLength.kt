package com.dmi.perfectreader.book.content.configure.common

import java.io.Serializable

interface ConfiguredLength : Serializable {
    fun compute(percentBase: Float): Float

    data class Absolute(val value: Float) : ConfiguredLength {
        init {
            require(value >= 0F)
        }

        override fun compute(percentBase: Float) = value
    }

    data class Percent(val percent: Float) : ConfiguredLength {
        init {
            require(percent >= 0F)
        }

        override fun compute(percentBase: Float) = percent * percentBase
    }
}
package com.dmi.perfectreader.layout.common

interface LayoutLength {
    fun compute(percentBase: Float): Float

    data class Absolute(val value: Float) : LayoutLength {
        override fun compute(percentBase: Float) = value
    }

    data class Percent(val percent: Float) : LayoutLength {
        override fun compute(percentBase: Float) = percent * percentBase
    }

    data class Min(val values: List<LayoutLength>) : LayoutLength {
        override fun compute(percentBase: Float) = values.map { it.compute(percentBase) }.min()!!
    }

    data class Max(val values: List<LayoutLength>) : LayoutLength {
        override fun compute(percentBase: Float) = values.map { it.compute(percentBase) }.max()!!
    }
}

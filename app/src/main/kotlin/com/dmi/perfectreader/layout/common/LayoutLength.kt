package com.dmi.perfectreader.layout.common

interface LayoutLength {
    fun compute(metric: LayoutSpace.Metric): Float

    data class Absolute(val value: Float) : LayoutLength {
        override fun compute(metric: LayoutSpace.Metric) = value
    }

    data class Percent(val percent: Float) : LayoutLength {
        override fun compute(metric: LayoutSpace.Metric) = percent * metric.percentBase
    }
}

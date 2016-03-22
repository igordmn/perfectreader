package com.dmi.perfectreader.layout.common

import hashCode
import safeEquals

sealed class LayoutLength {
    abstract fun compute(metric: LayoutSpace.Metric): Float

    class Absolute(val value: Float) : LayoutLength() {
        override fun compute(metric: LayoutSpace.Metric) = value

        override fun equals(other: Any?) = safeEquals(other) { value == it.value }
        override fun hashCode() = hashCode(value)
    }

    class Percent(val percent: Float) : LayoutLength() {
        override fun compute(metric: LayoutSpace.Metric) = percent * metric.percentBase

        override fun equals(other: Any?) = safeEquals(other) { percent == it.percent }
        override fun hashCode() = hashCode(percent)
    }
}

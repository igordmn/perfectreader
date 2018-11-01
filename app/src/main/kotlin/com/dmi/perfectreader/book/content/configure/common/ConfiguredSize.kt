package com.dmi.perfectreader.book.content.configure.common

import com.dmi.util.lang.clamp
import com.dmi.util.lang.safeEquals

data class ConfiguredSize(val width: Dimension, val height: Dimension) {
    sealed class Dimension {
        class Auto(val limits: Limits = Limits.NONE) : Dimension() {
            fun compute(autoValue: Float, percentBase: Float) = limits.clampCompute(autoValue, percentBase)

            override fun equals(other: Any?) = safeEquals(other) { limits == it.limits }
            override fun hashCode() = limits.hashCode()
        }

        class Fixed(val value: ConfiguredLength, val limits: Limits = Limits.NONE) : Dimension() {
            fun compute(percentBase: Float) = limits.clampCompute(value.compute(percentBase), percentBase)

            override fun equals(other: Any?) = safeEquals(other) { value == it.value && limits == it.limits }
            override fun hashCode() = com.dmi.util.lang.hashCode(value.hashCode(), limits.hashCode())
        }
    }

    data class Limits(val min: ConfiguredLength, val max: ConfiguredLength) {
        companion object {
            val NONE: Limits = Limits(ConfiguredLength.Zero, ConfiguredLength.Absolute(Float.POSITIVE_INFINITY))
        }

        fun clampCompute(value: Float, percentBase: Float) = clamp(
                value,
                min.compute(percentBase),
                max.compute(percentBase)
        )
    }
}
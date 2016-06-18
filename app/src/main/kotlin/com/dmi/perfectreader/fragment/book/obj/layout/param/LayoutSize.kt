package com.dmi.perfectreader.fragment.book.obj.layout.param

import com.dmi.perfectreader.fragment.book.obj.common.Length
import com.dmi.util.lang.clamp
import com.dmi.util.lang.hashCode
import com.dmi.util.lang.safeEquals

data class LayoutSize(val width: Dimension, val height: Dimension) {
    sealed class Dimension {
        class Auto(val limits: Limits = Limits.NONE) : Dimension() {
            fun compute(autoValue: Float, percentBase: Float) = limits.clampCompute(autoValue, percentBase)

            override fun equals(other: Any?) = safeEquals(other) { limits == it.limits }
            override fun hashCode() = limits.hashCode()
        }

        class Fixed(val value: Length, val limits: Limits = Limits.NONE) : Dimension() {
            fun compute(percentBase: Float) = limits.clampCompute(value.compute(percentBase), percentBase)

            override fun equals(other: Any?) = safeEquals(other) { value == it.value && limits == it.limits }
            override fun hashCode() = hashCode(value.hashCode(), limits.hashCode())
        }
    }

    data class Limits(val min: Length, val max: Length) {
        companion object {
            val NONE: Limits = Limits(Length.Absolute(0F), Length.Absolute(Float.MAX_VALUE))
        }

        fun clampCompute(value: Float, percentBase: Float) = clamp(
                value,
                min.compute(percentBase),
                max.compute(percentBase)
        )
    }
}
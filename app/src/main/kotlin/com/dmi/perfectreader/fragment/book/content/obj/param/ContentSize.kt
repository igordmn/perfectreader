package com.dmi.perfectreader.fragment.book.content.obj.param

import com.dmi.util.lang.clamp
import com.dmi.util.lang.safeEquals
import java.io.Serializable

class ContentSize(val width: Dimension, val height: Dimension) : Serializable {
    fun configure() = ComputedSize(
            width.configure(),
            height.configure()
    )

    class Dimension(val value: Length?, val min: Length?, val max: Length?) {
        fun configure(): ComputedSize.Dimension {
            val limits = ComputedSize.Limits(
                    min ?: Length.Absolute(0F),
                    max ?: Length.Absolute(Float.MAX_VALUE)
            )
            return if (value == null) {
                ComputedSize.Dimension.Auto(limits)
            } else {
                ComputedSize.Dimension.Fixed(value, limits)
            }
        }
    }
}

data class ComputedSize(val width: Dimension, val height: Dimension) {
    sealed class Dimension {
        class Auto(val limits: Limits = Limits.NONE) : Dimension() {
            fun compute(autoValue: Float, percentBase: Float) = limits.clampCompute(autoValue, percentBase)

            override fun equals(other: Any?) = safeEquals(other) { limits == it.limits }
            override fun hashCode() = limits.hashCode()
        }

        class Fixed(val value: Length, val limits: Limits = Limits.NONE) : Dimension() {
            fun compute(percentBase: Float) = limits.clampCompute(value.compute(percentBase), percentBase)

            override fun equals(other: Any?) = safeEquals(other) { value == it.value && limits == it.limits }
            override fun hashCode() = com.dmi.util.lang.hashCode(value.hashCode(), limits.hashCode())
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
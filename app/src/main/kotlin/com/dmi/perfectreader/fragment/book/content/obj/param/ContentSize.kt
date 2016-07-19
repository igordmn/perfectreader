package com.dmi.perfectreader.fragment.book.content.obj.param

import com.dmi.util.lang.clamp
import com.dmi.util.lang.safeEquals
import java.io.Serializable

class ContentSize(val width: Dimension, val height: Dimension) : Serializable {
    fun configure() = ConfiguredSize(
            width.configure(),
            height.configure()
    )

    class Dimension(val value: Length?, val min: Length?, val max: Length?) {
        fun configure(): ConfiguredSize.Dimension {
            val limits = ConfiguredSize.Limits(
                    min ?: Length.Absolute(0F),
                    max ?: Length.Absolute(Float.MAX_VALUE)
            )
            return if (value == null) {
                ConfiguredSize.Dimension.Auto(limits)
            } else {
                ConfiguredSize.Dimension.Fixed(value, limits)
            }
        }
    }
}

data class ConfiguredSize(val width: Dimension, val height: Dimension) {
    sealed class Dimension {
        class Auto(val limits: Limits = Limits.NONE) : Dimension() {
            fun configure(autoValue: Float, percentBase: Float) = limits.clampCompute(autoValue, percentBase)

            override fun equals(other: Any?) = safeEquals(other) { limits == it.limits }
            override fun hashCode() = limits.hashCode()
        }

        class Fixed(val value: Length, val limits: Limits = Limits.NONE) : Dimension() {
            fun configure(percentBase: Float) = limits.clampCompute(value.configure(percentBase), percentBase)

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
                min.configure(percentBase),
                max.configure(percentBase)
        )
    }
}
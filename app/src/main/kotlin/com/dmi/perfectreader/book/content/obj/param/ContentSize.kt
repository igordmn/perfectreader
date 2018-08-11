package com.dmi.perfectreader.book.content.obj.param

import com.dmi.util.lang.clamp
import com.dmi.util.lang.safeEquals
import java.io.Serializable

class ContentSize(val width: Dimension, val height: Dimension) : Serializable {
    fun configure(config: ContentConfig) = ConfiguredSize(
            width.configure(config),
            height.configure(config)
    )

    class Dimension(val value: Length?, val min: Length?, val max: Length?) {
        fun configure(config: ContentConfig): ConfiguredSize.Dimension {
            val limits = ConfiguredSize.Limits(
                    (min ?: Length.Absolute(0F)).configure(config),
                    (max ?: Length.Absolute(Float.POSITIVE_INFINITY)).configure(config)
            )
            return if (value == null) {
                ConfiguredSize.Dimension.Auto(limits)
            } else {
                ConfiguredSize.Dimension.Fixed(value.configure(config), limits)
            }
        }
    }
}

data class ConfiguredSize(val width: Dimension, val height: Dimension) {
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
            val NONE: Limits = Limits(Length.Absolute(0F), Length.Absolute(Float.POSITIVE_INFINITY))
        }

        fun clampCompute(value: Float, percentBase: Float) = clamp(
                value,
                min.compute(percentBase),
                max.compute(percentBase)
        )
    }
}
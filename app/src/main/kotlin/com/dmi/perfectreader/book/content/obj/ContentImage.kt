package com.dmi.perfectreader.book.content.obj

import com.dmi.perfectreader.book.content.configure.ConfiguredImage
import com.dmi.perfectreader.book.content.configure.common.ConfiguredLength
import com.dmi.perfectreader.book.content.configure.common.ConfiguredSize
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.obj.common.ContentConfig
import kotlin.math.min
import kotlin.math.round

private const val DEFAULT_DENSITY = 2F

class ContentImage(
        val src: String?,
        override val range: LocationRange
) : ContentObject {
    override val length = 32.0

    override fun configure(config: ContentConfig) = ConfiguredImage(
            ConfiguredSize(dimension(), dimension()),
            src,
            config.imageScale.configure(config),
            range
    )

    private fun dimension() = ConfiguredSize.Dimension.Auto(
            ConfiguredSize.Limits(
                    ConfiguredLength.Zero,
                    ConfiguredLength.Percent(1F)
            )
    )

    override fun toString() = "<img:$src>"

    sealed class Scale {
        abstract fun configure(config: ContentConfig): ConfiguredImage.Scale

        class Fixed(
                private val value: Float,
                private val incFilter: Boolean,
                private val decFilter: Boolean
        ) : Scale() {
            override fun configure(config: ContentConfig) =
                    ConfiguredImage.Scale(value, incFilter, decFilter)
        }

        class ByDPI(
                private val integer: Boolean,
                private val incFilter: Boolean,
                private val decFilter: Boolean
        ) : Scale() {
            override fun configure(config: ContentConfig) =
                    ConfiguredImage.Scale(
                            (config.density / DEFAULT_DENSITY).roundIfInteger(),
                            incFilter, decFilter
                    )

            private fun Float.roundIfInteger(): Float = min(1F, if (integer) round(this) else this)
        }
    }
}
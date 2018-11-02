package com.dmi.perfectreader.book.content.obj

import com.dmi.perfectreader.book.content.configure.ConfiguredBox
import com.dmi.perfectreader.book.content.configure.common.ConfiguredSize
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.obj.common.ContentCompositeClass
import com.dmi.perfectreader.book.content.obj.common.ContentConfig

class ContentBox(
        val children: List<ContentObject>,
        private val cls: ContentCompositeClass?
) : ContentObject {
    init {
        require(children.isNotEmpty())
    }

    override val length = children.sumByDouble { it.length }
    override val range = LocationRange(children.first().range.start, children.last().range.endInclusive)

    override fun configure(config: ContentConfig): ConfiguredBox {
        val style = config.style(cls)
        return ConfiguredBox(
                ConfiguredSize(ConfiguredSize.Dimension.Auto(), ConfiguredSize.Dimension.Auto()),
                style.boxAlign,
                children.map { it.configure(config) },
                range
        )
    }

    override fun toString() = children.joinToString("; ")
}
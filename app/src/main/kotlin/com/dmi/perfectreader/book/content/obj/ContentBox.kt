package com.dmi.perfectreader.book.content.obj

import com.dmi.perfectreader.book.content.common.Align
import com.dmi.perfectreader.book.content.configure.ConfiguredBox
import com.dmi.perfectreader.book.content.configure.common.ConfiguredSize
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.obj.common.ContentClass
import com.dmi.perfectreader.book.content.obj.common.ContentConfig

class ContentBox(
        val children: List<ContentObject>,
        private val cls: ContentClass?
) : ContentObject {
    init {
        require(children.isNotEmpty())
    }

    override val length = children.sumByDouble { it.length }
    override val range = LocationRange(children.first().range.start, children.last().range.endInclusive)

    override fun configure(config: ContentConfig): ConfiguredBox {
        val inherited = config.styled[cls].inherited
        return ConfiguredBox(
                ConfiguredSize(ConfiguredSize.Dimension.Auto(), ConfiguredSize.Dimension.Auto()),
                Align.LEFT,
                children.map { it.configure(inherited) },
                range
        )
    }
}
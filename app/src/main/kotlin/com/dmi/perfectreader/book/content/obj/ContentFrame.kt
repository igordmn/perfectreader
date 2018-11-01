package com.dmi.perfectreader.book.content.obj

import com.dmi.perfectreader.book.content.configure.ConfiguredFrame
import com.dmi.perfectreader.book.content.obj.common.ContentCompositeClass
import com.dmi.perfectreader.book.content.obj.common.ContentConfig

class ContentFrame(
        val child: ContentObject,
        private val cls: ContentCompositeClass?
) : ContentObject {
    override val length = child.length
    override val range = child.range

    override fun configure(config: ContentConfig): ConfiguredFrame {
        val style = config.style(cls)
        return ConfiguredFrame(
                style.margins.configure(config, style),
                ConfiguredFrame.Paddings.Zero,
                ConfiguredFrame.Borders.Zero,
                ConfiguredFrame.Background.Transparent,
                child.configure(config),
                range
        )
    }
}
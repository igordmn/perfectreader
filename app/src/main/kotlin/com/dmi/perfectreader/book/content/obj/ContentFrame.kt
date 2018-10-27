package com.dmi.perfectreader.book.content.obj

import com.dmi.perfectreader.book.content.configure.ConfiguredFrame
import com.dmi.perfectreader.book.content.configure.common.ConfiguredLength
import com.dmi.perfectreader.book.content.obj.common.ContentClass
import com.dmi.perfectreader.book.content.obj.common.ContentConfig
import com.dmi.util.graphic.Color

class ContentFrame(
        val child: ContentObject,
        private val cls: ContentClass?
) : ContentObject {
    override val length = child.length
    override val range = child.range

    override fun configure(config: ContentConfig): ConfiguredFrame {
        val styled = config.styled[cls]
        val style = styled.style
        val inherited = styled.inherited
        return ConfiguredFrame(
                style.margins.configure(styled),
                paddings,
                borders,
                background,
                child.configure(inherited),
                range
        )
    }

    companion object {
        private val paddings = ConfiguredFrame.Paddings(
                ConfiguredLength.Absolute(0F), ConfiguredLength.Absolute(0F), ConfiguredLength.Absolute(0F), ConfiguredLength.Absolute(0F)
        )

        private val borders = ConfiguredFrame.Borders(
                ConfiguredFrame.Border(0F, Color.TRANSPARENT),
                ConfiguredFrame.Border(0F, Color.TRANSPARENT),
                ConfiguredFrame.Border(0F, Color.TRANSPARENT),
                ConfiguredFrame.Border(0F, Color.TRANSPARENT)
        )

        private val background = ConfiguredFrame.Background(Color.TRANSPARENT)
    }
}
package com.dmi.perfectreader.book.content.obj.common

import com.dmi.perfectreader.book.content.configure.ConfiguredFrame

data class ContentMargins(val left: ContentLength, val right: ContentLength, val top: ContentLength, val bottom: ContentLength) {
    companion object {
        val Zero = ContentMargins(ContentLength.Zero, ContentLength.Zero, ContentLength.Zero, ContentLength.Zero)
    }
    
    fun configure(config: ContentConfig, style: ContentStyle) = ConfiguredFrame.Margins(
            left.configure(config, style),
            right.configure(config, style),
            top.configure(config, style),
            bottom.configure(config, style)
    )
}
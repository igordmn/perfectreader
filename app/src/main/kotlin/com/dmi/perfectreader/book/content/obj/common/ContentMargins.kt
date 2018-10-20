package com.dmi.perfectreader.book.content.obj.common

import com.dmi.perfectreader.book.content.configure.ConfiguredFrame

data class ContentMargins(val left: ContentLength, val right: ContentLength, val top: ContentLength, val bottom: ContentLength) {
    companion object {
        val Zero = ContentMargins(ContentLength.Zero, ContentLength.Zero, ContentLength.Zero, ContentLength.Zero)
    }
    
    fun configure(config: ContentConfig) = ConfiguredFrame.Margins(
            left.configure(config),
            right.configure(config),
            top.configure(config),
            bottom.configure(config)
    )
}
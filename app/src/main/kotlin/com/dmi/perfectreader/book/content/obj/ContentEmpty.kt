package com.dmi.perfectreader.book.content.obj

import com.dmi.perfectreader.book.content.configure.ConfiguredEmpty
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.obj.common.ContentConfig

class ContentEmpty(override val range: LocationRange) : ContentObject {
    override val length: Double = 1.0
    override fun configure(config: ContentConfig) = ConfiguredEmpty(range)
}
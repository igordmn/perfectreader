package com.dmi.perfectreader.book.content.configure

import com.dmi.perfectreader.book.content.common.Align
import com.dmi.perfectreader.book.content.configure.common.ConfiguredSize
import com.dmi.perfectreader.book.content.location.LocationRange

class ConfiguredBox(
        val size: ConfiguredSize,
        val contentAlign: Align,
        val children: List<ConfiguredObject>,
        override val range: LocationRange
) : ConfiguredObject
package com.dmi.perfectreader.book.content.configure

import com.dmi.perfectreader.book.content.location.LocationRange

class ConfiguredEmpty(override val range: LocationRange) : ConfiguredObject {
    override fun toString() = "<Empty>"
}
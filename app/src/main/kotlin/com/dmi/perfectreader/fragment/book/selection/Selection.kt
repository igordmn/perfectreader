package com.dmi.perfectreader.fragment.book.selection

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.util.base.BaseViewModel

class Selection : BaseViewModel() {
    val range = LocationRange(Location(0.0), Location(100.0))

}
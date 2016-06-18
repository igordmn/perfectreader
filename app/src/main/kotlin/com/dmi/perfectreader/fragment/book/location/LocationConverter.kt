package com.dmi.perfectreader.fragment.book.location

interface LocationConverter {
    fun locationToPercent(location: Location): Double
    fun percentToLocation(percent: Double): Location
}
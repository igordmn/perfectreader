package com.dmi.perfectreader.settingschange.common

import android.view.View

open class Places {
    private val places = ArrayList<Place>()

    operator fun get(id: Int): Place = places[id]

    @Suppress("LeakingThis")
    abstract inner class Place {
        val id: Int = places.size

        init {
            places.add(this)
        }

        abstract fun view(): View
    }
}
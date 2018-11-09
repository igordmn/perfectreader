package com.dmi.util.android.view

import android.view.View

typealias Id = Int

open class Places {
    private val places = ArrayList<Place>()

    operator fun get(id: Id): Place = places[id]

    @Suppress("LeakingThis")
    abstract inner class Place {
        val id: Int = places.size

        init {
            places.add(this)
        }

        abstract fun ViewBuild.view(): View
    }
}

fun Places.Place.view(build: ViewBuild): View = build.view()
val Places.Place.viewRef: ViewBuild.() -> View get() = { view() }
package com.dmi.util.android.view

import android.app.Dialog
import android.view.View

typealias Id = Int

open class Places {
    private val places = ArrayList<Place>()
    private var finished = false

    operator fun get(id: Id): Place = places[id]

    fun finish() {
        finished = true
    }

    fun place(init: PlaceBuild.() -> Unit) = object : Place() {
        private lateinit var buildView: (ViewBuild.() -> View)

        init {
            init(object : PlaceBuild {
                override fun view(build: ViewBuild.() -> View) {
                    buildView = build
                }
            })
        }

        override fun ViewBuild.view() = buildView()
    }

    @Suppress("LeakingThis")
    abstract inner class Place {
        val id: Int = places.size

        init {
            require(!finished)
            places.add(this)
        }

        abstract fun ViewBuild.view(): View
    }
}

fun Places.Place.view(build: ViewBuild): View = build.view()
val Places.Place.viewRef: ViewBuild.() -> View get() = { view() }

fun Places.dialog(createDialog: ViewBuild.() -> Dialog) = place {
    view {
        DialogView(context, createDialog)
    }
}

interface PlaceBuild {
    fun view(build: ViewBuild.() -> View)
}
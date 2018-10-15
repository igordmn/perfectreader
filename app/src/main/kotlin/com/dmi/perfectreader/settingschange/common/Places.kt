package com.dmi.perfectreader.settingschange.common

import android.view.View
import androidx.annotation.StringRes

open class Places {
    private val sections = ArrayList<Place>()

    operator fun get(id: Int): Place = sections[id]

    @Suppress("LeakingThis")
    abstract inner class Place(@StringRes val nameRes: Int) {
        val id: Int = sections.size

        init {
            sections.add(this)
        }

        abstract fun view(): View
    }
}
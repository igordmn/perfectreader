package com.dmi.perfectreader.settingschange.common

import android.view.View
import androidx.annotation.StringRes

open class SettingSections {
    private val sections = ArrayList<Section>()

    operator fun get(id: Int): Section = sections[id]

    @Suppress("LeakingThis")
    abstract inner class Section(@StringRes val nameRes: Int) {
        val id: Int = sections.size

        init {
            sections.add(this)
        }

        abstract fun view(): View
    }
}
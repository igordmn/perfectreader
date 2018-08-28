package com.dmi.perfectreader.settingschange

import android.view.View

interface SettingsNavigation {
    fun goDetails(title: String, view: View)
    fun goBack()
}
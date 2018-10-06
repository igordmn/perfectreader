package com.dmi.perfectreader.settingschange.detail

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import com.dmi.perfectreader.settingschange.SettingsChangeDetails
import com.dmi.perfectreader.settingschange.SettingsChangeDetailsContent

private val contentToDetails = HashMap<SettingsChangeDetailsContent, SettingsDetailViews>()

fun settingViewDetails(content: SettingsChangeDetailsContent) = contentToDetails[content]!!

class SettingsDetailViews(
        @StringRes
        val titleResId: Int,
        val content: SettingsChangeDetailsContent,
        val previewView: (Context) -> View,
        val contentView: (Context, SettingsChangeDetails) -> View
) {
    init {
        contentToDetails[content] = this
    }
}
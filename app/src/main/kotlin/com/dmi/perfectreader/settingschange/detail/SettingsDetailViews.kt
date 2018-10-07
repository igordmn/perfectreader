package com.dmi.perfectreader.settingschange.detail

import android.content.Context
import android.view.View
import androidx.annotation.StringRes

class SettingsDetailViews<M>(
        @StringRes
        val titleResId: Int,
        val initialState: Any,
        val previewView: (Context) -> View,
        val contentView: (Context, M) -> View
)
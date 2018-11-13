package com.dmi.perfectreader.settingsui.custom

import android.content.Context
import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
import com.dmi.perfectreader.settingsui.SettingsUI
import com.dmi.perfectreader.settingsui.common.PreviewView
import com.dmi.perfectreader.settingsui.common.SettingBitmapView
import com.dmi.perfectreader.settingsui.common.SettingChoiceListView
import com.dmi.perfectreader.settingsui.common.details
import com.dmi.util.android.view.GridAutoFitLayoutManager
import com.dmi.util.android.view.autorun
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding

fun themeBackgroundPictureDetails(context: Context, model: SettingsUI) = details(
        context, model, R.string.settingsUIThemeBackgroundPicture,
        SettingChoiceListView(
                context,
                context.main.settings.theme::backgroundPath,
                context.main.resources.backgrounds.map { it.toString() },
                {
                    SettingBitmapView(it, size = 64).apply {
                        padding = dip(12)
                    }
                },
                onItemClick = {},
                onItemSecondClick = model.screens::goBackward
        ).apply {
            layoutManager = GridAutoFitLayoutManager(context, columnWidth = dip(64 + 12 * 2))
            setPaddingRelative(dip(12), 0, dip(12), 0)
        }
)

fun themeBackgroundPicturePreview(context: Context) = PreviewView(
        SettingBitmapView(context, size = 24).apply {
            autorun {
                bind(context.main.settings.theme.backgroundPath)
            }
        }
)
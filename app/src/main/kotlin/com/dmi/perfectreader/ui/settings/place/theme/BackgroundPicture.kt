package com.dmi.perfectreader.ui.settings.place.theme

import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.common.PreviewView
import com.dmi.perfectreader.ui.settings.common.SettingBitmapView
import com.dmi.perfectreader.ui.settings.common.details
import com.dmi.perfectreader.ui.settings.common.list.SettingSingleChoiceListView
import com.dmi.util.android.view.GridAutoFitLayoutManager
import com.dmi.util.android.view.ViewBuild
import com.dmi.util.android.view.autorun
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding

fun ViewBuild.themeBackgroundPictureDetails(model: SettingsUI) = details(
        model, R.string.settingsUIThemeBackgroundPicture,
        SettingSingleChoiceListView(
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

fun ViewBuild.themeBackgroundPicturePreview() = PreviewView(
        SettingBitmapView(context, size = 24).apply {
            autorun {
                bind(context.main.settings.theme.backgroundPath)
            }
        }
)
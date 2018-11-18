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

fun ViewBuild.themePagePictureDetails(model: SettingsUI) = details(
        model, R.string.settingsUIThemePageImage,
        SettingSingleChoiceListView(
                context,
                context.main.settings.theme::pagePath,
                context.main.resources.pages.map { it.toString() },
                {
                    SettingBitmapView(it, size = context.dip(64)).apply {
                        padding = dip(12)
                    }
                },
                GridAutoFitLayoutManager(context, columnWidth = context.dip(64 + 12 * 2)),
                onItemClick = {},
                onItemSecondClick = model.screens::goBackward
        ).apply {
            setPaddingRelative(dip(12), 0, dip(12), 0)
        }
)

fun ViewBuild.themePageImagePreview() = PreviewView(
        SettingBitmapView(context, size = context.dip(24)).apply {
            autorun {
                bind(context.main.settings.theme.pagePath)
            }
        }
)
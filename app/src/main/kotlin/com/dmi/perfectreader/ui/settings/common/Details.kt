package com.dmi.perfectreader.ui.settings.common

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import com.dmi.perfectreader.R
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.util.android.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

fun ViewBuild.details(
        model: SettingsUI,
        @StringRes titleRes: Int,
        content: View
) = details(detailsToolbar(titleRes, model), content)

fun ViewBuild.details(
        toolbar: View,
        content: View
) = LinearLayoutExt(context).apply {
    orientation = LinearLayoutCompat.VERTICAL
    backgroundColor = color(R.color.background)

    child(params(matchParent, wrapContent, weight = 0F), toolbar)
    child(params(matchParent, matchParent, weight = 1F), content)
}

fun ViewBuild.detailsToolbar(
        titleRes: Int,
        model: SettingsUI
) = Toolbar(context).apply {
    setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
    backgroundColor = color(android.R.color.transparent)
    navigationIcon = drawable(R.drawable.ic_arrow_back)
    this.title = string(titleRes)
    setNavigationOnClickListener {
        model.screens.goBackward()
    }
}
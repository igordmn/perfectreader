package com.dmi.perfectreader.settingsui.common

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import com.dmi.perfectreader.R
import com.dmi.perfectreader.settingsui.SettingsUI
import com.dmi.util.android.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

fun details(
        context: Context,
        model: SettingsUI,
        @StringRes titleRes: Int,
        content: View
) = details(context, detailsToolbar(context, titleRes, model), content)

fun details(
        context: Context,
        toolbar: View,
        content: View
) = LinearLayoutExt(context).apply {
    orientation = LinearLayoutCompat.VERTICAL
    backgroundColor = color(R.color.background)

    child(params(matchParent, wrapContent, weight = 0F), toolbar)
    child(params(matchParent, matchParent, weight = 1F), content)
}

fun detailsToolbar(
        context: Context,
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
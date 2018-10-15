package com.dmi.perfectreader.settingschange.common

import android.content.Context
import android.view.Menu
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import com.dmi.perfectreader.R
import com.dmi.perfectreader.settingschange.SettingsChange
import com.dmi.util.android.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

fun details(
        context: Context,
        model: SettingsChange,
        @StringRes titleRes: Int,
        content: View,
        configureMenu: (Menu) -> Unit = {}
) = LinearLayoutExt(context).apply {
    orientation = LinearLayoutCompat.VERTICAL
    backgroundColor = color(R.color.background)

    child(params(matchParent, wrapContent, weight = 0F), Toolbar(context).apply {
        setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
        backgroundColor = color(android.R.color.transparent)
        navigationIcon = drawable(R.drawable.ic_arrow_back)
        this.title = string(titleRes)
        popupTheme = R.style.Theme_AppCompat_Light
        configureMenu(menu)

        setNavigationOnClickListener {
            model.screens.goBackward()
        }
    })

    child(params(matchParent, matchParent, weight = 1F), content)
}
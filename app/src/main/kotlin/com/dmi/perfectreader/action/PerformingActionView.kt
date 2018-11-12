package com.dmi.perfectreader.action

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import com.google.android.material.card.MaterialCardView
import org.jetbrains.anko.dip
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

fun ViewBuild.performingActionView(model: PerformingAction, old: PerformingActionView?): PerformingActionView {
    val view = old ?: PerformingActionView(context)
    view.set(model)
    return view
}

class PerformingActionView(context: Context) : FrameLayout(context) {
    private val name: TextView
    private val value: TextView

    init {
        child(params(wrapContent, wrapContent), MaterialCardView(context).apply {
            setBackgroundColor(color(R.color.secondary))
            elevation = dipFloat(3F)
            useCompatPadding = true

            child(params(wrapContent, wrapContent), LinearLayoutCompat(context).apply {
                orientation = LinearLayoutCompat.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(dip(12), dip(4), dip(12), dip(4))

                name = child(params(wrapContent, wrapContent), TextView(context).apply {
                    isAllCaps = true
                    textColor = color(R.color.onSecondary)
                    textSize = spFloat(16F)
                })

                value = child(params(wrapContent, wrapContent), TextView(context).apply {
                    isAllCaps = true
                    textColor = color(R.color.onSecondary)
                    textSize = spFloat(24F)
                })
            })
        })
    }

    fun set(action: PerformingAction) {
        name.text = settingActionName(context, action.id)
        value.text = formatSettingValue(action.id, action.value)
    }

    private fun formatSettingValue(id: SettingActionID, value: Any): String = when (id) {
        SettingActionID.NONE -> ""
        SettingActionID.PAGE_MARGINS -> formatFloatValue(value)
        SettingActionID.TEXT_SIZE -> formatFloatValue(value)
        SettingActionID.TEXT_LINE_HEIGHT -> formatFloatValue(value)
        SettingActionID.TEXT_GAMMA -> formatFloatValue(value)
        SettingActionID.TEXT_STROKE_WIDTH -> formatFloatValue(value)
        SettingActionID.TEXT_SCALE_X -> formatFloatValue(value)
        SettingActionID.TEXT_LETTER_SPACING -> formatFloatValue(value)
        SettingActionID.SCREEN_BRIGHTNESS -> formatFloatValue(value)
    }

    private fun formatFloatValue(value: Any) = String.format("%.2f", value as Float)
}
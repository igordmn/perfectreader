package com.dmi.perfectreader.action

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

fun Context.performingActionView(model: PerformingAction, old: PerformingActionView?): PerformingActionView {
    val view = old ?: PerformingActionView(this)
    view.set(model)
    return view
}

class PerformingActionView(context: Context) : FrameLayout(context) {
    private lateinit var name: TextView
    private lateinit var value: TextView

    init {
        child(::CardView, params(wrapContent, wrapContent)) {
            setBackgroundColor(color(R.color.gray900))
            elevation = dipFloat(3F)
            useCompatPadding = true

            child(::LinearLayoutCompat, params(wrapContent, wrapContent)) {
                orientation = LinearLayoutCompat.VERTICAL
                padding = dip(12)

                name = child(::TextView, params(wrapContent, wrapContent, Gravity.CENTER)) {
                    isAllCaps = true
                    textColor = color(R.color.text_secondary_light)
                    textSize = spFloat(16F)
                }

                value = child(::TextView, params(wrapContent, wrapContent, Gravity.CENTER)) {
                    isAllCaps = true
                    textColor = color(R.color.text_primary_light)
                    textSize = spFloat(24F)
                }
            }
        }
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
        SettingActionID.TEXT_COLOR_GAMMA -> formatFloatValue(value)
        SettingActionID.TEXT_COLOR_CONTRAST -> formatFloatValue(value)
        SettingActionID.TEXT_COLOR_BRIGHTNESS -> formatFloatValue(value)
        SettingActionID.TEXT_STROKE_WIDTH -> formatFloatValue(value)
        SettingActionID.TEXT_SCALE_X -> formatFloatValue(value)
        SettingActionID.TEXT_LETTER_SPACING -> formatFloatValue(value)
        SettingActionID.SCREEN_BRIGHTNESS -> formatFloatValue(value)
    }

    private fun formatFloatValue(value: Any) = String.format("%.2f", value as Float)
}
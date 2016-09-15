package com.dmi.perfectreader.fragment.reader

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.fragment.reader.action.ReaderSettingActionID
import com.dmi.perfectreader.fragment.reader.action.settingActionName
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater

class ActionPopup : FrameLayout {
    private val nameView by lazy { find<TextView>(R.id.name) }
    private val valueView by lazy { find<TextView>(R.id.value) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addView(context.layoutInflater.inflate(R.layout.action_popup, this, false))
    }

    fun set(id: ReaderSettingActionID, value: Any) {
        nameView.text = settingActionName(context, id)
        valueView.text = formatSettingValue(id, value)
    }

    private fun formatSettingValue(id: ReaderSettingActionID, value: Any): String = when(id) {
        ReaderSettingActionID.NONE -> ""
        ReaderSettingActionID.PAGE_MARGINS -> formatFloatValue(value)
        ReaderSettingActionID.TEXT_SIZE -> formatFloatValue(value)
        ReaderSettingActionID.TEXT_LINE_HEIGHT -> formatFloatValue(value)
        ReaderSettingActionID.TEXT_GAMMA -> formatFloatValue(value)
        ReaderSettingActionID.TEXT_COLOR_GAMMA -> formatFloatValue(value)
        ReaderSettingActionID.TEXT_COLOR_CONTRAST -> formatFloatValue(value)
        ReaderSettingActionID.TEXT_COLOR_BRIGHTNESS -> formatFloatValue(value)
        ReaderSettingActionID.TEXT_STROKE_WIDTH -> formatFloatValue(value)
        ReaderSettingActionID.TEXT_SCALE_X -> formatFloatValue(value)
        ReaderSettingActionID.TEXT_LETTER_SPACING -> formatFloatValue(value)
        ReaderSettingActionID.SCREEN_BRIGHTNESS -> formatFloatValue(value)
    }

    private fun formatFloatValue(value: Any) = String.format("%.2f", value as Float)
}
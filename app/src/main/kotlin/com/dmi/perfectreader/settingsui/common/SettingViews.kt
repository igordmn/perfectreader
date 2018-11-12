package com.dmi.perfectreader.settingsui.common

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewStub
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.settingsui.SettingsUI
import com.dmi.perfectreader.settingsui.chooseSettingValue
import com.dmi.util.android.view.*
import com.dmi.util.lang.initOnce
import org.jetbrains.anko.*
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

class PreviewView(val view: View, val withPadding: Boolean = true, val isClickable: Boolean = true)

fun emptyPreview(context: Context) = PreviewView(ViewStub(context))

fun <T> propertyPreview(context: Context, property: KProperty0<T>, format: (value: T) -> String) = PreviewView(TextView(context).apply {
    autorun {
        text = format(property.get())
    }
})

fun detailsSetting(
        context: Context,
        model: SettingsUI,
        preview: PreviewView,
        place: Places.Place,
        @StringRes
        titleRes: Int,
        @StringRes
        subtitleRes: Int? = null
) : View {
    return titleSetting(context, preview, titleRes, subtitleRes).apply {
        onClick {
            model.screens.goForward(place.id)
        }
    }
}

fun popupSetting(
        context: Context,
        model: SettingsUI,
        preview: PreviewView,
        place: Places.Place,
        @StringRes
        titleRes: Int,
        @StringRes
        subtitleRes: Int? = null
) : View {
    return titleSetting(context, preview, titleRes, subtitleRes).apply {
        onClick {
            model.popup = place.id
        }
    }
}

fun floatSetting(
        context: Context,
        property: KMutableProperty0<Float>,
        values: FloatArray,
        @StringRes
        titleRes: Int,
        @StringRes
        subtitleRes: Int? = null,
        decimalCount: Int = 2,
        ringValues: Boolean = false
): View {
    val min = values.first()
    val max = values.last()

    var editNumber: EditNumber by initOnce()
    val view = LinearLayoutCompat(context).apply {
        orientation = LinearLayoutCompat.HORIZONTAL
        child(params(dip(48), dip(48)), AppCompatImageButton(context).apply {
            backgroundResource = attr(R.attr.selectableItemBackground).resourceId
            image = drawable(R.drawable.ic_minus, color(R.color.onBackground).withOpacity(0.60))
            onContinousClick {
                val value = editNumber.floatValue
                if (value > min) {
                    val newValue = chooseSettingValue(values, value, -1)
                    property.set(newValue)
                    editNumber.floatValue = newValue
                } else if (ringValues) {
                    property.set(max)
                    editNumber.floatValue = max
                }
            }
        })

        editNumber = child(params(dip(48), dip(48)), EditNumber(context).apply {
            setPadding(0, dip(12), 0, dip(12))
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
            gravity = Gravity.CENTER
            textColor = color(R.color.onBackground).withOpacity(0.60)
            this.min = min
            this.max = max
            this.decimalCount = decimalCount
            floatValue = property.get()
            afterChange {
                property.set(floatValue)
            }
        })

        child(params(dip(48), dip(48)), AppCompatImageButton(context).apply {
            backgroundResource = attr(R.attr.selectableItemBackground).resourceId
            image = drawable(R.drawable.ic_plus, color(R.color.onBackground).withOpacity(0.60))
            onContinousClick {
                val value = editNumber.floatValue
                if (value < max) {
                    val newValue = chooseSettingValue(values, value, 1)
                    property.set(newValue)
                    editNumber.floatValue = newValue
                } else if (ringValues) {
                    property.set(min)
                    editNumber.floatValue = min
                }
            }
        })
    }

    return titleSetting(context, PreviewView(view, withPadding = false), titleRes, subtitleRes).apply {
        onClick {
            editNumber.requestFocus()
        }
    }
}

fun booleanSetting(
        context: Context,
        property: KMutableProperty0<Boolean>,
        @StringRes
        titleRes: Int,
        @StringRes
        subtitleRes: Int? = null
): View {
    val switch = SwitchCompat(context).apply {
        isClickable = false
        isFocusable = false
        isChecked = property.get()
        onCheckedChange { _, checked ->
            property.set(checked)
        }
    }

    return titleSetting(context, PreviewView(switch), titleRes, subtitleRes).apply {
        onClick {
            switch.performClick()
        }
    }
}

fun titleSetting(
        context: Context,
        previewView: PreviewView,
        @StringRes
        titleRes: Int,
        @StringRes
        subtitleRes: Int? = null
) = LinearLayoutCompat(context).apply {
    orientation = LinearLayoutCompat.HORIZONTAL
    setPadding(dip(16), 0, if (previewView.withPadding) dip(16) else 0, 0)

    child(params(matchParent, wrapContent, Gravity.CENTER_VERTICAL, weight = 1F), LinearLayoutCompat(context).apply {
        orientation = LinearLayoutCompat.VERTICAL
        setPaddingRelative(0, dip(12), dip(16), dip(12))
        child(params(wrapContent, wrapContent), TextView(context).apply {
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
            textColor = color(R.color.onBackground)
            text = string(titleRes)
        })
        if (subtitleRes != null) {
            child(params(wrapContent, wrapContent), TextView(context).apply {
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
                textColor = color(R.color.onBackground).withOpacity(0.60)
                text = string(subtitleRes)
            })
        }
    })

    child(params(wrapContent, wrapContent, Gravity.END or Gravity.CENTER_VERTICAL, weight = 0F), previewView.view)

    if (previewView.isClickable) {
        backgroundResource = attr(R.attr.selectableItemBackground).resourceId
        isClickable = true
        isFocusable = true
    }
}
package com.dmi.perfectreader.settingschange

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.settingschange.common.SettingListView
import com.dmi.util.android.view.*
import com.dmi.util.lang.initOnce
import org.jetbrains.anko.*
import kotlin.reflect.KMutableProperty0

class SettingItems<T, IV, PV>(
        val list: List<T>,
        val createItemView: (Context) -> IV,
        val createPreviewView: (Context) -> PV
)

class PreviewView(val view: View, val withPadding: Boolean = true, val isClickable: Boolean = true)

fun <T, IV, PV> Context.listSetting(
        navigation: SettingsNavigation,
        property: KMutableProperty0<T>,
        items: SettingItems<T, IV, PV>,
        @StringRes
        titleResId: Int,
        @StringRes
        subtitleResId: Int? = null
) : View where IV : View, IV : Bindable<T>, PV : View, PV : Bindable<T> {
    val previewView = items.createPreviewView(this)
    previewView.bind(property.get())

    return titleSetting(PreviewView(previewView), titleResId, subtitleResId).apply {
        onClick {
            val view = SettingListView(context, property.get(), items.list, items.createItemView)
            navigation.goDetails(string(titleResId), view)
        }
    }
}

fun Context.floatSetting(
        property: KMutableProperty0<Float>,
        values: FloatArray,
        decimalCount: Int = 2,
        @StringRes
        titleResId: Int,
        @StringRes
        subtitleResId: Int? = null
): View {
    val min = values.first()
    val max = values.last()

    var editNumber: EditNumber by initOnce()
    val view = LinearLayoutCompat(this).apply {
        orientation = LinearLayoutCompat.HORIZONTAL
        child(::AppCompatImageButton, params(dip(48), dip(48))) {
            backgroundResource = attr(R.attr.selectableItemBackground).resourceId
            image = drawable(R.drawable.minus, color(R.color.onBackground).withTransparency(0.60))
            onContinousClick {
                if (property.get() > min) {
                    val value = chooseSettingValue(values, property.get(), -1)
                    property.set(value)
                    editNumber.floatValue = value
                }
            }
        }

        editNumber = child(::EditNumber, params(dip(48), dip(48))) {
            setPadding(0, dip(12), 0, dip(12))
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
            gravity = Gravity.CENTER
            textColor = color(R.color.onBackground).withTransparency(0.60)
            this.min = min
            this.max = max
            this.decimalCount = decimalCount
            floatValue = property.get()
            afterChange {
                property.set(floatValue)
            }
        }

        child(::AppCompatImageButton, params(dip(48), dip(48))) {
            backgroundResource = attr(R.attr.selectableItemBackground).resourceId
            image = drawable(R.drawable.plus, color(R.color.onBackground).withTransparency(0.60))
            onContinousClick {
                if (property.get() < max) {
                    val value = chooseSettingValue(values, property.get(), 1)
                    property.set(value)
                    editNumber.floatValue = value
                }
            }
        }
    }

    return titleSetting(PreviewView(view, withPadding = false), titleResId, subtitleResId).apply {
        onClick {
            editNumber.requestFocus()
        }
    }
}

fun Context.booleanSetting(
        property: KMutableProperty0<Boolean>,
        @StringRes
        titleResId: Int,
        @StringRes
        subtitleResId: Int? = null
): View {
    val switch = SwitchCompat(this).apply {
        isClickable = false
        isFocusable = false
        isChecked = property.get()
        onCheckedChange { _, checked ->
            property.set(checked)
        }
    }

    return titleSetting(PreviewView(switch), titleResId, subtitleResId).apply {
        onClick {
            switch.performClick()
        }
    }
}

fun Context.titleSetting(
        previewView: PreviewView,
        @StringRes
        titleResId: Int,
        @StringRes
        subtitleResId: Int? = null
) = view(::LinearLayoutCompat) {
    orientation = LinearLayoutCompat.HORIZONTAL
    setPadding(dip(16), 0, if (previewView.withPadding) dip(16) else 0, 0)

    child(::LinearLayoutCompat, params(matchParent, wrapContent, weight = 1F, gravity = Gravity.CENTER_VERTICAL)) {
        orientation = LinearLayoutCompat.VERTICAL
        setPaddingRelative(0, dip(12), dip(16), dip(12))
        child(::TextView, params(wrapContent, wrapContent)) {
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
            textColor = color(R.color.onBackground)
            text = string(titleResId)
        }
        if (subtitleResId != null) {
            child(::TextView, params(wrapContent, wrapContent)) {
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
                textColor = color(R.color.onBackground).withTransparency(0.60)
                text = string(subtitleResId)
            }
        }
    }

    child(previewView.view, params(wrapContent, wrapContent, weight = 0F, gravity = Gravity.END or Gravity.CENTER_VERTICAL))

    if (previewView.isClickable) {
        backgroundResource = attr(R.attr.selectableItemBackground).resourceId
        isClickable = true
        isFocusable = true
    }
}
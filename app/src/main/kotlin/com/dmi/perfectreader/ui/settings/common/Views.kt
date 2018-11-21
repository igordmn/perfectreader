package com.dmi.perfectreader.ui.settings.common

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewStub
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.chooseSettingValue
import com.dmi.util.android.view.*
import com.dmi.util.lang.initOnce
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.sdk27.coroutines.onClick
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

fun ViewBuild.verticalScroll(vararg list: View) = NestedScrollView {
    id = generateId()
    VerticalLayout {
        list.forEach {
            it into container(matchParent, wrapContent)
        }
    } into container(matchParent, wrapContent)
}

infix fun View.visibleIf(condition: () -> Boolean): View {
    autorun {
        isVisible = condition()
    }
    return this
}

class PreviewView(val view: View, val withPadding: Boolean = true, val isClickable: Boolean = true)

fun ViewBuild.emptyPreview() = PreviewView(ViewStub(context))

fun <T> ViewBuild.propertyPreview(property: KProperty0<T>, format: (Context, value: T) -> String) = PreviewView(AppCompatTextView {
    autorun {
        text = format(context, property.get())
    }
})

fun ViewBuild.detailsSetting(
        model: SettingsUI,
        preview: PreviewView,
        place: Places.Place,
        @StringRes
        titleRes: Int,
        @StringRes
        subtitleRes: Int? = null
) : View {
    return titleSetting(preview, titleRes, subtitleRes).apply {
        onClick {
            model.screens.goForward(place.id)
        }
    }
}

fun ViewBuild.popupSetting(
        model: SettingsUI,
        preview: PreviewView,
        place: Places.Place,
        @StringRes
        titleRes: Int,
        @StringRes
        subtitleRes: Int? = null
) : View {
    return titleSetting(preview, titleRes, subtitleRes).apply {
        onClick {
            model.popup = place.id
        }
    }
}

fun ViewBuild.floatSetting(
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
    val view = HorizontalLayout {
        AppCompatImageButton {
            backgroundResource = attr(R.attr.selectableItemBackground).resourceId
            image = drawable(R.drawable.ic_minus, color(R.color.onBackground).withOpacity(0.60))
            onContinuousClick {
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
        } into container(dip(48), dip(48))

        editNumber = EditNumber {
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
        } into container(dip(48), dip(48))

        AppCompatImageButton {
            backgroundResource = attr(R.attr.selectableItemBackground).resourceId
            image = drawable(R.drawable.ic_plus, color(R.color.onBackground).withOpacity(0.60))
            onContinuousClick {
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
        } into container(dip(48), dip(48))
    }

    return titleSetting(PreviewView(view, withPadding = false), titleRes, subtitleRes).apply {
        onClick {
            editNumber.requestFocus()
        }
    }
}

fun ViewBuild.booleanSetting(
        property: KMutableProperty0<Boolean>,
        @StringRes
        titleRes: Int,
        @StringRes
        subtitleRes: Int? = null
): View {
    val switch = SwitchCompat {
        isClickable = false
        isFocusable = false
        isChecked = property.get()
        onCheckedChange { _, checked ->
            property.set(checked)
        }
    }

    return titleSetting(PreviewView(switch), titleRes, subtitleRes).apply {
        onClick {
            switch.performClick()
        }
    }
}

fun ViewBuild.titleSetting(
        previewView: PreviewView,
        @StringRes
        titleRes: Int,
        @StringRes
        subtitleRes: Int? = null
) = HorizontalLayout {
    setPadding(dip(16), 0, if (previewView.withPadding) dip(16) else 0, 0)

    VerticalLayout {
        setPaddingRelative(0, dip(12), dip(16), dip(12))
        AppCompatTextView {
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
            textColor = color(R.color.onBackground)
            text = string(titleRes)
        } into container(wrapContent, wrapContent)
        if (subtitleRes != null) {
            AppCompatTextView {
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
                textColor = color(R.color.onBackground).withOpacity(0.60)
                text = string(subtitleRes)
            } into container(wrapContent, wrapContent)
        }
    } into container(matchParent, wrapContent, Gravity.CENTER_VERTICAL, weight = 1F)

    previewView.view into container(wrapContent, wrapContent, Gravity.END or Gravity.CENTER_VERTICAL, weight = 0F)

    if (previewView.isClickable) {
        backgroundResource = attr(R.attr.selectableItemBackground).resourceId
        isClickable = true
        isFocusable = true
    }
}
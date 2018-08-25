package com.dmi.perfectreader.settingschange

import android.content.Context
import android.view.Gravity
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import org.jetbrains.anko.*
import kotlin.reflect.KMutableProperty0

fun Context.floatSetting(
        @StringRes
        stringResId: Int,
        property: KMutableProperty0<Float>,
        values: FloatArray,
        decimalCount: Int = 2
) = view(::LinearLayoutCompat) {
    val min = values.first()
    val max = values.last()

    orientation = LinearLayoutCompat.HORIZONTAL
    setPadding(dip(16), 0, dip(16), 0)

    child(::TextView, params(matchParent, dip(48), weight = 1F)) {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
        gravity = Gravity.CENTER_VERTICAL
        singleLine = true
        textColor = color(R.color.onBackground)
        text = string(stringResId)
    }

    child(::LinearLayoutCompat, params(wrapContent, dip(48), weight = 0F)) {
        orientation = LinearLayoutCompat.HORIZONTAL
        setPaddingRelative(dip(16), 0, 0, 0)

        child(::AppCompatImageButton, params(dip(48), dip(48))) {
            backgroundResource = attr(R.attr.selectableItemBackground).resourceId
            image = drawable(R.drawable.minus, color(R.color.onBackground))
            onContinousClick {
                if (property.get() > min)
                    property.set(chooseSettingValue(values, property.get(), -1))
            }
        }

        child(::AppCompatImageButton, params(dip(48), dip(48))) {
            backgroundResource = attr(R.attr.selectableItemBackground).resourceId
            image = drawable(R.drawable.plus, color(R.color.onBackground))
            onContinousClick {
                if (property.get() < max)
                    property.set(chooseSettingValue(values, property.get(), 1))
            }
        }

        child(::EditNumber, params(dip(48), dip(48))) {
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
            textColor = color(R.color.onBackground).withTransparency(0.60)
            this.min = min
            this.max = max
            this.decimalCount = decimalCount
            autorun {
                floatValue = property.get()
            }
            afterChange {
                property.set(floatValue)
            }
        }
    }
}

fun Context.booleanSetting(
        @StringRes
        stringResId: Int,
        property: KMutableProperty0<Boolean>
) = view(::LinearLayoutCompat) {
    orientation = LinearLayoutCompat.HORIZONTAL
    setPadding(dip(16), 0, dip(16), 0)

    child(::TextView, params(matchParent, dip(48), weight = 1F)) {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
        gravity = Gravity.CENTER_VERTICAL
        singleLine = true
        textColor = color(R.color.onBackground)
        text = string(stringResId)
    }

    child(::Switch, params(wrapContent, dip(48), weight = 0F)) {
        orientation = LinearLayoutCompat.HORIZONTAL
        setPaddingRelative(dip(16), 0, 0, 0)

        autorun {
            isChecked = property.get()
        }
        onCheckedChange { _, checked ->
            property.set(checked)
        }
    }
}
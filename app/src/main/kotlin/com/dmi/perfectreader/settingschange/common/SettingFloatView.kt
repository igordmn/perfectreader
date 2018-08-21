package com.dmi.perfectreader.settingschange.common

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.settingschange.chooseSettingValue
import com.dmi.util.android.view.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip
import org.jetbrains.anko.image
import kotlin.reflect.KMutableProperty0

fun Context.settingFloatView(
        property: KMutableProperty0<Float>,
        values: FloatArray,
        @DrawableRes decreaseIconId: Int,
        @DrawableRes increaseIconId: Int
) = view(::LinearLayoutCompat) {
    orientation = LinearLayoutCompat.HORIZONTAL

    child(::AppCompatImageButton, params(dip(48), dip(48))) {
        backgroundResource = attr(R.attr.selectableItemBackground).resourceId
        image = drawable(decreaseIconId, color(R.color.onBackground))
        onContinousClick {
            property.set(chooseSettingValue(values, property.get(), -1))
        }
    }

    child(::AppCompatImageButton, params(dip(48), dip(48))) {
        backgroundResource = attr(R.attr.selectableItemBackground).resourceId
        image = drawable(increaseIconId, color(R.color.onBackground))
        onContinousClick {
            property.set(chooseSettingValue(values, property.get(), 1))
        }
    }
}
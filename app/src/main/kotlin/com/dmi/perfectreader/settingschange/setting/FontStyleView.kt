package com.dmi.perfectreader.settingschange.setting

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.TooltipCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.settingschange.PreviewView
import com.dmi.util.android.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.image
import kotlin.reflect.KMutableProperty0

fun fontStyleView(
        context: Context,
        boldProperty: KMutableProperty0<Boolean>,
        italicProperty: KMutableProperty0<Boolean>,
        @StringRes
        boldStringId: Int,
        @StringRes
        italicStringId: Int
) = PreviewView(
        view = LinearLayoutCompat(context).apply {
            setPadding(0, 0, dip(12), 0)
            child(params(dip(48), dip(48)), CheckableImageButton(context).apply {
                contentDescription = string(boldStringId)
                TooltipCompat.setTooltipText(this, contentDescription)
                image = drawable(R.drawable.text_bold, color(R.color.onBackground))
                isChecked = boldProperty.get()

                onChecked {
                    boldProperty.set(it)
                }
            })

            child(params(dip(48), dip(48)), CheckableImageButton(context).apply {
                contentDescription = string(italicStringId)
                TooltipCompat.setTooltipText(this, contentDescription)
                image = drawable(R.drawable.text_italic, color(R.color.onBackground))
                isChecked = italicProperty.get()

                onChecked {
                    italicProperty.set(it)
                }
            })
        },
        withPadding = false,
        isClickable = false
)
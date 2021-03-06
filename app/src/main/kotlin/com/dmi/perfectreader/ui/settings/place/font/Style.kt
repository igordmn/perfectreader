package com.dmi.perfectreader.ui.settings.place.font

import androidx.annotation.StringRes
import androidx.appcompat.widget.TooltipCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.ui.settings.common.PreviewView
import com.dmi.util.android.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.image
import kotlin.reflect.KMutableProperty0

fun ViewBuild.fontStyleView(
        boldProperty: KMutableProperty0<Boolean>,
        italicProperty: KMutableProperty0<Boolean>,
        @StringRes
        boldStringId: Int,
        @StringRes
        italicStringId: Int
) = PreviewView(
        view = HorizontalLayout {
            setPadding(0, 0, dip(12), 0)

            CheckableImageButton {
                contentDescription = string(boldStringId)
                TooltipCompat.setTooltipText(this, contentDescription)
                image = drawable(R.drawable.ic_text_bold, color(R.color.onBackground))
                isChecked = boldProperty.get()

                onChecked {
                    boldProperty.set(it)
                }
            } into container(dip(48), dip(48))

            CheckableImageButton {
                contentDescription = string(italicStringId)
                TooltipCompat.setTooltipText(this, contentDescription)
                image = drawable(R.drawable.ic_text_italic, color(R.color.onBackground))
                isChecked = italicProperty.get()

                onChecked {
                    italicProperty.set(it)
                }
            } into container(dip(48), dip(48))
        },
        withPadding = false,
        isClickable = false
)
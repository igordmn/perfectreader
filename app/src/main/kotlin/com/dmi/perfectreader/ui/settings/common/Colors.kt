package com.dmi.perfectreader.ui.settings.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.InputFilter
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.dmi.perfectreader.R
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.util.android.view.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

fun Places.colorPlace(model: SettingsUI, property: KMutableProperty0<Int>, @StringRes titleRes: Int) = place {
    val hex = dialog {
        colorHEXDialog(model, property)
    }

    view {
        colorDetails(model, titleRes, property, hex)
    }
}

fun ViewBuild.colorDetails(
        model: SettingsUI,
        @StringRes titleRes: Int,
        property: KMutableProperty0<Int>,
        hex: Places.Place
) = details(
        detailsToolbar(titleRes, model).apply {
            menu.add(R.string.settingsUIColorHEX).apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                onClick {
                    model.popup = hex.id
                }
            }
        },
        ColorPickerView {
            setOnColorChangedListener {
                property.set(color)
            }
            autorun {
                color = property.get()
            }
        }
)

fun ViewBuild.colorHEXDialog(
        model: SettingsUI,
        property: KMutableProperty0<Int>
): AlertDialog {
    val editText = EditText {
        setText(colorToHEX(property.get()))
        filters = arrayOf(InputFilter.LengthFilter(6))
        keyListener = DigitsKeyListener.getInstance("0123456789ABCDEFabcdef")
        inputType = TYPE_CLASS_TEXT
    }
    val view = HorizontalLayout {
        TextView {
            text = "#"
        } into container(wrapContent, wrapContent, Gravity.CENTER_VERTICAL, weight = 0F)
        editText into container(matchParent, wrapContent, Gravity.CENTER_VERTICAL, weight = 1F)
    }
    return AlertDialog.Builder(context)
            .setTitle(R.string.settingsUIColorHEX)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val normalized = "#" + editText.text.toString().toLowerCase().padStart(6, '0')
                property.set(Color.parseColor(normalized))
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setOnDismissListener {
                model.popup = null
            }
            .create()
}

private fun colorToHEX(color: Int) = String.format("%06x", 0xFFFFFF and color)

fun ViewBuild.colorPreview(property: KProperty0<Int>) = PreviewView(ColorPreviewView(context, property))

class ColorPreviewView(
        context: Context,
        private val property: KProperty0<Int>
) : TextView(context) {
    private val paint = Paint()
    private var color: Int = property.get()
    private val strokeWidth = 1F
    private val diameter = dipFloat(24F)

    init {
        minimumWidth = diameter.toInt()
        minimumHeight = diameter.toInt()

        autorun {
            color = property.get()
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = color
        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2 - strokeWidth, paint)
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth = strokeWidth
        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2 - strokeWidth, paint)
    }
}
package com.dmi.perfectreader.settingschange.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.InputFilter
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.settingschange.SettingsChange
import com.dmi.util.android.view.*
import com.jaredrummler.android.colorpicker.ColorPickerView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

fun colorDetails(
        context: Context,
        model: SettingsChange,
        @StringRes titleRes: Int,
        property: KMutableProperty0<Int>,
        hex: Places.Place
) = details(
        context, model, titleRes,
        ColorPickerView(context).apply {
            setOnColorChangedListener {
                property.set(color)
            }
            autorun {
                color = property.get()
            }
        },
        configureMenu = { menu ->
            menu.add(R.string.settingsChangeColorHEX).apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                onClick {
                    model.popup = hex.id
                }
            }
        }
)

fun colorHEXDialog(
        context: Context,
        model: SettingsChange,
        property: KMutableProperty0<Int>
): AlertDialog {
    val editText = EditText(context).apply {
        setText(colorToHEX(property.get()))
        filters = arrayOf(InputFilter.LengthFilter(6))
        keyListener = DigitsKeyListener.getInstance("0123456789ABCDEFabcdef")
        inputType = TYPE_CLASS_TEXT
    }
    val view = LinearLayoutCompat(context).apply {
        orientation = LinearLayout.HORIZONTAL
        child(params(wrapContent, wrapContent, Gravity.CENTER_VERTICAL, weight = 0F), TextView(context).apply {
            text = "#"
        })
        child(params(matchParent, wrapContent, Gravity.CENTER_VERTICAL, weight = 1F), editText)
    }
    return AlertDialog.Builder(context)
            .setTitle(R.string.settingsChangeColorHEX)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val normalized = "#" + editText.text.toString().toLowerCase().padStart(6, '0')
                property.set(Color.parseColor(normalized))
                model.popup = null
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
}

private fun colorToHEX(color: Int) = String.format("%06x", 0xFFFFFF and color)

fun colorPreview(context: Context, property: KProperty0<Int>) = PreviewView(ColorPreviewView(context, property))

class ColorPreviewView(
        context: Context,
        private val property: KProperty0<Int>
) : TextView(context) {
    private val paint = Paint()
    private var color: Int = property.get()
    private val size = dipFloat(32F)
    private val radius = dipFloat(24F)

    init {
        minimumWidth = size.toInt()
        minimumHeight = size.toInt()

        autorun {
            color = property.get()
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = color
        canvas.drawCircle(size / 2, size / 2, radius / 2, paint)
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth = 2F
        canvas.drawCircle(size / 2, size / 2, radius / 2, paint)
    }
}
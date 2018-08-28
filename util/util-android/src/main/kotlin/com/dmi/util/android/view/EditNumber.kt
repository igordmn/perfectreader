package com.dmi.util.android.view

import android.content.Context
import android.graphics.Rect
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.text.method.DigitsKeyListener
import android.view.ActionMode
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.dmi.util.android.system.hideSoftKeyboard
import com.dmi.util.android.system.showSoftKeyboard
import com.dmi.util.lang.intCeil
import com.dmi.util.lang.intFloor
import kotlin.math.max

class EditNumber(context: Context) : EditText(context), ClearFocusOnClickOutside {
    var min: Float? = null
        set(value) {
            field = value
            update()
        }

    var max: Float? = null
        set(value) {
            field = value
            update()
        }

    var decimalCount: Int = 0
        set(value) {
            field = value
            update()
        }

    private fun update() {
        val min = min
        val max = max
        val decimalCount = decimalCount

        val filters = ArrayList<InputFilter>()

        if (min != null && max != null) {
            val minLength = intFloor(min).toString().length
            val maxLength = intCeil(max).toString().length
            var length = max(minLength, maxLength)
            if (decimalCount > 0)
                length += 1 + decimalCount
            if (min < 0)
                length++
            filters.add(InputFilter.LengthFilter(length))
        }

        if (decimalCount > 0)
            filters.add(DecimalCountFilter(decimalCount))

        this.filters = filters.toTypedArray()

        var inputType = EditorInfo.TYPE_CLASS_NUMBER
        if (min == null || min < 0)
            inputType = inputType or InputType.TYPE_NUMBER_FLAG_SIGNED
        if (decimalCount > 0)
            inputType = inputType or InputType.TYPE_NUMBER_FLAG_DECIMAL
        setRawInputType(inputType)

        // we always use '.' instead ','
        // even in Russian language (we don't specify Locale)
        @Suppress("DEPRECATION")
        keyListener = DigitsKeyListener.getInstance(min == null || min < 0, decimalCount > 0)
    }

    private var beforeEdit: (() -> Unit)? = null
    private var afterChange: (() -> Unit)? = null

    fun beforeEdit(action: () -> Unit) {
        beforeEdit = action
    }

    fun afterChange(action: () -> Unit) {
        afterChange = action
    }

    private lateinit var valueOnFocus: String

    init {
        customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false
            override fun onDestroyActionMode(mode: ActionMode) = Unit
            override fun onCreateActionMode(mode: ActionMode, menu: Menu) = false
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem) = false
        }
        background = null
        imeOptions = EditorInfo.IME_ACTION_DONE
        inputType = EditorInfo.TYPE_CLASS_NUMBER
    }

    var floatValue: Float
        get() = text.toString().toFloat()
        set(value) {
            super.setText(formatValue(value))
            hint = text
        }

    var intValue: Int
        get() = text.toString().toInt()
        set(value) {
            super.setText(formatValue(value.toFloat()))
            hint = text
        }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            require(text.isNotEmpty())
            beforeEdit?.invoke()
            valueOnFocus = text.toString()
            text.clear()
            showSoftKeyboard()
        } else {
            val parsed = text.toString().toFloatOrNull()
            super.setText(when {
                parsed == null -> valueOnFocus
                min != null && parsed < min!! -> formatValue(min!!)
                max != null && parsed > max!! -> formatValue(max!!)
                else -> formatValue(text.toString().toFloat())
            })
            hint = text
            if (text.toString() != valueOnFocus)
                afterChange?.invoke()
            hideSoftKeyboard()
        }
    }

    override fun onEditorAction(actionCode: Int) {
        super.onEditorAction(actionCode)
        if (actionCode == EditorInfo.IME_ACTION_DONE)
            clearFocus()
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            super.setText(valueOnFocus)
            clearFocus()
        }
        return super.onKeyPreIme(keyCode, event)
    }

    private fun formatValue(value: Float) = String.format("%.${decimalCount}f", value)
}

private class DecimalCountFilter(private val count: Int) : InputFilter {
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence {
        val inserting = source.subSequence(start, end)
        val destDotIndex = dest.indexOf('.')
        val insDotIndex = inserting.indexOf('.')

        fun isInsertingAfterDot() = destDotIndex in 0 until dstart
        fun isInsertingDot() = insDotIndex >= 0
        fun afterDotCount() = dest.length - (destDotIndex + 1)
        fun insertingAfterDotCount() = inserting.length - (insDotIndex + 1)
        fun afterInsCount() = dest.length - dend

        return when {
            inserting.isEmpty() -> inserting
            isInsertingAfterDot() -> if (afterDotCount() + inserting.length > count) "" else inserting
            isInsertingDot() && insertingAfterDotCount() + afterInsCount() > count -> ""
            else -> inserting
        }
    }
}
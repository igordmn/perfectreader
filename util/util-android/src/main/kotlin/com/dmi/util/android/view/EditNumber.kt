package com.dmi.util.android.view

import android.content.Context
import android.graphics.Rect
import android.text.InputType
import android.view.ActionMode
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.dmi.util.android.R
import com.dmi.util.android.system.hideSoftKeyboard
import com.dmi.util.android.system.showSoftKeyboard
import org.jetbrains.anko.padding



class EditNumber(context: Context) : EditText(context), ClearFocusOnClickOutside {
    var min: Int? = null
    var max: Int? = null

    var isDecimal: Boolean = false
        set(value) {
            field = value
            updateInputType()
        }

    var isSigned: Boolean = false
        set(value) {
            field = value
            updateInputType()
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

    private fun updateInputType() {
        var inputType = EditorInfo.TYPE_CLASS_NUMBER
        if (isDecimal)
            inputType = inputType or InputType.TYPE_NUMBER_FLAG_DECIMAL
        if (isSigned)
            inputType = inputType or InputType.TYPE_NUMBER_FLAG_SIGNED
        this.inputType = inputType
    }

    init {
        customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false
            override fun onDestroyActionMode(mode: ActionMode) = Unit
            override fun onCreateActionMode(mode: ActionMode, menu: Menu) = false
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem) = false
        }
        padding = 0
        background = null
        imeOptions = EditorInfo.IME_ACTION_DONE
        inputType = EditorInfo.TYPE_CLASS_NUMBER
    }

    var floatValue: Float
        get() = text.toString().toFloat()
        set(value) {
            require(isDecimal && (!isSigned || isSigned && value >= 0))
            setText(value.toString())
            hint = text
        }

    var intValue: Int
        get() = text.toString().toInt()
        set(value) {
            setText(value.toString())
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
            if (!isValid())
                setText(valueOnFocus)
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
            setText(valueOnFocus)
            clearFocus()
        }
        return super.onKeyPreIme(keyCode, event)
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        updateError()
    }

    private fun isValid(): Boolean {
        val parsed = text.toString().toIntOrNull()
        return when {
            parsed == null -> false
            min != null && parsed < min!! -> false
            max != null && parsed > max!! -> false
            else -> true
        }
    }

    private fun updateError() {
        setError(
                if (isValid() || text.isEmpty()) {
                    null
                } else {
                    if (min != null && max != null) {
                        string(R.string.editNumberLimitError, min!!, max!!)
                    } else {
                        string(R.string.editNumberError)
                    }
                },
                null
        )
    }


}
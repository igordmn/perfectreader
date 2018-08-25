package com.dmi.util.android.system

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.showSoftKeyboard() {
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, 0)
}

fun View.hideSoftKeyboard() {
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}
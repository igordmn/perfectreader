package com.dmi.util.android.system

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun Context.copyPlainText(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.primaryClip = ClipData.newPlainText("Book text", text)
}
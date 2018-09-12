package com.dmi.perfectreader.selection

import android.content.*
import com.dmi.perfectreader.Main
import com.dmi.perfectreader.R
import org.jetbrains.anko.toast

class TextActions(
        private val main: Main,
        private val context: Context = main.applicationContext
) {
    fun copy(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip = ClipData.newPlainText("Book text", text)
        context.toast(R.string.selectionCopiedToClipboard)
    }

    fun translate(text: String) {
        val packageName = "com.google.android.apps.translate"
        val activityName = context.packageManager.queryIntentActivities(Intent().apply {
            action = "android.intent.action.PROCESS_TEXT"
            type = "text/plain"
        }, 0).firstOrNull {
            it.activityInfo.packageName == packageName
        }?.activityInfo?.name

        if (activityName != null) {
            val intent = Intent()
            intent.action = "android.intent.action.PROCESS_TEXT"
            intent.type = "text/plain"
            intent.putExtra("android.intent.extra.PROCESS_TEXT", text)
            intent.component = ComponentName(packageName, activityName)
            context.startActivity(intent)
        } else {
            context.toast(R.string.googleTranslateNotInstalled)
        }
    }
}
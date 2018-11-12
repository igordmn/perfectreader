package com.dmi.perfectreader.selection

import android.app.Activity
import android.app.SearchManager
import android.content.*
import android.net.Uri
import com.dmi.perfectreader.R
import org.jetbrains.anko.toast
import java.net.URLEncoder
import java.util.*

class TextActions(private val activity: Activity) {
    fun copy(text: String) {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip = ClipData.newPlainText("Book text", text)
        activity.toast(R.string.selectionCopiedToClipboard)
    }

    fun translate(text: String) {
        val packageName = "com.google.android.apps.translate"
        val activityName = activity.packageManager.queryIntentActivities(Intent().apply {
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
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.selectionGoogleTranslateNotInstalled)
        }
    }

    fun searchWeb(text: String) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, text)
        activity.startActivity(intent)
    }

    fun searchWiki(text: String, locale: Locale) {
        val lang = locale.toLanguageTag()
        val escapedQuery = URLEncoder.encode(text, "UTF-8")
        val uri = Uri.parse("https://$lang.wikipedia.org/wiki/Special:Search/$escapedQuery")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        activity.startActivity(intent)
    }
}
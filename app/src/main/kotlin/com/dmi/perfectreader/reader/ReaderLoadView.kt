package com.dmi.perfectreader.reader

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
import com.dmi.perfectreader.reader.ReaderLoad.LoadError
import com.dmi.perfectreader.settings.ScreenOrientation
import com.dmi.util.android.view.*
import org.jetbrains.anko.*

fun ViewBuild.readerLoadView(model: ReaderLoad) = FrameLayoutExt(context).apply {
    isFocusable = true
    isFocusableInTouchMode = true

    bindChild(params(matchParent, matchParent, Gravity.CENTER), model::reader, ViewBuild::readerView, defferStateRestore = true).apply {
        id = generateId()
    }
    child(params(wrapContent, wrapContent, Gravity.CENTER), ProgressBar(context).apply {
        autorun {
            visibility = if (model.isLoading) View.VISIBLE else View.GONE
        }
    })
    child(params(wrapContent, wrapContent, Gravity.CENTER), TextView(context).apply {
        fun showError(strId: Int) {
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
            visibility = View.VISIBLE
            gravity = Gravity.CENTER
            text = string(strId)
            textColor = color(R.color.onBackground).withOpacity(0.60)
        }

        padding = dip(16)
        autorun {
            when (model.error) {
                null -> visibility = View.GONE
                is LoadError.IO -> showError(R.string.bookOpenError)
            }
        }
    })

    applyOrientation()

    onInterceptKeyDown(KeyEvent.KEYCODE_BACK) {
        if (model.reader == null) {
            model.close()
            true
        } else {
            false
        }
    }
}

private fun View.applyOrientation() {
    val activity = context as Activity

    autorun {
        activity.requestedOrientation = when (context.main.settings.screen.orientation) {
            ScreenOrientation.SYSTEM -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            ScreenOrientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            ScreenOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
package com.dmi.perfectreader.reader

import android.content.Context
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.reader.ReaderLoad.LoadError
import com.dmi.util.android.view.*
import org.jetbrains.anko.*

fun readerLoadView(context: Context, model: ReaderLoad) = FrameLayoutExt(context).apply {
    keepScreenOn = true
    isFocusable = true
    isFocusableInTouchMode = true

    bindChild(params(matchParent, matchParent, Gravity.CENTER), model::reader, ::readerView)
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
            textColor = color(R.color.onBackground).withTransparency(0.60)
        }

        padding = dip(16)
        autorun {
            when (model.error) {
                null -> visibility = View.GONE
                is LoadError.IO -> showError(R.string.bookOpenError)
                is LoadError.NeedOpenThroughFileManager -> showError(R.string.bookNeedOpenThroughFileManager)
                is LoadError.NeedStoragePermissions -> showError(R.string.bookNeedStoragePermissions)
            }
        }
    })

    onInterceptKeyDown(KeyEvent.KEYCODE_BACK) {
        if (model.reader == null) {
            model.back()
            true
        } else {
            false
        }
    }
}
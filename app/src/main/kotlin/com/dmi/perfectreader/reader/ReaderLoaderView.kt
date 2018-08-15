package com.dmi.perfectreader.reader

import android.content.Context
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.reader.ReaderLoader.LoadError
import com.dmi.util.android.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.wrapContent

fun Context.readerLoaderView(model: ReaderLoader) = view(::FrameLayoutExt) {
    keepScreenOn = true
    bindChild(model::reader, ::readerView, params(matchParent, matchParent, Gravity.CENTER))
    child(::ProgressBar, params(wrapContent, wrapContent, Gravity.CENTER)) {
        autorun {
            visibility = if (model.isLoading) View.VISIBLE else View.GONE
        }
    }
    child(::TextView, params(wrapContent, wrapContent, Gravity.CENTER)) {
        fun showError(strId: Int) {
            visibility = View.VISIBLE
            text = string(strId)
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
    }

    onInterceptKeyDown(KeyEvent.KEYCODE_BACK) {
        if (model.reader == null) {
            model.close()
            true
        } else {
            false
        }
    }
}
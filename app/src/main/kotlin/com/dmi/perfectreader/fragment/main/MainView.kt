package com.dmi.perfectreader.fragment.main

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.fragment.main.Main.LoadError
import com.dmi.perfectreader.fragment.reader.Reader
import com.dmi.perfectreader.fragment.reader.ReaderView
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.base.find

class MainView(
        private val context: Context,
        private val model: Main,
        private val createReader: (Reader) -> ReaderView
) : BaseView(context, R.layout.fragment_main) {
    private var reader: ReaderView? = null
    private var readerContainer = find<ViewGroup>(R.id.readerContainer)
    private val loadIndicator = find<ProgressBar>(R.id.loadIndicator)
    private val error = find<TextView>(R.id.error)

    init {
        widget.id = 1 // Если id не установить, не будет восстанавливаться состояние View

        subscribe(model.isLoadingObservable) {
            loadIndicator.visibility = if (it) View.VISIBLE else View.GONE
        }

        subscribe(model.loadErrorObservable) {
            fun showError(strId: Int) {
                error.visibility = View.VISIBLE
                error.text = context.getString(strId)
            }

            when (it) {
                null -> error.visibility = View.GONE
                is LoadError.IO -> showError(R.string.bookOpenError)
                is LoadError.NeedOpenThroughFileManager -> showError(R.string.bookNeedOpenThroughFileManager)
            }
        }

        subscribe(model.readerObservable) {
            reader = toggleChildByModel(it, reader, R.id.readerContainer) {
                createReader(it)
            }
            readerContainer.visibility = if (reader != null) View.VISIBLE else View.GONE
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (reader == null) {
            if (keyCode == KeyEvent.KEYCODE_BACK)
                model.close()
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }
}
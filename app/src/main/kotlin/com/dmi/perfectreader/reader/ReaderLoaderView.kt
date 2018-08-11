package com.dmi.perfectreader.reader

import android.annotation.SuppressLint
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.ViewContext
import com.dmi.perfectreader.reader.ReaderLoader.LoadError
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.base.find
import com.dmi.util.android.base.string

class ReaderLoaderView(
        viewContext: ViewContext,
        private val model: ReaderLoader
) : BaseView(viewContext.android, R.layout.fragment_main) {
    private var reader: ReaderView? = null
    private var readerContainer = find<ViewGroup>(R.id.readerContainer)
    private val loadIndicator = find<ProgressBar>(R.id.loadIndicator)
    private val error = find<TextView>(R.id.error)

    init {
        @SuppressLint("ResourceType")
        widget.id = 1 // Если id не установить, не будет восстанавливаться состояние View

        autorun {
            loadIndicator.visibility = if (model.isLoading) View.VISIBLE else View.GONE
        }

        autorun {
            fun showError(strId: Int) {
                error.visibility = View.VISIBLE
                error.text = string(strId)
            }

            when (model.error) {
                null -> error.visibility = View.GONE
                is LoadError.IO -> showError(R.string.bookOpenError)
                is LoadError.NeedOpenThroughFileManager -> showError(R.string.bookNeedOpenThroughFileManager)
                is LoadError.NeedStoragePermissions -> showError(R.string.bookNeedStoragePermissions)
            }
        }

        autorun {
            reader = toggleChildByModel(model.reader, reader, R.id.readerContainer) { ReaderView(viewContext, it) }
            readerContainer.visibility = if (reader != null) View.VISIBLE else View.GONE
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (reader == null) {
            if (keyCode == KeyEvent.KEYCODE_BACK)
                model.close()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }
}
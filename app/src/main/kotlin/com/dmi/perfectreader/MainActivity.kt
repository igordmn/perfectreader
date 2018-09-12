package com.dmi.perfectreader

import android.content.Intent
import com.dmi.perfectreader.reader.ReaderLoad
import com.dmi.perfectreader.reader.readerLoadView
import com.dmi.util.android.view.ActivityExt

class MainActivity : ActivityExt<ReaderLoad>() {
    override fun createModel() = ReaderLoad(main, intent, ::back)
    override fun createView(model: ReaderLoad) = readerLoadView(this, model)
    private fun back() = finish()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recreateModel()
    }
}
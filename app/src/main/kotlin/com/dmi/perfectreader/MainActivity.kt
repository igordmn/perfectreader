package com.dmi.perfectreader

import android.content.Intent
import android.os.Bundle
import com.dmi.perfectreader.reader.ReaderLoad
import com.dmi.perfectreader.reader.readerLoadView
import com.dmi.util.android.view.ActivityExt

class MainActivity : ActivityExt<ReaderLoad>() {
    override fun createModel() = ReaderLoad(main, window, intent)
    override fun createView(model: ReaderLoad) = readerLoadView(model)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recreateModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        main.currentActivity = this
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        main.currentActivity = null
    }
}
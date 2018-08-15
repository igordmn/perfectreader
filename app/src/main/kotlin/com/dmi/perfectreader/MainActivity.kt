package com.dmi.perfectreader

import android.content.Intent
import android.os.Bundle
import com.dmi.perfectreader.reader.ReaderLoader
import com.dmi.perfectreader.reader.readerLoaderView
import com.dmi.util.android.view.ActivityExt

class MainActivity : ActivityExt<ReaderLoader>() {
    override fun createModel() = ReaderLoader(main, window, intent)
    override fun createView(model: ReaderLoader) = readerLoaderView(model)

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
package com.dmi.perfectreader

import android.content.Intent
import android.os.Bundle
import com.dmi.perfectreader.common.ViewContext
import com.dmi.perfectreader.reader.ReaderLoader
import com.dmi.perfectreader.reader.ReaderLoaderView
import com.dmi.util.android.base.BaseActivity

class MainActivity : BaseActivity<ReaderLoader, ReaderLoaderView>() {
    override fun createModel() = ReaderLoader(main, window, intent)
    override fun createView(viewModel: ReaderLoader) = ReaderLoaderView(ViewContext(this, window, main), viewModel)

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
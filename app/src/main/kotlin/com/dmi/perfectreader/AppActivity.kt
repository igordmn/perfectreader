package com.dmi.perfectreader

import android.content.Intent
import com.dmi.perfectreader.main.Main
import com.dmi.perfectreader.main.MainView
import com.dmi.util.android.base.BaseActivity

class AppActivity : BaseActivity<MainView, Main>() {
    override fun createViewModel() = app.objects.createMain(this)
    override fun createView(viewModel: Main) = app.objects.createMainView(this, viewModel)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recreateViewModel()
    }
}
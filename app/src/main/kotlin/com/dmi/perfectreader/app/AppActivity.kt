package com.dmi.perfectreader.app

import android.content.Intent
import com.dmi.perfectreader.fragment.main.Main
import com.dmi.perfectreader.fragment.main.MainView
import com.dmi.util.base.BaseActivity

class AppActivity : BaseActivity<MainView, Main>() {
    override fun createViewModel() = app.objects.createMain(this)
    override fun createView(viewModel: Main) = app.objects.createMainView(this, viewModel)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recreateViewModel()
    }
}
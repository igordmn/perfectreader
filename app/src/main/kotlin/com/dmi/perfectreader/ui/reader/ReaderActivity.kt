package com.dmi.perfectreader.ui.reader

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.dmi.perfectreader.main
import com.dmi.perfectreader.ui.library.LibraryActivity
import com.dmi.util.android.view.ActivityExt
import com.dmi.util.android.view.ViewBuild
import kotlinx.serialization.cbor.CBOR.Companion.dump
import kotlinx.serialization.cbor.CBOR.Companion.load

// todo if error occurred, close activity, and show library
// todo change task name by book name
class ReaderActivity : ActivityExt<ReaderLoad>() {
    override fun createModel(stateData: ByteArray?) = ReaderLoad(readerContext(), intent.data!!, ::close, ::showLibrary, loadState(stateData))
    override fun saveModel(model: ReaderLoad) = saveState(model.state)
    override fun ViewBuild.view(model: ReaderLoad) = readerLoadView(model)
    private fun loadState(stateData: ByteArray?): ReaderLoadState = if (stateData != null) load(stateData) else ReaderLoadState()
    private fun saveState(state: ReaderLoadState): ByteArray = dump(state)
    private fun readerContext() = ReaderContext(main, this)

    private fun close() = finish()

    private fun showLibrary() {
        finish()
        LibraryActivity.start(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        main.settings.state.isLibrary = false
        main.settings.state.bookUri = intent.data!!.toString()
    }

    companion object {
        fun start(context: Context, uri: Uri): Unit = context.startActivity(
                Intent(context, ReaderActivity::class.java).apply {
                    data = uri
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                }
        )
    }
}
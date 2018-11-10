package com.dmi.perfectreader.reader

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import com.dmi.perfectreader.main
import com.dmi.util.android.view.ActivityExt
import com.dmi.util.android.view.ViewBuild
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.cbor.CBOR.Companion.dump
import kotlinx.serialization.cbor.CBOR.Companion.load

class ReaderActivity : ActivityExt<ReaderLoad>() {
    override fun createModel(stateData: ByteArray?) = ReaderLoad(readerContext(), intent.data!!, ::close, loadState(stateData))
    override fun saveModel(model: ReaderLoad) = saveState(model.state)
    override fun ViewBuild.view(model: ReaderLoad) = readerLoadView(model)
    private fun loadState(stateData: ByteArray?): ReaderLoadState = if (stateData != null) load(stateData) else ReaderLoadState()
    private fun saveState(state: ReaderLoadState): ByteArray = dump(state)
    private fun close() = finish()
    private fun readerContext() = ReaderContext(main, this)

    companion object {
        fun open(context: Context, uri: Uri): Unit = context.startActivity(
                Intent(context, ReaderActivity::class.java).apply {
                    data = uri
                    flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                }
        )
    }
}
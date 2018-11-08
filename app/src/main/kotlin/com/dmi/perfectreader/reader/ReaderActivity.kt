package com.dmi.perfectreader.reader

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.dmi.perfectreader.main
import com.dmi.util.android.view.ActivityExt
import kotlinx.serialization.cbor.CBOR.Companion.dump
import kotlinx.serialization.cbor.CBOR.Companion.load

class ReaderActivity : ActivityExt<ReaderLoad>() {
    override fun createModel(stateData: ByteArray?) = ReaderLoad(main, intent.data!!, ::close, loadState(stateData))
    override fun saveModel(model: ReaderLoad) = saveState(model.state)
    override fun view(model: ReaderLoad) = readerLoadView(this, model)
    private fun loadState(stateData: ByteArray?): ReaderLoadState = if (stateData != null) load(stateData) else ReaderLoadState()
    private fun saveState(state: ReaderLoadState): ByteArray = dump(state)
    private fun close() = finish()

    companion object {
        fun open(context: Context, uri: Uri): Unit = context.startActivity(
                Intent(context, ReaderActivity::class.java).apply {
                    data = uri
                    flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                }
        )
    }
}
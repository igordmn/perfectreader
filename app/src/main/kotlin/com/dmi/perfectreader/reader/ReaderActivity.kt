package com.dmi.perfectreader.reader

import android.content.Intent
import com.dmi.perfectreader.main
import com.dmi.util.android.view.ActivityExt
import kotlinx.serialization.cbor.CBOR.Companion.dump
import kotlinx.serialization.cbor.CBOR.Companion.load

class ReaderActivity : ActivityExt<ReaderLoad>() {
    override fun createModel(stateData: ByteArray?) = ReaderLoad(main, intent.data!!, ::back, loadState(stateData))
    override fun saveModel(model: ReaderLoad) = saveState(model.state)
    override fun view(model: ReaderLoad) = readerLoadView(this, model)
    private fun loadState(stateData: ByteArray?): ReaderLoadState = if (stateData != null) load(stateData) else ReaderLoadState()
    private fun saveState(state: ReaderLoadState): ByteArray = dump(state)
    private fun back() = finish()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recreateModel()
    }
}
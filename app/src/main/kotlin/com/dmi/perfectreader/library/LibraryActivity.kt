package com.dmi.perfectreader.library

import android.net.Uri
import com.dmi.perfectreader.main
import com.dmi.perfectreader.reader.ReaderActivity
import com.dmi.util.android.view.ActivityExt
import com.dmi.util.android.view.ViewBuild
import kotlinx.serialization.cbor.CBOR.Companion.dump
import kotlinx.serialization.cbor.CBOR.Companion.load


class LibraryActivity : ActivityExt<Library>() {
    override fun createModel(stateData: ByteArray?) = Library(main, ::back, ::openBook, loadState(stateData))
    override fun saveModel(model: Library) = saveState(model.state)
    override fun ViewBuild.view(model: Library) = libraryView(model)
    private fun loadState(stateData: ByteArray?): LibraryState = if (stateData != null) load(stateData) else LibraryState(0)
    private fun saveState(state: LibraryState): ByteArray = dump(state)
    private fun back() = finish()

    private fun openBook(uri: Uri) = ReaderActivity.open(this, uri)
}
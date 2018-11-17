package com.dmi.perfectreader.ui.library

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.dmi.perfectreader.main
import com.dmi.perfectreader.settings.OptionalLibraryLocationsState
import com.dmi.perfectreader.ui.reader.ReaderActivity
import com.dmi.util.android.view.ActivityExt
import com.dmi.util.android.view.ViewBuild
import kotlinx.serialization.cbor.CBOR.Companion.dump
import kotlinx.serialization.cbor.CBOR.Companion.load

class LibraryActivity : ActivityExt<Library>() {
    override fun createModel(
            stateData: ByteArray?
    ) = Library(main, ::back, ::openBook, loadState = { loadState(it, stateData) })

    private fun loadState(
            folders: Library.Folders,
            stateData: ByteArray?
    ): LibraryState {
        val locationsState = main.settings.state.locations.state
        return when {
            stateData != null -> load(stateData)
            locationsState != null -> LibraryState(locations = locationsState)
            else -> LibraryState(locations = LibraryLocationsState(folders))
        }
    }

    override fun saveModel(model: Library) = dump(model.state)

    override fun ViewBuild.view(model: Library) = libraryView(model)

    private fun back() = finish()

    private fun openBook(uri: Uri) = ReaderActivity.start(this, uri)

    override fun onStart() {
        super.onStart()
        model.refresh()
        main.settings.state.isLibrary = true
    }
    override fun onPause() {
        super.onPause()
        main.settings.state.locations = OptionalLibraryLocationsState(model.state.locations)
    }

    companion object {
        fun start(context: Context): Unit = context.startActivity(
                Intent(context, LibraryActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                }
        )
    }
}
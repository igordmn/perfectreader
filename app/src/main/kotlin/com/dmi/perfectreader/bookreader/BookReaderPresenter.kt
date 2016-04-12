package com.dmi.perfectreader.bookreader

import com.dmi.perfectreader.cache.BookResourceCache
import com.dmi.perfectreader.userdata.UserData
import com.dmi.util.base.BasePresenter

import java.io.File

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
open class BookReaderPresenter : BasePresenter() {
    @Inject
    @Named("requestedBookFile")
    protected lateinit var requestedBookFile: File
    @Inject
    protected lateinit var userData: UserData
    @Inject
    protected lateinit var view: BookReaderController
    @Inject
    protected lateinit var bookResourceCache: BookResourceCache

    override fun onDestroy() {
        bookResourceCache.close()
    }

    fun openBook() {
        val bookFile = getBookFile(requestedBookFile)
        if (bookFile != null) {
            view.openBook(bookFile)
        } else {
            view.showNeedOpenThroughFileManager()
        }
    }

    private fun getBookFile(requestedBookFile: File?): File? {
        if (requestedBookFile != null) {
            userData.saveLastBookFile(requestedBookFile)
            return requestedBookFile
        } else {
            return userData.loadLastBookFile()
        }
    }

    open fun toggleMenu() {
        view.toggleMenu()
    }

    open fun exit() {
        view.exit()
    }
}

package com.dmi.perfectreader.book

import android.content.Context
import com.dmi.perfectreader.app.AppThreads.postIOTask
import com.dmi.perfectreader.book.pagebook.PageBook
import com.dmi.perfectreader.book.pagebook.PageBookRenderer
import com.dmi.perfectreader.bookstorage.EPUBBookStorage
import com.dmi.perfectreader.setting.AppSettings
import com.dmi.perfectreader.userdata.UserData
import com.dmi.util.base.BasePresenter
import com.dmi.util.setting.AbstractSettingsApplier
import com.google.common.base.MoreObjects.firstNonNull
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
open class BookPresenter : BasePresenter() {
    @Inject
    @Named("bookFile")
    protected lateinit  var bookFile: File
    @Inject
    @Named("applicationContext")
    protected lateinit var context: Context
    @Inject
    protected lateinit var userData: UserData
    @Inject
    protected lateinit var appSettings: AppSettings
    @Inject
    protected lateinit var bookStorage: EPUBBookStorage
    @Inject
    protected lateinit var view: BookFragment

    private val settingsApplier = SettingsApplier()
    private val pageBook: PageBook? = null
    private var tapHandler: TapHandler? = null

    public override fun onCreate() {
        settingsApplier.startListen()
        pageBook!!.goPercent(loadLocation())
        postIOTask { this.loadBook() }
    }

    public override fun onDestroy() {
        settingsApplier.stopListen()
    }

    fun createRenderer(): PageBookRenderer {
        return object: PageBookRenderer {
            override val isLoading: Boolean
                get() = throw UnsupportedOperationException()

            override fun onSurfaceCreated() {
                throw UnsupportedOperationException()
            }

            override fun onSurfaceChanged(width: Int, height: Int) {
                throw UnsupportedOperationException()
            }

            override fun onFreeResources() {
                throw UnsupportedOperationException()
            }

            override fun onDrawFrame() {
                throw UnsupportedOperationException()
            }
        }
    }

    protected fun loadBook() {
        try {
            bookStorage.load(bookFile)
            postIOTask { this.afterBookStorageLoad() }
        } catch (e: IOException) {
            Timber.e(e, "Load book error")
            view.showBookLoadingError()
        }

    }

    protected fun afterBookStorageLoad() {
        settingsApplier.applyAll()
    }

    private fun loadLocation(): Double {
        return firstNonNull(userData.loadBookLocation(bookFile), 0.0)
    }

    protected fun saveLocation() {
        val currentLocation = 0
        postIOTask { userData.saveBookLocation(bookFile, currentLocation.toDouble()) }
    }

    fun resize(width: Int, height: Int) {
        pageBook!!.resize(width, height)
    }

    fun resume() {
    }

    fun pause() {
    }

    open fun currentPercent(): Double {
        return 0.0
    }

    open fun tap(x: Float, y: Float, tapDiameter: Float, tapHandler: TapHandler) {
        this.tapHandler = tapHandler
        pageBook!!.tap(x, y, tapDiameter)
    }

    private fun handleTap() {
        if (tapHandler != null) {
            tapHandler!!.handleTap()
            tapHandler = null
        }
    }

    open fun goPercent(percent: Double) {
        view.reset(Runnable {
            pageBook!!.goPercent(percent)
            saveLocation()
        })
    }

    open fun goNextPage() {
        if (pageBook!!.canGoPage(-view.currentPageRelativeIndex() + 1) !== PageBook.CanGoResult.CANNOT) {
            view.goNextPage()
        }
    }

    open fun goPreviewPage() {
        if (pageBook!!.canGoPage(-view.currentPageRelativeIndex() - 1) !== PageBook.CanGoResult.CANNOT) {
            view.goPreviewPage()
        }
    }

    fun synchronizeCurrentPage(currentPageRelativeIndex: Int): Int {
        if (currentPageRelativeIndex < 0) {
            when (pageBook!!.canGoPage(1)) {
                PageBook.CanGoResult.CAN -> {
                    pageBook.goNextPage()
                    saveLocation()
                    return currentPageRelativeIndex + 1
                }
                PageBook.CanGoResult.CANNOT -> return 0
            }
        } else if (currentPageRelativeIndex > 0) {
            when (pageBook!!.canGoPage(-1)) {
                PageBook.CanGoResult.CAN -> {
                    pageBook.goPreviewPage()
                    saveLocation()
                    return currentPageRelativeIndex - 1
                }
                PageBook.CanGoResult.CANNOT -> return 0
            }
        }
        return currentPageRelativeIndex
    }

    private inner class SettingsApplier : AbstractSettingsApplier() {
        fun applyAll() {
        }

        override fun listen() {
        }
    }

    interface TapHandler {
        fun handleTap()
    }
}

package com.dmi.perfectreader.bookreader

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.dmi.perfectreader.R
import com.dmi.perfectreader.app.App
import com.dmi.perfectreader.book.BookController
import com.dmi.perfectreader.bookcontrol.BookControlController
import com.dmi.perfectreader.menu.MenuController
import com.dmi.util.base.BaseController
import com.dmi.util.base.argumentAt
import com.dmi.util.base.argumentsBundle
import com.dmi.util.layout.HasLayout
import dagger.ObjectGraph
import dagger.Provides
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HasLayout(R.layout.controller_book_reader)
class BookReaderController : BaseController {
    private val requestedBookFile: File?

    @Inject
    lateinit override var presenter: BookReaderPresenter

    val book = findChild<BookController>()

    constructor(requestedBookFile: File?) : this(argumentsBundle(
            requestedBookFile
    ))

    protected constructor(bundle: Bundle) : super(bundle) {
        requestedBookFile = bundle.argumentAt(0)
    }

    override fun createObjectGraph(parentGraph: ObjectGraph): ObjectGraph {
        return parentGraph.plus(Module())
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        if (findChild<BookController>() == null) {
            presenter.openBook()
        }
    }

    private fun isBookOpened() = findChild<BookController>() != null

    fun openBook(bookFile: File) {
        check(!isBookOpened())
        addChild(BookController(bookFile), R.id.bookContainer)
        addChild(BookControlController(), R.id.bookControlContainer)
    }


    fun toggleMenu() {
        val menuController = findChild<MenuController>()
        if (menuController == null) {
            addChild(MenuController(), FadeChangeHandler(), R.id.menuContainer)
        } else {
            removeChildController(menuController)
        }
    }

    fun exit() {
        activity.finish()
    }

    fun showNeedOpenThroughFileManager() {
        Toast.makeText(activity, R.string.bookNotLoaded, Toast.LENGTH_SHORT).show()
    }

    @dagger.Module(library = true, addsTo = App.Module::class, injects = arrayOf(BookReaderController::class, BookReaderPresenter::class))
    inner class Module {
        @Provides
        fun view(): BookReaderController {
            return this@BookReaderController
        }

        @Provides
        @Named("requestedBookFile")
        fun presenter(): File? {
            return requestedBookFile
        }
    }
}

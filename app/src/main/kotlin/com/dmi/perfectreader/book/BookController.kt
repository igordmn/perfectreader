package com.dmi.perfectreader.book

import android.view.View
import android.widget.Toast
import com.dmi.perfectreader.R
import com.dmi.perfectreader.book.animation.SlidePageAnimation
import com.dmi.perfectreader.book.pagebook.PageBookView
import com.dmi.perfectreader.bookreader.BookReaderController
import com.dmi.util.base.BaseController
import com.dmi.util.layout.HasLayout
import dagger.ObjectGraph
import dagger.Provides
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HasLayout(R.layout.controller_book)
class BookController(private val bookFile: File) : BaseController() {
    companion object {
        private val TIME_FOR_ONE_SLIDE_IN_SECONDS = 0.4f
    }

    private val pageBookView by bindView<PageBookView>(R.id.pageBookView)

    @Inject
    override lateinit var presenter: BookPresenter

    override fun createObjectGraph(parentGraph: ObjectGraph): ObjectGraph {
        return parentGraph.plus(Module())
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        pageBookView!!.setClient(object : PageBookView.Client {
            override fun resize(width: Int, height: Int) {
                presenter.resize(width, height)
            }

            override fun synchronizeCurrentPage(currentPageRelativeIndex: Int): Int {
                return presenter.synchronizeCurrentPage(currentPageRelativeIndex)
            }
        })
        pageBookView!!.setPageAnimation(SlidePageAnimation(TIME_FOR_ONE_SLIDE_IN_SECONDS))
        pageBookView!!.setRenderer(presenter.createRenderer())
        pageBookView!!.onResume()  // todo проверить при сворачивании
    }

    override fun onViewDestroyed(view: View) {
        pageBookView!!.onPause()  // todo проверить при сворачивании
    }

    fun refresh() {
        if (pageBookView != null) {
            pageBookView!!.refresh()
        }
    }

    fun currentPageRelativeIndex(): Int {
        return pageBookView!!.currentPageRelativeIndex()
    }

    fun reset(resetter: Runnable) {
        pageBookView!!.reset(resetter)
    }

    fun goNextPage() {
        pageBookView!!.goNextPage()
    }

    fun goPreviewPage() {
        pageBookView!!.goPreviewPage()
    }

    fun showBookLoadingError() {
        Toast.makeText(activity, R.string.bookOpenError, Toast.LENGTH_SHORT).show()
    }

    @dagger.Module(addsTo = BookReaderController.Module::class, injects = arrayOf(BookController::class, BookPresenter::class))
    inner class Module {
        @Provides
        fun view(): BookController {
            return this@BookController
        }

        @Provides
        @Named("bookFile")
        fun presenter(): File {
            return bookFile
        }
    }
}

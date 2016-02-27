package com.dmi.perfectreader.book

import android.view.View
import android.widget.Toast
import butterknife.Bind
import com.dmi.perfectreader.R
import com.dmi.perfectreader.app.AppActivity
import com.dmi.perfectreader.book.animation.SlidePageAnimation
import com.dmi.perfectreader.book.pagebook.PageBookView
import com.dmi.perfectreader.bookreader.BookReaderFragment
import com.dmi.util.base.BaseFragment
import com.dmi.util.layout.HasLayout
import dagger.ObjectGraph
import dagger.Provides
import me.tatarka.simplefragment.SimpleFragmentIntent
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HasLayout(R.layout.fragment_book)
class BookFragment : BaseFragment() {

    @Bind(R.id.pageBookView)
    protected var pageBookView: PageBookView? = null

    @Inject
    protected lateinit  var presenter: BookPresenter

    override fun createObjectGraph(parentGraph: ObjectGraph): ObjectGraph {
        return parentGraph.plus(Module())
    }

    public override fun presenter(): BookPresenter {
        return presenter
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
    }

    override fun onResume() {
        presenter().resume()
        pageBookView!!.onResume()
    }

    override fun onPause() {
        pageBookView!!.onPause()
        presenter().pause()
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
        Toast.makeText(getActivity<AppActivity>(), R.string.bookOpenError, Toast.LENGTH_SHORT).show()
    }

    @dagger.Module(addsTo = BookReaderFragment.Module::class, injects = arrayOf(BookFragment::class, BookPresenter::class))
    inner class Module {
        @Provides
        fun view(): BookFragment {
            return this@BookFragment
        }

        @Provides
        @Named("bookFile")
        fun presenter(): File {
            return intent.getSerializableExtra("bookFile") as File
        }
    }

    companion object {
        private val TIME_FOR_ONE_SLIDE_IN_SECONDS = 0.4f

        fun intent(bookFile: File): SimpleFragmentIntent<BookFragment> {
            return SimpleFragmentIntent.of(BookFragment::class.java).putExtra("bookFile", bookFile)
        }
    }
}

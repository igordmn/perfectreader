package com.dmi.perfectreader.bookreader

import android.view.View
import android.widget.Toast
import com.dmi.perfectreader.R
import com.dmi.perfectreader.app.App
import com.dmi.perfectreader.app.AppActivity
import com.dmi.perfectreader.book.BookFragment
import com.dmi.perfectreader.bookcontrol.BookControlFragment
import com.dmi.perfectreader.menu.MenuFragment
import com.dmi.util.base.BaseFragment
import com.dmi.util.layout.HasLayout
import com.google.common.base.Preconditions.checkState
import dagger.ObjectGraph
import dagger.Provides
import me.tatarka.simplefragment.SimpleFragment
import me.tatarka.simplefragment.SimpleFragmentIntent
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HasLayout(R.layout.fragment_book_reader)
class BookReaderFragment : BaseFragment() {
    @Inject
    protected lateinit var presenter: BookReaderPresenter

    override fun createObjectGraph(parentGraph: ObjectGraph): ObjectGraph {
        return parentGraph.plus(Module())
    }

    public override fun presenter(): BookReaderPresenter {
        return presenter
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        if (findChild<SimpleFragment?>(R.id.bookContainer) == null) {
            presenter.openBook()
        }
    }

    fun openBook(bookFile: File) {
        checkState(findChild<SimpleFragment?>(R.id.bookContainer) == null)
        addChild(BookFragment.intent(bookFile), R.id.bookContainer)
        addChild(BookControlFragment::class.java, R.id.bookControlContainer)
    }

    fun toggleMenu() {
        if (findChild<SimpleFragment?>(R.id.menuContainer) == null) {
            addChild(MenuFragment::class.java, R.id.menuContainer)
        } else {
            removeChild(R.id.menuContainer)
        }
    }

    fun exit() {
        getActivity<AppActivity>().finish()
    }

    fun book(): BookFragment {
        return findChild(R.id.bookContainer)
    }

    fun showNeedOpenThroughFileManager() {
        Toast.makeText(getActivity<AppActivity>(), R.string.bookNotLoaded, Toast.LENGTH_SHORT).show()
    }

    @dagger.Module(library = true, addsTo = App.Module::class, injects = arrayOf(BookReaderFragment::class, BookReaderPresenter::class))
    inner class Module {
        @Provides
        fun view(): BookReaderFragment {
            return this@BookReaderFragment
        }

        @Provides
        @Named("requestedBookFile")
        fun presenter(): File {
            return intent.getSerializableExtra("requestedBookFile") as File
        }
    }

    companion object {
        fun intent(requestedBookFile: File?): SimpleFragmentIntent<BookReaderFragment> {
            return SimpleFragmentIntent.of(BookReaderFragment::class.java).putExtra("requestedBookFile", requestedBookFile)
        }
    }
}

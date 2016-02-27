package com.dmi.perfectreader.menu

import android.support.v4.view.ViewCompat
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import butterknife.Bind
import com.dmi.perfectreader.R
import com.dmi.perfectreader.book.BookPresenter
import com.dmi.perfectreader.bookreader.BookReaderFragment
import com.dmi.util.Units.dipToPx
import com.dmi.util.base.BaseFragment
import com.dmi.util.layout.HasLayout
import dagger.ObjectGraph
import dagger.Provides
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import javax.inject.Inject

@HasLayout(R.layout.fragment_menu)
class MenuFragment : BaseFragment() { // implements KeyEvent.Callback {

    @Bind(R.id.toolbar)
    protected lateinit var toolbar: Toolbar
    @Bind(R.id.currentChapterText)
    protected lateinit var currentChapterText: TextView
    @Bind(R.id.currentPageText)
    protected lateinit var currentPageText: TextView
    @Bind(R.id.locationSlider)
    protected lateinit var locationSlider: DiscreteSeekBar
    @Bind(R.id.middleSpace)
    protected lateinit var middleSpace: FrameLayout

    @Inject
    protected lateinit var presenter: MenuPresenter

    override fun createObjectGraph(parentGraph: ObjectGraph): ObjectGraph {
        return parentGraph.plus(Module())
    }

    public override fun presenter(): MenuPresenter {
        return presenter
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        initTopBar()
        initBottomBar()
        initMiddleSpace()
    }

    private fun initTopBar() {
        ViewCompat.setElevation(toolbar, dipToPx(2f))
        toolbar.title = "Alice's Adventures in Wonderland"
        toolbar.subtitle = "Lewis Carroll"
        val menuItem = toolbar.menu.add(R.string.bookMenuSettings)
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menuItem.setIcon(R.drawable.ic_settings_white_24dp)
        menuItem.setOnMenuItemClickListener { item ->
            presenter.showSettings()
            true
        }
        currentChapterText.text = "X — Alice's evidence"
        currentPageText.text = "302 / 2031"
    }

    private fun initBottomBar() {
        locationSlider.max = SEEK_BAR_MAX_PROGRESS
        locationSlider.setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(discreteSeekBar: DiscreteSeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    presenter.goPosition(progress, SEEK_BAR_MAX_PROGRESS)
                }
            }

            override fun onStartTrackingTouch(discreteSeekBar: DiscreteSeekBar) {
            }

            override fun onStopTrackingTouch(discreteSeekBar: DiscreteSeekBar) {
            }
        })
        presenter.requestCurrentPercent(SEEK_BAR_MAX_PROGRESS)
    }

    private fun initMiddleSpace() {
        middleSpace.setOnClickListener { view -> close() }
    }

    fun setPosition(position: Int) {
        locationSlider.progress = position
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            close()
        }
        return true
    }

    fun close() {
        val bookReaderFragment = parent as BookReaderFragment
        bookReaderFragment.toggleMenu()
    }

    @dagger.Module(addsTo = BookReaderFragment.Module::class, injects = arrayOf(MenuFragment::class, MenuPresenter::class))
    inner class Module {
        @Provides
        fun view(): MenuFragment {
            return this@MenuFragment
        }

        @Provides
        fun bookPresenter(): BookPresenter {
            val bookReaderFragment = parentFragment<BookReaderFragment>()
            return bookReaderFragment.book().presenter()
        }
    }

    companion object {
        private val SEEK_BAR_MAX_PROGRESS = 1024
    }
}

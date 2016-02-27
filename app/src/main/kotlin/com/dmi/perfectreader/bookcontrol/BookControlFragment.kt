package com.dmi.perfectreader.bookcontrol

import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import butterknife.Bind
import com.dmi.perfectreader.R
import com.dmi.perfectreader.book.BookPresenter
import com.dmi.perfectreader.bookreader.BookReaderFragment
import com.dmi.util.base.BaseFragment
import com.dmi.util.layout.HasLayout
import dagger.ObjectGraph
import dagger.Provides
import javax.inject.Inject

@HasLayout(R.layout.fragment_bookcontrol)
open class BookControlFragment : BaseFragment(), View.OnTouchListener {
    @Bind(R.id.spaceView)
    protected lateinit var spaceView: FrameLayout

    @Inject
    protected lateinit var presenter: BookControlPresenter

    private val touchInfo = TouchInfo()

    override fun createObjectGraph(parentGraph: ObjectGraph): ObjectGraph {
        return parentGraph.plus(Module())
    }

    public override fun presenter(): BookControlPresenter {
        return presenter
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        spaceView.setOnTouchListener(this)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        touchInfo.width = spaceView.width.toFloat()
        touchInfo.height = spaceView.height.toFloat()
        touchInfo.x = event.x
        touchInfo.y = event.y
        touchInfo.touchDiameter = event.touchMajor

        when (event.action) {
            MotionEvent.ACTION_DOWN -> presenter.onTouchDown(touchInfo)
            MotionEvent.ACTION_MOVE -> presenter.onTouchMove(touchInfo)
            MotionEvent.ACTION_UP -> presenter.onTouchUp(touchInfo)
        }

        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        presenter().onKeyDown(HardKey.fromKeyCode(keyCode))
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return true
    }

    @dagger.Module(addsTo = BookReaderFragment.Module::class, injects = arrayOf(BookControlFragment::class, BookControlPresenter::class))
    inner class Module {
        @Provides
        fun view(): BookControlFragment {
            return this@BookControlFragment
        }

        @Provides
        fun bookPresenter(): BookPresenter {
            val bookReaderFragment = parentFragment<BookReaderFragment>()
            return bookReaderFragment.book().presenter()
        }
    }
}

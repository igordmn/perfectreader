package com.dmi.perfectreader.bookcontrol

import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import butterknife.Bind
import com.dmi.perfectreader.R
import com.dmi.perfectreader.book.BookPresenter
import com.dmi.perfectreader.bookreader.BookReaderController
import com.dmi.util.base.BaseController
import com.dmi.util.layout.HasLayout
import dagger.ObjectGraph
import dagger.Provides
import javax.inject.Inject

@HasLayout(R.layout.controller_bookcontrol)
open class BookControlController : BaseController(), View.OnTouchListener {
    @Bind(R.id.spaceView)
    protected lateinit var spaceView: FrameLayout

    @Inject
    lateinit override var presenter: BookControlPresenter

    private val touchInfo = TouchInfo()

    override fun createObjectGraph(parentGraph: ObjectGraph): ObjectGraph {
        return parentGraph.plus(Module())
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

    // todo реализовать

    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        presenter.onKeyDown(HardKey.fromKeyCode(keyCode))
        return true
    }

    fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return true
    }

    @dagger.Module(addsTo = BookReaderController.Module::class, injects = arrayOf(BookControlController::class, BookControlPresenter::class))
    inner class Module {
        @Provides
        fun view(): BookControlController {
            return this@BookControlController
        }

        @Provides
        fun bookPresenter(): BookPresenter {
            TODO()
//            val bookReaderFragment = parentController<BookReaderController>()
//            return bookReaderFragment.book().presenter
        }
    }
}

package com.dmi.perfectreader.manualtest.book

import android.opengl.GLES20.*
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup.LayoutParams
import com.dmi.perfectreader.book.animation.SlidePageAnimation
import com.dmi.perfectreader.book.pagebook.PageBookRenderer
import com.dmi.perfectreader.book.pagebook.PageBookView
import com.dmi.util.base.BaseActivity
import com.dmi.util.log.Log
import java.util.concurrent.Executors

class PageBookViewTestActivity : BaseActivity() {
    private lateinit var pageBook: TestPageBook
    private lateinit var pageBookView: PageBookView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageBook = TestPageBook()
        pageBookView = PageBookView(this)
        pageBookView.setClient(PageBookViewClient())
        pageBookView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        pageBookView.setPageAnimation(SlidePageAnimation(1f))
        pageBookView.setRenderer(pageBook)
        setContentView(pageBookView)
    }

    override fun onDestroy() {
        pageBook.stop()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (pageBook.canGoPage(pageBookView.currentPageRelativeIndex() + 1)) {
                pageBookView.goNextPage()
            }
            return true
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (pageBook.canGoPage(pageBookView.currentPageRelativeIndex() - 1)) {
                pageBookView.goPreviewPage()
            }
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                super.onKeyUp(keyCode, event)
    }

    private fun afterAnimate() {
        pageBookView.refresh()
    }

    private inner class PageBookViewClient : PageBookView.Client {
        override fun resize(width: Int, height: Int) {
        }

        override fun synchronizeCurrentPage(currentPageRelativeIndex: Int): Int {
            if (currentPageRelativeIndex < 0) {
                if (pageBook.canGoPage(1)) {
                    pageBook.goNextPage()
                    return currentPageRelativeIndex + 1
                } else {
                    return 0
                }
            } else if (currentPageRelativeIndex > 0) {
                if (pageBook.canGoPage(-1)) {
                    pageBook.goPreviewPage()
                    return currentPageRelativeIndex - 1
                } else {
                    return 0
                }
            }
            return currentPageRelativeIndex
        }
    }

    private inner class TestPageBook : PageBookRenderer {
        private val executor = Executors.newSingleThreadExecutor()

        private var color = 1.0f
        private var colorDecreasing = true
        private var previewTime: Long = -1
        private val maxPages = 20
        private var currentPage = 0
        @Volatile private var loadCount = 0
        @Volatile private var stopped = false

        init {
            executor.execute { this.updateLoop() }
        }

        fun stop() {
            stopped = true
        }

        fun canGoPage(offset: Int): Boolean {
            val targetPage = currentPage + offset
            return targetPage >= 0 && targetPage < maxPages - 1
        }

        fun goNextPage() {
            if (canGoPage(1)) {
                loadCount++
                currentPage++
                executor.execute {
                    delay()
                    loadCount--
                }
            } else {
                Log.w("DDD cannot go next page")
            }
        }

        fun goPreviewPage() {
            if (canGoPage(-1)) {
                loadCount++
                currentPage--
                executor.execute {
                    delay()
                    loadCount--
                }
            } else {
                Log.w("DDD cannot go preview page")
            }
        }

        private fun updateLoop() {
            val nowTime = System.nanoTime()
            val dt = if (previewTime != -1L) (nowTime - previewTime) / 1E9F else 0F
            previewTime = nowTime
            update(dt)
            if (!stopped) {
                executor.execute { this.updateLoop() }
            }
        }

        private fun update(dt: Float) {
            if (colorDecreasing) {
                color -= (dt * 0.3).toFloat()
                if (color < 0.5f) {
                    color = 0.5f
                    colorDecreasing = false
                }
            } else {
                color += (dt * 0.3).toFloat()
                if (color > 1) {
                    color = 1f
                    colorDecreasing = true
                }
            }
            delay()
            afterAnimate()
        }

        override fun onSurfaceCreated() {
        }

        override fun onSurfaceChanged(width: Int, height: Int) {
        }

        override fun onDrawFrame() {
            when (currentPage % 3) {
                0 -> {
                    glClearColor(color, 0.0f, 0.0f, 1.0f)
                    glClear(GL_COLOR_BUFFER_BIT)
                }
                1 -> {
                    glClearColor(0.0f, color, 0.0f, 1.0f)
                    glClear(GL_COLOR_BUFFER_BIT)
                }
                2 -> {
                    glClearColor(0.0f, 0.0f, color, 1.0f)
                    glClear(GL_COLOR_BUFFER_BIT)
                }
            }
            delay()
        }

        private fun delay() {
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }

        override val isLoading: Boolean
            get() = loadCount > 0
    }
}
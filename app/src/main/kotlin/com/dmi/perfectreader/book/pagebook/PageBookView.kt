package com.dmi.perfectreader.book.pagebook

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.support.annotation.UiThread
import android.util.AttributeSet
import com.dmi.perfectreader.R
import com.dmi.perfectreader.book.animation.PageAnimation
import com.dmi.util.collection.DuplexBuffer
import com.dmi.util.concurrent.Interrupts.waitTask
import com.dmi.perfectreader.app.AppThreads.postUITask
import com.dmi.util.opengl.DeltaTimeGLSurfaceView
import com.dmi.util.opengl.Graphics.createProgram
import com.dmi.util.opengl.Graphics.floatBuffer
import com.dmi.util.opengl.Graphics.glGenFramebuffer
import com.dmi.util.opengl.Graphics.glGenTexture
import java.lang.Math.abs
import java.lang.String.format
import java.nio.FloatBuffer
import java.util.*

class PageBookView : DeltaTimeGLSurfaceView {

    private var client: Client? = null
    private var pageAnimation: PageAnimation? = null
    private var renderer: PageBookRenderer? = null

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)
    private val plane = Plane()

    private val visiblePages = DuplexBuffer<Page?>(MAX_VISIBLE_PAGES)
    private val freePages = Stack<Page>()
    private val allPages = Stack<Page>()

    private var currentPageRelativeIndex: Int = 0
    private var animationCurrentPageRelativeIndex: Int = 0

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    @UiThread
    fun setClient(client: Client) {
        this.client = client
    }

    @UiThread
    fun setPageAnimation(pageAnimation: PageAnimation) {
        this.pageAnimation = pageAnimation
    }

    @UiThread
    fun setRenderer(renderer: PageBookRenderer) {
        this.renderer = renderer
        for (i in 0..MAX_VISIBLE_PAGES_WITH_CONTENT - 1) {
            val page = Page()
            allPages.add(page)
            freePages.add(page)
        }
        setEGLContextClientVersion(2)
        runRender()
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        client!!.resize(w, h)
    }

    fun currentPageRelativeIndex(): Int {
        return currentPageRelativeIndex
    }

    fun refresh() {
        requestRender()
    }

    fun reset(resetter: Runnable) {
        currentPageRelativeIndex = 0
        queueEvent {
            animationCurrentPageRelativeIndex = 0
            pageAnimation!!.reset()
            freeInvisiblePages()
            waitTask(postUITask { resetter.run() })
        }
        requestRender()
    }

    fun goNextPage() {
        currentPageRelativeIndex--
        queueEvent {
            animationCurrentPageRelativeIndex--
            pageAnimation!!.moveNext()
            visiblePages.shiftLeft()
        }
        requestRender()
    }

    fun goPreviewPage() {
        currentPageRelativeIndex++
        queueEvent {
            animationCurrentPageRelativeIndex++
            pageAnimation!!.movePreview()
            visiblePages.shiftRight()
        }
        requestRender()
    }

    override fun onSurfaceCreated() {
        glClearColor(1f, 1f, 1f, 1f)
        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
        renderer!!.onSurfaceCreated()
        plane.init()
        for (page in allPages) {
            page.init()
        }
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        glViewport(0, 0, width, height)
        Matrix.orthoM(projectionMatrix, 0, 0f, width.toFloat(), height.toFloat(), 0.0f, -1f, 1f)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        pageAnimation!!.setPageWidth(width.toFloat())
        plane.setSize(width, height)
        for (page in allPages) {
            page.setSize(width, height)
        }
    }

    override fun onFreeResources() {
        renderer!!.onFreeResources()
    }

    override fun onUpdate(dt: Float) {
        pageAnimation!!.update(dt)
        if (pageAnimation!!.isAnimate) {
            requestRender()
        } else {
            resetTimer()
        }
    }

    override fun onDrawFrame() {
        synchronizeCurrentPage()
        refreshPages()
        drawPages()
    }

    private fun synchronizeCurrentPage() {
        if (animationCurrentPageRelativeIndex != 0) {
            if (canSynchronizeCurrentPage()) {
                waitTask(postUITask {
                    currentPageRelativeIndex = client!!.synchronizeCurrentPage(currentPageRelativeIndex)
                    animationCurrentPageRelativeIndex = currentPageRelativeIndex
                })
            }
        }
        if (animationCurrentPageRelativeIndex != 0) {
            requestRender()
        }
    }

    private fun canSynchronizeCurrentPage(): Boolean {
        val currentIndex = animationCurrentPageRelativeIndex
        val animationState = pageAnimation!!.state()
        val pageVisible = animationState.minRelativeIndex() >= currentIndex || currentIndex <= animationState.maxRelativeIndex()
        val pageDrawn = abs(currentIndex) > MAX_VISIBLE_PAGES || visiblePages.get(currentIndex) != null
        return pageVisible && pageDrawn || !pageVisible
    }

    fun refreshPages() {
        freeInvisiblePages()
        redrawCurrentPage()
    }

    private fun freeInvisiblePages() {
        val animationState = pageAnimation!!.state()
        for (i in -visiblePages.maxRelativeIndex()..animationState.minRelativeIndex() - 1) {
            freePage(visiblePages, i)
        }
        for (i in visiblePages.maxRelativeIndex() downTo animationState.maxRelativeIndex() + 1) {
            freePage(visiblePages, i)
        }
    }

    private fun redrawCurrentPage() {
        val currentIndex = animationCurrentPageRelativeIndex
        if (abs(currentIndex) <= MAX_VISIBLE_PAGES && !renderer!!.isLoading) {
            var currentPage: Page? = visiblePages.get(currentIndex)
            if (currentPage == null) {
                if (!freePages.empty()) {
                    currentPage = acquirePage(visiblePages, currentIndex)
                    currentPage.refresh()
                }
            } else if (!pageAnimation!!.isAnimate) {
                currentPage.refresh()
            }
        }
    }

    private fun drawPages() {
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)
        val animationState = pageAnimation!!.state()
        for (i in 0..animationState.pageCount() - 1) {
            val relativeIndex = animationState.pageRelativeIndex(i)
            val positionX = animationState.pagePositionX(i)
            Matrix.translateM(viewMatrix, 0, positionX, 0f, 0f)
            Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            if (abs(relativeIndex) <= MAX_VISIBLE_PAGES && visiblePages.get(relativeIndex) != null) {
                visiblePages.get(relativeIndex)?.draw(viewProjectionMatrix)
            }
            Matrix.translateM(viewMatrix, 0, -positionX, 0f, 0f)
        }
    }

    private fun acquirePage(pages: DuplexBuffer<Page?>, index: Int): Page {
        val page = freePages.pop()
        pages.set(index, page)
        return page
    }

    private fun freePage(pages: DuplexBuffer<Page?>, index: Int) {
        val page = pages.get(index)
        if (page != null) {
            pages.set(index, null)
            freePages.push(page)
        }
    }

    private inner class Page {
        private var textureId: Int = 0
        private var frameBufferId: Int = 0

        fun init() {
            textureId = glGenTexture()
            frameBufferId = glGenFramebuffer()
        }

        fun setSize(width: Int, height: Int) {
            glBindTexture(GL_TEXTURE_2D, textureId)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glBindTexture(GL_TEXTURE_2D, 0)

            glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId)
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0)
            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
                throw RuntimeException(
                        format("frame buffer init error. status: 0x%08X, error code: 0x%08X",
                                glCheckFramebufferStatus(GL_FRAMEBUFFER), glGetError()))
            }
            glBindFramebuffer(GL_FRAMEBUFFER, 0)
        }

        fun refresh() {
            glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId)
            glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
            glClear(GL_COLOR_BUFFER_BIT)
            renderer!!.onDrawFrame()
            glBindFramebuffer(GL_FRAMEBUFFER, 0)
        }

        fun draw(matrix: FloatArray) {
            glBindTexture(GL_TEXTURE_2D, textureId)
            plane.draw(matrix)
            glBindTexture(GL_TEXTURE_2D, 0)
        }
    }

    private inner class Plane {
        private val VERTEX_COUNT = 4

        private var programId: Int = 0
        private var coordinateHandle: Int = 0
        private var mvpMatrixHandle: Int = 0
        private var textureHandle: Int = 0

        private var vertexBuffer: FloatBuffer? = null

        fun init() {
            programId = createProgram(resources, R.raw.shader_page_vertex, R.raw.shader_page_fragment)
            coordinateHandle = glGetAttribLocation(programId, "coordinate")
            mvpMatrixHandle = glGetUniformLocation(programId, "mvpMatrix")
            textureHandle = glGetUniformLocation(programId, "texture")
        }

        fun setSize(width: Int, height: Int) {
            vertexBuffer = floatBuffer(floatArrayOf(0f, 0f, 0f, 1f, width.toFloat(), 0f, 1f, 1f, 0f, height.toFloat(), 0f, 0f, width.toFloat(), height.toFloat(), 1f, 0f))
        }

        fun draw(matrix: FloatArray) {
            glUseProgram(programId)
            glEnableVertexAttribArray(coordinateHandle)
            glVertexAttribPointer(coordinateHandle, 4,
                    GL_FLOAT, false,
                    0, vertexBuffer)
            glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrix, 0)
            glUniform1i(textureHandle, 0)
            glDrawArrays(GL_TRIANGLE_STRIP, 0, VERTEX_COUNT)
            glDisableVertexAttribArray(coordinateHandle)
            glUseProgram(0)
        }
    }

    interface Client {
        fun resize(width: Int, height: Int)

        fun synchronizeCurrentPage(currentPageRelativeIndex: Int): Int
    }

    companion object {
        private val MAX_VISIBLE_PAGES = 32
        private val MAX_VISIBLE_PAGES_WITH_CONTENT = 3
    }
}

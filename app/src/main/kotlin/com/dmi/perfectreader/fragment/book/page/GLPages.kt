package com.dmi.perfectreader.fragment.book.page

import android.content.Context
import android.opengl.Matrix.*
import com.dmi.perfectreader.fragment.book.page.PagesRenderModel.Slide
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.util.android.opengl.GLPlane
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.graphic.Size
import com.dmi.util.refWatcher
import java.util.*

class GLPages(
        context: Context,
        size: Size,
        density: Float,
        createRefresher: (GLPages, Size) -> GLPagesRefresher
) {
    private val sizeF = size.toFloat()

    private val refresher = createRefresher(this, size)

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private val plane = GLPlane(context, sizeF / density)

    val onNeedDraw = refresher.onNeedRefresh

    var loadingPageTexture: GLTexture? = null
    val loadedPageToTexture = HashMap<Page, GLTexture>()

    init {
        orthoM(projectionMatrix, 0, 0F, sizeF.width / density, sizeF.height / density, 0F, -1F, 1F)
        setLookAtM(viewMatrix, 0, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 1F, 0F)
    }

    fun destroy() {
        refresher.destroy()
        refWatcher.watch(this)
    }

    fun draw(model: PagesRenderModel) {
        refresher.refreshBy(model)
        model.visibleSlides.forEach { drawSlide(it) }
    }

    private fun drawSlide(slide: Slide) {
        translateM(viewMatrix, 0, slide.offsetX, 0F, 0F)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        textureFor(slide.page)?.draw(viewProjectionMatrix)
        translateM(viewMatrix, 0, -slide.offsetX, 0F, 0F)
    }

    private fun GLTexture.draw(matrix: FloatArray) = plane.draw(matrix, this)

    private fun textureFor(page: Page?): GLTexture? = if (page != null) {
        loadedPageToTexture[page] ?: loadingPageTexture
    } else {
        loadingPageTexture
    }
}
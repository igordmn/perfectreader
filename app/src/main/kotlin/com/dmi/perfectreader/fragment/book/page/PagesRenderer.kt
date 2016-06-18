package com.dmi.perfectreader.fragment.book.page

import android.content.Context
import android.opengl.Matrix.*
import com.dmi.perfectreader.fragment.book.layout.pagination.Page
import com.dmi.perfectreader.fragment.book.page.PagesRenderModel.Slide
import com.dmi.util.graphic.Size
import com.dmi.util.opengl.Plane
import com.dmi.util.opengl.Texture
import java.util.*

class PagesRenderer(
        context: Context,
        size: Size,
        private val density: Float,
        private val createRefresher: (PagesRenderer, Size) -> PagesRefresher
) {
    private val sizeF = size.toFloat()

    private val refresher = createRefresher(this, size)

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private val plane = Plane(context, sizeF / density)

    val onNeedDraw = refresher.onNeedRefresh

    var loadingPageTexture: Texture? = null
    val loadedPageToTexture = HashMap<Page, Texture>()

    init {
        orthoM(projectionMatrix, 0, 0F, sizeF.width / density, sizeF.height / density, 0F, -1F, 1F)
        setLookAtM(viewMatrix, 0, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 1F, 0F)
    }

    fun destroy() {
        refresher.destroy()
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

    private fun Texture.draw(matrix: FloatArray) = plane.draw(matrix, this)

    private fun textureFor(page: Page?): Texture? = if (page != null) {
        loadedPageToTexture[page] ?: loadingPageTexture
    } else {
        loadingPageTexture
    }
}
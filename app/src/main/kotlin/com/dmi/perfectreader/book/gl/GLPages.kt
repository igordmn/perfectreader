package com.dmi.perfectreader.book.gl

import android.content.Context
import com.dmi.perfectreader.book.page.VisiblePages
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.perfectreader.book.render.factory.PageRenderer
import com.dmi.util.android.opengl.GLQuad
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.collection.ImmediatelyCreatePool
import com.dmi.util.graphic.Size
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Scope

class GLPages(
        private val model: GLBookModel,
        context: Context,
        private val pageRenderer: PageRenderer,
        private val size: Size,
        private val scope: Scope = Scope()
) : Disposable by scope {
    private val quad by scope.disposable(GLQuad(context))
    private val texturePool by scope.disposable(ImmediatelyCreatePool(VisiblePages.COUNT) { GLTexture(size) })
    private fun createPage(page: Page) = GLPage(page, model, quad, texturePool, GLPageRefresher(size, pageRenderer))

    private val cache by scope.disposable(Cache(this::createPage))
    val left: GLPage? by scope.cached { cache.update(model.pages)[model.pages.left] }
    val right: GLPage? by scope.cached { cache.update(model.pages)[model.pages.right] }
    val leftProgress: Float get() = model.pages.leftProgress
    val rightProgress: Float get() = model.pages.rightProgress

    private class Cache(private val create: (Page) -> GLPage) : Disposable {
        private val map = HashMap<Page, GLPage>()
        operator fun get(page: Page?): GLPage? = map[page]
        override fun dispose() = map.values.forEach(GLPage::dispose)

        fun update(pages: Iterable<Page?>): Cache {
            val notNull = pages.filterNotNull()
            val set = notNull.toSet()
            val toRemove = ArrayList<Page>()
            val toAdd = ArrayList<Page>()
            notNull.forEach {
                if (!map.containsKey(it))
                    toAdd.add(it)
            }
            map.keys.forEach {
                if (!set.contains(it))
                    toRemove.add(it)
            }
            toRemove.forEach {
                map.remove(it)!!.dispose()
            }
            toAdd.forEach {
                map[it] = create(it)
            }
            return this
        }
    }
}
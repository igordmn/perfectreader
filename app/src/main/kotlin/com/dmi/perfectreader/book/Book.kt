package com.dmi.perfectreader.book

import android.net.Uri
import com.dmi.perfectreader.book.bitmap.AndroidBitmapDecoder
import com.dmi.perfectreader.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.book.bitmap.CachedBitmapDecoder
import com.dmi.perfectreader.book.content.*
import com.dmi.perfectreader.book.content.common.PageConfig
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.obj.common.ContentConfig
import com.dmi.perfectreader.book.layout.UniversalObjectLayouter
import com.dmi.perfectreader.book.layout.layout
import com.dmi.perfectreader.book.layout.paragraph.breaker.CompositeBreaker
import com.dmi.perfectreader.book.layout.paragraph.breaker.LineBreaker
import com.dmi.perfectreader.book.layout.paragraph.breaker.ObjectBreaker
import com.dmi.perfectreader.book.layout.paragraph.breaker.WordBreaker
import com.dmi.perfectreader.book.layout.paragraph.hyphenator.CachedHyphenatorResolver
import com.dmi.perfectreader.book.layout.paragraph.hyphenator.TeXHyphenatorResolver
import com.dmi.perfectreader.book.layout.paragraph.hyphenator.TeXPatternsSource
import com.dmi.perfectreader.book.layout.paragraph.liner.BreakLiner
import com.dmi.perfectreader.book.layout.paragraph.metrics.PaintTextMetrics
import com.dmi.perfectreader.book.page.*
import com.dmi.perfectreader.book.pagination.column.columns
import com.dmi.perfectreader.book.pagination.page.pages
import com.dmi.perfectreader.book.pagination.part.parts
import com.dmi.perfectreader.book.selection.BookSelections
import com.dmi.perfectreader.common.UserData
import com.dmi.perfectreader.reader.ReaderContext
import com.dmi.util.graphic.SizeF
import com.dmi.util.scope.*
import com.dmi.util.system.seconds

suspend fun book(context: ReaderContext, uri: Uri): Book {
    val userData: UserData = context.main.userData
    val bookParsers = context.main.bookParsers
    val content: Content = bookParsers[uri].content()
    val userBook: UserBook = userBook(userData, uri)
    val bitmapDecoder = CachedBitmapDecoder(AndroidBitmapDecoder(content.resource))
    val text = ContentText(content)
    return Book(context, text, userBook, content, bitmapDecoder)
}

class Book(
        private val context: ReaderContext,
        val text: ContentText,
        private val userBook: UserBook,
        private val content: Content,
        val bitmapDecoder: BitmapDecoder,
        scope: Scope = Scope()
) : Disposable by scope {
    var size by observable(SizeF(100F, 100F))

    private val layouter = UniversalObjectLayouter(
            PaintTextMetrics(),
            BreakLiner(CompositeBreaker(
                    LineBreaker(),
                    ObjectBreaker(),
                    WordBreaker(
                            CachedHyphenatorResolver(
                                    TeXHyphenatorResolver(context.main.log,
                                            TeXPatternsSource(context.main.android)
                                    )
                            )
                    )
            )),
            bitmapDecoder
    )

    private val sized: Sized by scope.cachedDisposable {
        val size = size
        val contentConfig = ContentConfig(context.main)
        val pageConfig = PageConfig(context.main, size)

        dontObserve {
            val settings = context.main.settings
            val dip2px = context.main.dip2px
            val locations = Locations(content, pageConfig.contentSize, contentConfig, settings)
            val sequence = content.sequence
                    .configure(contentConfig)
                    .layout(layouter, pageConfig.contentSize)
                    .parts()
                    .columns(pageConfig.contentSize.height)
                    .pages(pageConfig, locations, tableOfContents, description, layouter, contentConfig)
            val loadingPages = LoadingPages(LoadingPages.pages(sequence, locations, userBook))
            val animator = SmoothPageAnimator(seconds(0.4))
            val animatedPages = AnimatedPages(
                    size,
                    AnimatedPages.pages(loadingPages),
                    context.main.display,
                    animator,
                    speedToTurnPage = dip2px(20F)
            )
            val demoAnimatedPages = DemoAnimatedPages(
                    size,
                    DemoAnimatedPages.pages(loadingPages),
                    context.main.display,
                    animator
            )
            Sized(locations, loadingPages, animatedPages, demoAnimatedPages)
        }
    }

    private val locations: Locations get() = sized.locations
    private val loadingPages: LoadingPages get() = sized.loadingPages
    private val animatedPages: AnimatedPages get() = sized.animatedPages
    private val demoAnimatedPages: DemoAnimatedPages get() = sized.demoAnimatedPages

    val description: BookDescription get() = content.description
    val tableOfContents: TableOfContents? get() = content.tableOfContents

    val selections: BookSelections? by scope.cached {
        val page = animatedPages.visible.left
        if (page != null) {
            BookSelections(page, text)
        } else {
            null
        }
    }

    val location: Location get() = userBook.location
    val isMoving: Boolean get() = animatedPages.isMoving || demoAnimatedPages.isMoving
    val pages: VisiblePages get() = if (demoAnimatedPages.isMoving) demoAnimatedPages.visible else animatedPages.visible

    val percent: Double by scope.cached { locations.locationToPercent(location) }
    val pageNumber: Int by scope.cached { locations.locationToPageNumber(location) }
    val chapter: TableOfContents.Chapter? by scope.cached { tableOfContents?.chapterAt(location) }
    val numberOfPages: Int by scope.cached { locations.numberOfPages }

    fun pageNumberOf(chapter: TableOfContents.Chapter) = locations.locationToPageNumber(chapter.location)
    fun pageNumberOf(location: Location) = locations.locationToPageNumber(location)

    fun showDemoAnimation() = demoAnimatedPages.animate()

    fun goLocation(location: Location) {
        loadingPages.goLocation(location)
        demoAnimatedPages.reset()
        animatedPages.reset()
    }

    fun goRelative(relativeIndex: Int) {
        loadingPages.goRelative(relativeIndex)
        demoAnimatedPages.reset()
        animatedPages.reset()
    }

    fun goPercent(percent: Double) = goLocation(locations.percentToLocation(percent))
    fun goPageNumber(pageNumber: Int) = goLocation(locations.pageNumberToLocation(pageNumber))
    fun goChapter(chapter: TableOfContents.Chapter) = goLocation(chapter.location)

    fun animateRelative(relativeIndex: Int) {
        demoAnimatedPages.reset()
        animatedPages.animateRelative(relativeIndex)
    }

    fun scroll(): PageScroller {
        demoAnimatedPages.reset()
        return animatedPages.scroll()
    }

    private class Sized(
            val locations: Locations,
            val loadingPages: LoadingPages,
            val animatedPages: AnimatedPages,
            val demoAnimatedPages: DemoAnimatedPages
    ) : Disposable by loadingPages and animatedPages and demoAnimatedPages
}
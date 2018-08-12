package com.dmi.perfectreader.book

import android.net.Uri
import com.dmi.perfectreader.Main
import com.dmi.perfectreader.book.bitmap.AndroidBitmapDecoder
import com.dmi.perfectreader.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.book.bitmap.CachedBitmapDecoder
import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.content.ContentText
import com.dmi.perfectreader.book.content.configure
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.obj.param.appFormatConfig
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
import com.dmi.perfectreader.book.page.AnimatedPages
import com.dmi.perfectreader.book.page.LoadingPages
import com.dmi.perfectreader.book.page.SmoothPageAnimator
import com.dmi.perfectreader.book.page.VisiblePages
import com.dmi.perfectreader.book.pagination.column.columns
import com.dmi.perfectreader.book.pagination.page.pages
import com.dmi.perfectreader.book.pagination.part.parts
import com.dmi.perfectreader.book.parse.BookContentParserFactory
import com.dmi.perfectreader.book.parse.settingsParseConfig
import com.dmi.perfectreader.book.selection.BookSelections
import com.dmi.perfectreader.common.UserData
import com.dmi.util.coroutine.IOPool
import com.dmi.util.graphic.SizeF
import com.dmi.util.graphic.shrink
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Scope.Companion.onchange
import com.dmi.util.scope.Scoped
import com.dmi.util.scope.and
import com.dmi.util.system.seconds
import kotlinx.coroutines.withContext

suspend fun book(main: Main, uri: Uri): Book {
    val log = main.log
    val settings = main.settings
    val userData: UserData = main.userData
    val parseConfig = settingsParseConfig(settings)
    val bookContentParserFactory = BookContentParserFactory(log, parseConfig)
    val content: Content = withContext(IOPool) {
        bookContentParserFactory.parserFor(uri).parse()
    }
    val userBook: UserBook = userBook(userData, uri)   // todo need dispose if coroutine cancelled?
    val bitmapDecoder = CachedBitmapDecoder(AndroidBitmapDecoder(content.openResource))
    val text = ContentText(content)
    return Book(main, text, userBook, content, bitmapDecoder)
}

class Book(
        private val main: Main,
        val text: ContentText,
        userBook: UserBook,
        private val content: Content,
        val bitmapDecoder: BitmapDecoder
) : Scoped by Scoped.Impl() {
    var size by scope.value(SizeF(100F, 100F))

    private val userBook: UserBook by scope.disposable(userBook)

    private val layouter = UniversalObjectLayouter(
            PaintTextMetrics(),
            BreakLiner(CompositeBreaker(
                    LineBreaker(),
                    ObjectBreaker(),
                    WordBreaker(
                            CachedHyphenatorResolver(
                                    TeXHyphenatorResolver(main.log,
                                            TeXPatternsSource(main.applicationContext)
                                    )
                            )
                    )
            )),
            bitmapDecoder
    )

    private val formatConfig by scope.cached {
        val userDirectory = main.protocols.fileFor(main.settings.system.fontsPath)
        val fontCollection = main.fontCollectionCache.collectionFor(userDirectory)
        appFormatConfig(main.applicationContext, main.settings, fontCollection)
    }

    private val sized: Sized by scope.cachedDisposable(recache = onchange { size; formatConfig }) {
        val settings = main.settings
        val dip2px = main.dip2px
        val paddings = formatConfig.pagePaddingsDip * formatConfig.density
        val contentSize = size.shrink(paddings.left + paddings.right, paddings.top + paddings.bottom)
        val sequence = content.sequence
                .configure(formatConfig)
                .layout(layouter, contentSize)
                .parts()
                .columns(contentSize.height)
                .pages(size, formatConfig)
        val locations = Locations(content, contentSize, formatConfig, settings)
        val loadingPages = LoadingPages(LoadingPages.pages(sequence, locations, userBook))
        val animatedPages = AnimatedPages(
                size,
                AnimatedPages.pages(loadingPages),
                main.display,
                speedToTurnPage = dip2px(50F),
                animator = SmoothPageAnimator(seconds(0.4))
        )
        Sized(locations, loadingPages, animatedPages)
    }

    val locations: Locations get() = sized.locations
    private val loadingPages: LoadingPages get() = sized.loadingPages
    private val animatedPages: AnimatedPages get() = sized.animatedPages

    val selections: BookSelections? by scope.cached {
        val page = animatedPages.visible.left
        if (page != null) {
            BookSelections(page, text)
        } else {
            null
        }
    }

    val location: Location get() = userBook.location
    val isMoving: Boolean get() = animatedPages.isMoving
    val pages: VisiblePages get() = animatedPages.visible

    fun goLocation(location: Location) {
        loadingPages.goLocation(location)
        animatedPages.reset()
    }

    fun goRelative(relativeIndex: Int) {
        loadingPages.goRelative(relativeIndex)
        animatedPages.reset()
    }

    fun animateRelative(relativeIndex: Int) = animatedPages.animateRelative(relativeIndex)
    fun scroll() = animatedPages.scroll()

    private class Sized(
            val locations: Locations,
            val loadingPages: LoadingPages,
            val animatedPages: AnimatedPages
    ) : Disposable by loadingPages and animatedPages
}
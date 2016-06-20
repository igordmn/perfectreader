package com.dmi.perfectreader.app

import android.content.Context
import com.dmi.perfectreader.data.UserData
import com.dmi.perfectreader.data.UserSettings
import com.dmi.perfectreader.fragment.book.*
import com.dmi.perfectreader.fragment.book.bitmap.AndroidBitmapDecoder
import com.dmi.perfectreader.fragment.book.bitmap.CachedBitmapDecoder
import com.dmi.perfectreader.fragment.book.layout.layouter.UniversalLayouter
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.breaker.CompositeBreaker
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.breaker.LineBreaker
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.breaker.ObjectBreaker
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.breaker.WordBreaker
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.hyphenator.CachedHyphenatorResolver
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.hyphenator.TeXHyphenatorResolver
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.hyphenator.TeXPatternsSource
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.liner.BreakLiner
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.metrics.PaintTextMetrics
import com.dmi.perfectreader.fragment.book.layout.pagination.*
import com.dmi.perfectreader.fragment.book.layout.painter.UniversalObjectPainter
import com.dmi.perfectreader.fragment.book.obj.common.settingsLayoutConfig
import com.dmi.perfectreader.fragment.book.page.*
import com.dmi.perfectreader.fragment.book.page.RefreshScheduler.BitmapBuffer
import com.dmi.perfectreader.fragment.book.parse.BookContentParserFactory
import com.dmi.perfectreader.fragment.bookcontrol.BookControl
import com.dmi.perfectreader.fragment.bookcontrol.BookControlView
import com.dmi.perfectreader.fragment.main.Main
import com.dmi.perfectreader.fragment.main.MainView
import com.dmi.perfectreader.fragment.menu.Menu
import com.dmi.perfectreader.fragment.menu.MenuView
import com.dmi.perfectreader.fragment.reader.Reader
import com.dmi.perfectreader.fragment.reader.ReaderView
import com.dmi.util.graphic.Size
import com.dmi.util.graphic.SizeF

class AppObjects(applicationContext: Context) {
    val databases = AppDatabases(applicationContext)
    val userData = UserData(databases.user)
    val userSettings = UserSettings(databases.user)
    val bookContentParserFactory = BookContentParserFactory(applicationContext)

    val createMain = { activity: AppActivity ->
        val intent = activity.intent
        val closeApp = { activity.finish() }

        val createReader = { bookData: BookData ->
            val createBook = {
                val bitmapDecoder = CachedBitmapDecoder(AndroidBitmapDecoder(bookData.content.openResource))
                val patternsSource = TeXPatternsSource(applicationContext)
                val hyphenatorResolver = CachedHyphenatorResolver(TeXHyphenatorResolver(patternsSource))
                val wordBreaker = WordBreaker(hyphenatorResolver)
                val breaker = CompositeBreaker(LineBreaker(), ObjectBreaker(), wordBreaker)
                val layouter = UniversalLayouter(PaintTextMetrics(), BreakLiner(breaker), bitmapDecoder)

                val createSized = { size: SizeF ->
                    val createPages = { Pages(bookData.location) }
                    val createPagesLoader = { pages: Pages ->
                        val layoutConfig = settingsLayoutConfig(userSettings)
                        val pageConfig = settingsPageConfig(size, userSettings)
                        val layoutSequence = LayoutSequence(bookData.content.sequence, layoutConfig)
                        val renderSequence = RenderSequence(layoutSequence, layouter, pageConfig.contentSize)
                        val contentRowSequence = RenderPartSequence(renderSequence)
                        val contentPageSequence = RenderColumnSequence(contentRowSequence, pageConfig.contentSize.height)
                        val pageSequence = PageSequence(contentPageSequence, pageConfig)
                        PagesLoader(pages, pageSequence)
                    }
                    val renderModel = BookRenderModel(size)

                    SizedBook(createPages, createPagesLoader, renderModel)
                }

                val locationConverter = bookData.content.locationConverter

                Book(createSized, bookData, bitmapDecoder, locationConverter)
            }

            val createBookControl = { reader: Reader -> BookControl(userSettings, reader.book, reader, closeApp) }
            val createMenu = { reader: Reader, close: () -> Unit ->
                Menu(reader.book, close)
            }

            Reader(createBook, createBookControl, createMenu)
        }

        Main(intent, bookContentParserFactory, userData, createReader, closeApp)
    }

    val createMainView = { activity: AppActivity, model: Main ->
        val context: Context = activity
        val lifeCycle = activity.lifeCycle
        val density = activity.resources.displayMetrics.density

        val createReaderView = { model: Reader ->
            val createBookView = { model: Book ->
                val bitmapDecoder = model.bitmapDecoder
                val createRenderer = { size: Size ->
                    val renderModel = model.renderModel

                    val objectPainter = UniversalObjectPainter(bitmapDecoder)
                    val contentRowPainter = RenderPartPainter(objectPainter)
                    val contentPagePainter = RenderColumnPainter(contentRowPainter)
                    val pagePainter = PagePainter(contentPagePainter)
                    val refreshScheduler = RefreshScheduler(BitmapBuffer(size, density))
                    val createRefresher = { renderer: PagesRenderer, size: Size -> PagesRefresher(pagePainter, renderer, refreshScheduler, size) }
                    val createPagesRenderer = { PagesRenderer(context, size, density, createRefresher) }

                    BookRenderer(size, renderModel, createPagesRenderer)
                }

                BookView(context, model, createRenderer, lifeCycle)
            }


            val createBookControlView = { model: BookControl -> BookControlView(context, model) }
            val createMenuView = { model: Menu -> MenuView(context, model) }

            ReaderView(context, model, createBookView, createBookControlView, createMenuView)
        }

        MainView(activity, model, createReaderView)
    }
}
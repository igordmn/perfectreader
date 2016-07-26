package com.dmi.perfectreader.app

import android.content.Context
import com.dmi.perfectreader.data.UserData
import com.dmi.perfectreader.data.UserSettings
import com.dmi.perfectreader.fragment.book.*
import com.dmi.perfectreader.fragment.book.bitmap.AndroidBitmapDecoder
import com.dmi.perfectreader.fragment.book.bitmap.CachedBitmapDecoder
import com.dmi.perfectreader.fragment.book.content.ConfiguredSequence
import com.dmi.perfectreader.fragment.book.content.obj.param.settingsLayoutConfig
import com.dmi.perfectreader.fragment.book.layout.LayoutSequence
import com.dmi.perfectreader.fragment.book.layout.UniversalObjectLayouter
import com.dmi.perfectreader.fragment.book.layout.paragraph.breaker.CompositeBreaker
import com.dmi.perfectreader.fragment.book.layout.paragraph.breaker.LineBreaker
import com.dmi.perfectreader.fragment.book.layout.paragraph.breaker.ObjectBreaker
import com.dmi.perfectreader.fragment.book.layout.paragraph.breaker.WordBreaker
import com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator.CachedHyphenatorResolver
import com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator.TeXHyphenatorResolver
import com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator.TeXPatternsSource
import com.dmi.perfectreader.fragment.book.layout.paragraph.liner.BreakLiner
import com.dmi.perfectreader.fragment.book.layout.paragraph.metrics.PaintTextMetrics
import com.dmi.perfectreader.fragment.book.page.GLPages
import com.dmi.perfectreader.fragment.book.page.GLTextureRefresher
import com.dmi.perfectreader.fragment.book.page.Pages
import com.dmi.perfectreader.fragment.book.page.PagesLoader
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumnSequence
import com.dmi.perfectreader.fragment.book.pagination.page.PageSequence
import com.dmi.perfectreader.fragment.book.pagination.page.settingsPageConfig
import com.dmi.perfectreader.fragment.book.pagination.part.LayoutPartSequence
import com.dmi.perfectreader.fragment.book.paint.ColumnPainter
import com.dmi.perfectreader.fragment.book.paint.PagePainter
import com.dmi.perfectreader.fragment.book.paint.PartPainter
import com.dmi.perfectreader.fragment.book.paint.UniversalObjectPainter
import com.dmi.perfectreader.fragment.book.parse.BookContentParserFactory
import com.dmi.perfectreader.fragment.book.parse.settingsParseConfig
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
    val dip2px = { value: Float -> value * applicationContext.resources.displayMetrics.density }

    val createMain = { activity: AppActivity ->
        val intent = activity.intent
        val closeApp = { activity.finish() }

        val parseConfig = settingsParseConfig(userSettings)
        val bookContentParserFactory = BookContentParserFactory(parseConfig, applicationContext)
        val patternsSource = TeXPatternsSource(applicationContext)
        val hyphenatorResolver = CachedHyphenatorResolver(TeXHyphenatorResolver(patternsSource))

        val createReader = { bookData: BookData ->
            val createBook = {
                val bitmapDecoder = CachedBitmapDecoder(AndroidBitmapDecoder(bookData.content.openResource))

                val wordBreaker = WordBreaker(hyphenatorResolver)
                val breaker = CompositeBreaker(LineBreaker(), ObjectBreaker(), wordBreaker)
                val layouter = UniversalObjectLayouter(PaintTextMetrics(), BreakLiner(breaker), bitmapDecoder)

                val createSized = { size: SizeF ->
                    val createPages = { Pages(bookData.location) }
                    val createPagesLoader = { pages: Pages ->
                        val layoutConfig = settingsLayoutConfig(applicationContext, userSettings)
                        val pageConfig = settingsPageConfig(size, userSettings)
                        val configuredSequence = ConfiguredSequence(bookData.content.sequence, layoutConfig)
                        val layoutSequence = LayoutSequence(configuredSequence, layouter, pageConfig.contentSize)
                        val layoutPartSequence = LayoutPartSequence(layoutSequence)
                        val layoutColumnSequence = LayoutColumnSequence(layoutPartSequence, pageConfig.contentSize.height)
                        val pageSequence = PageSequence(layoutColumnSequence, pageConfig)
                        PagesLoader(pages, pageSequence)
                    }
                    val renderModel = BookRenderModel(size)

                    SizedBook(createPages, createPagesLoader, renderModel)
                }

                val locationConverter = bookData.content.locationConverter

                Book(createSized, bookData, bitmapDecoder, locationConverter)
            }

            val createBookControl = { reader: Reader -> BookControl(userSettings, reader.book, reader, closeApp, dip2px) }
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

        val createReaderView = { model: Reader ->
            val createBookView = { model: Book ->
                val bitmapDecoder = model.bitmapDecoder
                val createGLBook = { size: Size ->
                    val renderModel = model.renderModel

                    val objectPainter = UniversalObjectPainter(bitmapDecoder)
                    val layoutPartPainter = PartPainter(objectPainter)
                    val layoutColumnPainter = ColumnPainter(layoutPartPainter)
                    val pagePainter = PagePainter(layoutColumnPainter)
                    val pageTextureRefresher = GLTextureRefresher(size)
                    val createGLPages = { GLPages(context, size, pageTextureRefresher, pagePainter) }

                    GLBook(size, renderModel, createGLPages)
                }

                BookView(context, model, createGLBook, lifeCycle)
            }


            val createBookControlView = { model: BookControl -> BookControlView(context, model) }
            val createMenuView = { model: Menu -> MenuView(context, model) }

            ReaderView(context, model, createBookView, createBookControlView, createMenuView)
        }

        MainView(activity, model, createReaderView)
    }
}
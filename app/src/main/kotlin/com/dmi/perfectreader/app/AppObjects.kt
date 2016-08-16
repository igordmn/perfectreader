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
import com.dmi.perfectreader.fragment.book.page.Pages
import com.dmi.perfectreader.fragment.book.page.PagesLoader
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumnSequence
import com.dmi.perfectreader.fragment.book.pagination.page.PageSequence
import com.dmi.perfectreader.fragment.book.pagination.page.settingsPageConfig
import com.dmi.perfectreader.fragment.book.pagination.part.LayoutPartSequence
import com.dmi.perfectreader.fragment.book.parse.BookContentParserFactory
import com.dmi.perfectreader.fragment.book.parse.settingsParseConfig
import com.dmi.perfectreader.fragment.book.render.paint.*
import com.dmi.perfectreader.fragment.book.render.render.ImageRenderer
import com.dmi.perfectreader.fragment.book.render.render.PageRenderer
import com.dmi.perfectreader.fragment.book.render.render.UniversalObjectRenderer
import com.dmi.perfectreader.fragment.control.Control
import com.dmi.perfectreader.fragment.control.ControlView
import com.dmi.perfectreader.fragment.main.Main
import com.dmi.perfectreader.fragment.main.MainView
import com.dmi.perfectreader.fragment.menu.Menu
import com.dmi.perfectreader.fragment.menu.MenuView
import com.dmi.perfectreader.fragment.reader.Reader
import com.dmi.perfectreader.fragment.reader.ReaderView
import com.dmi.perfectreader.fragment.selection.Selection
import com.dmi.perfectreader.fragment.selection.SelectionView
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

                val createAnimated = { size: SizeF ->
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
                    val staticBook = StaticBook(createPages, createPagesLoader)

                    AnimatedBook(size, staticBook)
                }

                val locationConverter = bookData.content.locationConverter

                Book(createAnimated, bookData, bitmapDecoder, locationConverter)
            }

            val createControl = { reader: Reader -> Control(userSettings, reader.book, reader, closeApp, dip2px) }
            val createSelection = { reader: Reader -> Selection(reader.book, userSettings) }
            val createMenu = { reader: Reader, close: () -> Unit ->
                Menu(reader.book, close)
            }

            Reader(createBook, createControl, createSelection, createMenu)
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
                    val objectPainter = UniversalObjectPainter(
                            FramePainter(),
                            ImagePainter(),
                            TextPainter()
                    )
                    val objectRenderer = UniversalObjectRenderer(
                            ImageRenderer(bitmapDecoder)
                    )
                    val pageRenderer = PageRenderer(objectRenderer)
                    val pagePainter = PagePainter(objectPainter)

                    GLBook(context, size, model, pageRenderer, pagePainter)
                }

                BookView(context, model, createGLBook, lifeCycle)
            }


            val createControlView = { model: Control -> ControlView(context, model) }
            val createSelectionView = { model: Selection -> SelectionView(context, model)}
            val createMenuView = { model: Menu -> MenuView(context, model) }

            ReaderView(context, model, createBookView, createControlView, createSelectionView, createMenuView)
        }

        MainView(activity, model, createReaderView)
    }
}
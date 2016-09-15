package com.dmi.perfectreader.app

import android.content.Context
import com.dmi.perfectreader.data.UserData
import com.dmi.perfectreader.data.UserSettings
import com.dmi.perfectreader.fragment.book.*
import com.dmi.perfectreader.fragment.book.bitmap.AndroidBitmapDecoder
import com.dmi.perfectreader.fragment.book.bitmap.CachedBitmapDecoder
import com.dmi.perfectreader.fragment.book.content.ConfiguredSequence
import com.dmi.perfectreader.fragment.book.content.obj.param.appContentConfig
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
import com.dmi.perfectreader.fragment.book.pagination.page.PageConfig
import com.dmi.perfectreader.fragment.book.pagination.page.PageSequence
import com.dmi.perfectreader.fragment.book.pagination.page.settingsPageConfig
import com.dmi.perfectreader.fragment.book.pagination.part.LayoutPartSequence
import com.dmi.perfectreader.fragment.book.parse.BookContentParserFactory
import com.dmi.perfectreader.fragment.book.parse.settingsParseConfig
import com.dmi.perfectreader.fragment.book.render.factory.FramePainter
import com.dmi.perfectreader.fragment.book.render.factory.ImagePainter
import com.dmi.perfectreader.fragment.book.render.factory.PageRenderer
import com.dmi.perfectreader.fragment.book.render.factory.TextPainter
import com.dmi.perfectreader.fragment.control.Control
import com.dmi.perfectreader.fragment.control.ControlView
import com.dmi.perfectreader.fragment.control.ReaderActionProvider
import com.dmi.perfectreader.fragment.control.settingsGestureDetector
import com.dmi.perfectreader.fragment.main.Main
import com.dmi.perfectreader.fragment.main.MainView
import com.dmi.perfectreader.fragment.menu.Menu
import com.dmi.perfectreader.fragment.menu.MenuView
import com.dmi.perfectreader.fragment.reader.Reader
import com.dmi.perfectreader.fragment.reader.ReaderView
import com.dmi.perfectreader.fragment.reader.action.ReaderActions
import com.dmi.perfectreader.fragment.selection.Selection
import com.dmi.perfectreader.fragment.selection.SelectionView
import com.dmi.util.action.TouchActionPerformer
import com.dmi.util.android.font.androidFontCollectionCache
import com.dmi.util.android.system.copyPlainText
import com.dmi.util.graphic.Size
import com.dmi.util.graphic.SizeF
import org.jetbrains.anko.displayMetrics

class AppObjects(applicationContext: Context) {
    val databases = AppDatabases(applicationContext)
    val userData = UserData(databases.user)
    val settings = UserSettings(databases.user)
    val protocols = AppProtocols()
    val fontCollectionCache = androidFontCollectionCache()
    val density = applicationContext.displayMetrics.density
    val dip2px = { value: Float -> value * density }
    val copyPlainText = { text: String -> applicationContext.copyPlainText(text) }

    val createMain = { activity: AppActivity ->
        val intent = activity.intent
        val closeApp = { activity.finish() }

        val parseConfig = settingsParseConfig(settings)
        val bookContentParserFactory = BookContentParserFactory(parseConfig)
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
                    val createPageConfig = { settingsPageConfig(applicationContext, size, settings) }
                    val createPagesLoader = { pages: Pages, pageConfig: PageConfig ->
                        val userFontsDirectory = userFontsDirectory(protocols, settings)
                        val fontCollection = fontCollectionCache.collectionFor(userFontsDirectory)
                        val contentConfig = appContentConfig(applicationContext, settings, fontCollection)
                        val configuredSequence = ConfiguredSequence(bookData.content.sequence, contentConfig)
                        val createColumnSequence = { contentSize: SizeF ->
                            val layoutSequence = LayoutSequence(configuredSequence, layouter, contentSize)
                            val partSequence = LayoutPartSequence(layoutSequence)
                            LayoutColumnSequence(partSequence, contentSize.height)
                        }
                        val pageSequence = PageSequence(createColumnSequence, pageConfig)
                        PagesLoader(pages, pageSequence)
                    }
                    val createLocationConverter = { pageConfig: PageConfig ->
                        LocationConverter(bookData.content, pageConfig, settings)
                    }
                    val staticBook = StaticBook(createPages, createPageConfig, createPagesLoader, createLocationConverter)

                    AnimatedBook(size, dip2px, staticBook)
                }

                Book(createAnimated, bookData, bitmapDecoder)
            }

            val createControl = { reader: Reader ->
                val createGestureDetector = { size: SizeF ->
                    val actionProvider = ReaderActionProvider(size, density, settings, reader)
                    val listener = TouchActionPerformer(actionProvider)
                    settingsGestureDetector(density, settings, listener)
                }
                Control(settings, createGestureDetector, reader.actions)
            }
            val createSelection = { reader: Reader -> Selection(reader.book, settings, copyPlainText, dip2px) }
            val createMenu = { reader: Reader, close: () -> Unit ->
                Menu(reader.book, close)
            }
            val createActions = { reader: Reader -> ReaderActions(density, activity, settings, reader) }
            Reader(createBook, createControl, createSelection, createMenu, createActions)
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
                    val pageRenderer = PageRenderer(FramePainter(), ImagePainter(bitmapDecoder), TextPainter())

                    GLBook(context, size, model, pageRenderer)
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
package com.dmi.perfectreader.ui.book.gl

import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.settings.Settings
import com.dmi.perfectreader.ui.book.Book
import com.dmi.perfectreader.ui.book.page.VisiblePages
import com.dmi.perfectreader.ui.reader.Reader
import com.dmi.util.graphic.Color
import com.dmi.util.scope.CopyScope
import java.net.URI

class GLBookModel(scope: CopyScope, settings: Settings, reader: Reader, book: Book) {
    val pages: VisiblePages by scope.computed { book.pages }
    val selection: LocationRange? by scope.computed { reader.selection?.range }
    val animationPath: URI by scope.computed { URI(settings.screen.animationPath) }

    val themeUnderColor: Color by scope.computed { Color(settings.theme.underColor) }
    val themePageIsImage: Boolean by scope.computed { settings.theme.pageIsImage }
    val themePageColor: Color by scope.computed { Color(settings.theme.pageColor) }
    val themePagePath: URI by scope.computed { URI(settings.theme.pagePath) }
    val themePageContentAwareResize: Boolean by scope.computed { settings.theme.pageContentAwareResize }
}
package com.dmi.perfectreader.book.gl

import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.page.VisiblePages
import com.dmi.perfectreader.reader.Reader
import com.dmi.perfectreader.settings.Settings
import com.dmi.util.graphic.Color
import com.dmi.util.scope.CopyScope
import java.net.URI

class GLBookModel(scope: CopyScope, settings: Settings, reader: Reader, book: Book) {
    val pages: VisiblePages by scope.computed { book.pages }
    val selection: LocationRange? by scope.computed { reader.selection?.range }
    val pageAnimationPath: URI by scope.computed { URI(settings.format.pageAnimationPath) }

    val pageBackgroundIsImage: Boolean by scope.computed { settings.format.pageBackgroundIsImage }
    val pageBackgroundColor: Color by scope.computed { Color(settings.format.pageBackgroundColor) }
    val pageBackgroundPath: URI by scope.computed { URI(settings.format.pageBackgroundPath) }
    val pageBackgroundContentAwareResize: Boolean by scope.computed { settings.format.pageBackgroundContentAwareResize }
}
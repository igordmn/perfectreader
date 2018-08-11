package com.dmi.perfectreader.book.gl

import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.page.VisiblePages
import com.dmi.perfectreader.reader.Reader
import com.dmi.perfectreader.settings.Settings
import com.dmi.util.scope.CopyScope
import com.dmi.util.scope.Disposable
import java.net.URI

class GLBookModel(scope: CopyScope, settings: Settings, reader: Reader, book: Book) : Disposable by scope {
    val pages: VisiblePages by scope.computed { book.pages }
    val selection: LocationRange? by scope.computed { reader.selection?.range }
    val pageAnimationPath: URI by scope.computed { URI(settings.format.pageAnimationPath) }
}
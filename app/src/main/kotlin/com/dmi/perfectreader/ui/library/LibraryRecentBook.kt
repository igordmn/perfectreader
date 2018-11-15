package com.dmi.perfectreader.ui.library

import android.content.Context
import android.view.Gravity
import android.widget.ProgressBar
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.dmi.util.android.view.container
import com.dmi.util.android.view.into
import com.dmi.util.graphic.Size
import com.dmi.util.lang.unsupported
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.wrapContent

class LibraryRecentBook(
        context: Context,
        private val library: Library,
        private val get: (index: Int) -> Library.Item.Book
) : LibraryItemView(context) {
    init {
        layoutParams = LinearLayoutCompat.LayoutParams(wrapContent, wrapContent)
    }

    private val cover = BookCover(context, Size(dip(48 * 2), dip(72 * 2)))

    private val readProgress = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
        max = 10000
    }

    private var ProgressBar.progressPercent: Double
        get() = unsupported()
        set(value) {
            progress = (value * max).toInt()
        }

    init {
        LinearLayoutCompat(context).apply {
            setPadding(dip(8), dip(8), dip(8), dip(4))
            orientation = LinearLayoutCompat.VERTICAL

            cover into container(wrapContent, wrapContent, gravity = Gravity.CENTER_HORIZONTAL)
            readProgress into container(matchParent, wrapContent)
        } into container(matchParent, wrapContent)
    }

    override fun bind(model: Int) {
        val index = model
        val book = get(index)

        val name: String = book.description.name ?: book.description.fileName
        readProgress.progressPercent = book.readPercent ?: 0.0
        readProgress.isVisible = book.readPercent != null
        cover.bind(BookCover.Content(book.description.cover, name))

        onClick {
            library.open(book)
        }
    }
}
package com.dmi.perfectreader.library

import android.content.Context
import android.text.TextUtils
import android.text.format.Formatter
import android.view.Gravity
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import com.dmi.util.graphic.Size
import com.dmi.util.lang.unsupported
import org.jetbrains.anko.*

abstract class LibraryItemView(context: Context) : FrameLayout(context), Bindable<Int> {
    init {
        backgroundResource = attr(android.R.attr.selectableItemBackground).resourceId
        layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
    }
}

class FolderItemView(
        context: Context,
        private val library: Library,
        private val get: (index: Int) -> Library.Item.Folder
) : LibraryItemView(context) {
    private val name = TextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
        maxLines = 2
        ellipsize = TextUtils.TruncateAt.END
    }
    private val count = TextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
        textColor = color(R.color.onBackground).withOpacity(0.60)
    }

    init {
        child(params(matchParent, wrapContent), LinearLayoutCompat(context).apply {
            setPadding(dip(16), dip(8), dip(16), dip(8))

            orientation = LinearLayoutCompat.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            child(params(dip(48), dip(48), weight = 0F), ImageView(context).apply {
                image = drawable(R.drawable.library_folder, color(R.color.secondary))
                scaleType = ImageView.ScaleType.CENTER
            })
            child(params(matchParent, wrapContent, weight = 1F), LinearLayoutCompat(context).apply {
                setPadding(dip(16), 0, 0, 0)
                orientation = LinearLayoutCompat.VERTICAL
                child(params(matchParent, wrapContent), name)
                child(params(matchParent, wrapContent), count)
            })
        })
    }

    override fun bind(model: Int) {
        val index = model
        val folder = get(index)
        name.text = folder.name
        count.text = resources.getQuantityString(R.plurals.libraryFolderBookCount, folder.deepBookCount, folder.deepBookCount)

        onClick {
            library.open(folder)
        }
    }
}

class BookItemView(
        context: Context,
        private val library: Library,
        private val get: (index: Int) -> Library.Item.Book
) : LibraryItemView(context) {
    private val cover = BookCover(context, Size(dip(48), dip(72)))

    private val name = TextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
        maxLines = 2
        ellipsize = TextUtils.TruncateAt.END
    }

    private val author = TextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
        textColor = color(R.color.onBackground).withOpacity(0.60)
        maxLines = 2
        ellipsize = TextUtils.TruncateAt.END
    }

    private val fileSize = TextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
        textColor = color(R.color.onBackground).withOpacity(0.60)
    }

    private val readProgress = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
        max = 10000
    }

    private var ProgressBar.progressPercent: Double
        get() = unsupported()
        set(value) {
            progress = (value * max).toInt()
        }

    init {
        child(params(matchParent, wrapContent), LinearLayoutCompat(context).apply {
            setPadding(dip(16), dip(8), dip(16), dip(8))
            orientation = LinearLayoutCompat.HORIZONTAL

            child(params(wrapContent, wrapContent, weight = 0F, gravity = Gravity.CENTER_VERTICAL), cover)
            child(params(matchParent, wrapContent, weight = 1F, gravity = Gravity.CENTER_VERTICAL), LinearLayoutCompat(context).apply {
                orientation = LinearLayoutCompat.VERTICAL
                setPadding(dip(16), 0, 0, 0)

                child(params(matchParent, wrapContent), LinearLayoutCompat(context).apply {
                    orientation = LinearLayoutCompat.HORIZONTAL
                    child(params(matchParent, wrapContent, weight = 1F), name)
                    child(params(wrapContent, wrapContent, weight = 0F), fileSize).apply {
                        setPadding(dip(16), 0, 0, 0)
                    }
                })
                child(params(matchParent, wrapContent), author)
                child(params(matchParent, wrapContent), readProgress)
            })
        })
    }

    override fun bind(model: Int) {
        val index = model
        val book = get(index)

        val name: String = book.description.name ?: book.description.fileName
        this.name.text = name
        author.text = book.description.author
        author.isGone = book.description.author == null
        fileSize.text = Formatter.formatShortFileSize(context, book.fileSize)
        readProgress.progressPercent = book.readPercent ?: 0.0
        readProgress.isVisible = book.readPercent != null
        cover.bind(BookCover.Content(book.description.cover, name))

        onClick {
            library.open(book)
        }
    }
}
package com.dmi.perfectreader.library

import android.content.Context
import android.text.format.Formatter
import android.view.Gravity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.core.widget.TextViewCompat.setTextAppearance
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmi.perfectreader.R
import com.dmi.util.android.graphics.toBitmap
import com.dmi.util.android.screen.withPopup
import com.dmi.util.android.view.*
import com.dmi.util.graphic.Size
import com.dmi.util.lang.unsupported
import com.google.common.io.ByteSource
import org.jetbrains.anko.*

fun ViewBuild.libraryView(model: Library): View {
    val places = object : Places() {
        val sort = object : Place() {
            private val values = arrayOf(Library.Sort.Field.Name, Library.Sort.Field.Author, Library.Sort.Field.Size, Library.Sort.Field.ReadPercent)
            private val names = arrayOf(
                    R.string.librarySortName, R.string.librarySortAuthor,
                    R.string.librarySortSize, R.string.librarySortReadPercent
            ).map { context.string(it) }.toTypedArray()

            override fun ViewBuild.view() = DialogView(context) {
                var fieldIndex = values.indexOf(model.sort.field)

                fun apply(method: Library.Sort.Method) {
                    val field = values[fieldIndex]
                    model.sort = Library.Sort(field, method)
                }

                AlertDialog.Builder(context)
                        .setTitle(R.string.librarySort)
                        .setPositiveButton(R.string.librarySortAsc) { _, _ ->
                            apply(Library.Sort.Method.ASC)
                        }
                        .setNegativeButton(R.string.librarySortDesc) { _, _ ->
                            apply(Library.Sort.Method.DESC)
                        }
                        .setSingleChoiceItems(names, fieldIndex) { _, which ->
                            fieldIndex = which
                        }
                        .setOnDismissListener {
                            model.popup = null
                        }
                        .create()
            }
        }
    }

    fun addressBar() = BreadCrumbsView(context).apply {
        subscribe(
                model.folders,
                afterPush = {
                    val reverseIndex = size
                    push(it.name, onClick = {
                        model.currentIndex = size - 1 - reverseIndex
                    })
                },
                afterPop = ::pop
        )
        autorun {
            current = model.currentIndex
        }
    }

    fun toolbar() = Toolbar(context).apply {
        backgroundColor = color(android.R.color.transparent)
        menu.add(R.string.librarySearch).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
            actionView = SearchView(context)
            icon = drawable(R.drawable.ic_search, color(R.color.onBackground))
        }
        menu.add(R.string.librarySort).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            icon = drawable(R.drawable.ic_sort, color(R.color.onBackground))
            onClick {
                model.popup = places.sort.id
            }
        }
    }

    fun topBar() = LinearLayoutCompat(context).apply {
        orientation = LinearLayoutCompat.VERTICAL
        child(params(matchParent, wrapContent), addressBar())
        child(params(matchParent, wrapContent, topMargin = dip(-12)), toolbar())
    }

    fun folders() = RecyclerView(context, null, R.attr.verticalRecyclerViewStyle).apply {
        val viewTypes = object {
            val folder = 1
            val book = 2
        }
        val adapter = object : BindableViewAdapter<LibraryItemView>() {
            override fun getItemCount() = model.items?.size ?: 0

            override fun view(viewType: Int): LibraryItemView = when (viewType) {
                viewTypes.folder -> FolderItemView(context, model) { model.items!![it] as Library.Item.Folder }
                viewTypes.book -> BookItemView(context, model) { model.items!![it] as Library.Item.Book }
                else -> unsupported()
            }

            override fun getItemViewType(position: Int): Int = when (model.items!![position]) {
                is Library.Item.Folder -> viewTypes.folder
                is Library.Item.Book -> viewTypes.book
            }
        }
        id = generateId()
        this.layoutManager = LinearLayoutManager(context)
        this.adapter = adapter

        autorun {
            isVisible = model.items != null
            adapter.notifyDataSetChanged()
        }
    }

    fun progress(): View = ProgressBar(context).apply {
        padding = dip(16)
        autorun {
            isVisible = model.items == null
        }
    }

    fun emptyFolder(): View = TextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
        padding = dip(16)
        textColor = color(R.color.onBackground).withOpacity(0.60)
        text = string(R.string.libraryEmptyFolder)

        autorun {
            isVisible = model.items?.isEmpty() ?: false
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun ViewBuild.popupView(popup: Any): View {
        return when (popup) {
            is Id -> places[popup].view(this)
            else -> unsupported(popup)
        }
    }

    return LinearLayoutExt(context).apply {
        orientation = LinearLayoutCompat.VERTICAL
        child(params(matchParent, wrapContent, weight = 0F), topBar())

        child(params(matchParent, matchParent, weight = 1F), FrameLayout(context).apply {
            child(params(matchParent, matchParent), folders())
            child(params(wrapContent, wrapContent, Gravity.CENTER), progress())
            child(params(wrapContent, wrapContent, Gravity.CENTER), emptyFolder())
        })

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) {
            model.back()
            true
        }
    }.withPopup(this, model::popup, ViewBuild::popupView)
}

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
        setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
    }
    private val count = TextView(context).apply {
        setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
        textColor = color(R.color.onBackground).withOpacity(0.60)
    }

    init {
        child(params(matchParent, wrapContent), LinearLayoutCompat(context).apply {
            setPadding(dip(16), dip(8), dip(16), dip(8))

            orientation = LinearLayoutCompat.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            child(params(dip(48), dip(48), weight = 0F), ImageView(context).apply {
                image = drawable(R.drawable.ic_library_folder, color(R.color.secondary))
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
        setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
    }

    private val author = TextView(context).apply {
        setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
        textColor = color(R.color.onBackground).withOpacity(0.60)
    }

    private val fileSize = TextView(context).apply {
        setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
        textColor = color(R.color.onBackground).withOpacity(0.60)
    }

    init {
        child(params(matchParent, wrapContent), LinearLayoutCompat(context).apply {
            setPadding(dip(16), dip(8), dip(16), dip(8))
            orientation = LinearLayoutCompat.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            child(params(wrapContent, wrapContent, weight = 0F), cover)
            child(params(matchParent, wrapContent, weight = 1F), LinearLayoutCompat(context).apply {
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
        cover.bind(BookCover.Content(book.description.cover, name))

        onClick {
            library.open(book)
        }
    }
}

class BookCover(context: Context, private val imageSize: Size) : FrameLayout(context), Bindable<BookCover.Content> {
    private val image = child(params(imageSize.width, imageSize.height), ImageView(context).apply {
        scaleType = ImageView.ScaleType.CENTER
    })
    private val text = child(params(imageSize.width, imageSize.height), TextView(context).apply {
        padding = imageSize.width / 12
        gravity = Gravity.CENTER
        textColor = color(R.color.onSecondary)
        textSize = imageSize.width / 8F
    })

    private val load = ViewLoad(this)

    override fun bind(model: Content) {
        image.setImageBitmap(null)
        text.text = null
        load.start {
            val bitmap = model.image?.toBitmap(imageSize)
            if (bitmap != null) {
                image.setImageBitmap(bitmap)
            } else {
                image.imageResource = R.drawable.library_cover
                text.text = model.name
            }
        }
    }

    class Content(val image: ByteSource?, val name: String)
}
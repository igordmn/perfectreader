package com.dmi.perfectreader.library

import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmi.perfectreader.R
import com.dmi.util.android.screen.withPopup
import com.dmi.util.android.view.*
import com.dmi.util.lang.unsupported
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
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

    return CoordinatorLayout(context).apply {
        val toolbar = child(params(matchParent, wrapContent), AppBarLayout(context).apply {
            elevation = dipFloat(4F)
            child(params(matchParent, wrapContent, SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS), topBar())
        })

        val recycler = child(params(matchParent, matchParent, behavior = AppBarLayout.ScrollingViewBehavior()), folders())

        child(params(wrapContent, wrapContent, Gravity.CENTER), progress())
        child(params(wrapContent, wrapContent, Gravity.CENTER), emptyFolder())

        removeElevationOnScroll(recycler, toolbar)
    }.withPopup(this, model::popup, ViewBuild::popupView)
}
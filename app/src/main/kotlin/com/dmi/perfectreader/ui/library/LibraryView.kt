package com.dmi.perfectreader.ui.library

import android.graphics.Color
import android.view.Gravity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmi.perfectreader.R
import com.dmi.util.android.screen.withPopup
import com.dmi.util.android.view.*
import com.dmi.util.lang.max
import com.dmi.util.lang.unsupported
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
import org.jetbrains.anko.*

fun ViewBuild.libraryView(model: Library): View {
    val places = object : Places() {
        val sort = place {
            val values = arrayOf(Library.Sort.Field.Name, Library.Sort.Field.Author, Library.Sort.Field.Size, Library.Sort.Field.ReadPercent)
            val names = arrayOf(
                    R.string.librarySortName, R.string.librarySortAuthor, R.string.librarySortSize, R.string.librarySortReadPercent
            ).map { context.string(it) }.toTypedArray()

            DialogView(context) {
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

    fun recentBooks() = RecyclerView(context, null, R.attr.verticalRecyclerViewStyle).apply {
        setPadding(dip(8), dip(8), dip(8), dip(8))

        val adapter = object : BindableViewAdapter<LibraryItemView>() {
            override fun getItemCount() = model.recentBooks?.size ?: 0
            override fun view(viewType: Int) = LibraryRecentBook(context, model) { model.recentBooks!![it] }
        }
        id = generateId()
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        this.adapter = adapter

        autorun {
            isVisible = model.recentBooks != null
            adapter.notifyDataSetChanged()
        }
    }

    fun addressBar() = BreadCrumbsView {
        subscribe(
                model.folders,
                afterAdd = {
                    val index = size
                    add(it.name, onClick = {
                        model.currentIndex = index
                    })
                },
                afterRemove = ::remove
        )
        autorun {
            current = model.currentIndex
        }
    }

    fun toolbar() = Toolbar {
        backgroundColor = color(android.R.color.transparent)
        menu.add(R.string.librarySort).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            icon = drawable(R.drawable.ic_sort, color(R.color.onBackground))
            onClick {
                model.popup = places.sort.id
            }
        }
    }

    fun collapsingBar() = VerticalLayout {
        recentBooks() into container(matchParent, wrapContent)
        toolbar() into container(matchParent, wrapContent)
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
            isVisible = model.items != null && model.items!!.isNotEmpty()
            adapter.notifyDataSetChanged()
        }
    }

    fun emptyFolder(): View = TextView {
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

    return CoordinatorLayoutExt {
        AppBarLayout {
            val collapsing = CollapsingToolbarLayout {
                collapsingBar() into container(matchParent, wrapContent, mode = COLLAPSE_MODE_PARALLAX, parallaxMultiplier = 0.7F)
                scrimVisibleHeightTrigger = 100000000
            } into container(matchParent, wrapContent, scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)
            addressBar() into container(matchParent, wrapContent, scrollFlags = SCROLL_FLAG_SCROLL)
            setFadingScrimOnHide(collapsing)
        } into container(matchParent, wrapContent)

        SwipeRefreshLayout {
            folders() into container(matchParent, matchParent)

            setOnRefreshListener {
                model.refresh()
            }
            autorun {
                if (model.items == null)
                    isRefreshing = false
            }
        } into container(matchParent, matchParent, behavior = AppBarLayout.ScrollingViewBehavior())

        emptyFolder() into container(wrapContent, wrapContent, Gravity.CENTER)

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) {
            model.back()
            true
        }
    }.withPopup(this, model::popup, ViewBuild::popupView)
}

private fun AppBarLayout.setFadingScrimOnHide(collapsing: CollapsingToolbarLayout) {
    val minScrimActivation = dip(16)
    collapsing.setContentScrimColor(Color.TRANSPARENT)
    addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        val collapsePercent = max(0, -verticalOffset - minScrimActivation).toFloat() / (collapsing.height - minScrimActivation)
        collapsing.setContentScrimColor(Color.BLACK.withOpacity(0.60 * collapsePercent))
    })
}
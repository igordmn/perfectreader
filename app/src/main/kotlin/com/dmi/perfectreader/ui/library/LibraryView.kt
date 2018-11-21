package com.dmi.perfectreader.ui.library

import android.graphics.Color
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmi.perfectreader.R
import com.dmi.util.android.screen.withPopup
import com.dmi.util.android.view.*
import com.dmi.util.lang.unsupported
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
import me.dkzwm.widget.srl.SmoothRefreshLayout
import me.dkzwm.widget.srl.extra.header.MaterialHeader
import me.dkzwm.widget.srl.indicator.DefaultIndicator
import me.dkzwm.widget.srl.utils.QuickConfigAppBarUtil
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

fun ViewBuild.libraryView(model: Library): View {
    val places = object : Places() {
        val sort = place {
            view {
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
    }
    places.finish()

    fun recentBooks() = RecyclerView(context, null, R.attr.verticalRecyclerViewStyle).apply {
        setPadding(dip(8), dip(8), dip(8), dip(0))
        setHasFixedSize(true)

        val adapter = object : BindableViewAdapter<LibraryItemView>() {
            override fun getItemCount() = model.recentBooks?.size ?: 0
            override fun view(viewType: Int) = LibraryRecentBook(context, model) { model.recentBooks!![it] }
        }
        id = generateId()
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        this.adapter = adapter

        if (!model.hasRecentBooks())
            isVisible = false

        autorun {
            val recentBooks = model.recentBooks
            if (recentBooks != null && recentBooks.isNotEmpty())
                isVisible = true
            adapter.notifyDataSetChanged()
        }
    }

    fun addressBar() = BreadCrumbsView {
        subscribe(
                model.locations,
                afterAdd = {
                    val index = size
                    add(it.folder.name, onClick = {
                        model.currentIndex = index
                    })
                },
                afterRemove = ::remove
        )
        autorun {
            current = model.currentIndex
        }
    }

    fun foldersBar() = HorizontalLayout {
        backgroundColor = color(R.color.primary)
        addressBar() into container(matchParent, wrapContent, weight = 1F)
        AppCompatImageView {
            padding = dip(4)
            image = drawable(R.drawable.ic_sort, color(R.color.onBackground))
            backgroundResource = attr(android.R.attr.selectableItemBackgroundBorderless).resourceId
            ViewCompat.setTooltipText(this, string(R.string.librarySort))
            onClick {
                model.popup = places.sort.id
            }
        } into container(dip(32), dip(32), weight = 0F, gravity = Gravity.CENTER_VERTICAL, leftMargin = dip(8), rightMargin = dip(8))
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
        val layoutManager = LinearLayoutManager(context)
        this.layoutManager = layoutManager
        this.adapter = adapter

        addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val startView = getChildAt(0)
                model.currentLocation.scrollPosition = LibraryScrollPosition(
                        index = layoutManager.findFirstVisibleItemPosition(),
                        offset = if (startView == null) 0 else startView.top - paddingTop
                )
            }
        })

        autorun {
            val scrollPosition = model.currentLocation.scrollPosition
            val items = model.items

            isVisible = items != null && items.isNotEmpty()
            adapter.notifyDataSetChanged()
            layoutManager.scrollToPositionWithOffset(scrollPosition.index, scrollPosition.offset)
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

    return MaterialSmoothRefreshLayoutExt {
        @Suppress("UNCHECKED_CAST")
        (headerView as MaterialHeader<DefaultIndicator>).setColorSchemeColors(intArrayOf(color(R.color.secondary)))
        materialStyle()
        setDisableWhenAnotherDirectionMove(true)
        setEnableDynamicEnsureTargetView(true)
        addLifecycleObserver(QuickConfigAppBarUtil())

        setOnRefreshListener(object : SmoothRefreshLayout.OnRefreshListener {
            override fun onLoadingMore() = Unit
            override fun onRefreshing() = model.refresh()
        })

        autorun {
            if (model.items != null && model.recentBooks != null)
                refreshComplete()
        }

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) {
            model.back()
            true
        }

        CoordinatorLayoutExt {
            AppBarLayout {
                val collapsing = CollapsingToolbarLayout {
                    recentBooks() into container(matchParent, dip(184), mode = COLLAPSE_MODE_PARALLAX, parallaxMultiplier = 0.7F)
                    scrimVisibleHeightTrigger = 100000000
                } into container(matchParent, wrapContent, scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)
                val foldersBar = foldersBar() into container(matchParent, wrapContent)
                configureCollapsing(collapsing, foldersBar)
            } into container(matchParent, wrapContent)

            folders() into container(matchParent, matchParent, behavior = AppBarLayout.ScrollingViewBehavior())
            emptyFolder() into container(wrapContent, wrapContent, Gravity.CENTER)
        } into container(matchParent, matchParent)
    }.withPopup(this, model::popup, ViewBuild::popupView)
}

private fun AppBarLayout.configureCollapsing(collapsing: CollapsingToolbarLayout, toolbar: View) {
    collapsing.setContentScrimColor(Color.TRANSPARENT)
    addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        val collapsePercent = -verticalOffset.toFloat() / collapsing.height
        collapsing.setContentScrimColor(Color.BLACK.withOpacity(0.60 * collapsePercent))
        toolbar.elevation = if (verticalOffset == 0) 0F else dipFloat(8F)
    })
}
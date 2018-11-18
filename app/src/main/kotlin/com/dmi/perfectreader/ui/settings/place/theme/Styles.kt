package com.dmi.perfectreader.ui.settings.place.theme

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
import com.dmi.perfectreader.settings.SavedTheme
import com.dmi.perfectreader.settings.SavedThemes
import com.dmi.perfectreader.settings.themeSettings
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.common.SettingBitmapView
import com.dmi.perfectreader.ui.settings.common.details
import com.dmi.perfectreader.ui.settings.common.detailsToolbar
import com.dmi.perfectreader.ui.settings.common.list.SettingMultiChoiceViewAdapter
import com.dmi.util.android.view.*
import com.dmi.util.collection.removeAt
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.*

fun ViewBuild.stylesDetails(model: SettingsUI): LinearLayoutExt {
    val adapter = SettingMultiChoiceViewAdapter(
            context,
            { context.main.settings.styles.saved.list },
            ::ThemeSavedItemView,
            onItemClick = { position, it ->
                context.main.settings.theme.load(it)
                context.main.settings.styles.lastAppliedIndex = position
                model.screens.goBackward()
            }
    )
    val recyclerView = RecyclerView(context, null, R.attr.verticalRecyclerViewStyle).apply {
        setHasFixedSize(true)
        this.layoutManager = GridAutoFitLayoutManager(context, columnWidth = context.dip(64 + 12 * 2))
        this.adapter = adapter
    }

    val undoRemoved = object {
        private var old: SavedThemes? = null
        private var removedPositions: Collection<Int>? = null

        fun show(old: SavedThemes, removedPositions: Collection<Int>) {
            val self = this
            val text = if (old.list.size > 1) R.string.settingsUIThemeSavedRemovedMultiple else R.string.settingsUIThemeSavedRemovedSingle
            Snackbar
                    .make(recyclerView, text, 6000)
                    .setAction(R.string.settingsUIThemeSavedUndoRemove) {
                        undo()
                    }
                    .setActionTextColor(recyclerView.color(R.color.secondaryVariantLight))
                    .addCallback(object : Snackbar.Callback() {
                        override fun onShown(sb: Snackbar?) {
                            self.old = old
                            self.removedPositions = removedPositions
                        }

                        override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                            self.old = null
                            self.removedPositions = null
                        }
                    })
                    .show()
        }

        private fun undo() {
            context.main.settings.styles.saved = old!!

            if (recyclerView.isAttachedToWindow) {
                removedPositions!!
                        .sorted()
                        .forEach(adapter::notifyItemInserted)
            }
        }
    }

    fun selectAll() = adapter.selectAll()

    fun removeSelected() {
        val old = context.main.settings.styles.saved
        val positions = adapter.selectedPositions
        context.main.settings.styles.saved = SavedThemes(context.main.settings.styles.saved.list.removeAt(positions))
        positions
                .sortedDescending()
                .forEach(adapter::notifyItemRemoved)
        adapter.deselect()
        undoRemoved.show(old = old, removedPositions = positions)
    }

    fun add() {
        val theme = context.main.settings.theme.save()
        context.main.settings.styles.saved = SavedThemes(context.main.settings.styles.saved.list + theme)
        val lastIndex = context.main.settings.styles.saved.list.size - 1
        adapter.notifyItemInserted(lastIndex)
        recyclerView.smoothScrollToPosition(lastIndex)
    }

    fun selectionToolbar() = Toolbar(ContextThemeWrapper(context, R.style.ThemeOverlay_AppCompat_Dark_ActionBar)).apply {
        setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
        backgroundColor = color(R.color.dark)
        navigationIcon = drawable(R.drawable.ic_arrow_left, color(R.color.onSecondary))
        setNavigationOnClickListener { adapter.deselect() }
        menu.add(R.string.settingsUIThemeSavedSelectAll).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            icon = drawable(R.drawable.ic_select_all, color(R.color.onSecondary))
            onClick { selectAll() }
        }
        menu.add(R.string.settingsUIThemeSavedRemove).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            icon = drawable(R.drawable.ic_delete, color(R.color.onSecondary))
            onClick { removeSelected() }
        }

        autorun {
            title = adapter.selectedPositions.size.toString()
        }
    }

    fun toolbar() = FrameLayout {
        detailsToolbar(R.string.settingsUIThemeSaved, model).apply {
            menu.add(R.string.settingsUIThemeSavedAdd).apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                icon = drawable(R.drawable.ic_plus, color(R.color.onBackground))
                onClick { add() }
            }
            autorun {
                isVisible = adapter.selectedPositions.isEmpty()
            }
        } into container(matchParent, wrapContent)
        selectionToolbar().apply {
            autorun {
                isVisible = adapter.selectedPositions.isNotEmpty()
            }
        } into container(matchParent, wrapContent)
    }

    return details(toolbar(), recyclerView)
}

class ThemeSavedItemView(context: Context) : FrameLayout(context), Bindable<SavedTheme> {
    private val pageBitmap = SettingBitmapView(context, size = dip(64))
    private val pageColor = View(context)
    private val letter = TextView(context).apply {
        textSize = dipFloat(16F)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        text = "A"
    }

    init {
        padding = dip(12)
        pageBitmap into container(wrapContent, wrapContent, gravity = Gravity.CENTER)
        pageColor into container(dip(64), dip(64), gravity = Gravity.CENTER)
        letter into container(wrapContent, wrapContent, gravity = Gravity.CENTER)
    }

    override fun bind(model: SavedTheme) {
        val settings = themeSettings(model)
        pageBitmap.isVisible = settings.pageIsImage
        pageBitmap.bind(if (settings.pageIsImage) settings.pagePath else "")
        pageColor.isVisible = !settings.pageIsImage
        pageColor.backgroundColor = settings.pageColor
        letter.textColor = settings.textColor
    }
}
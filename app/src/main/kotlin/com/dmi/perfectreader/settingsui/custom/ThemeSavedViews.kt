package com.dmi.perfectreader.settingsui.custom

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
import com.dmi.perfectreader.settingsui.SettingsUI
import com.dmi.perfectreader.settingsui.common.SettingBitmapView
import com.dmi.perfectreader.settingsui.common.SettingSelectListViewAdapter
import com.dmi.perfectreader.settingsui.common.details
import com.dmi.perfectreader.settingsui.common.detailsToolbar
import com.dmi.util.android.view.*
import com.dmi.util.collection.removeAt
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.*

fun themeSavedDetails(context: Context, model: SettingsUI): LinearLayoutExt {
    val adapter = SettingSelectListViewAdapter(
            context,
            { context.main.settings.savedThemes.list },
            ::ThemeSavedItemView,
            onItemClick = {
                context.main.settings.theme.load(it)
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
            context.main.settings.savedThemes = old!!

            if (recyclerView.isAttachedToWindow) {
                removedPositions!!
                        .sorted()
                        .forEach(adapter::notifyItemInserted)
            }
        }
    }

    fun selectAll() = adapter.selectAll()

    fun removeSelected() {
        val old = context.main.settings.savedThemes
        val positions = adapter.selectedPositions
        context.main.settings.savedThemes = SavedThemes(context.main.settings.savedThemes.list.removeAt(positions))
        positions
                .sortedDescending()
                .forEach(adapter::notifyItemRemoved)
        adapter.deselect()
        undoRemoved.show(old = old, removedPositions = positions)
    }

    fun add() {
        val theme = context.main.settings.theme.save()
        context.main.settings.savedThemes = SavedThemes(context.main.settings.savedThemes.list + theme)
        val lastIndex = context.main.settings.savedThemes.list.size - 1
        adapter.notifyItemInserted(lastIndex)
        recyclerView.smoothScrollToPosition(lastIndex)
    }

    fun selectionToolbar() = Toolbar(ContextThemeWrapper(context, R.style.ThemeOverlay_AppCompat_Dark_ActionBar)).apply {
        setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
        backgroundColor = color(R.color.dark)
        navigationIcon = drawable(R.drawable.ic_arrow_back, color(R.color.onSecondary))
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

    fun toolbar() = FrameLayout(context).apply {
        child(params(matchParent, wrapContent), detailsToolbar(context, R.string.settingsUIThemeSaved, model).apply {
            menu.add(R.string.settingsUIThemeSavedAdd).apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                icon = drawable(R.drawable.ic_plus, color(R.color.onBackground))
                onClick { add() }
            }
            autorun {
                isVisible = adapter.selectedPositions.isEmpty()
            }
        })
        child(params(matchParent, wrapContent), selectionToolbar().apply {
            autorun {
                isVisible = adapter.selectedPositions.isNotEmpty()
            }
        })
    }

    return details(
            context,
            toolbar(),
            recyclerView
    )
}

class ThemeSavedItemView(context: Context) : FrameLayout(context), Bindable<SavedTheme> {
    private val backgroundBitmap = SettingBitmapView(context, size = 64)
    private val backgroundColor = View(context)
    private val letter = TextView(context).apply {
        textSize = dipFloat(16F)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        text = "A"
    }

    init {
        padding = dip(12)
        child(params(wrapContent, wrapContent, gravity = Gravity.CENTER), backgroundBitmap)
        child(params(dip(64), dip(64), gravity = Gravity.CENTER), backgroundColor)
        child(params(wrapContent, wrapContent, gravity = Gravity.CENTER), letter)
    }

    override fun bind(model: SavedTheme) {
        val settings = themeSettings(model)
        backgroundBitmap.isVisible = settings.backgroundIsImage
        backgroundBitmap.bind(if (settings.backgroundIsImage) settings.backgroundPath else "")
        backgroundColor.isVisible = !settings.backgroundIsImage
        backgroundColor.backgroundColor = settings.backgroundColor
        letter.textColor = settings.textColor
    }
}
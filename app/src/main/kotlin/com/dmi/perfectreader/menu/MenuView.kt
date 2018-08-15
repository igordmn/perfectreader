package com.dmi.perfectreader.menu

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import com.dmi.util.lang.intRound
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.jetbrains.anko.*

fun Context.menuView(model: Menu): View {
    fun DiscreteSeekBar.progress() = intRound(model.percent * max)

    fun DiscreteSeekBar.onProgressChangeListener(): DiscreteSeekBar.OnProgressChangeListener {
        return object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    model.goPercent(progress.toDouble() / max)
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) = Unit
        }
    }

    fun DiscreteSeekBar.numericTransformer(): DiscreteSeekBar.NumericTransformer {
        return object : DiscreteSeekBar.NumericTransformer() {
            override fun transform(value: Int): Int {
                val percent = value.toFloat() / max
                return intRound(percent * 100)
            }
        }
    }

    fun top() = view(::LinearLayoutCompat) {
        isClickable = true
        isFocusable = true
        orientation = LinearLayoutCompat.VERTICAL
        backgroundResource = R.color.white
        elevation = dipFloat(4F)

        child(::Toolbar, params(matchParent, wrapContent)) {
            navigationIcon = drawable(R.drawable.ic_arrow_back)
            popupTheme = R.style.Theme_AppCompat_Light
            title = "Alice's Adventures in Wonderland"
            subtitle = "Lewis Carroll"
            menu.add(R.string.bookMenuSettings).apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                icon = drawable(R.drawable.ic_settings, color(R.color.icon_dark))
                onClick {
                    model.showSettings()
                }
            }
        }

        child(::RelativeLayout, params(matchParent, dip(48))) {
            child(::TextView, params(matchParent, wrapContent)) {
                textColor = R.color.text_primary_dark
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                TextViewCompat.setTextAppearance(this, attr(android.R.attr.textAppearanceSmall).data)
                text = "X — Alice's evidence"
            }
            child(::TextView, params(wrapContent, wrapContent)) {
                textColor = R.color.text_primary_dark
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                autorun {
                    @SuppressLint("SetTextI18n")
                    text = "${model.pageNumber} / ${model.numberOfPages}"
                }
            }
        }
    }

    fun middle() = view(::FrameLayout) {
        backgroundResource = android.R.color.transparent
        isClickable = true
        isFocusable = true
        onClick { model.close() }
    }

    fun bottom() = view(::LinearLayoutCompat) {
        orientation = LinearLayoutCompat.VERTICAL
        backgroundResource = android.R.color.white
        elevation = dipFloat(8F)
        isClickable = true
        isFocusable = true

        child(::DiscreteSeekBar, params(
                matchParent, matchParent,
                leftMargin = dip(4),
                topMargin = dip(4),
                rightMargin = dip(4),
                bottomMargin = dip(4)
        )) {
            min = 0
            max = 100
            setIndicatorFormatter("%d%%")
            autorun {
                progress = progress()
            }
            numericTransformer = numericTransformer()
            setOnProgressChangeListener(onProgressChangeListener())
        }
        child(::LinearLayoutCompat, params(matchParent, dip(48))) {
            child(::AppCompatImageButton, params(dip(0), matchParent, weight = 1F)) {
                backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                contentDescription = string(R.string.bookMenuSearch)
                image = drawable(R.drawable.ic_search, color(R.color.icon_dark))
                onLongClick { showHint(); true }
            }
            child(::AppCompatImageButton, params(dip(0), matchParent, weight = 1F)) {
                backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                contentDescription = string(R.string.bookMenuSwitchTheme)
                image = drawable(R.drawable.ic_style, color(R.color.icon_dark))
                onLongClick { showHint(); true }
            }
            child(::AppCompatImageButton, params(dip(0), matchParent, weight = 1F)) {
                backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                contentDescription = string(R.string.bookMenuAutoScroll)
                image = drawable(R.drawable.ic_slideshow, color(R.color.icon_dark))
                onLongClick { showHint(); true }
            }
            child(::AppCompatImageButton, params(dip(0), matchParent, weight = 1F)) {
                backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                contentDescription = string(R.string.bookMenuTextToSpeech)
                image = drawable(R.drawable.ic_volume_up, color(R.color.icon_dark))
                onLongClick { showHint(); true }
            }
            child(::AppCompatImageButton, params(dip(0), matchParent, weight = 1F)) {
                backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                contentDescription = string(R.string.bookMenuAddBookmark)
                image = drawable(R.drawable.ic_bookmark_border, color(R.color.icon_dark))
                onLongClick { showHint(); true }
            }
        }
    }

    return view(::LinearLayoutExt) {
        orientation = LinearLayoutCompat.VERTICAL

        child(top(), params(matchParent, wrapContent, weight = 0F))
        child(middle(), params(matchParent, wrapContent, weight = 1F))
        child(bottom(), params(matchParent, wrapContent, weight = 0F))

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.close(); true }
        onInterceptKeyDown(KeyEvent.KEYCODE_MENU) { model.close(); true }
    }
}
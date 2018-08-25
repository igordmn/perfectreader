package com.dmi.perfectreader.menu

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.InputFilter
import android.view.Gravity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.TooltipCompat
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
        orientation = LinearLayoutCompat.VERTICAL
        backgroundColor = color(R.color.background)
        elevation = dipFloat(4F)

        child(::Toolbar, params(matchParent, wrapContent)) {
            setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
            backgroundColor = color(android.R.color.transparent)
            navigationIcon = drawable(R.drawable.ic_arrow_back)
            popupTheme = R.style.Theme_AppCompat_Light
            title = "Alice's Adventures in Wonderland"
            menu.add(R.string.bookMenuSettings).apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                icon = drawable(R.drawable.ic_settings, color(R.color.onBackground))
                onClick {
                    model.showSettings()
                }
            }
        }

        child(::LinearLayoutCompat, params(matchParent, wrapContent)) {
            child(::TextView, params(matchParent, wrapContent, weight = 1F)) {
                isClickable = true
                isFocusable = true
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dip(16), 0, dip(8), dip(16))
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Subtitle2)
                textColor = color(R.color.onBackground).withTransparency(0.60)
                text = "X — Alice's evidence"

                onClick {
                    toast("Show table of contents")
                }
            }

            val pageNumber = child(::EditNumber, params(wrapContent, wrapContent, weight = 0F)) {
                setPadding(dip(8), 0, dip(0), dip(16))
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Subtitle2)
                textColor = color(R.color.onBackground).withTransparency(0.60)
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                gravity = Gravity.END

                beforeEdit {
                    filters = arrayOf(InputFilter.LengthFilter(model.numberOfPages.toString().length))
                    min = 1
                    max = model.numberOfPages
                }

                afterChange {
                    model.goPageNumber(intValue)
                }

                autorun {
                    intValue = model.pageNumber
                }
            }

            child(::TextView, params(wrapContent, wrapContent, weight = 0F)) {
                setPadding(0, 0, dip(16), dip(16))
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Subtitle2)
                textColor = color(R.color.onBackground).withTransparency(0.60)
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                autorun {
                    @SuppressLint("SetTextI18n")
                    text = " / ${model.numberOfPages}"
                }
                onClick {
                    pageNumber.requestFocus()
                }
            }
        }
    }

    fun middle() = view(::FrameLayout) {
        backgroundColor = color(android.R.color.transparent)
        isClickable = true
        isFocusable = true
        onClick { model.back() }
    }

    fun bottom() = view(::LinearLayoutCompat) {
        orientation = LinearLayoutCompat.VERTICAL
        backgroundColor = color(R.color.background)
        elevation = dipFloat(8F)

        child(::DiscreteSeekBar, params(
                matchParent, matchParent,
                leftMargin = dip(4),
                topMargin = dip(8),
                rightMargin = dip(4),
                bottomMargin = dip(0)
        )) {
            min = 0
            max = 100
            setIndicatorFormatter("%d%%")
            autorun {
                progress = progress()
            }
            setThumbColor(color(R.color.secondary), color(R.color.secondary))
            setTrackColor(color(R.color.secondary).withTransparency(0.38))
            setScrubberColor(color(R.color.secondary))
            numericTransformer = numericTransformer()
            setOnProgressChangeListener(onProgressChangeListener())
        }
        child(::LinearLayoutCompat, params(matchParent, dip(48))) {
            child(::AppCompatImageButton, params(dip(0), matchParent, weight = 1F)) {
                backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                contentDescription = string(R.string.bookMenuSearch)
                image = drawable(R.drawable.ic_search, color(R.color.onBackground))
                TooltipCompat.setTooltipText(this, contentDescription)
            }
            child(::AppCompatImageButton, params(dip(0), matchParent, weight = 1F)) {
                backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                contentDescription = string(R.string.bookMenuSwitchTheme)
                image = drawable(R.drawable.ic_style, color(R.color.onBackground))
                TooltipCompat.setTooltipText(this, contentDescription)
            }
            child(::AppCompatImageButton, params(dip(0), matchParent, weight = 1F)) {
                backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                contentDescription = string(R.string.bookMenuAutoScroll)
                image = drawable(R.drawable.ic_slideshow, color(R.color.onBackground))
                TooltipCompat.setTooltipText(this, contentDescription)
            }
            child(::AppCompatImageButton, params(dip(0), matchParent, weight = 1F)) {
                backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                contentDescription = string(R.string.bookMenuTextToSpeech)
                image = drawable(R.drawable.ic_volume_up, color(R.color.onBackground))
                TooltipCompat.setTooltipText(this, contentDescription)
            }
            child(::AppCompatImageButton, params(dip(0), matchParent, weight = 1F)) {
                backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                contentDescription = string(R.string.bookMenuAddBookmark)
                image = drawable(R.drawable.ic_bookmark_border, color(R.color.onBackground))
                TooltipCompat.setTooltipText(this, contentDescription)
            }
        }
    }

    return view(::LinearLayoutExt) {
        orientation = LinearLayoutCompat.VERTICAL

        child(top(), params(matchParent, wrapContent, weight = 0F))
        child(middle(), params(matchParent, wrapContent, weight = 1F))
        child(bottom(), params(matchParent, wrapContent, weight = 0F))

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.back(); true }
        onInterceptKeyDown(KeyEvent.KEYCODE_MENU) { model.back(); true }
    }
}
package com.dmi.perfectreader.ui.menu

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.Gravity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import com.dmi.util.lang.intRound
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

fun ViewBuild.menuView(model: Menu): View {
    val book = model.book

    fun DiscreteSeekBar.progress() = intRound(book.percent * max)

    fun DiscreteSeekBar.onProgressChangeListener(): DiscreteSeekBar.OnProgressChangeListener {
        return object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    book.goPercent(progress.toDouble() / max)
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

    fun top() = VerticalLayout {
        backgroundColor = color(R.color.background)
        elevation = dipFloat(4F)

        Toolbar {
            setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
            backgroundColor = color(android.R.color.transparent)
            navigationIcon = drawable(R.drawable.ic_arrow_left)
            title = book.description.name ?: book.description.fileName
            contentInsetStartWithNavigation = 0
            setNavigationOnClickListener {
                model.showLibrary()
            }
            menu.add(R.string.menuSearch).apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                icon = drawable(R.drawable.ic_search, color(R.color.onBackground))
                onClick {
                    model.showSearch()
                }
            }
            menu.add(R.string.menuSettings).apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                icon = drawable(R.drawable.ic_settings, color(R.color.onBackground))
                onClick {
                    model.showSettings()
                }
            }
        } into container(matchParent, wrapContent)

        LinearLayoutCompat {
            orientation = LinearLayoutCompat.HORIZONTAL

            AppCompatTextView {
                isClickable = true
                isFocusable = true
                setPadding(dip(16), dip(16), 0, dip(16))
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Subtitle2)
                textColor = color(R.color.onBackground).withOpacity(0.60)

                autorun {
                    text = model.locationText
                }

                if (book.tableOfContents != null) {
                    backgroundResource = attr(android.R.attr.selectableItemBackground).resourceId
                    onClick {
                        model.showTableOfContents()
                    }
                }
            } into container(matchParent, wrapContent, weight = 1F)

            val pageNumber = EditNumber {
                setPadding(dip(16), dip(16), 0, dip(16))
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Subtitle2)
                textColor = color(R.color.onBackground).withOpacity(0.60)
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                gravity = Gravity.END

                beforeEdit {
                    min = 1F
                    max = book.numberOfPages.toFloat()
                }

                afterChange {
                    book.goPageNumber(intValue)
                }

                autorun {
                    intValue = book.pageNumber
                }
            } into container(wrapContent, wrapContent, weight = 0F)

            AppCompatTextView {
                setPadding(0, dip(16), dip(16), dip(16))
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Subtitle2)
                textColor = color(R.color.onBackground).withOpacity(0.60)
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                autorun {
                    @SuppressLint("SetTextI18n")
                    text = " / ${book.numberOfPages}"
                }
                onClick {
                    pageNumber.requestFocus()
                }
            } into container(wrapContent, wrapContent, weight = 0F)
        } into container(matchParent, wrapContent, topMargin = -dip(8))
    }

    fun middle() = FrameLayout {
        backgroundColor = color(android.R.color.transparent)
        isClickable = true
        isFocusable = true
        onClick { model.back() }
    }

    fun bottom() = VerticalLayout {
        backgroundColor = color(R.color.background)
        elevation = dipFloat(8F)

        DiscreteSeekBar {
            min = 0
            max = 100
            setIndicatorFormatter("%d%%")
            autorun {
                progress = progress()
            }
            setThumbColor(color(R.color.secondary), color(R.color.secondary))
            setTrackColor(color(R.color.secondary).withOpacity(0.38))
            setScrubberColor(color(R.color.secondary))
            numericTransformer = numericTransformer()
            setOnProgressChangeListener(onProgressChangeListener())
        } into container(
                matchParent, matchParent,
                leftMargin = dip(4),
                topMargin = dip(12),
                rightMargin = dip(4),
                bottomMargin = dip(12)
        )
    }

    return VerticalLayoutExt {
        top() into container(matchParent, wrapContent, weight = 0F)
        middle() into container(matchParent, wrapContent, weight = 1F)
        bottom() into container(matchParent, wrapContent, weight = 0F)

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.back(); true }
        onInterceptKeyDown(KeyEvent.KEYCODE_MENU) { model.back(); true }
    }
}
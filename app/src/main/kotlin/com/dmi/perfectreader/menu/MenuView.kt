package com.dmi.perfectreader.menu

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.Gravity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import com.dmi.util.lang.intRound
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.jetbrains.anko.*

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

    fun top() = LinearLayoutCompat(context).apply {
        orientation = LinearLayoutCompat.VERTICAL
        backgroundColor = color(R.color.background)
        elevation = dipFloat(4F)

        child(params(matchParent, wrapContent), Toolbar(context).apply {
            setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
            backgroundColor = color(android.R.color.transparent)
            navigationIcon = drawable(R.drawable.ic_arrow_back)
            popupTheme = R.style.Theme_AppCompat_Light
            setNavigationOnClickListener {
                model.close()
            }
            menu.add(R.string.bookMenuSearch).apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                icon = drawable(R.drawable.ic_search, color(R.color.onBackground))
                onClick {
                    model.showSearch()
                }
            }
            menu.add(R.string.bookMenuSettings).apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                icon = drawable(R.drawable.ic_settings, color(R.color.onBackground))
                onClick {
                    model.showSettings()
                }
            }
        })

        child(params(matchParent, wrapContent, topMargin = -dip(12)), LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.HORIZONTAL

            child(params(matchParent, wrapContent, weight = 1F), TextView(context).apply {
                isClickable = true
                isFocusable = true
                setPadding(dip(16), dip(12), 0, dip(12))
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
            })

            val pageNumber = child(params(wrapContent, wrapContent, weight = 0F), EditNumber(context).apply {
                setPadding(dip(16), dip(12), 0, dip(12))
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
            })

            child(params(wrapContent, wrapContent, weight = 0F), TextView(context).apply {
                setPadding(0, dip(12), dip(16), dip(12))
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
            })
        })
    }

    fun middle() = FrameLayout(context).apply {
        backgroundColor = color(android.R.color.transparent)
        isClickable = true
        isFocusable = true
        onClick { model.back() }
    }

    fun bottom() = LinearLayoutCompat(context).apply {
        orientation = LinearLayoutCompat.VERTICAL
        backgroundColor = color(R.color.background)
        elevation = dipFloat(8F)

        child(params(
                matchParent, matchParent,
                leftMargin = dip(4),
                topMargin = dip(12),
                rightMargin = dip(4),
                bottomMargin = dip(12)
        ), DiscreteSeekBar(context).apply {
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
        })
    }

    return LinearLayoutExt(context).apply {
        orientation = LinearLayoutCompat.VERTICAL

        child(params(matchParent, wrapContent, weight = 0F), top())
        child(params(matchParent, wrapContent, weight = 1F), middle())
        child(params(matchParent, wrapContent, weight = 0F), bottom())

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.back(); true }
        onInterceptKeyDown(KeyEvent.KEYCODE_MENU) { model.back(); true }
    }
}
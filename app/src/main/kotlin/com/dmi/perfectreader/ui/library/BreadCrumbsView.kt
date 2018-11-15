package com.dmi.perfectreader.ui.library

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import com.dmi.util.collection.removeLast
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

fun ViewBuild.BreadCrumbsView(build: BreadCrumbsView.() -> Unit) = BreadCrumbsView(context).apply(build)

class BreadCrumbsView(context: Context) : HorizontalScrollView(context) {
    private val textViews = ArrayList<TextView>()
    private val dividers = ArrayList<View>()
    private val container = LinearLayoutCompat(context).apply {
        setPadding(dip(8), dip(0), dip(8), dip(0))
        orientation = LinearLayoutCompat.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    init {
        child(params(matchParent, matchParent), container)
        isHorizontalScrollBarEnabled = false
    }

    val size: Int get() = textViews.size

    var current: Int? = null
        set(value) {
            require(value == null || value in 0 until size)
            textViews.forEachIndexed { i, it ->
                it.textColor = if (i == value) color(R.color.secondary) else color(R.color.onBackground).withOpacity(0.38)
                if (i == value)
                    requestChildFocus(it, it)
            }
        }

    fun add(name: String, onClick: () -> Unit) {
        if (size > 0) {
            val divider = divider()
            dividers.add(divider)
            container.addView(divider)
        }

        val textView = TextView(context).apply {
            setPadding(dip(8), dip(16), dip(8), dip(16))
            text = name
            setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Overline)
            textColor = color(R.color.onBackground)
            backgroundResource = attr(R.attr.selectableItemBackground).resourceId
            this.onClick { onClick() }
        }
        textViews.add(textView)
        container.addView(textView)
    }

    fun remove() {
        require(size > 0)
        if (dividers.size > 0)
            container.removeView(dividers.removeLast())
        container.removeView(textViews.removeLast())
    }

    private fun divider() = ImageView(context).apply {
        image = drawable(R.drawable.ic_keyboard_arrow_right, color(R.color.onBackground).withOpacity(0.38))
    }
}
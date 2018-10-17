package com.dmi.perfectreader.search

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.TextViewCompat.setTextAppearance
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.book.content.TableOfContents
import com.dmi.util.android.system.hideSoftKeyboard
import com.dmi.util.android.view.*
import org.jetbrains.anko.*


fun searchUIView(
        context: Context,
        model: SearchUI
): View = LinearLayoutCompat(context).apply {
    val book = model.book

    fun content(): View = RecyclerView(context).apply {


        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
//        adapter = object : BindableViewAdapter<SearchResultView>() {
//            override fun getItemCount() = chapters.size
//            override fun view() = SearchResultView(context, model, book, currentIndex, chapters)
//        }
    }

    orientation = LinearLayoutCompat.VERTICAL
    backgroundColor = color(R.color.background)

    child(params(matchParent, wrapContent, weight = 0F), Toolbar(context).apply {
        setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
        backgroundColor = color(android.R.color.transparent)
        navigationIcon = drawable(R.drawable.ic_arrow_back)
        setContentInsetsRelative(0, dip(16))

        child(params(matchParent, wrapContent), SearchView(context).apply {
            queryHint = string(R.string.searchUIHint)
            setIconifiedByDefault(false)
            isIconified = false
            findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
                image = null
                layoutParams = LinearLayout.LayoutParams(0, 0)
            }
            inputType = InputType.TYPE_CLASS_TEXT

            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (!hasFocus)
                    hideSoftKeyboard()
            }

            requestFocus()
        })

        setNavigationOnClickListener {
            model.back()
        }
    })

    child(params(matchParent, matchParent, weight = 1F), content())
}

class SearchResultView(
        context: Context,
        private val model: SearchUI,
        private val book: Book,
        private val currentIndex: Int,
        private val chapters: List<TableOfContents.PlainChapter>
) : LinearLayoutCompat(context), Bindable<Int> {
    private val chapterView = TextView(context)
    private val pageView = TextView(context)

    init {
        layoutParams = params(matchParent, wrapContent)

        child(params(matchParent, wrapContent, Gravity.CENTER_VERTICAL, weight = 1F), chapterView)
        child(params(wrapContent, wrapContent, Gravity.END or Gravity.CENTER_VERTICAL, weight = 0F), pageView)

        orientation = LinearLayoutCompat.HORIZONTAL
        isClickable = true
        isFocusable = true
        backgroundResource = attr(android.R.attr.selectableItemBackground).resourceId

        setPadding(dip(16), dip(12), dip(16), dip(12))
    }

    @SuppressLint("SetTextI18n")
    override fun bind(model: Int) {
        val index = model
        val chapter = chapters[index]
        val pageNumber = book.pageNumberOf(chapter.original)
        val isCurrent = index == currentIndex

        val style = when (chapter.level) {
            0 -> R.style.TextAppearance_MaterialComponents_Body1
            else -> R.style.TextAppearance_MaterialComponents_Body1
        }
        setTextAppearance(chapterView, style)
        setTextAppearance(pageView, style)
        chapterView.text = "    ".repeat(chapter.level) + chapter.original.name
        pageView.text = pageNumber.toString()
        chapterView.textColor = color(R.color.onBackground)
        pageView.textColor = color(R.color.onBackground)

        if (isCurrent) {
            chapterView.typeface = Typeface.create(chapterView.typeface, Typeface.BOLD)
            pageView.typeface = Typeface.create(pageView.typeface, Typeface.BOLD)
        }

        onClick {
            goChapter(chapter)
        }
    }

    private fun goChapter(chapter: TableOfContents.PlainChapter) {
        model.back()
        book.goChapter(chapter.original)
    }
}
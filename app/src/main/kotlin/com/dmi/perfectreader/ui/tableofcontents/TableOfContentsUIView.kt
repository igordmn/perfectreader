package com.dmi.perfectreader.ui.tableofcontents

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.TextViewCompat.setTextAppearance
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.book.content.TableOfContents
import com.dmi.perfectreader.ui.book.Book
import com.dmi.util.android.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

fun ViewBuild.tableOfContentsUIView(
        model: TableOfContentsUI
): View = VerticalLayout {
    val book = model.book

    fun content(): View = RecyclerView(context, null, R.attr.verticalRecyclerViewStyle).apply {
        val tableOfContents = book.tableOfContents!!
        val chapters = tableOfContents.allChapters
        val currentChapter = book.chapter
        val currentIndex = chapters.indexOfFirst { it.original == currentChapter }

        setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        this.layoutManager = layoutManager
        adapter = object : BindableViewAdapter<ChapterView>() {
            override fun getItemCount() = chapters.size
            override fun view(viewType: Int) = ChapterView(context, model, book, currentIndex, chapters)
        }

        if (currentIndex >= 0)
            layoutManager.scrollToPositionWithOffset(currentIndex, dip(80))
    }

    backgroundColor = color(R.color.background)

    Toolbar {
        val description = book.description
        setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
        backgroundColor = color(android.R.color.transparent)
        navigationIcon = drawable(R.drawable.ic_arrow_left)
        this.title = description.name ?: ""
        this.subtitle = description.author ?: ""

        setNavigationOnClickListener {
            model.back()
        }
    } into container(matchParent, wrapContent, weight = 0F)

    content() into container(matchParent, matchParent, weight = 1F)
}

class ChapterView(
        context: Context,
        private val model: TableOfContentsUI,
        private val book: Book,
        private val currentIndex: Int,
        private val chapters: List<TableOfContents.PlainChapter>
) : LinearLayoutCompat(context), Bindable<Int> {
    private val chapterView = AppCompatTextView(context)
    private val pageView = AppCompatTextView(context)

    init {
        layoutParams = LinearLayoutCompat.LayoutParams(matchParent, wrapContent)

        chapterView into container(matchParent, wrapContent, Gravity.TOP, weight = 1F)
        pageView into container(wrapContent, wrapContent, Gravity.END or Gravity.TOP, weight = 0F)

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
        chapterView.text = chapter.original.name
        chapterView.setPadding(dip(16) * chapter.level, dip(0), dip(16), dip(0))
        pageView.text = pageNumber.toString()
        chapterView.textColor = color(R.color.onBackground)
        pageView.textColor = color(R.color.onBackground)

        if (isCurrent) {
            chapterView.typeface = Typeface.create(chapterView.typeface, Typeface.BOLD)
            pageView.typeface = Typeface.create(pageView.typeface, Typeface.BOLD)
            chapterView.textColor = color(R.color.secondary)
            pageView.textColor = color(R.color.secondary)
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
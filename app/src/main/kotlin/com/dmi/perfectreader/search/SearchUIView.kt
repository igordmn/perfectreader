package com.dmi.perfectreader.search

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.InputFilter
import android.text.InputType
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.core.widget.TextViewCompat.setTextAppearance
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.util.android.system.hideSoftKeyboard
import com.dmi.util.android.view.*
import com.rubengees.easyheaderfooteradapter.EasyHeaderFooterAdapter
import org.jetbrains.anko.*

fun searchUIView(
        context: Context,
        model: SearchUI
): View = LinearLayoutCompat(context).apply {
    val book = model.book

    fun results(): View = RecyclerView(context).apply {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        val adapter = EasyHeaderFooterAdapter(object : BindableViewAdapter<SearchResultView>() {
            override fun getItemCount() = model.results?.list?.size ?: 0
            override fun view() = SearchResultView(context, model, book) { model.results!!.list[it] }
        }).apply {
            header = TextView(context).apply {
                setPadding(dip(16), dip(12), dip(16), dip(12))
                layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
                textColor = color(R.color.onBackground).withOpacity(0.60)
                autorun {
                    val results = model.results
                    text = if (results != null) {
                        val res = if (results.isOverMax) R.string.searchResultsOverMaxInfo else R.string.searchResultsInfo
                        string(res, results.list.size)
                    } else {
                        ""
                    }
                }
            }
        }
        this.adapter = adapter
        autorun {
            val modelResults = model.results
            isVisible = modelResults != null && modelResults.list.isNotEmpty()
            adapter.notifyDataSetChanged()
        }
    }

    fun progress(): View = ProgressBar(context).apply {
        padding = dip(16)
        autorun {
            isVisible = model.isLoading
        }
    }

    fun noResults(): View = TextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
        gravity = Gravity.CENTER
        padding = dip(16)
        textColor = color(R.color.onBackground).withOpacity(0.60)
        text = string(R.string.searchNoResultsFound)

        autorun {
            val modelResults = model.results
            isVisible = modelResults != null && modelResults.list.isEmpty()
        }
    }

    orientation = LinearLayoutCompat.VERTICAL
    backgroundColor = color(R.color.background)

    child(params(matchParent, wrapContent, weight = 0F), Toolbar(context).apply {
        setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
        backgroundColor = color(android.R.color.transparent)
        navigationIcon = drawable(R.drawable.ic_arrow_back)
        setContentInsetsRelative(0, dip(16))

        child(params(matchParent, wrapContent), SearchView(context).apply {
            queryHint = string(R.string.searchHint)
            setIconifiedByDefault(false)
            isIconified = false
            findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
                image = null
                layoutParams = LinearLayout.LayoutParams(0, 0)
            }
            findViewById<TextView>(R.id.search_src_text).filters = arrayOf<InputFilter>(InputFilter.LengthFilter(model.searchQueryMaxLength))
            inputType = InputType.TYPE_CLASS_TEXT

            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (!hasFocus)
                    hideSoftKeyboard()
            }

            setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    model.searchQuery = query
                    hideSoftKeyboard()
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean = false
            })

            requestFocus()
        })

        setNavigationOnClickListener {
            model.back()
        }
    })

    child(params(matchParent, matchParent, weight = 1F), FrameLayout(context).apply {
        child(params(matchParent, matchParent), results())
        child(params(wrapContent, wrapContent, Gravity.CENTER_HORIZONTAL), progress())
        child(params(matchParent, wrapContent), noResults())
    })
}

class SearchResultView(
        context: Context,
        private val model: SearchUI,
        private val book: Book,
        private val getResult: (index: Int) -> SearchUI.Result
) : LinearLayoutCompat(context), Bindable<Int> {
    private val textView = TextView(context).apply {
        setPadding(dip(0), dip(0), dip(16), dip(0))
    }
    private val pageView = TextView(context)

    init {
        layoutParams = params(matchParent, wrapContent)

        child(params(matchParent, wrapContent, Gravity.TOP, weight = 1F), textView)
        child(params(wrapContent, wrapContent, Gravity.END or Gravity.TOP, weight = 0F), pageView)

        orientation = LinearLayoutCompat.HORIZONTAL
        isClickable = true
        isFocusable = true
        backgroundResource = attr(android.R.attr.selectableItemBackground).resourceId

        setPadding(dip(16), dip(12), dip(16), dip(12))
    }

    @SuppressLint("SetTextI18n")
    override fun bind(model: Int) {
        val index = model
        val result = getResult(index)
        val pageNumber = book.pageNumberOf(result.range.start)

        setTextAppearance(textView, R.style.TextAppearance_MaterialComponents_Body1)
        setTextAppearance(pageView, R.style.TextAppearance_MaterialComponents_Body1)

        val text = SpannableString(result.adjacentText.text)
        val queryStart = result.adjacentText.queryIndices.start
        val queryEnd = result.adjacentText.queryIndices.endInclusive + 1
        text.setSpan(StyleSpan(Typeface.BOLD), queryStart, queryEnd, 0)

        textView.text = text
        pageView.text = pageNumber.toString()
        textView.textColor = color(R.color.onBackground)
        pageView.textColor = color(R.color.onBackground)

        onClick {
            goLocation(result.range.start)
        }
    }

    private fun goLocation(location: Location) {
        model.back()
        book.goLocation(location)
    }
}
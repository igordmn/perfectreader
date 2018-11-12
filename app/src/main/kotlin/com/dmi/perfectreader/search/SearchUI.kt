package com.dmi.perfectreader.search

import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.book.content.ContentText
import com.dmi.perfectreader.book.content.continuousTexts
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observableProperty
import com.dmi.util.screen.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class SearchUI(
        val book: Book,
        val back: () -> Unit,
        val state: SearchUIState,
        private val scope: Scope = Scope()
) : Screen by Screen(scope) {
    var searchQuery: String by observableProperty(state::searchQuery)
    val searchQueryMaxLength = 200
    private val maxResults = 10000

    val results: Results? by scope.async {
        val searchQuery = searchQuery
        val maxResults = maxResults
        require(searchQuery.length <= searchQueryMaxLength)

        return@async if (searchQuery.isEmpty()) {
            null
        } else {
            search(book.text, searchQuery, maxResults)
        }
    }

    val isLoading get() = searchQuery.isNotEmpty() && results == null

    private suspend fun search(contentText: ContentText, query: String, maxResults: Int): Results = withContext(Dispatchers.Default) {
        val results = Results.Builder(maxResults)

        contentText.continuousTexts { text ->
            val textStr = text.toString()
            textStr.indicesOf(query, ignoreCase = true).forEach {
                results += Result(
                        adjacentText(textStr, it),
                        text.rangeOf(it)
                )
                if (results.isOverMax)
                    return@continuousTexts
            }
        }

        return@withContext results.build()
    }

    class Results(val list: List<Result>, val isOverMax: Boolean) {
        class Builder(val max: Int) {
            var isOverMax = false
                private set
            private val list = ArrayList<Result>()

            operator fun plusAssign(result: Result) {
                if (list.size < max) {
                    list.add(result)
                } else {
                    isOverMax = true
                }
            }

            fun build() = Results(list, isOverMax)
        }
    }

    class Result(val adjacentText: AdjacentText, val range: LocationRange)
}

typealias Indices = IntRange

fun String.indicesOf(query: String, ignoreCase: Boolean = true): Sequence<Indices> {
    require(query.isNotEmpty())
    return sequence {
        var index = indexOf(query, 0, ignoreCase)
        while (index >= 0) {
            val indices = index until index + query.length
            yield(indices)
            index = indexOf(query, indices.start + 1, ignoreCase)
        }
    }
}

fun adjacentText(fullText: String, queryIndices: IntRange, distance: Int = 20): AdjacentText {
    val queryStart = queryIndices.start
    val queryEnd = queryIndices.endInclusive + 1
    val querySize = queryEnd - queryStart

    val start = (queryStart - distance).coerceAtLeast(0)
    val end = (queryEnd + distance).coerceAtMost(fullText.length)

    val str = StringBuilder()
    if (start > 0)
        str.append('…')

    str.append(fullText, start, queryStart)
    str.append(fullText, queryStart, queryEnd)
    str.append(fullText, queryEnd, end)

    if (end < fullText.length)
        str.append('…')

    val prefixSize = (if (start > 0) 1 else 0) + (queryStart - start)

    return AdjacentText(str.toString(), prefixSize until prefixSize + querySize)
}

data class AdjacentText(val text: String, val queryIndices: IntRange) {
    init {
        require(queryIndices.start in text.indices)
        require(queryIndices.endInclusive in text.indices)
    }
}

@Serializable
data class SearchUIState(var searchQuery: String = "")
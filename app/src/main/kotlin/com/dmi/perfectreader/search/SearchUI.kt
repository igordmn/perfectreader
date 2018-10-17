package com.dmi.perfectreader.search

import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observable
import com.dmi.util.screen.Screen
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

class SearchUI(
        val book: Book,
        val back: () -> Unit,
        val state: SearchUIState,
        private val scope: Scope = Scope()
) : Screen by Screen(scope) {
    var searchQuery: String by observable("")

    private val _results = ArrayList<Result>()

    val results: List<Result>? by scope.async(resetOnRecompute = true) {
        if (searchQuery.isEmpty())
            return@async null

        _results.clear()

        delay(2000)

        _results.add(Result("Hahahaahaha", LocationRange(Location(1000.0), Location(1200.0))))

        _results
    }

    val isLoading get() = searchQuery.isNotEmpty() && results == null

    class Result(val adjacentText: String, val range: LocationRange)
}

@Serializable
class SearchUIState
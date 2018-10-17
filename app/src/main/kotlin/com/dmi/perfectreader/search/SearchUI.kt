package com.dmi.perfectreader.search

import com.dmi.perfectreader.book.Book
import com.dmi.util.screen.Screen
import kotlinx.serialization.Serializable

class SearchUI(
        val book: Book,
        val back: () -> Unit,
        val state: SearchUIState
) : Screen by Screen()

@Serializable
class SearchUIState
package com.dmi.perfectreader.tableofcontentsui

import com.dmi.perfectreader.book.Book
import com.dmi.util.screen.Screen
import kotlinx.serialization.Serializable

class TableOfContentsUI(
        val book: Book,
        val back: () -> Unit,
        val state: TableOfContentsUIState
) : Screen by Screen()

@Serializable
class TableOfContentsUIState
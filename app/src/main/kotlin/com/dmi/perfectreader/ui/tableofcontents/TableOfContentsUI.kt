package com.dmi.perfectreader.ui.tableofcontents

import com.dmi.perfectreader.ui.book.Book
import com.dmi.util.screen.Screen
import kotlinx.serialization.Serializable

class TableOfContentsUI(
        val book: Book,
        val back: () -> Unit,
        val state: TableOfContentsUIState
) : Screen by Screen()

@Serializable
class TableOfContentsUIState
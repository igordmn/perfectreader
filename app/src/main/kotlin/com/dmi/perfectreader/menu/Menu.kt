package com.dmi.perfectreader.menu

import com.dmi.perfectreader.book.Book
import com.dmi.util.screen.Screen
import kotlinx.serialization.Serializable

class Menu(
        val book: Book,
        val showSettings: () -> Unit,
        val showTableOfContents: () -> Unit,
        val showSearch: () -> Unit,
        val back: () -> Unit,
        val close: () -> Unit,
        val state: MenuState
) : Screen by Screen() {
    val locationText: String get() = if (book.pageNumber == 0) bookName else chapterName
    private val bookName: String get() = book.description.name ?: book.description.fileName
    private val chapterName: String get() = book.chapter?.name ?: bookName
}

@Serializable
class MenuState
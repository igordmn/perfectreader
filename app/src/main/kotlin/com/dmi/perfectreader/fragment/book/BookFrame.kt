package com.dmi.perfectreader.fragment.book

import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import java.util.*

/**
 * Кадр состояния книги, используемый в GL потоке для рисования
 */
class BookFrame {
    val loadedPages = LinkedHashSet<Page>()
    val visibleSlides = ArrayList<AnimatedBook.Slide>()
    lateinit var pageContext: PageContext
}
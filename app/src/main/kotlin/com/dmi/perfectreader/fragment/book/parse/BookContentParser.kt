package com.dmi.perfectreader.fragment.book.parse

import com.dmi.perfectreader.fragment.book.content.BookContent

interface BookContentParser {
    fun parse(): BookContent
}
package com.dmi.perfectreader.book.parse

import com.dmi.perfectreader.book.content.Content

interface BookContentParser {
    fun parse(): Content
}
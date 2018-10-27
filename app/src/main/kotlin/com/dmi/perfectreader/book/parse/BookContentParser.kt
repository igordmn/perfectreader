package com.dmi.perfectreader.book.parse

import com.dmi.perfectreader.book.content.Content

interface BookContentParser {
    // todo suspend
    fun parse(): Content
}
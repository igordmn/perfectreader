package com.dmi.perfectreader.fragment.book.parse

import com.dmi.perfectreader.fragment.book.content.Content

interface BookContentParser {
    fun parse(): Content
}
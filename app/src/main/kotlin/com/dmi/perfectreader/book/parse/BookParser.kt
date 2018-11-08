package com.dmi.perfectreader.book.parse

import com.dmi.perfectreader.book.content.BookDescription
import com.dmi.perfectreader.book.content.Content

interface BookParser {
    suspend fun content(): Content
    suspend fun description(): BookDescription
    suspend fun descriptionOnFail(): BookDescription
}
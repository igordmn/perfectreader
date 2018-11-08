package com.dmi.perfectreader.book.content

import com.google.common.io.ByteSource

class BookDescription(
        val author: String?,
        val name: String?,
        val fileName: String,
        val cover: ByteSource?
)
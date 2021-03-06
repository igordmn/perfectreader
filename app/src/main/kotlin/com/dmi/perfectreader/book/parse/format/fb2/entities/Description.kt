package com.dmi.perfectreader.book.parse.format.fb2.entities

import com.dmi.util.xml.ElementDesc

class Author : ElementDesc() {
    private val firstName: String? by element("first-name")
    private val lastName: String? by element("last-name")

    fun fullName(): String {
        val firstName = firstName?.trim() ?: ""
        val lastName = lastName?.trim() ?: ""
        return when {
            firstName.isEmpty() -> lastName
            lastName.isEmpty() -> firstName
            else -> "$firstName $lastName"
        }
    }
}

class CoverPage : ElementDesc() {
    val image: Image? by element("image", ::Image)
}

class TitleInfo : ElementDesc() {
    private val authors: List<Author> by elements("author", ::Author)
    val bookTitle: String? by element("book-title")
    val coverpage: CoverPage? by element("coverpage", ::CoverPage)
    val lang: String? by element("lang")

    fun compositeAuthorName(): String? {
        val result = authors.joinToString(", ") { it.fullName() }
        return if (result.isNotEmpty()) result else null
    }

    fun bookTitleFormatted(): String? {
        val trimmed = bookTitle?.trim()
        return if (trimmed != null && trimmed.isNotEmpty()) trimmed else null
    }
}

class Description : ElementDesc() {
    val titleInfo: TitleInfo? by element("title-info", ::TitleInfo)
}
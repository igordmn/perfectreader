package com.dmi.perfectreader.book.content.obj.common

enum class ContentClass {
    STRONG,
    EMPHASIS,
    H0, H1, H2, H3, H4, H5,
    CODE_BLOCK,
    CODE_LINE,
    POEM_STANZA,
    POEM_LINE,
    EPIGRAPH,
    AUTHOR;

    companion object {
        fun H(level: Int) = when (level) {
            0 -> ContentClass.H0
            1 -> ContentClass.H1
            2 -> ContentClass.H2
            3 -> ContentClass.H3
            4 -> ContentClass.H4
            else -> ContentClass.H5
        }
    }
}

data class ContentCompositeClass(val parent: ContentCompositeClass?, val cls: ContentClass)
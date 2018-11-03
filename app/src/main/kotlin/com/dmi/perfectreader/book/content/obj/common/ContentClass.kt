package com.dmi.perfectreader.book.content.obj.common

enum class ContentClass {
    STRONG,
    EMPHASIS,
    H0, H1, H2, H3, H4, H5,
    H0_BLOCK, H1_BLOCK, H2_BLOCK, H3_BLOCK, H4_BLOCK, H5_BLOCK,
    CODE_BLOCK,
    CODE_LINE,
    POEM_STANZA,
    POEM_LINE,
    EPIGRAPH,
    IMAGE_BLOCK,
    AUTHOR,
    FOOTER;

    companion object {
        fun H(level: Int) = when (level) {
            0 -> ContentClass.H0
            1 -> ContentClass.H1
            2 -> ContentClass.H2
            3 -> ContentClass.H3
            4 -> ContentClass.H4
            else -> ContentClass.H5
        }

        fun H_BLOCK(level: Int) = when (level) {
            0 -> ContentClass.H0_BLOCK
            1 -> ContentClass.H1_BLOCK
            2 -> ContentClass.H2_BLOCK
            3 -> ContentClass.H3_BLOCK
            4 -> ContentClass.H4_BLOCK
            else -> ContentClass.H5_BLOCK
        }
    }
}

data class ContentCompositeClass(val parent: ContentCompositeClass?, val cls: ContentClass)
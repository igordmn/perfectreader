package com.dmi.perfectreader.fragment.book.content

import com.dmi.perfectreader.fragment.book.content.obj.ContentObject
import com.dmi.perfectreader.fragment.book.content.obj.ContentParagraph
import com.dmi.perfectreader.fragment.book.content.obj.param.ContentConfig
import com.dmi.perfectreader.fragment.book.content.obj.param.ContentFontStyle
import com.dmi.perfectreader.fragment.book.content.obj.param.StyleType
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.location
import com.dmi.perfectreader.range
import com.dmi.test.shouldEqual
import org.junit.Test

@Suppress("IllegalIdentifier")
class ContentWordsTest {
    @Test
    fun `find word bounds in single-word-single-run-paragraph`() {
        val par = paragraph(
                textRun("aaa", 0, 3)
        )

        par.wordBeginBefore(location(-100)) shouldEqual null
        par.wordBeginBefore(location(0)) shouldEqual null
        par.wordBeginBefore(location(1)) shouldEqual location(0)
        par.wordBeginBefore(location(100)) shouldEqual location(0)

        par.wordEndAfter(location(-100)) shouldEqual location(3)
        par.wordEndAfter(location(2)) shouldEqual location(3)
        par.wordEndAfter(location(3)) shouldEqual null
        par.wordEndAfter(location(100)) shouldEqual null
    }

    @Test
    fun `find word bounds in multiple-word-single-run-paragraph`() {
        val par = paragraph(
                //       0123456789012
                textRun("aa1, bbb ccc-", 0, 13)
        )

        par.wordBeginBefore(location(0)) shouldEqual null
        par.wordBeginBefore(location(3)) shouldEqual location(0)
        par.wordBeginBefore(location(4)) shouldEqual location(3)
        par.wordBeginBefore(location(5)) shouldEqual location(3)
        par.wordBeginBefore(location(6)) shouldEqual location(5)
        par.wordBeginBefore(location(9)) shouldEqual location(5)
        par.wordBeginBefore(location(10)) shouldEqual location(9)
        par.wordBeginBefore(location(12)) shouldEqual location(9)
        par.wordBeginBefore(location(13)) shouldEqual location(12)
        par.wordBeginBefore(location(100)) shouldEqual location(12)

        par.wordEndAfter(location(13)) shouldEqual null
        par.wordEndAfter(location(12)) shouldEqual location(13)
        par.wordEndAfter(location(11)) shouldEqual location(12)
        par.wordEndAfter(location(8)) shouldEqual location(12)
        par.wordEndAfter(location(7)) shouldEqual location(8)
        par.wordEndAfter(location(4)) shouldEqual location(8)
        par.wordEndAfter(location(3)) shouldEqual location(4)
        par.wordEndAfter(location(2)) shouldEqual location(3)
        par.wordEndAfter(location(-100)) shouldEqual location(3)
    }

    @Test
    fun `find word bounds in multiple-run-paragraph`() {
        val par = paragraph(
                // 0     0123456789012
                textRun("aaa, bbb ccc-", 0, 13),
                // 1     345
                textRun("aaa", 13, 16),
                // 1     67890
                textRun("-aaa ", 16, 21),
                // 2     123
                textRun("aaa", 21, 24),
                // 2     456
                textRun("aaa", 24, 27),
                // 2     7890
                textRun(" aaa", 27, 31),
                // 3     1
                objectRun(31, 32),
                // 3     2
                objectRun(32, 33),
                // 3     345
                textRun("aaa", 33, 36),
                // 3     6
                objectRun(36, 37)
        )

        par.wordBeginBefore(location(13)) shouldEqual location(12)
        par.wordBeginBefore(location(14)) shouldEqual location(13)
        par.wordBeginBefore(location(16)) shouldEqual location(13)
        par.wordBeginBefore(location(17)) shouldEqual location(16)
        par.wordBeginBefore(location(18)) shouldEqual location(17)
        par.wordBeginBefore(location(21)) shouldEqual location(17)
        par.wordBeginBefore(location(22)) shouldEqual location(21)
        par.wordBeginBefore(location(24)) shouldEqual location(21)
        par.wordBeginBefore(location(25)) shouldEqual location(21)
        par.wordBeginBefore(location(27)) shouldEqual location(21)
        par.wordBeginBefore(location(28)) shouldEqual location(21)
        par.wordBeginBefore(location(29)) shouldEqual location(28)
        par.wordBeginBefore(location(31)) shouldEqual location(28)
        par.wordBeginBefore(location(33)) shouldEqual location(28)
        par.wordBeginBefore(location(34)) shouldEqual location(33)
        par.wordBeginBefore(location(36)) shouldEqual location(33)
        par.wordBeginBefore(location(100)) shouldEqual location(33)

        par.wordEndAfter(location(36)) shouldEqual null
        par.wordEndAfter(location(35)) shouldEqual location(36)
        par.wordEndAfter(location(31)) shouldEqual location(36)
        par.wordEndAfter(location(30)) shouldEqual location(31)
        par.wordEndAfter(location(27)) shouldEqual location(31)
        par.wordEndAfter(location(26)) shouldEqual location(27)
        par.wordEndAfter(location(20)) shouldEqual location(27)
        par.wordEndAfter(location(19)) shouldEqual location(20)
        par.wordEndAfter(location(17)) shouldEqual location(20)
        par.wordEndAfter(location(16)) shouldEqual location(17)
        par.wordEndAfter(location(15)) shouldEqual location(16)
        par.wordEndAfter(location(13)) shouldEqual location(16)
        par.wordEndAfter(location(12)) shouldEqual location(13)
    }

    @Test
    fun `round requested locations to char indices`() {
        val par = paragraph(
                //       0123
                textRun("aaa ", 0, 4),
                //       456
                textRun("aaa", 4, 7),
                //       7890
                textRun("-aaa", 7, 11)
        )

        par.wordBeginBefore(Location(0.4)) shouldEqual null
        par.wordBeginBefore(Location(0.5)) shouldEqual location(0)
        par.wordBeginBefore(Location(4.4)) shouldEqual location(0)
        par.wordBeginBefore(Location(4.5)) shouldEqual location(4)
        par.wordBeginBefore(Location(7.4)) shouldEqual location(4)
        par.wordBeginBefore(Location(7.5)) shouldEqual location(7)
        par.wordBeginBefore(Location(8.4)) shouldEqual location(7)
        par.wordBeginBefore(Location(8.5)) shouldEqual location(8)

        par.wordEndAfter(Location(10.5)) shouldEqual null
        par.wordEndAfter(Location(10.4)) shouldEqual location(11)
        par.wordEndAfter(Location(7.5)) shouldEqual location(11)
        par.wordEndAfter(Location(7.4)) shouldEqual location(8)
        par.wordEndAfter(Location(6.5)) shouldEqual location(8)
        par.wordEndAfter(Location(6.4)) shouldEqual location(7)
        par.wordEndAfter(Location(2.5)) shouldEqual location(7)
        par.wordEndAfter(Location(2.4)) shouldEqual location(3)
    }

    fun paragraph(vararg runs: ContentParagraph.Run) =
            ContentParagraph(StyleType.NORMAL, null, runs.toList(), 0F, null, range(0, 1000))

    fun textRun(text: String, begin: Int, end: Int) =
            ContentParagraph.Run.Text(text, ContentFontStyle(null, null), range(begin, end))

    fun objectRun(begin: Int, end: Int) = ContentParagraph.Run.Object(object : ContentObject(range(begin, end)) {
        override val length = 0.0
        override fun configure(config: ContentConfig) = throw UnsupportedOperationException()
    })
}
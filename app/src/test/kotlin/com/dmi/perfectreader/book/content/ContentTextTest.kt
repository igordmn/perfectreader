package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.obj.ContentObject
import com.dmi.perfectreader.book.content.obj.ContentParagraph
import com.dmi.perfectreader.book.content.obj.common.ContentConfig
import com.dmi.perfectreader.location
import com.dmi.perfectreader.range
import com.dmi.test.shouldBe
import org.junit.Test

@Suppress("IllegalIdentifier")
class ContentTextTest {
    @Test
    fun `find word bounds in single-word-single-run-paragraph`() {
        val par = paragraph(
                textRun("aaa", 0, 3)
        )

        par.wordBeginBefore(location(-100)) shouldBe null
        par.wordBeginBefore(location(0)) shouldBe null
        par.wordBeginBefore(location(1)) shouldBe location(0)
        par.wordBeginBefore(location(100)) shouldBe location(0)

        par.wordEndAfter(location(-100)) shouldBe location(3)
        par.wordEndAfter(location(2)) shouldBe location(3)
        par.wordEndAfter(location(3)) shouldBe null
        par.wordEndAfter(location(100)) shouldBe null
    }

    @Test
    fun `find word bounds in multiple-word-single-run-paragraph`() {
        val par = paragraph(
                //       0123456789012
                textRun("aa1, bbb ccc-", 0, 13)
        )

        par.wordBeginBefore(location(0)) shouldBe null
        par.wordBeginBefore(location(3)) shouldBe location(0)
        par.wordBeginBefore(location(4)) shouldBe location(3)
        par.wordBeginBefore(location(5)) shouldBe location(3)
        par.wordBeginBefore(location(6)) shouldBe location(5)
        par.wordBeginBefore(location(9)) shouldBe location(5)
        par.wordBeginBefore(location(10)) shouldBe location(9)
        par.wordBeginBefore(location(12)) shouldBe location(9)
        par.wordBeginBefore(location(13)) shouldBe location(12)
        par.wordBeginBefore(location(100)) shouldBe location(12)

        par.wordEndAfter(location(13)) shouldBe null
        par.wordEndAfter(location(12)) shouldBe location(13)
        par.wordEndAfter(location(11)) shouldBe location(12)
        par.wordEndAfter(location(8)) shouldBe location(12)
        par.wordEndAfter(location(7)) shouldBe location(8)
        par.wordEndAfter(location(4)) shouldBe location(8)
        par.wordEndAfter(location(3)) shouldBe location(4)
        par.wordEndAfter(location(2)) shouldBe location(3)
        par.wordEndAfter(location(-100)) shouldBe location(3)
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

        par.wordBeginBefore(location(13)) shouldBe location(12)
        par.wordBeginBefore(location(14)) shouldBe location(13)
        par.wordBeginBefore(location(16)) shouldBe location(13)
        par.wordBeginBefore(location(17)) shouldBe location(16)
        par.wordBeginBefore(location(18)) shouldBe location(17)
        par.wordBeginBefore(location(21)) shouldBe location(17)
        par.wordBeginBefore(location(22)) shouldBe location(21)
        par.wordBeginBefore(location(24)) shouldBe location(21)
        par.wordBeginBefore(location(25)) shouldBe location(21)
        par.wordBeginBefore(location(27)) shouldBe location(21)
        par.wordBeginBefore(location(28)) shouldBe location(21)
        par.wordBeginBefore(location(29)) shouldBe location(28)
        par.wordBeginBefore(location(31)) shouldBe location(28)
        par.wordBeginBefore(location(33)) shouldBe location(28)
        par.wordBeginBefore(location(34)) shouldBe location(33)
        par.wordBeginBefore(location(36)) shouldBe location(33)
        par.wordBeginBefore(location(100)) shouldBe location(33)

        par.wordEndAfter(location(36)) shouldBe null
        par.wordEndAfter(location(35)) shouldBe location(36)
        par.wordEndAfter(location(31)) shouldBe location(36)
        par.wordEndAfter(location(30)) shouldBe location(31)
        par.wordEndAfter(location(27)) shouldBe location(31)
        par.wordEndAfter(location(26)) shouldBe location(27)
        par.wordEndAfter(location(20)) shouldBe location(27)
        par.wordEndAfter(location(19)) shouldBe location(20)
        par.wordEndAfter(location(17)) shouldBe location(20)
        par.wordEndAfter(location(16)) shouldBe location(17)
        par.wordEndAfter(location(15)) shouldBe location(16)
        par.wordEndAfter(location(13)) shouldBe location(16)
        par.wordEndAfter(location(12)) shouldBe location(13)
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

        par.wordBeginBefore(Location(0.4)) shouldBe null
        par.wordBeginBefore(Location(0.5)) shouldBe location(0)
        par.wordBeginBefore(Location(4.4)) shouldBe location(0)
        par.wordBeginBefore(Location(4.5)) shouldBe location(4)
        par.wordBeginBefore(Location(7.4)) shouldBe location(4)
        par.wordBeginBefore(Location(7.5)) shouldBe location(7)
        par.wordBeginBefore(Location(8.4)) shouldBe location(7)
        par.wordBeginBefore(Location(8.5)) shouldBe location(8)

        par.wordEndAfter(Location(10.5)) shouldBe null
        par.wordEndAfter(Location(10.4)) shouldBe location(11)
        par.wordEndAfter(Location(7.5)) shouldBe location(11)
        par.wordEndAfter(Location(7.4)) shouldBe location(8)
        par.wordEndAfter(Location(6.5)) shouldBe location(8)
        par.wordEndAfter(Location(6.4)) shouldBe location(7)
        par.wordEndAfter(Location(2.5)) shouldBe location(7)
        par.wordEndAfter(Location(2.4)) shouldBe location(3)
    }

    fun paragraph(vararg runs: ContentParagraph.Run) =
            ContentParagraph(runs.toList(), null, null)

    fun textRun(text: String, begin: Int, end: Int) =
            ContentParagraph.Run.Text(text, null, range(begin, end))

    fun objectRun(begin: Int, end: Int) = ContentParagraph.Run.Object(object : ContentObject {
        override val range= range(begin, end)
        override val length = 0.0
        override fun configure(config: ContentConfig) = throw UnsupportedOperationException()
    })
}
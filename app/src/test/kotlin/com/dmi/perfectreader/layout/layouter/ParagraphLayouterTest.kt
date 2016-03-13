package com.dmi.perfectreader.layout.layouter

import com.dmi.perfectreader.layout.LayoutObject
import com.dmi.perfectreader.layout.LayoutParagraph
import com.dmi.perfectreader.layout.config.LayoutContext
import com.dmi.perfectreader.layout.config.LayoutSize
import com.dmi.perfectreader.layout.liner.Liner
import com.dmi.perfectreader.layout.paragraph.*
import com.dmi.perfectreader.render.RenderLine
import com.dmi.perfectreader.render.RenderObject
import com.dmi.perfectreader.render.RenderSpace
import com.dmi.perfectreader.render.RenderText
import com.dmi.perfectreader.style.FontStyle
import com.dmi.perfectreader.style.TextAlign
import com.dmi.util.shouldEquals
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.util.*

class ParagraphLayouterTest {
    val HYPHEN_STRING = LayoutChars.HYPHEN.toString()

    @Test
    fun configure_lines() {
        // given
        var params = object {
            lateinit var config: Liner.Config
        }
        val liner = object : Liner {
            override fun makeLines(measuredText: Liner.MeasuredText, config: Liner.Config): List<Liner.Line> {
                params.config = config
                return emptyList()
            }
        }
        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(),
                liner
        )
        val hangingConfig = object : HangingConfig {
            override fun leftHangFactor(ch: Char) = 0.6F
            override fun rightHangFactor(ch: Char) = 0.5F
        }

        // when
        layouter.layout(
                LayoutParagraph(
                        size(200F), Locale.US,
                        listOf(
                                Run.Text("text", style())
                        ),
                        20F, TextAlign.LEFT, hangingConfig
                ),
                context()
        )

        // then
        with (params.config) {
            firstLineIndent shouldEquals 20F
            maxWidth shouldEquals 200F
            leftHangFactor('(') shouldEquals 0.6F
            rightHangFactor(',') shouldEquals 0.5F
        }
    }

    @Test
    fun measure_text_runs() {
        // given
        val LETTER_WIDTH1 = 10F
        val SPACE_WIDTH1 = 5F
        val HYPHEN_WIDTH1 = 5F
        val style1 = style()

        val LETTER_WIDTH2 = 15F
        val SPACE_WIDTH2 = 10F
        val HYPHEN_WIDTH2 = 10F
        val style2 = style()

        var params = object {
            lateinit var measuredText: Liner.MeasuredText
        }
        val liner = object : Liner {
            override fun makeLines(measuredText: Liner.MeasuredText, config: Liner.Config): List<Liner.Line> {
                params.measuredText = measuredText
                return emptyList()
            }
        }
        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(mapOf(
                        style1 to TestMetrics(LETTER_WIDTH1, SPACE_WIDTH1, HYPHEN_WIDTH1),
                        style2 to TestMetrics(LETTER_WIDTH2, SPACE_WIDTH2, HYPHEN_WIDTH2)
                )),
                liner
        )

        // when
        layouter.layout(
                LayoutParagraph(
                        size(200F), Locale.US,
                        listOf(
                                Run.Text("some ", style1),
                                Run.Text("text", style2)
                        ),
                        20F, TextAlign.LEFT, DefaultHangingConfig()
                ),
                context()
        )

        // then
        with (params.measuredText) {
            plainText.toString() shouldEquals "some text"
            locale shouldEquals Locale.US
            widthOf(0) shouldEquals LETTER_WIDTH1
            widthOf(4) shouldEquals SPACE_WIDTH1
            widthOf(0, 9) shouldEquals 4 * LETTER_WIDTH1 + SPACE_WIDTH1 + 4 * LETTER_WIDTH2
            widthOf(4, 8) shouldEquals SPACE_WIDTH1 + 3 * LETTER_WIDTH2
            hyphenWidthAfter(0) shouldEquals HYPHEN_WIDTH1
            hyphenWidthAfter(4) shouldEquals HYPHEN_WIDTH1
            hyphenWidthAfter(5) shouldEquals HYPHEN_WIDTH2
            hyphenWidthAfter(8) shouldEquals HYPHEN_WIDTH2
        }
    }

    @Test
    fun measure_object_runs() {
        // given
        val object1 = layoutObj()
        val object2 = layoutObj()

        var params = object {
            lateinit var measuredText: Liner.MeasuredText
        }
        val liner = object : Liner {
            override fun makeLines(measuredText: Liner.MeasuredText, config: Liner.Config): List<Liner.Line> {
                params.measuredText = measuredText
                return emptyList()
            }
        }
        val layouter = ParagraphLayouter(
                childLayouter(mapOf(
                        object1 to renderObj(30F, 0F),
                        object2 to renderObj(100F, 0F)
                )),
                textMetrics(),
                liner
        )

        // when
        layouter.layout(
                LayoutParagraph(
                        size(200F), Locale.US,
                        listOf(
                                Run.Object(object1),
                                Run.Text("text", style()),
                                Run.Object(object2)
                        ),
                        20F, TextAlign.LEFT, DefaultHangingConfig()
                ),
                context()
        )

        // then
        with (params.measuredText) {
            plainText.toString() shouldEquals "\uFFFCtext\uFFFC"
            locale shouldEquals Locale.US
            widthOf(0) shouldEquals 30F
            widthOf(5) shouldEquals 100F
            hyphenWidthAfter(0) shouldEquals 0F
            hyphenWidthAfter(5) shouldEquals 0F
        }
    }

    @Test
    fun layout_context_for_object_runs() {
        // given
        var params = object {
            lateinit var context: LayoutContext
        }
        val childLayouter = object : Layouter<LayoutObject, RenderObject> {
            override fun layout(obj: LayoutObject, context: LayoutContext): RenderObject {
                params.context = context
                return renderObj()
            }
        }
        val obj = layoutObj()
        val layouter = ParagraphLayouter(
                childLayouter,
                textMetrics(),
                liner()
        )

        // when
        layouter.layout(
                LayoutParagraph(
                        size(200F), Locale.US,
                        listOf(Run.Object(obj)),
                        20F, TextAlign.LEFT, DefaultHangingConfig()
                ),
                context()
        )

        // then
        with (params.context) {
            with (parentSize) {
                width shouldEquals 200F
                height shouldEquals 0F
            }
            with (areaSize) {
                width shouldEquals 200F
                height shouldEquals 0F
            }
        }
    }

    @Test
    fun render_text_runs() {
        // given
        val LETTER_WIDTH1 = 10F
        val SPACE_WIDTH1 = 5F
        val HYPHEN_WIDTH1 = 5F
        val ASCENT1 = -8F
        val DESCENT1 = 5F
        val LEADING1 = 1F
        val style1 = style()

        val LETTER_WIDTH2 = 15F
        val SPACE_WIDTH2 = 10F
        val HYPHEN_WIDTH2 = 10F
        val ASCENT2 = -16F
        val DESCENT2 = 10F
        val LEADING2 = 1F
        val style2 = style()

        val runs = listOf(
                Run.Text("some t", style1),
                Run.Text("ext words ", style2),
                Run.Text(" qwerty", style1)
        )
        val lines = listOf(
                TestLine(left = 20F, width = 100F, text = "some "),
                TestLine(left = -5F, width = 150F, text = "text words"),
                TestLine(left = 0F, width = 80F, text = "  qwert"),
                TestLine(left = 0F, width = 80F, text = "y")
        )

        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(mapOf(
                        style1 to TestMetrics(LETTER_WIDTH1, SPACE_WIDTH1, HYPHEN_WIDTH1, ASCENT1, DESCENT1, LEADING1),
                        style2 to TestMetrics(LETTER_WIDTH2, SPACE_WIDTH2, HYPHEN_WIDTH2, ASCENT2, DESCENT2, LEADING2)
                )),
                liner(lines)
        )

        // when
        val renderObj = layouter.layout(
                LayoutParagraph(size(200F), Locale.US, runs, 20F, TextAlign.LEFT, DefaultHangingConfig()),
                context()
        )

        // then
        with (renderObj) {
            val HEIGHT1 = -ASCENT1 + DESCENT1 + LEADING1
            val HEIGHT2 = -ASCENT2 + DESCENT2 + LEADING2

            with (child(0).obj as RenderLine) {

                childTexts(this) shouldEquals listOf("some", " ")
                childIsSpaces(this) shouldEquals listOf(false, true)
                childBaselines(this) shouldEquals listOf(-ASCENT1, -ASCENT1)
                childStyles(this) shouldEquals listOf(style1, style1)

                childWidths(this) shouldEquals listOf(4 * LETTER_WIDTH1, 1 * SPACE_WIDTH1)
                childHeights(this) shouldEquals listOf(HEIGHT1, HEIGHT1)
                childX(this) shouldEquals listOf(20F, 20 + 4 * LETTER_WIDTH1)
                childY(this) shouldEquals listOf(0F, 0F)
            }

            with (child(1).obj as RenderLine) {
                childTexts(this) shouldEquals listOf("t", "ext", " ", "words")
                childIsSpaces(this) shouldEquals listOf(false, false, true, false)
                childBaselines(this) shouldEquals listOf(-ASCENT1, -ASCENT2, -ASCENT2, -ASCENT2)
                childStyles(this) shouldEquals listOf(style1, style2, style2, style2)

                childWidths(this) shouldEquals listOf(1 * LETTER_WIDTH1, 3 * LETTER_WIDTH2, 1 * SPACE_WIDTH2, 5 * LETTER_WIDTH2)
                childHeights(this) shouldEquals listOf(HEIGHT1, HEIGHT2, HEIGHT2, HEIGHT2)
                childX(this) shouldEquals listOf(
                        -5F,
                        -5F + 1 * LETTER_WIDTH1,
                        -5F + 1 * LETTER_WIDTH1 + 3 * LETTER_WIDTH2,
                        -5F + 1 * LETTER_WIDTH1 + 3 * LETTER_WIDTH2 + 1 * SPACE_WIDTH2
                )
                childY(this) shouldEquals listOf(ASCENT1 - ASCENT2, 0F, 0F, 0F)
            }

            with (child(2).obj as RenderLine) {
                childTexts(this) shouldEquals listOf(" ", " ", "qwert")
                childIsSpaces(this) shouldEquals listOf(true, true, false)
                childBaselines(this) shouldEquals listOf(-ASCENT2, -ASCENT1, -ASCENT1)
                childStyles(this) shouldEquals listOf(style2, style1, style1)

                childWidths(this) shouldEquals listOf(1 * SPACE_WIDTH2, 1 * SPACE_WIDTH1, 5 * LETTER_WIDTH1)
                childHeights(this) shouldEquals listOf(HEIGHT2, HEIGHT1, HEIGHT1)
                childX(this) shouldEquals listOf(0F, SPACE_WIDTH2, SPACE_WIDTH2 + SPACE_WIDTH1)
                childY(this) shouldEquals listOf(0F, ASCENT1 - ASCENT2, ASCENT1 - ASCENT2)
            }

            with (child(3).obj as RenderLine) {
                childTexts(this) shouldEquals listOf("y")
                childIsSpaces(this) shouldEquals listOf(false)
                childBaselines(this) shouldEquals listOf(-ASCENT1)
                childStyles(this) shouldEquals listOf(style1)

                childWidths(this) shouldEquals listOf(1 * LETTER_WIDTH1)
                childHeights(this) shouldEquals listOf(HEIGHT1)
                childX(this) shouldEquals listOf(0F)
                childY(this) shouldEquals listOf(0F)
            }

            width shouldEquals 200F
            height shouldEquals 1 * HEIGHT1 + 2 * HEIGHT2 + 1 * HEIGHT1
            childWidths(this) shouldEquals listOf(200F, 200F, 200F, 200F)
            childHeights(this) shouldEquals listOf(HEIGHT1, HEIGHT2, HEIGHT2, HEIGHT1)
            childX(this) shouldEquals listOf(0F, 0F, 0F, 0F)
            childY(this) shouldEquals listOf(0F, HEIGHT1, HEIGHT1 + HEIGHT2, HEIGHT1 + HEIGHT2 + HEIGHT2)
        }

        renderObj.children.map { it.obj }.forEach {
            (it as RenderLine).children.map { it.obj }.forEach {
                it as RenderText
                assertThat(it.locale, equalTo(Locale.US))
                if (it is RenderSpace)
                    assertThat(it.scaleX, equalTo(1F))
            }
        }
    }

    @Test
    fun render_object_runs() {
        // given
        val CHAR_WIDTH = 10F
        val ASCENT = -20F
        val DESCENT = 6F
        val style = style()

        val object1 = layoutObj()
        val object2 = layoutObj()
        val renderObj1 = renderObj(50F, 10F)
        val renderObj2 = renderObj(70F, 70F)

        val runs = listOf(
                Run.Object(object1),
                Run.Text("text", style),
                Run.Object(object2)
        )
        val lines = listOf(
                TestLine(left = 0F, width = 100F, text = "\uFFFCtext\uFFFC")
        )

        val layouter = ParagraphLayouter(
                childLayouter(mapOf(
                        object1 to renderObj1,
                        object2 to renderObj2
                )),
                textMetrics(mapOf(
                        style to TestMetrics(CHAR_WIDTH, CHAR_WIDTH, CHAR_WIDTH, ASCENT, DESCENT)
                )),
                liner(lines)
        )

        // when
        val renderObj = layouter.layout(
                LayoutParagraph(size(200F), Locale.US, runs, 20F, TextAlign.LEFT, DefaultHangingConfig()),
                context()
        )

        // then
        with (renderObj) {
            with (child(0).obj as RenderLine) {
                child(0).obj shouldEquals renderObj1
                child(2).obj shouldEquals renderObj2

                with (child(1).obj as RenderText) {
                    text.toString() shouldEquals "text"
                    baseline shouldEquals 20F
                    style shouldEquals style
                }

                childWidths(this) shouldEquals listOf(50F, 4 * CHAR_WIDTH, 70F)
                childHeights(this) shouldEquals listOf(10F, 26F, 70F)
                childX(this) shouldEquals listOf(0F, 50F, 50F + 4 * CHAR_WIDTH)
                childY(this) shouldEquals listOf(70F - 10, 70F - 20, 70F - 70)
            }

            width shouldEquals 200F
            height shouldEquals 70F + 6F
            childWidths(this) shouldEquals listOf(200F)
            childHeights(this) shouldEquals listOf(70F + 6)
            childX(this) shouldEquals listOf(0F)
            childY(this) shouldEquals listOf(0F)
        }
    }

    @Test
    fun render_hyphen() {
        // given
        val CHAR_WIDTH1 = 10F
        val HYPHEN_WIDTH1 = 5F
        val ASCENT1 = -8F
        val DESCENT1 = 5F
        val style1 = style()

        val CHAR_WIDTH2 = 20F
        val HYPHEN_WIDTH2 = 15F
        val ASCENT2 = -16F
        val DESCENT2 = 10F
        val style2 = style()

        val object1 = layoutObj()
        val renderObj1 = renderObj(50F, 10F)

        val runs = listOf(
                Run.Text("text", style1),
                Run.Object(object1),
                Run.Text("t", style1),
                Run.Text("ext2", style2)
        )
        val lines = listOf(
                TestLine(left = 0F, width = 100F, text = "t", hasHyphenAfter = true),
                TestLine(left = 0F, width = 100F, text = "ext", hasHyphenAfter = true),
                TestLine(left = 0F, width = 100F, text = "\uFFFC", hasHyphenAfter = true),
                TestLine(left = 0F, width = 100F, text = "text2", hasHyphenAfter = true)
        )

        val layouter = ParagraphLayouter(
                childLayouter(mapOf(
                        object1 to renderObj1
                )),
                textMetrics(mapOf(
                        style1 to TestMetrics(CHAR_WIDTH1, CHAR_WIDTH1, HYPHEN_WIDTH1, ASCENT1, DESCENT1),
                        style2 to TestMetrics(CHAR_WIDTH2, CHAR_WIDTH2, HYPHEN_WIDTH2, ASCENT2, DESCENT2)
                )),
                liner(lines)
        )

        // when
        val renderObj = layouter.layout(
                LayoutParagraph(size(200F), Locale.US, runs, 20F, TextAlign.LEFT, DefaultHangingConfig()),
                context()
        )

        // then
        with (renderObj) {
            val HEIGHT1 = -ASCENT1 + DESCENT1
            val HEIGHT2 = -ASCENT2 + DESCENT2

            with (child(0).obj as RenderLine) {
                childTexts(this) shouldEquals listOf("t", HYPHEN_STRING)
                childBaselines(this) shouldEquals listOf(-ASCENT1, -ASCENT1)
                childStyles(this) shouldEquals listOf(style1, style1)

                childWidths(this) shouldEquals listOf(1 * CHAR_WIDTH1, 1 * HYPHEN_WIDTH1)
                childHeights(this) shouldEquals listOf(HEIGHT1, HEIGHT1)
                childX(this) shouldEquals listOf(0F, 1 * CHAR_WIDTH1)
                childY(this) shouldEquals listOf(0F, 0F)
            }

            with (child(1).obj as RenderLine) {
                childTexts(this) shouldEquals listOf("ext", HYPHEN_STRING)
                childBaselines(this) shouldEquals listOf(-ASCENT1, -ASCENT1)
                childStyles(this) shouldEquals listOf(style1, style1)

                childWidths(this) shouldEquals listOf(3 * CHAR_WIDTH1, 1 * HYPHEN_WIDTH1)
                childHeights(this) shouldEquals listOf(HEIGHT1, HEIGHT1)
                childX(this) shouldEquals listOf(0F, 3 * CHAR_WIDTH1)
                childY(this) shouldEquals listOf(0F, 0F)
            }

            with (child(2).obj as RenderLine) {
                child(0).obj shouldEquals renderObj1

                childWidths(this) shouldEquals listOf(50F)
                childHeights(this) shouldEquals listOf(10F)
                childX(this) shouldEquals listOf(0F)
                childY(this) shouldEquals listOf(0F)
            }

            with (child(3).obj as RenderLine) {
                childTexts(this) shouldEquals listOf("t", "ext2", HYPHEN_STRING)
                childBaselines(this) shouldEquals listOf(-ASCENT1, -ASCENT2, -ASCENT2)
                childStyles(this) shouldEquals listOf(style1, style2, style2)

                childWidths(this) shouldEquals listOf(1 * CHAR_WIDTH1, 4 * CHAR_WIDTH2, 1 * HYPHEN_WIDTH2)
                childHeights(this) shouldEquals listOf(HEIGHT1, HEIGHT2, HEIGHT2)
                childX(this) shouldEquals listOf(0F, 1 * CHAR_WIDTH1, 1 * CHAR_WIDTH1 + 4 * CHAR_WIDTH2)
                childY(this) shouldEquals listOf(8F, 0F, 0F)
            }

            width shouldEquals 200F
            height shouldEquals HEIGHT1 + HEIGHT1 + 10 + HEIGHT2
            childWidths(this) shouldEquals listOf(200F, 200F, 200F, 200F)
            childHeights(this) shouldEquals listOf(HEIGHT1, HEIGHT1, 10F, HEIGHT2)
            childX(this) shouldEquals listOf(0F, 0F, 0F, 0F)
            childY(this) shouldEquals listOf(0F, HEIGHT1, HEIGHT1 + HEIGHT1, HEIGHT1 + HEIGHT1 + 10)
        }
    }

    @Test
    fun align_text_right() {
        // given
        val LETTER_WIDTH = 10F
        val SPACE_WIDTH = 5F
        val style = style()

        val runs = listOf(
                Run.Text(" text1   text2   text3 text4", style)
        )
        val lines = listOf(
                TestLine(left = -10F, width = 180F, text = " text1   text2 "),
                TestLine(left = 20F, width = 50F, text = "  t", hasHyphenAfter = true),
                TestLine(left = 0F, width = 300F, text = "ext3 text4")
        )

        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(mapOf(
                        style to TestMetrics(LETTER_WIDTH, SPACE_WIDTH, LETTER_WIDTH)
                )),
                liner(lines)
        )

        // when
        val renderObj = layouter.layout(
                LayoutParagraph(size(200F), Locale.US, runs, 20F, TextAlign.RIGHT, DefaultHangingConfig()),
                context()
        )

        // then
        with (renderObj.child(0).obj as RenderLine) {
            val widths = listOf(1 * SPACE_WIDTH, 5 * LETTER_WIDTH, 3 * SPACE_WIDTH, 5 * LETTER_WIDTH, 1 * SPACE_WIDTH)
            childWidths(this) shouldEquals widths
            childX(this) shouldEquals listOf(
                    200F - 180,
                    20F + widths[0],
                    20F + widths[0] + widths[1],
                    20F + widths[0] + widths[1] + widths[2],
                    20F + widths[0] + widths[1] + widths[2] + widths[3]
            )
        }

        with (renderObj.child(1).obj as RenderLine) {
            val widths = listOf(2 * SPACE_WIDTH, 1 * LETTER_WIDTH, 1 * LETTER_WIDTH)
            childWidths(this) shouldEquals widths
            childX(this) shouldEquals listOf(
                    200F - 50,
                    150F + widths[0],
                    150F + widths[0] + widths[1]
            )
        }

        with (renderObj.child(2).obj as RenderLine) {
            val widths = listOf(4 * LETTER_WIDTH, 1 * SPACE_WIDTH, 5 * LETTER_WIDTH)
            childWidths(this) shouldEquals widths
            childX(this) shouldEquals listOf(
                    200F - 300,
                    -100F + widths[0],
                    -100F + widths[0] + widths[1]
            )
        }
    }

    @Test
    fun align_text_center() {
        // given
        val LETTER_WIDTH = 10F
        val SPACE_WIDTH = 5F
        val style = style()

        val runs = listOf(
                Run.Text(" text1   text2   text3 text4", style)
        )
        val lines = listOf(
                TestLine(left = -10F, width = 180F, text = " text1   text2 "),
                TestLine(left = 20F, width = 50F, text = "  t", hasHyphenAfter = true),
                TestLine(left = 0F, width = 300F, text = "ext3 text4")
        )

        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(mapOf(
                        style to TestMetrics(LETTER_WIDTH, SPACE_WIDTH, LETTER_WIDTH)
                )),
                liner(lines)
        )

        // when
        val renderObj = layouter.layout(
                LayoutParagraph(size(200F), Locale.US, runs, 20F, TextAlign.CENTER, DefaultHangingConfig()),
                context()
        )

        // then
        with (renderObj.child(0).obj as RenderLine) {
            val widths = listOf(1 * SPACE_WIDTH, 5 * LETTER_WIDTH, 3 * SPACE_WIDTH, 5 * LETTER_WIDTH, 1 * SPACE_WIDTH)
            childWidths(this) shouldEquals widths
            childX(this) shouldEquals listOf(
                    -10F + (200 + 10 - 180) / 2,
                    5F + widths[0],
                    5F + widths[0] + widths[1],
                    5F + widths[0] + widths[1] + widths[2],
                    5F + widths[0] + widths[1] + widths[2] + widths[3]
            )
        }

        with (renderObj.child(1).obj as RenderLine) {
            val widths = listOf(2 * SPACE_WIDTH, 1 * LETTER_WIDTH, 1 * LETTER_WIDTH)
            childWidths(this) shouldEquals widths
            childX(this) shouldEquals listOf(
                    20F + (200 - 20 - 50) / 2,
                    85F + widths[0],
                    85F + widths[0] + widths[1]
            )
        }

        with (renderObj.child(2).obj as RenderLine) {
            val widths = listOf(4 * LETTER_WIDTH, 1 * SPACE_WIDTH, 5 * LETTER_WIDTH)
            childWidths(this) shouldEquals widths
            childX(this) shouldEquals listOf(
                    0F + (200 - 300) / 2,
                    -50F + widths[0],
                    -50F + widths[0] + widths[1]
            )
        }
    }

    @Test
    fun align_text_justify() {
        // given
        val LETTER_WIDTH = 10F
        val SPACE_WIDTH = 5F
        val style = style()

        val runs = listOf(
                Run.Text(" text1   t ext2   text3 text4  text5 ", style)
        )
        val lines = listOf(
                TestLine(left = -10F, width = 180F, text = " text1   t ext2 "),
                TestLine(left = 20F, width = 50F, text = "  t", hasHyphenAfter = true),
                TestLine(left = 0F, width = 300F, text = "ext3 t"),
                TestLine(left = 0F, width = 100F, text = "ext4  text5 ")
        )

        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(mapOf(
                        style to TestMetrics(LETTER_WIDTH, SPACE_WIDTH, LETTER_WIDTH)
                )),
                liner(lines)
        )

        // when
        val renderObj = layouter.layout(
                LayoutParagraph(size(200F), Locale.US, runs, 20F, TextAlign.JUSTIFY, DefaultHangingConfig()),
                context()
        )

        // then
        with (renderObj.child(0).obj as RenderLine)
        {
            val freeSpace = 200F - 180F + 10F
            val midSpaceWidths = (3 + 1) * SPACE_WIDTH
            val midSpaceScaleX = (freeSpace + midSpaceWidths) / midSpaceWidths

            val widths = listOf(
                    1 * SPACE_WIDTH,
                    5 * LETTER_WIDTH,
                    3 * SPACE_WIDTH * midSpaceScaleX,
                    1 * LETTER_WIDTH,
                    1 * SPACE_WIDTH * midSpaceScaleX,
                    4 * LETTER_WIDTH,
                    1 * SPACE_WIDTH
            )
            childScaleX(this) shouldEquals listOf(1F, null, midSpaceScaleX, null, midSpaceScaleX, null, 1F)
            childWidths(this) shouldEquals widths
            childX(this) shouldEquals listOf(
                    -10F,
                    -10F + widths[0],
                    -10F + widths[0] + widths[1],
                    -10F + widths[0] + widths[1] + widths[2],
                    -10F + widths[0] + widths[1] + widths[2] + widths[3],
                    -10F + widths[0] + widths[1] + widths[2] + widths[3] + widths[4],
                    -10F + widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + widths[5]
            )
        }

        with (renderObj.child(1).obj as RenderLine)
        {
            val widths = listOf(2 * SPACE_WIDTH, 1 * LETTER_WIDTH, 1 * LETTER_WIDTH)
            childScaleX(this) shouldEquals listOf(1F, null, null)
            childWidths(this) shouldEquals widths
            childX(this) shouldEquals listOf(
                    20F,
                    20F + widths[0],
                    20F + widths[0] + widths[1]
            )
        }

        with (renderObj.child(2).obj as RenderLine)
        {
            val widths = listOf(4 * LETTER_WIDTH, 1 * SPACE_WIDTH, 1 * LETTER_WIDTH)
            childScaleX(this) shouldEquals listOf(null, 1F, null)
            childWidths(this) shouldEquals widths
            childX(this) shouldEquals listOf(
                    0F,
                    widths[0],
                    widths[0] + widths[1]
            )
        }

        with (renderObj.child(3).obj as RenderLine)
        {
            val widths = listOf(4 * LETTER_WIDTH, 2 * SPACE_WIDTH, 5 * LETTER_WIDTH, 1 * SPACE_WIDTH)
            childScaleX(this) shouldEquals listOf(null, 1F, null, 1F)
            childWidths(this) shouldEquals widths
            childX(this) shouldEquals listOf(
                    0F,
                    widths[0],
                    widths[0] + widths[1],
                    widths[0] + widths[1] + widths[2]
            )
        }
    }

    @Test
    fun render_empty_paragraph() {
        // given
        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(),
                liner()
        )

        // when
        val renderObj = layouter.layout(
                LayoutParagraph(size(200F), Locale.US, listOf(), 20F, TextAlign.LEFT, DefaultHangingConfig()),
                context()
        )

        // then
        with (renderObj) {
            width shouldEquals 200F
            height shouldEquals 0F
            children.size shouldEquals 0
        }
    }

    @Test
    fun compute_lines_width_when_wrap_width() {
        // given
        val runs = listOf(
                Run.Text("texttexttext", style())
        )
        val lines = listOf(
                TestLine(left = 30F, width = 100F, text = "text"),
                TestLine(left = -10F, width = 180F, text = "text"),
                TestLine(left = 0F, width = 50F, text = "text")
        )

        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(),
                liner(lines)
        )

        // when
        var renderObj = layouter.layout(
                LayoutParagraph(fixedHeightSize(500F), Locale.US, runs, 20F, TextAlign.LEFT, DefaultHangingConfig()),
                context()
        )

        // then
        renderObj.height shouldEquals 500F
    }

    @Test
    fun set_height() {
        // given
        val runs = listOf(
                Run.Text("texttexttext", style())
        )
        val lines = listOf(
                TestLine(left = 30F, width = 100F, text = "text"),
                TestLine(left = -10F, width = 180F, text = "text"),
                TestLine(left = 0F, width = 50F, text = "text")
        )

        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(),
                liner(lines)
        )

        // when
        var renderObj = layouter.layout(
                LayoutParagraph(wrapWidthSize(), Locale.US, runs, 20F, TextAlign.LEFT, DefaultHangingConfig()),
                context()
        )

        // then
        renderObj.width shouldEquals 170F
        childWidths(renderObj) shouldEquals listOf(170F, 170F, 170F)

        // when
        renderObj = layouter.layout(
                LayoutParagraph(wrapWidthSize(), Locale.US, runs, 20F, TextAlign.RIGHT, DefaultHangingConfig()),
                context()
        )

        // then
        renderObj.width shouldEquals 170F
        childWidths(renderObj) shouldEquals listOf(170F, 170F, 170F)
        childX(renderObj.child(0).obj) shouldEquals listOf(170F - 100)
        childX(renderObj.child(1).obj) shouldEquals listOf(170F - 180)
        childX(renderObj.child(2).obj) shouldEquals listOf(170F - 50)

        // when
        renderObj = layouter.layout(
                LayoutParagraph(wrapWidthSize(), Locale.US, runs, 20F, TextAlign.CENTER, DefaultHangingConfig()),
                context()
        )

        // then
        renderObj.width shouldEquals 170F
        childWidths(renderObj) shouldEquals listOf(170F, 170F, 170F)
        childX(renderObj.child(0).obj) shouldEquals listOf(30F + (170 - 30 - 100) / 2)
        childX(renderObj.child(1).obj) shouldEquals listOf(-10F + (170 + 10 - 180) / 2)
        childX(renderObj.child(2).obj) shouldEquals listOf(0F + (170 + 0 - 50) / 2)
    }

    fun childLayouter(layoutToRenderObject: Map<LayoutObject, RenderObject> = emptyMap()) =
            object : Layouter<LayoutObject, RenderObject> {
                override fun layout(obj: LayoutObject, context: LayoutContext) = layoutToRenderObject[obj]!!
            }
    
    fun size(width: Float) =  LayoutSize(
            LayoutSize.LimitedValue(
                    LayoutSize.Value.Absolute(width), LayoutSize.Limit.None(), LayoutSize.Limit.None()
            ),
            LayoutSize.LimitedValue(
                    LayoutSize.Value.WrapContent(), LayoutSize.Limit.None(), LayoutSize.Limit.None()
            )
    )
    
    fun wrapWidthSize() =  LayoutSize(
            LayoutSize.LimitedValue(
                    LayoutSize.Value.WrapContent(), LayoutSize.Limit.None(), LayoutSize.Limit.None()
            ),
            LayoutSize.LimitedValue(
                    LayoutSize.Value.WrapContent(), LayoutSize.Limit.None(), LayoutSize.Limit.None()
            )
    )

    fun fixedHeightSize(height: Float) =  LayoutSize(
            LayoutSize.LimitedValue(
                    LayoutSize.Value.WrapContent(), LayoutSize.Limit.None(), LayoutSize.Limit.None()
            ),
            LayoutSize.LimitedValue(
                    LayoutSize.Value.Absolute(height), LayoutSize.Limit.None(), LayoutSize.Limit.None()
            )
    )
    
    fun context() = LayoutContext.root(200F, 200F)

    fun layoutObj() = object : LayoutObject() {}

    fun renderObj(width: Float = 0F, height: Float = 0F) =
            object : RenderObject(width, height, emptyList()) {
                override fun canPartiallyPainted() = false
            }

    fun style() = FontStyle(0F, 0, FontStyle.RenderParams(false, false, false, false))

    fun textMetrics() =
            object : TextMetrics {
                override fun charWidths(text: CharSequence, style: FontStyle) = FloatArray(text.length)
                override fun verticalMetrics(style: FontStyle) = TextMetrics.VerticalMetrics()
            }

    fun textMetrics(styleToParams: Map<FontStyle, TestMetrics>) =
            object : TextMetrics {
                override fun charWidths(text: CharSequence, style: FontStyle): FloatArray {
                    val params = styleToParams[style] as TestMetrics

                    if (text == HYPHEN_STRING) {
                        return floatArrayOf(params.hyphenWidth)
                    } else {
                        return text.map {
                            if (it == ' ') params.spaceWidth else params.letterWidth
                        }.toFloatArray()
                    }
                }

                @Override
                override fun verticalMetrics(style: FontStyle): TextMetrics.VerticalMetrics {
                    val params = styleToParams[style] as TestMetrics
                    return TextMetrics.VerticalMetrics().apply {
                        ascent = params.ascent
                        descent = params.descent
                        leading = params.leading
                    }
                }
            }


    fun liner(testLines: List<TestLine> = emptyList()) = object : Liner {
        override fun makeLines(measuredText: Liner.MeasuredText, config: Liner.Config) = lines(testLines)
    }

    fun lines(testLines: List<TestLine>): List<Liner.Line> {
        val lines = ArrayList<Liner.Line>()
        var beginIndex = 0
        for (testLine in testLines) {
            lines.add(Liner.Line().apply {
                this.left = testLine.left
                this.width = testLine.width
                this.hasHyphenAfter = testLine.hasHyphenAfter
                this.tokens.addAll(tokens(beginIndex, testLine.text))
            })
            beginIndex += testLine.text.length
        }
        return lines
    }

    fun tokens(beginIndex: Int, text: String): ArrayList<Liner.Token> {
        fun isSpace(index: Int) = text[index] == ' '

        val tokens = ArrayList<Liner.Token>()

        var begin = 0
        for (end in 1..text.length) {
            if (end == text.length || isSpace(end) != isSpace(begin)) {
                tokens.add(Liner.Token().apply {
                    this.isSpace = isSpace(begin)
                    this.beginIndex = beginIndex + begin
                    this.endIndex = beginIndex + end
                })
                begin = end
            }
        }

        return tokens
    }

    fun childWidths(renderObj: RenderObject) =
            renderObj.children.map { it.obj.width }

    fun childHeights(renderObj: RenderObject) =
            renderObj.children.map { it.obj.height }

    fun childX(renderObj: RenderObject) =
            renderObj.children.map { it.x }

    fun childY(renderObj: RenderObject) =
            renderObj.children.map { it.y }

    fun childTexts(renderObj: RenderObject) =
            renderObj.children.map {
                with (it.obj as RenderText) { text.toString() }
            }

    fun childIsSpaces(renderObj: RenderObject) =
            renderObj.children.map { it.obj is RenderSpace }

    fun childScaleX(renderObj: RenderObject) =
            renderObj.children.map {
                if (it.obj is RenderSpace) (it.obj as RenderSpace).scaleX else null
            }

    fun childBaselines(renderObj: RenderObject) =
            renderObj.children.map {
                with (it.obj as RenderText) { baseline }
            }

    fun childStyles(renderObj: RenderObject) =
            renderObj.children.map {
                with (it.obj as RenderText) { style }
            }

    data class TestMetrics(
            val letterWidth: Float = 0F,
            val spaceWidth: Float = 0F,
            val hyphenWidth: Float = 0F,
            val ascent: Float = 0F,
            val descent: Float = 0F,
            val leading: Float = 0F
    )

    data class TestLine(
            val left: Float,
            val width: Float,
            val hasHyphenAfter: Boolean = false,
            val text: String
    )
}

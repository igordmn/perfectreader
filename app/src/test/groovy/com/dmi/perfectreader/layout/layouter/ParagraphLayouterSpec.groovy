package com.dmi.perfectreader.layout.layouter

import com.dmi.perfectreader.layout.LayoutObject
import com.dmi.perfectreader.layout.LayoutParagraph
import com.dmi.perfectreader.layout.config.*
import com.dmi.perfectreader.layout.liner.Liner
import com.dmi.perfectreader.layout.run.ObjectRun
import com.dmi.perfectreader.layout.run.TextRun
import com.dmi.perfectreader.render.*
import com.dmi.perfectreader.style.FontStyle
import com.dmi.perfectreader.style.TextAlign
import spock.lang.Specification

import static java.util.Collections.emptyList

class ParagraphLayouterSpec extends Specification {
    final HYPHEN_STRING = String.valueOf(LayoutChars.HYPHEN)

    def "configure lines"() {
        given:
        def liner = Mock(Liner)
        def layouter = new ParagraphLayouter(
                childLayouter(),
                textMetrics(),
                liner
        )
        def hangingConfig = new HangingConfig() {
            @Override
            float leftHangFactor(char ch) {
                return 0.6
            }

            @Override
            float rightHangFactor(char ch) {
                return 0.5
            }
        }

        when:
        layouter.layout(
                new LayoutParagraph(
                        true, Locale.US,
                        [
                                new TextRun("text", GroovyMock(FontStyle))
                        ],
                        20, TextAlign.LEFT, hangingConfig
                ),
                new LayoutArea(200, 200)
        )

        then:
        1 * liner.makeLines(_, _) >> { _, config ->
            assert config.firstLineIndent() == 20
            assert config.maxWidth() == 200
            assert config.leftHangFactor('(' as char) == 0.6F
            assert config.rightHangFactor(',' as char) == 0.5F

            return emptyList()
        }
    }

    def "measure text runs"() {
        given:
        final LETTER_WIDTH1 = 10F
        final SPACE_WIDTH1 = 5F
        final HYPHEN_WIDTH1 = 5F
        final style1 = GroovyMock(FontStyle)

        final LETTER_WIDTH2 = 15F
        final SPACE_WIDTH2 = 10F
        final HYPHEN_WIDTH2 = 10F
        final style2 = GroovyMock(FontStyle)

        def liner = Mock(Liner)
        def layouter = new ParagraphLayouter(
                childLayouter(),
                textMetrics([
                        (style1): [LETTER_WIDTH1, SPACE_WIDTH1, HYPHEN_WIDTH1],
                        (style2): [LETTER_WIDTH2, SPACE_WIDTH2, HYPHEN_WIDTH2],
                ]),
                liner
        )

        when:
        layouter.layout(
                new LayoutParagraph(
                        true, Locale.US,
                        [
                                new TextRun("some ", style1),
                                new TextRun("text", style2)
                        ],
                        20, TextAlign.LEFT, new DefaultHangingConfig()
                ),
                new LayoutArea(200, 200)
        )

        then:
        1 * liner.makeLines(_, _) >> { text, _ ->
            assert text.plainText().toString() == "some text"
            assert text.locale() == Locale.US
            assert text.widthOf(0) == LETTER_WIDTH1
            assert text.widthOf(4) == SPACE_WIDTH1
            assert text.widthOf(8) == LETTER_WIDTH2
            assert text.widthOf(0, 0) == 0
            assert text.widthOf(0, 1) == LETTER_WIDTH1
            assert text.widthOf(0, 9) == 4 * LETTER_WIDTH1 + SPACE_WIDTH1 + 4 * LETTER_WIDTH2
            assert text.widthOf(4, 8) == SPACE_WIDTH1 + 3 * LETTER_WIDTH2
            assert text.hyphenWidthAfter(0) == HYPHEN_WIDTH1
            assert text.hyphenWidthAfter(4) == HYPHEN_WIDTH1
            assert text.hyphenWidthAfter(5) == HYPHEN_WIDTH2
            assert text.hyphenWidthAfter(8) == HYPHEN_WIDTH2

            return emptyList()
        }
    }

    def "measure object runs"() {
        given:
        def object1 = GroovyMock(LayoutObject)
        def object2 = GroovyMock(LayoutObject)

        def liner = Mock(Liner)
        def layouter = new ParagraphLayouter(
                childLayouter([
                        (object1): renderObject(30, 0),
                        (object2): renderObject(100, 0),
                ]),
                textMetrics(),
                liner
        )

        when:
        layouter.layout(
                new LayoutParagraph(
                        true, Locale.US,
                        [
                                new ObjectRun(object1),
                                new TextRun("text", GroovyMock(FontStyle)),
                                new ObjectRun(object2)
                        ],
                        20, TextAlign.LEFT, new DefaultHangingConfig()
                ),
                new LayoutArea(200, 200)
        )

        then:
        1 * liner.makeLines(_, _) >> { text, _ ->
            assert text.plainText().toString() == "\uFFFCtext\uFFFC"
            assert text.widthOf(0) == 30
            assert text.widthOf(5) == 100
            assert text.hyphenWidthAfter(0) == 0
            assert text.hyphenWidthAfter(5) == 0

            return emptyList()
        }
    }

    def "layout area for object runs"() {
        given:
        def childLayouter = Mock(Layouter)
        def object = GroovyMock(LayoutObject)
        def layouter = new ParagraphLayouter(
                childLayouter,
                textMetrics(),
                liner()
        )

        when:
        layouter.layout(
                new LayoutParagraph(
                        true, Locale.US,
                        [new ObjectRun(object)],
                        20, TextAlign.LEFT, new DefaultHangingConfig()
                ),
                new LayoutArea(200, 200)
        )

        then:
        1 * childLayouter.layout(_, _) >> { _, area ->
            assert area.width() == 200
            assert area.height() == 0

            return renderObject(0, 0)
        }
    }

    def "render text runs"() {
        given:
        final LETTER_WIDTH1 = 10F
        final SPACE_WIDTH1 = 5F
        final HYPHEN_WIDTH1 = 5F
        final ASCENT1 = -8F
        final DESCENT1 = 5F
        final LEADING1 = 1F
        def style1 = GroovyMock(FontStyle)

        final LETTER_WIDTH2 = 15F
        final SPACE_WIDTH2 = 10F
        final HYPHEN_WIDTH2 = 10F
        final ASCENT2 = -16F
        final DESCENT2 = 10F
        final LEADING2 = 1F
        def style2 = GroovyMock(FontStyle)

        def runs = [
                new TextRun("some t", style1),
                new TextRun("ext words ", style2),
                new TextRun(" qwerty", style1)
        ]
        def lines = lines([
                [left: 20, width: 100, text: "some "],
                [left: -5, width: 150, text: "text words"],
                [left: 0, width: 80, text: "  qwert"],
                [left: 0, width: 80, text: "y"],
        ])

        def layouter = new ParagraphLayouter(
                childLayouter(),
                textMetrics([
                        (style1): [LETTER_WIDTH1, SPACE_WIDTH1, HYPHEN_WIDTH1, ASCENT1, DESCENT1, LEADING1],
                        (style2): [LETTER_WIDTH2, SPACE_WIDTH2, HYPHEN_WIDTH2, ASCENT2, DESCENT2, LEADING2]
                ]),
                liner(lines)
        )

        when:
        RenderParagraph renderObject = layouter.layout(
                new LayoutParagraph(true, Locale.US, runs, 20, TextAlign.LEFT, new DefaultHangingConfig()),
                new LayoutArea(200, 200)
        )

        then:
        with(renderObject) {
            float HEIGHT1 = -ASCENT1 + DESCENT1 + LEADING1
            float HEIGHT2 = -ASCENT2 + DESCENT2 + LEADING2

            with(it.child(0).object() as RenderLine) {
                childTexts(it) == ["some", " "]
                childBaselines(it) == [-ASCENT1, -ASCENT1]
                childStyles(it) == [style1, style1]

                childWidths(it) == [4 * LETTER_WIDTH1, 1 * SPACE_WIDTH1]
                childHeights(it) == [HEIGHT1, HEIGHT1]
                childX(it) == [20, 20 + 4 * LETTER_WIDTH1]
                childY(it) == [0, 0]
            }

            with(it.child(1).object() as RenderLine) {
                childTexts(it) == ["t", "ext", " ", "words"]
                childBaselines(it) == [-ASCENT1, -ASCENT2, -ASCENT2, -ASCENT2]
                childStyles(it) == [style1, style2, style2, style2]

                childWidths(it) == [1 * LETTER_WIDTH1, 3 * LETTER_WIDTH2, 1 * SPACE_WIDTH2, 5 * LETTER_WIDTH2]
                childHeights(it) == [HEIGHT1, HEIGHT2, HEIGHT2, HEIGHT2]
                childX(it) == [
                        -5,
                        -5 + 1 * LETTER_WIDTH1,
                        -5 + 1 * LETTER_WIDTH1 + 3 * LETTER_WIDTH2,
                        -5 + 1 * LETTER_WIDTH1 + 3 * LETTER_WIDTH2 + 1 * SPACE_WIDTH2
                ]
                childY(it) == [ASCENT1 - ASCENT2, 0, 0, 0]
            }

            with(it.child(2).object() as RenderLine) {
                childTexts(it) == [" ", " ", "qwert"]
                childBaselines(it) == [-ASCENT2, -ASCENT1, -ASCENT1]
                childStyles(it) == [style2, style1, style1]

                childWidths(it) == [1 * SPACE_WIDTH2, 1 * SPACE_WIDTH1, 5 * LETTER_WIDTH1]
                childHeights(it) == [HEIGHT2, HEIGHT1, HEIGHT1]
                childX(it) == [0, SPACE_WIDTH2, SPACE_WIDTH2 + SPACE_WIDTH1]
                childY(it) == [0, ASCENT1 - ASCENT2, ASCENT1 - ASCENT2]
            }

            with(it.child(3).object() as RenderLine) {
                childTexts(it) == ["y"]
                childBaselines(it) == [-ASCENT1]
                childStyles(it) == [style1]

                childWidths(it) == [1 * LETTER_WIDTH1]
                childHeights(it) == [HEIGHT1]
                childX(it) == [0]
                childY(it) == [0]
            }

            it.width() == 200
            it.height() == 1 * HEIGHT1 + 2 * HEIGHT2 + 1 * HEIGHT1
            childWidths(it) == [200, 200, 200, 200]
            childHeights(it) == [HEIGHT1, HEIGHT2, HEIGHT2, HEIGHT1]
            childX(it) == [0, 0, 0, 0]
            childY(it) == [0, HEIGHT1, HEIGHT1 + HEIGHT2, HEIGHT1 + HEIGHT2 + HEIGHT2]
        }

        renderObject.children().collect { it.object() }.each { RenderLine line ->
            line.children().collect { it.object() }.each { RenderText text ->
                assert text.locale() == Locale.US
                if (text instanceof RenderSpace) {
                    assert text.scaleX() == 1.0F
                }
            }
        }
    }

    def "render object runs"() {
        given:
        final CHAR_WIDTH = 10F
        final ASCENT = -20F
        final DESCENT = 6F
        def style = GroovyMock(FontStyle)

        def object1 = GroovyMock(LayoutObject)
        def object2 = GroovyMock(LayoutObject)
        def renderObject1 = renderObject(50, 10)
        def renderObject2 = renderObject(70, 70)

        def runs = [
                new ObjectRun(object1),
                new TextRun("text", style),
                new ObjectRun(object2),
        ]
        def lines = lines([
                [left: 0, width: 100, text: "\uFFFCtext\uFFFC"],
        ])

        def layouter = new ParagraphLayouter(
                childLayouter([
                        (object1): renderObject1,
                        (object2): renderObject2
                ]),
                textMetrics([
                        (style): [CHAR_WIDTH, CHAR_WIDTH, CHAR_WIDTH, ASCENT, DESCENT],
                ]),
                liner(lines)
        )

        when:
        RenderParagraph renderObject = layouter.layout(
                new LayoutParagraph(true, Locale.US, runs, 20, TextAlign.LEFT, new DefaultHangingConfig()),
                new LayoutArea(200, 200)
        )

        then:
        with(renderObject) {
            with(it.child(0).object() as RenderLine) {
                it.child(0).object() == renderObject1
                it.child(2).object() == renderObject2

                with(it.child(1).object()as RenderText) {
                    it.text().toString() == "text"
                    it.baseline() == 20
                    it.style() == style
                }

                childWidths(it) == [50, 4 * CHAR_WIDTH, 70]
                childHeights(it) == [10, 26, 70]
                childX(it) == [0, 50, 50 + 4 * CHAR_WIDTH]
                childY(it) == [70 - 10, 70 - 20, 70 - 70]
            }

            it.width() == 200
            it.height() == 70 + 6
            childWidths(it) == [200]
            childHeights(it) == [70 + 6]
            childX(it) == [0]
            childY(it) == [0]
        }
    }

    def "render hyphen"() {
        given:
        final CHAR_WIDTH1 = 10F
        final HYPHEN_WIDTH1 = 5F
        final ASCENT1 = -8F
        final DESCENT1 = 5F
        def style1 = GroovyMock(FontStyle)

        final CHAR_WIDTH2 = 20F
        final HYPHEN_WIDTH2 = 15F
        final ASCENT2 = -16F
        final DESCENT2 = 10F
        def style2 = GroovyMock(FontStyle)

        def object1 = GroovyMock(LayoutObject)
        def renderObject1 = renderObject(50, 10)

        def runs = [
                new TextRun("text", style1),
                new ObjectRun(object1),
                new TextRun("t", style1),
                new TextRun("ext2", style2)
        ]
        def lines = lines([
                [left: 0, width: 100, text: "t", hasHyphenAfter: true],
                [left: 0, width: 100, text: "ext", hasHyphenAfter: true],
                [left: 0, width: 100, text: "\uFFFC", hasHyphenAfter: true],
                [left: 0, width: 100, text: "text2", hasHyphenAfter: true],
        ])

        def layouter = new ParagraphLayouter(
                childLayouter([
                        (object1): renderObject1
                ]),
                textMetrics([
                        (style1): [CHAR_WIDTH1, CHAR_WIDTH1, HYPHEN_WIDTH1, ASCENT1, DESCENT1],
                        (style2): [CHAR_WIDTH2, CHAR_WIDTH2, HYPHEN_WIDTH2, ASCENT2, DESCENT2]
                ]),
                liner(lines)
        )

        when:
        RenderParagraph renderObject = layouter.layout(
                new LayoutParagraph(true, Locale.US, runs, 20, TextAlign.LEFT, new DefaultHangingConfig()),
                new LayoutArea(200, 200)
        )

        then:
        with(renderObject) {
            def HEIGHT1 = -ASCENT1 + DESCENT1
            def HEIGHT2 = -ASCENT2 + DESCENT2

            with(it.child(0).object() as RenderLine) {
                childTexts(it) == ["t", HYPHEN_STRING]
                childBaselines(it) == [-ASCENT1, -ASCENT1]
                childStyles(it) == [style1, style1]

                childWidths(it) == [1 * CHAR_WIDTH1, 1 * HYPHEN_WIDTH1]
                childHeights(it) == [HEIGHT1, HEIGHT1]
                childX(it) == [0, 1 * CHAR_WIDTH1]
                childY(it) == [0, 0]
            }

            with(it.child(1).object() as RenderLine) {
                childTexts(it) == ["ext", HYPHEN_STRING]
                childBaselines(it) == [-ASCENT1, -ASCENT1]
                childStyles(it) == [style1, style1]

                childWidths(it) == [3 * CHAR_WIDTH1, 1 * HYPHEN_WIDTH1]
                childHeights(it) == [HEIGHT1, HEIGHT1]
                childX(it) == [0, 3 * CHAR_WIDTH1]
                childY(it) == [0, 0]
            }

            with(it.child(2).object() as RenderLine) {
                it.child(0).object() == renderObject1

                childWidths(it) == [50]
                childHeights(it) == [10]
                childX(it) == [0]
                childY(it) == [0]
            }

            with(it.child(3).object() as RenderLine) {
                childTexts(it) == ["t", "ext2", HYPHEN_STRING]
                childBaselines(it) == [-ASCENT1, -ASCENT2, -ASCENT2]
                childStyles(it) == [style1, style2, style2]

                childWidths(it) == [1 * CHAR_WIDTH1, 4 * CHAR_WIDTH2, 1 * HYPHEN_WIDTH2]
                childHeights(it) == [HEIGHT1, HEIGHT2, HEIGHT2]
                childX(it) == [0, 1 * CHAR_WIDTH1, 1 * CHAR_WIDTH1 + 4 * CHAR_WIDTH2]
                childY(it) == [8, 0, 0]
            }

            it.width() == 200
            it.height() == HEIGHT1 + HEIGHT1 + 10 + HEIGHT2
            childWidths(it) == [200, 200, 200, 200]
            childHeights(it) == [HEIGHT1, HEIGHT1, 10, HEIGHT2]
            childX(it) == [0, 0, 0, 0]
            childY(it) == [0, HEIGHT1, HEIGHT1 + HEIGHT1, HEIGHT1 + HEIGHT1 + 10]
        }
    }

    def "align text right"() {
        given:
        final LETTER_WIDTH = 10
        final SPACE_WIDTH = 5
        def style = GroovyMock(FontStyle)

        def runs = [
                new TextRun(" text1   text2   text3 text4", style)
        ]
        def lines = lines([
                [left: -10, width: 180, text: " text1   text2 "],
                [left: 20, width: 50, text: "  t", hasHyphenAfter: true],
                [left: 0, width: 300, text: "ext3 text4"],
        ])

        def layouter = new ParagraphLayouter(
                childLayouter(),
                textMetrics([
                    (style): [LETTER_WIDTH, SPACE_WIDTH, LETTER_WIDTH]
                ]),
                liner(lines)
        )

        when:
        RenderParagraph renderObject = layouter.layout(
                new LayoutParagraph(true, Locale.US, runs, 20, TextAlign.RIGHT, new DefaultHangingConfig()),
                new LayoutArea(200, 200)
        )

        then:
        with(renderObject.child(0).object() as RenderLine) {
            def widths = [1 * SPACE_WIDTH, 5 * LETTER_WIDTH, 3 * SPACE_WIDTH, 5 * LETTER_WIDTH, 1 * SPACE_WIDTH]
            childWidths(it) == widths
            childX(it) == [
                    200 - 180,
                    20 + widths[0],
                    20 + widths[0] + widths[1],
                    20 + widths[0] + widths[1] + widths[2],
                    20 + widths[0] + widths[1] + widths[2] + widths[3]
            ]
        }

        with(renderObject.child(1).object() as RenderLine) {
            def widths = [2 * SPACE_WIDTH, 1 * LETTER_WIDTH, 1 * LETTER_WIDTH]
            childWidths(it) == widths
            childX(it) == [
                    200 - 50,
                    150 + widths[0],
                    150 + widths[0] + widths[1],
            ]
        }

        with(renderObject.child(2).object() as RenderLine) {
            def widths = [4 * LETTER_WIDTH, 1 * SPACE_WIDTH, 5 * LETTER_WIDTH]
            childWidths(it) == widths
            childX(it) == [
                    200 - 300,
                    -100 + widths[0],
                    -100 + widths[0] + widths[1],
            ]
        }
    }

    def "align text center"() {
        given:
        final LETTER_WIDTH = 10
        final SPACE_WIDTH = 5
        def style = GroovyMock(FontStyle)

        def runs = [
                new TextRun(" text1   text2   text3 text4", style)
        ]
        def lines = lines([
                [left: -10, width: 180, text: " text1   text2 "],
                [left: 20, width: 50, text: "  t", hasHyphenAfter: true],
                [left: 0, width: 300, text: "ext3 text4"],
        ])

        def layouter = new ParagraphLayouter(
                childLayouter(),
                textMetrics([
                    (style): [LETTER_WIDTH, SPACE_WIDTH, LETTER_WIDTH]
                ]),
                liner(lines)
        )

        when:
        RenderParagraph renderObject = layouter.layout(
                new LayoutParagraph(true, Locale.US, runs, 20, TextAlign.CENTER, new DefaultHangingConfig()),
                new LayoutArea(200, 200)
        )

        then:
        with(renderObject.child(0).object() as RenderLine) {
            def widths = [1 * SPACE_WIDTH, 5 * LETTER_WIDTH, 3 * SPACE_WIDTH, 5 * LETTER_WIDTH, 1 * SPACE_WIDTH]
            childWidths(it) == widths
            childX(it) == [
                    -10 + (200 + 10 - 180) / 2,
                    5 + widths[0],
                    5 + widths[0] + widths[1],
                    5 + widths[0] + widths[1] + widths[2],
                    5 + widths[0] + widths[1] + widths[2] + widths[3]
            ]
        }

        with(renderObject.child(1).object() as RenderLine) {
            def widths = [2 * SPACE_WIDTH, 1 * LETTER_WIDTH, 1 * LETTER_WIDTH]
            childWidths(it) == widths
            childX(it) == [
                    20 + (200 - 20 - 50) / 2,
                    85 + widths[0],
                    85 + widths[0] + widths[1],
            ]
        }

        with(renderObject.child(2).object() as RenderLine) {
            def widths = [4 * LETTER_WIDTH, 1 * SPACE_WIDTH, 5 * LETTER_WIDTH]
            childWidths(it) == widths
            childX(it) == [
                    0 + (200 - 300) / 2,
                    -50 + widths[0],
                    -50 + widths[0] + widths[1],
            ]
        }
    }

    def "align text justify"() {
        given:
        final LETTER_WIDTH = 10
        final SPACE_WIDTH = 5
        def style = GroovyMock(FontStyle)

        def runs = [
                new TextRun(" text1   t ext2   text3 text4  text5 ", style)
        ]
        def lines = lines([
                [left: -10, width: 180, text: " text1   t ext2 "],
                [left: 20, width: 50, text: "  t", hasHyphenAfter: true],
                [left: 0, width: 300, text: "ext3 t"],
                [left: 0, width: 100, text: "ext4  text5 ", isLast: true],
        ])

        def layouter = new ParagraphLayouter(
                childLayouter(),
                textMetrics([
                    (style): [LETTER_WIDTH, SPACE_WIDTH, LETTER_WIDTH]
                ]),
                liner(lines)
        )

        when:
        RenderParagraph renderObject = layouter.layout(
                new LayoutParagraph(true, Locale.US, runs, 20, TextAlign.JUSTIFY, new DefaultHangingConfig()),
                new LayoutArea(200, 200)
        )

        then:
        with(renderObject.child(0).object() as RenderLine) {
            float freeSpace = 200 - 180 + 10
            float midSpaceWidths = (3 + 1) * SPACE_WIDTH
            float midSpaceScaleX = (freeSpace + midSpaceWidths) / midSpaceWidths

            def widths = [
                    1 * SPACE_WIDTH,
                    5 * LETTER_WIDTH,
                    3 * SPACE_WIDTH * midSpaceScaleX,
                    1 * LETTER_WIDTH,
                    1 * SPACE_WIDTH * midSpaceScaleX,
                    4 * LETTER_WIDTH,
                    1 * SPACE_WIDTH
            ]
            childScaleX(it) == [1F, null, midSpaceScaleX, null, midSpaceScaleX, null, 1F]
            childWidths(it) == widths
            childX(it) == [
                    -10,
                    -10 + widths[0],
                    -10 + widths[0] + widths[1],
                    -10 + widths[0] + widths[1] + widths[2],
                    -10 + widths[0] + widths[1] + widths[2] + widths[3],
                    -10 + widths[0] + widths[1] + widths[2] + widths[3] + widths[4],
                    -10 + widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + widths[5]
            ]
        }

        with(renderObject.child(1).object() as RenderLine) {
            def widths = [2 * SPACE_WIDTH, 1 * LETTER_WIDTH, 1 * LETTER_WIDTH]
            childScaleX(it) == [1F, null, null]
            childWidths(it) == widths
            childX(it) == [
                    20,
                    20 + widths[0],
                    20 + widths[0] + widths[1],
            ]
        }

        with(renderObject.child(2).object() as RenderLine) {
            def widths = [4 * LETTER_WIDTH, 1 * SPACE_WIDTH, 1 * LETTER_WIDTH]
            childScaleX(it) == [null, 1F, null]
            childWidths(it) == widths
            childX(it) == [
                    0,
                    widths[0],
                    widths[0] + widths[1],
            ]
        }

        with(renderObject.child(3).object() as RenderLine) {
            def widths = [4 * LETTER_WIDTH, 2 * SPACE_WIDTH, 5 * LETTER_WIDTH, 1 * SPACE_WIDTH]
            childScaleX(it) == [null, 1F, null, 1F]
            childWidths(it) == widths
            childX(it) == [
                    0,
                    widths[0],
                    widths[0] + widths[1],
                    widths[0] + widths[1] + widths[2],
            ]
        }
    }

    def "render empty paragraph"() {
        given:
        def layouter = new ParagraphLayouter(
                childLayouter(),
                textMetrics(),
                liner(lines())
        )

        when:
        RenderParagraph renderObject = layouter.layout(
                new LayoutParagraph(true, Locale.US, [], 20, TextAlign.LEFT, new DefaultHangingConfig()),
                new LayoutArea(200, 200)
        )

        then:
        with(renderObject) {
            it.width() == 200
            it.height() == 0
            it.children().size() == 0
        }
    }

    def "compute lines width when not fit area width"() {
        given:
        def runs = [
                new TextRun("texttexttext", GroovyMock(FontStyle))
        ]
        def lines = lines([
                [left: 30, width: 100, text: "text"],
                [left: -10, width: 180, text: "text"],
                [left: 0, width: 50, text: "text"],
        ])

        def layouter = new ParagraphLayouter(
                childLayouter(),
                textMetrics(),
                liner(lines)
        )

        when:
        RenderParagraph renderObject = layouter.layout(
                new LayoutParagraph(false, Locale.US, runs, 20, TextAlign.LEFT, new DefaultHangingConfig()),
                new LayoutArea(200, 200)
        )

        then:
        renderObject.width() == 170
        childWidths(renderObject) == [170, 170, 170]

        when:
        renderObject = layouter.layout(
                new LayoutParagraph(false, Locale.US, runs, 20, TextAlign.RIGHT, new DefaultHangingConfig()),
                new LayoutArea(200, 200)
        )

        then:
        renderObject.width() == 170
        childWidths(renderObject) == [170, 170, 170]
        childX(renderObject.child(0).object()) == [170 - 100]
        childX(renderObject.child(1).object()) == [170 - 180]
        childX(renderObject.child(2).object()) == [170 - 50]

        when:
        renderObject = layouter.layout(
                new LayoutParagraph(false, Locale.US, runs, 20, TextAlign.CENTER, new DefaultHangingConfig()),
                new LayoutArea(200, 200)
        )

        then:
        renderObject.width() == 170
        childWidths(renderObject) == [170, 170, 170]
        childX(renderObject.child(0).object()) == [30 + (170 - 30 - 100) / 2]
        childX(renderObject.child(1).object()) == [-10 + (170 + 10 - 180) / 2]
        childX(renderObject.child(2).object()) == [0 + (170 + 0 - 50) / 2]
    }

    def childLayouter(layoutToRenderObject = [:]) {
        return new Layouter<LayoutObject, RenderObject>() {
            @Override
            RenderObject layout(LayoutObject object, LayoutArea area) {
                return layoutToRenderObject.get(object)
            }
        }
    }

    def renderObject(width, height) {
        return new RenderObject(width, height, emptyList()) {
            @Override
            boolean canPartiallyPainted() {
                return false
            }
        }
    }

    def textMetrics() {
        return new TextMetrics() {
            @Override
            float[] charWidths(CharSequence text, FontStyle style) {
                return new float[text.length()]
            }

            @Override
            TextMetrics.VerticalMetrics verticalMetrics(FontStyle style) {
                return new TextMetrics.VerticalMetrics()
            }
        }
    }

    def textMetrics(styleToParams) {
        return new TextMetrics() {
            @Override
            float[] charWidths(CharSequence text, FontStyle style) {
                def params = styleToParams.get(style)
                float letterWidth = params[0] ?: 0
                float spaceWidth = params[1] ?: 0
                float hyphenWidth = params[2] ?: 0
                if (text == HYPHEN_STRING) {
                    return [hyphenWidth]
                } else {
                    float[] widths = new float[text.length()]
                    for (int i = 0; i < text.length(); i++) {
                        char ch = text.charAt(i)
                        widths[i] = ch == ' ' as char ? spaceWidth : letterWidth
                    }
                    return widths
                }
            }

            @Override
            TextMetrics.VerticalMetrics verticalMetrics(FontStyle style) {
                def params = styleToParams.get(style)
                return new TextMetrics.VerticalMetrics() {{
                    ascent = params[3] ?: 0
                    descent = params[4] ?: 0
                    leading = params[5] ?: 0
                }}
            }
        }
    }

    def liner(lines = []) {
        return new Liner() {
            @Override
            List<Liner.Line> makeLines(Liner.MeasuredText measuredText, Liner.Config config) {
                return lines
            }
        }
    }

    def lines(infoList) {
        def lines = []
        int beginIndex = 0
        for (def info : infoList) {
            lines.add(
                    Stub(Liner.Line) {
                        it.left() >> (info.left ?: 0)
                        it.width() >> (info.width ?: 0)
                        it.hasHyphenAfter() >> (info.hasHyphenAfter ?: false)
                        it.isLast() >> (info.isLast ?: false)
                        it.tokens() >> (info.text ? tokens(beginIndex, info.text) : emptyList())
                    }
            )
            beginIndex += info.text.length()
        }
        return lines
    }

    def tokens(beginIndex, text) {
        def isSpace = { index -> text.charAt(index) == " " }

        def tokens = []

        int begin = 0
        for (int end = 1; end <= text.length(); end++) {
            if (end == text.length() || isSpace(end) != isSpace(begin)) {
                tokens.add(
                        Stub(Liner.Token) {
                            it.isSpace() >> isSpace(begin)
                            it.beginIndex() >> beginIndex + begin
                            it.endIndex() >> beginIndex + end
                        }
                )
                begin = end
            }
        }

        return tokens
    }

    def childWidths(renderObject) {
        return renderObject.children().collect({
            it.object().width()
        })
    }

    def childHeights(renderObject) {
        return renderObject.children().collect({
            it.object().height()
        })
    }

    def childX(renderObject) {
        return renderObject.children().collect({
            it.x()
        })
    }

    def childY(renderObject) {
        return renderObject.children().collect({
            it.y()
        })
    }

    def childTexts(renderLine) {
        return renderLine.children().collect({
            it.object().text().toString()
        })
    }

    def childScaleX(renderLine) {
        return renderLine.children().collect({
            if (it.object() instanceof RenderSpace) {
                it.object().scaleX()
            } else {
                null
            }
        })
    }

    def childBaselines(renderLine) {
        return renderLine.children().collect({
            it.object().baseline()
        })
    }

    def childStyles(renderLine) {
        return renderLine.children().collect({
            it.object().style()
        })
    }
}

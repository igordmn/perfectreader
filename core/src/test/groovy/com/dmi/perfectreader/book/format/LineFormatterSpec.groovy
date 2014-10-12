package com.dmi.perfectreader.book.format

import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.content.ContentHandler
import com.dmi.perfectreader.book.content.Text
import com.dmi.perfectreader.book.font.FontFace
import com.dmi.perfectreader.book.format.line.Liner
import com.dmi.perfectreader.book.item.TextBreak
import com.dmi.perfectreader.book.position.Position
import spock.lang.Specification

import static com.dmi.perfectreader.book.content.Size.size
import static com.dmi.perfectreader.book.position.Position.toPosition
import static com.dmi.perfectreader.book.position.Range.range

class LineFormatterSpec extends Specification {
    def lineHeight = 20
    def lineWidth = 100
    def symbolWidth = 5
    def fontFace = Mock(FontFace)
    def pageConfig = new PageConfig(lineWidth, 0, 0, 0, 0, 0)
    def formatConfig = new FormatConfig(12, 5)

    def "should format single line"() {
        given:
        def liner = new TestLiner();
        def formatter = new LineFormatter(pageConfig, formatConfig, liner)

        when:
        def lines = new Lines()
        Formatter.Appender appender = formatter.format(lines)
        appendText(appender, text)

        then:
        lines.mainParams() == expectedLines

        where:
        [text, expectedLines] << [
                [
                        "abc",
                        [
                                [
                                        begin: Position.BEGIN,
                                        end: Position.END,
                                        text: "abc",
                                        coordinates: [0, 0, 5, 0, 10, 0],
                                        size: size(100, 20)
                                ]
                        ]
                ]
        ]
    }

    def "should format paragraphs"() {
        given:
        def liner = new TestLiner();
        def formatter = new LineFormatter(pageConfig, formatConfig, liner)

        when:
        def lines = new Lines()
        Formatter.Appender appender = formatter.format(lines)
        appendText(appender, text)

        then:
        lines.mainParams() == expectedParams

        where:
        [text, expectedParams] << [
                [
                        "\nabc",
                        [
                                [
                                        begin: toPosition(0, 4),
                                        end: toPosition(1, 4),
                                        text: "\n",
                                        coordinates: [0, 0],
                                        size: size(100, 20)
                                ],
                                [
                                        begin: toPosition(1, 4),
                                        end: toPosition(4, 4),
                                        text: "abc",
                                        coordinates: [12, 5, 17, 5, 22, 5],
                                        size: size(100, 20 + 5)
                                ]
                        ]
                ],

                [
                        "\nabc\n",
                        [
                                [
                                        begin: toPosition(0, 5),
                                        end: toPosition(1, 5),
                                        text: "\n",
                                        coordinates: [0, 0],
                                        size: size(100, 20)
                                ],
                                [
                                        begin: toPosition(1, 5),
                                        end: toPosition(5, 5),
                                        text: "abc\n",
                                        coordinates: [12, 5, 17, 5, 22, 5, 27, 5],
                                        size: size(100, 20 + 5)
                                ]
                        ]
                ],

                [
                        "\n\n\n",
                        [
                                [
                                        begin: toPosition(0, 3),
                                        end: toPosition(1, 3),
                                        text: "\n",
                                        coordinates: [0, 0],
                                        size: size(100, 20)
                                ],
                                [
                                        begin: toPosition(1, 3),
                                        end: toPosition(2, 3),
                                        text: "\n",
                                        coordinates: [12, 5],
                                        size: size(100, 20 + 5)
                                ],
                                [
                                        begin: toPosition(2, 3),
                                        end: toPosition(3, 3),
                                        text: "\n",
                                        coordinates: [12, 5],
                                        size: size(100, 20 + 5)
                                ]
                        ]
                ],

                [
                        "abcde\na\nb",
                        [
                                [
                                        begin: toPosition(0, 9),
                                        end: toPosition(6, 9),
                                        text: "abcde\n",
                                        coordinates: [0, 0, 5, 0, 10, 0, 15, 0, 20, 0, 25, 0],
                                        size: size(100, 20)
                                ],
                                [
                                        begin: toPosition(6, 9),
                                        end: toPosition(8, 9),
                                        text: "a\n",
                                        coordinates: [12, 5, 17, 5],
                                        size: size(100, 20 + 5)
                                ],
                                [
                                        begin: toPosition(8, 9),
                                        end: toPosition(9, 9),
                                        text: "b",
                                        coordinates: [12, 5],
                                        size: size(100, 20 + 5)
                                ]
                        ]
                ],
        ]
    }

    void appendText(Formatter.Appender appender, String text) {
        appender.appendFontFace(Position.BEGIN, fontFace)
        for (int i = 0; i < text.length(); i++) {
            def position = toPosition(i, text.length())
            if (text[i] == '\n') {
                appender.appendBreak position, TextBreak.PARAGRAPH
            } else {
                appender.appendChar position, text[i] as char
            }
        }
        appender.finish(Position.END)
    }

    Content expectedContent(def params) {
        return new Content(
                range(params.begin, params.end),
                new Text(
                        expectedCodepoints(params.text),
                        expectedCoordinates(params.text),
                        size(lineWidth, lineHeight)
                ),
                size(lineWidth, lineHeight)
        )
    }

    int[] expectedCodepoints(String string) {
        return string.toCharArray()
    }

    float[] expectedCoordinates(String string) {
        float[] coordinates = new float[string.length() * 2]
        int k = 0;
        float x = 0
        float y = 0
        for (int i = 0; i < string.length(); i++) {
            coordinates[k++] = x
            coordinates[k++] = y
            x += symbolWidth
        }
        return coordinates
    }

    class Lines implements ContentHandler {
        List<Content> list = new ArrayList<>()

        @Override
        void handleContent(Content content) {
            list.add(content)
        }

        def mainParams() {
            List<Map> mapList = new ArrayList<>()
            for (Content content : list) {
                mapList.add([
                        begin: content.range().begin(),
                        end: content.range().end(),
                        text: new String(content.text().codepoints() as char[]),
                        coordinates: content.text().coordinates(),
                        size: content.text().size()
                ])
            }
            return mapList
        }
    }

    class TestLiner implements Liner {
        @Override
        Liner.Appender makeLines(
                Liner.CurrentWidthProvider currentWidthProvider,
                ContentHandler contentHandler) {

            return new Liner.Appender() {
                List<String> lines = new ArrayList<>()
                List<Position> lineBegins = new ArrayList<>()
                StringBuilder currentLineBuilder = new StringBuilder()

                @Override
                void appendFontFace(Position position, FontFace fontFace) {
                }

                @Override
                void appendChar(Position position, char character) {
                    if (currentLineBuilder.length() == 0) {
                        lineBegins.add(position)
                    }

                    currentLineBuilder.append(character)

                    if (character == '\n') {
                        lines.add(currentLineBuilder.toString())
                        currentLineBuilder.setLength(0)
                    }
                }

                @Override
                void finish(Position position) {
                    if (currentLineBuilder.length() > 0) {
                        lines.add(currentLineBuilder.toString())
                    }

                    for (int i = 0; i < lines.size(); i++) {
                        Position begin = lineBegins[i]
                        Position end = i + 1 < lines.size() ? lineBegins[i + 1] : position
                        def content = expectedContent(begin: begin, end: end, text: lines[i])
                        contentHandler.handleContent(content)
                    }
                }
            }
        }
    }
}

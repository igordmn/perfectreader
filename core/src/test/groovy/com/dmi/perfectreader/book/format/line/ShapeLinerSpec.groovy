package com.dmi.perfectreader.book.format.line

import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.content.ContentHandler
import com.dmi.perfectreader.book.content.Size
import com.dmi.perfectreader.book.font.FontFace
import com.dmi.perfectreader.book.format.line.Liner.Appender
import com.dmi.perfectreader.book.format.shape.Shape
import com.dmi.perfectreader.book.format.shape.Shaper
import com.dmi.perfectreader.book.position.Position
import com.dmi.perfectreader.book.position.Range
import spock.lang.Specification

import static com.dmi.perfectreader.book.content.Size.size
import static com.dmi.perfectreader.book.position.Position.toPosition
import static com.dmi.perfectreader.book.position.Range.range
import static java.lang.Math.min

class ShapeLinerSpec extends Specification {
    float glyphAdvanceX = 10
    float glyphAdvanceYfirst = 2
    float glyphWidth = 5
    float ffLigatureWidth = 3

    float ascent = 12
    float descent = -3
    float linegap = 1

    def glyphMap = [
            " " : " ", "a" : "a", "b" : "b", "c" : "c", "d" : "d", "e" : "e", "f" : "f", "g" : "g",
            "h" : "h", "i" : "i", "j" : "j", "k" : "k", "l" : "l", "m" : "m", "n" : "n", "o" : "o",
            "p" : "p", "q" : "q", "r" : "r", "s" : "s", "t" : "t", "u" : "u", "v" : "v", "w" : "w",
            "x" : "x", "y" : "y", "z" : "z", "á" : "á", "ffi" : "ﬃ", "ff" : "ﬀ", "fi" : "ﬁ",
            "." : ".", "," : ",", "\"": "\"", "…" : "...", "..." : "…", "-" : "-", "\n" : " "
    ]

    FontFace fontFace = Mock()

    def "should break to lines"() {
        given:
        def text = "this is á difficult algorithm"
        def breakConfig = new BreakConfig([] as char[], '-' as char);

        def shaper = new TestShaper();
        def textBreaker = new TestTextBreaker();
        def liner = new ShapeLiner(breakConfig, shaper, textBreaker)

        expect:
        "this is á diﬃcult algorithm" == shapeGlyphs(shaper.shape(fontFace, text.toCharArray(), 0, text.size()))

        when:
        def lines = makeLines(liner, text, [width] as float[])

        then:
        assertLines text, lines, breakConfig, expectedLines

        where:
        [width, expectedLines] <<
                        multiArray(2,
                                1000,
                                [
                                        0, "this is á diﬃcult algorithm"
                                ],

                                275,
                                [
                                        0, "this is á diﬃcult algorithm"
                                ],

                                274,
                                [
                                        0, "this is á diﬃcult algorith",
                                        28, "m"
                                ],

                                265,
                                [
                                        0, "this is á diﬃcult algorith",
                                        28, "m"
                                ],

                                264,
                                [
                                        0, "this is á diﬃcult algorit",
                                        27, "hm"
                                ],

                                195,
                                [
                                        0, "this is á diﬃcult a",
                                        21, "lgorithm"
                                ],

                                194,
                                [
                                        0, "this is á diﬃcult ",
                                        20, "algorithm"
                                ],

                                180,
                                [
                                        0, "this is á diﬃcult ",
                                        20, "algorithm"
                                ],

                                179,
                                [
                                        0, "this is á diﬃcult",
                                        19, " algorithm"
                                ],

                                175,
                                [
                                        0, "this is á diﬃcult",
                                        19, " algorithm"
                                ],

                                174,
                                [
                                        0, "this is á diﬃcul",
                                        18, "t algorithm"
                                ],

                                135,
                                [
                                        0, "this is á diﬃ",
                                        15, "cult algorithm"
                                ],

                                134,
                                [
                                        0, "this is á diﬀ",
                                        14, "icult algorit",
                                        27, "hm"
                                ],

                                133,
                                [
                                        0, "this is á diﬀ",
                                        14, "icult algorit",
                                        27, "hm"
                                ],

                                132,
                                [
                                        0, "this is á di",
                                        12, "ﬃcult algorit",
                                        27, "hm"
                                ],

                                95,
                                [
                                        0, "this is á",
                                        9, " diﬃcult a",
                                        21, "lgorithm"
                                ],

                                94,
                                [
                                        0, "this is ",
                                        8, "á diﬃcul",
                                        18, "t algorit",
                                        27, "hm"
                                ],

                                0,
                                [
                                        0, "t",
                                        1, "h",
                                        2, "i",
                                        3, "s",
                                        4, " ",
                                        5, "i",
                                        6, "s",
                                        7, " ",
                                        8, "á",
                                        9, " ",
                                        10, "d",
                                        11, "i",
                                        12, "f",
                                        13, "f",
                                        14, "i",
                                        15, "c",
                                        16, "u",
                                        17, "l",
                                        18, "t",
                                        19, " ",
                                        20, "a",
                                        21, "l",
                                        22, "g",
                                        23, "o",
                                        24, "r",
                                        25, "i",
                                        26, "t",
                                        27, "h",
                                        28, "m"
                                ]
                        )
    }

    def "should process small text"() {
        given:
        def breakConfig = new BreakConfig([] as char[], '-' as char);

        def shaper = new TestShaper();
        def textBreaker = new TestTextBreaker();
        def liner = new ShapeLiner(breakConfig, shaper, textBreaker)

        when:
        def lines = makeLines(liner, text, [width] as float[])

        then:
        assertLines text, lines, breakConfig, expectedLines

        where:
        [text, width, expectedLines] << multiArray(3,
                "", 100, [
                ],

                "", 0, [
                ],

                ".", 100, [
                        0, "."
                ],

                ".", 0, [
                        0, "."
                ],

                "...", 100, [
                        0, "…"
                ],

                "...", 0, [
                        0, ".",
                        1, ".",
                        2, "."
                ],

                "…", 100, [
                        0, "..."
                ],

                "…", 0, [
                        0, "..."
                ],

                "á", 100, [
                        0, "á"
                ],

                "á", 0, [
                        0, "á"
                ]
        )
    }

    def "should process big text"() {
        given:
        def text = "  this is á difficult algorithm    this is á dificult algorithm" +
                "t h i s i s á d i f ic u l t a l g o r i t h     this is á" +
                "difffffffififififfifififiififififififififififififiififificult algorithm" +
                "thisisádificultalgorithmthisisádificultalgorithmthisisádificultalgorithmthisisádifi" +
                "ultalgorithmthisisádificultalgorithmthisisádificultalgorithmthisisádificultalgorithm" +
                "thisisádificultalgorithmthisisádificultalgorithmthisisádificultalgorithmadb fjabv " +
                "bgiuer gibgib bkjbkjb fgfgdf g dfg re g erg gf gre gemhgmqeff  "
        def breakConfig = new BreakConfig([] as char[], '-' as char);

        def shaper = new TestShaper();
        def textBreaker = new TestTextBreaker();
        def liner = new ShapeLiner(breakConfig, shaper, textBreaker,
                shapeBufferSize, shapeBufferSize, shapeBufferSize / 2 as int)

        when:
        def lines = makeLines(liner, text, 100)

        then:
        assertLines text, lines, breakConfig, [
                0, "  this is ",
                10, "á diﬃcult ",
                22, "algorithm  ",
                33, "  this is ",
                43, "á diﬁcult ",
                54, "algorithmt ",
                65, "h i s i s ",
                75, "á d i f i",
                84, "c u l t a ",
                94, "l g o r i ",
                104, "t h     th",
                114, "is is ádi",
                123, "ﬀﬀﬀﬁﬁﬁﬁﬃﬁﬁ",
                144, "ﬁiﬁﬁﬁﬁﬁﬁﬁﬁ",
                163, "ﬁﬁﬁﬁiﬁﬁﬁcu",
                180, "lt algorit",
                190, "hmthisisá",
                199, "diﬁcultalg",
                210, "orithmthis",
                220, "isádiﬁcul",
                230, "talgorithm",
                240, "thisisádi",
                249, "ﬁcultalgor",
                260, "ithmthisis",
                270, "ádiﬁultal",
                280, "gorithmthi",
                290, "sisádiﬁcu",
                300, "ltalgorith",
                310, "mthisisád",
                319, "iﬁcultalgo",
                330, "rithmthisi",
                340, "sádiﬁcult",
                350, "algorithmt",
                360, "hisisádiﬁ",
                370, "cultalgori",
                380, "thmthisis",
                389, "ádiﬁculta",
                399, "lgorithmth",
                409, "isisádiﬁc",
                419, "ultalgorit",
                429, "hmadb fjab",
                439, "v bgiuer g",
                449, "ibgib bkjb",
                459, "kjb fgfgdf ",
                470, "g dfg re g ",
                481, "erg gf gre ",
                492, "gemhgmqeﬀ  "
        ]

        where:
        shapeBufferSize << [64 * 1024, 1024, 504, 503, 250, 100]
    }

    def "should make lines with different widths"() {
        given:
        def text = "this is á difficult algorithm"
        def breakConfig = new BreakConfig([] as char[], '-' as char);

        def shaper = new TestShaper();
        def textBreaker = new TestTextBreaker();
        def liner = new ShapeLiner(breakConfig, shaper, textBreaker)

        when:
        def lines = makeLines(liner, text, 94, 0, 33, 1000)

        then:
        assertLines text, lines, breakConfig, [
                0, "this is ",
                8, "á",
                9, " diﬀ",
                14, "icult algorithm"
        ]
    }

    def "should not break spaces"() {
        given:
        def breakConfig = new BreakConfig([' ', '\n'] as char[], '-' as char);

        def shaper = new TestShaper();
        def textBreaker = new TextBreaker() {
            @Override
            TextBreaker.BreakResult breakText(char[] chars, int begin, int end, int leftLimit, int rightLimit) {
                def paragraphIndex = indexOf(chars, begin, end, '\n' as char)
                def breakIndex = paragraphIndex >= 0 ? min(paragraphIndex + 1, end) : end

                return new TextBreaker.BreakResult(breakIndex, false)
            }

            int indexOf(char[] chars, int begin, int end, char ch) {
                for (int i = begin; i < end; i++) {
                    if (chars[i] == ch) {
                        return i
                    }
                }
                return -1
            }
        };
        def liner = new ShapeLiner(breakConfig, shaper, textBreaker)

        expect:
        expectedSize(" a  ", breakConfig) == size(15, 16)
        expectedCoordinates(" a  ") == [0, 12, 10, 14, 20, 14, 30, 14]

        when:
        def lines = makeLines(liner, text, widths as float[])

        then:
        assertLines text, lines, breakConfig, expectedLines

        where:
        [text, widths, expectedLines] << multiArray(3,
                "                  this is á diffi      cult algorithm                                              g",
                [100, 94, 0, 35, 140, 1000], [
                        0, "                  ",
                        18, "this is ",
                        26, "á ",
                        28, "diﬃ      ",
                        39, "cult algorithm                                              ",
                        99, "g"
                ],

                " this is á difficult algorithm    ", [200], [
                        0, " this is á diﬃcult ",
                        21, "algorithm    "
                ],

                " ", [0], [
                        0, " "
                ],

                "         ", [0], [
                        0, "         "
                ],

                "         .", [0], [
                        0, "         ",
                        9, "."
                ],

                " a", [0], [
                        0, " ",
                        1, "a"
                ],

                "\n", [0], [
                        0, " "
                ],

                "\n ", [0], [
                        0, " ",
                        1, " "
                ],

                " \n ", [0], [
                        0, "  ",
                        2, " "
                ],

                "\n\n", [0], [
                        0, " ",
                        1, " "
                ],

                "\n \n ", [0], [
                        0, " ",
                        1, "  ",
                        3, " "
                ],

                " \n a", [0], [
                        0, "  ",
                        2, " ",
                        3, "a",
                ],

                "     \n     a     ", [0], [
                        0, "      ",
                        6, "     ",
                        11, "a     ",
                ],

                "     \n     a     ", [0], [
                        0, "      ",
                        6, "     ",
                        11, "a     ",
                ],

                "abc \n d", [25], [
                        0, "abc  ",
                        5, " d"
                ]
        )
    }

    def "should add hyphens"() {
        given:
        def text = "this is á difficult algorithm"
        def breakConfig = new BreakConfig([' '] as char[], '-' as char);

        def shaper = new TestShaper();
        def textBreaker = new TextBreaker() {
            @Override
            TextBreaker.BreakResult breakText(char[] chars, int begin, int end, int leftLimit, int rightLimit) {
                int breakIndex = end - 5 > begin && chars[end - 1] == 'i' ? end - 3 : end
                def wordBreaked = breakIndex < rightLimit && chars[breakIndex - 1] != ' '
                return new TextBreaker.BreakResult(breakIndex, wordBreaked)
            }
        };
        def liner = new ShapeLiner(breakConfig, shaper, textBreaker)

        when:
        def lines = makeLines(liner, text, [width] as float[])

        then:
        assertLines text, lines, breakConfig, expectedLines

        where:
        [width, expectedLines] << multiArray(2,
                275, [
                        0, "this is á diﬃcult algorithm"
                ],

                274, [
                        0, "this is á diﬃcult algorit-",
                        27, "hm"
                ],

                265, [
                        0, "this is á diﬃcult algorit-",
                        27, "hm"
                ],

                264, [
                        0, "this is á diﬃcult alg-",
                        23, "orithm"
                ],

                195, [
                        0, "this is á diﬃcult ",
                        20, "algorithm"
                ],

                175, [
                        0, "this is á diﬃcult ",
                        20, "algorithm"
                ],

                174, [
                        0, "this is á diﬃcu-",
                        17, "lt algorithm"
                ],

                135, [
                        0, "this is á di-",
                        12, "ﬃcult algorit-",
                        27, "hm"
                ],

                0, [
                        0, "t-",
                        1, "h-",
                        2, "i-",
                        3, "s ",
                        5, "i-",
                        6, "s ",
                        8, "á ",
                        10, "d-",
                        11, "i-",
                        12, "f-",
                        13, "f-",
                        14, "i-",
                        15, "c-",
                        16, "u-",
                        17, "l-",
                        18, "t ",
                        20, "a-",
                        21, "l-",
                        22, "g-",
                        23, "o-",
                        24, "r-",
                        25, "i-",
                        26, "t-",
                        27, "h-",
                        28, "m"
                ],
        )
    }

    Lines makeLines(Liner liner, String text, float... widths) {
        def widthProvider = new TestWidths(widths)
        def lines = new Lines(widthProvider)
        def appender = liner.makeLines(widthProvider, lines)
        appendText(appender, text)
        return lines
    }

    void appendText(Appender appender, String text) {
        appender.appendFontFace(Position.BEGIN, fontFace)
        for (int i = 0; i < text.length(); i++) {
            appender.appendChar toPosition(i, text.length()), text.charAt(i)
        }
        appender.finish(Position.END)
    }

    void assertLines(String text, Lines lines, BreakConfig breakConfig, List<Object> expectedLineData) {
        int expectedCount = expectedLineData.size() / 2
        assert expectedCount == lines.size()
        for (int i = 0; i < lines.size(); i++) {
            Content actualLine = lines[i]
            int expectedBegin = expectedLineData[i * 2]
            int expectedEnd = i + 1 < lines.size() ? expectedLineData[(i + 1) * 2] : text.length()
            String expectedGlyphs = expectedLineData[i * 2 + 1]
            assert expectedGlyphs == contentGlyphs(actualLine)
            Range expectedRange = range toPosition(expectedBegin, text.length()), toPosition(expectedEnd, text.length())
            assert expectedRange == actualLine.range()
            assert expectedSize(expectedGlyphs, breakConfig) == actualLine.size()
            assert expectedCoordinates(expectedGlyphs) == actualLine.text().coordinates()
        }
    }

    String shapeGlyphs(Shape shape) {
        StringBuilder glyphString = new StringBuilder()
        for (int codepoint : shape.glyphs()) {
            glyphString.append((char) codepoint)
        }
        return glyphString.toString()
    }

    String contentGlyphs(Content content) {
        StringBuilder glyphString = new StringBuilder()
        for (int codepoint : content.text().codepoints()) {
            glyphString.append((char) codepoint)
        }
        return glyphString.toString()
    }

    Size expectedSize(String glyphs, BreakConfig breakConfig) {
        String trimmedGlyphs = breakConfig.blankChars().size() > 0 ? rtrim(glyphs) : glyphs;

        int lastIndex = trimmedGlyphs.size() - 1
        float width = lastIndex >= 0 ?
                glyphAdvanceX * lastIndex + glyphWidth(trimmedGlyphs[lastIndex] as char) :
                0
        float height = ascent - descent + linegap

        return size(width, height)
    }

    private static String rtrim(String str) {
        return str.replaceAll('\\s+$','');
    }

    private float glyphWidth(char glyph) {
        return glyph == 'ﬀ' ? ffLigatureWidth :
                glyph == ' ' ? 0 : glyphWidth
    }

    float[] expectedCoordinates(String glyphs) {
        float[] coordinates = new float[2 * glyphs.length()]
        float x = 0

        for (int i = 0; i < 2 * glyphs.length(); i += 2) {
            coordinates[i] = x
            x += glyphAdvanceX
        }
        for (int i = 1; i < 2 * glyphs.length(); i += 2) {
            coordinates[i] = (i > 1 ? glyphAdvanceYfirst : 0) + ascent
        }

        return coordinates
    }

    Object[][] multiArray(int itemLength, Object[] array) {
        Object[][] newArray = new Object[array.length / itemLength][]
        for (int i = 0; i < array.length / itemLength; i++) {
            newArray[i] = new Object[itemLength]
            for (int j = 0; j < itemLength; j++) {
                newArray[i][j] = array[i * itemLength + j]
            }
        }
        return newArray
    }

    class TestShaper implements Shaper {
        @Override
        Shape shape(FontFace fontFace, char[] chars, int offset, int length) {
            List<Character> glyphList = new ArrayList<>()
            List<Integer> glyphCharIndices = new ArrayList<>()

            int i = offset
            while (i < offset + length) {
                String piece1 = piece(chars, offset + length, i, 1)
                String piece2 = piece(chars, offset + length, i, 2)
                String piece3 = piece(chars, offset + length, i, 3)

                int oldI = i

                String glyph
                if (piece3 && glyphMap[piece3]) {
                    glyph = glyphMap[piece3]
                    i += 3
                } else if (piece2 && glyphMap[piece2]) {
                    glyph = glyphMap[piece2]
                    i += 2
                } else if (piece1 && glyphMap[piece1]) {
                    glyph = glyphMap[piece1]
                    i += 1
                } else {
                    glyph = ""
                    i += 1
                }

                for (int j = 0; j < glyph.length(); j++) {
                    glyphList.add(glyph.charAt(j))
                    glyphCharIndices.add(oldI)
                }
            }

            int[] glyphs = new int[glyphList.size()]
            int[] charIndices = new int[glyphList.size()]
            int[] glyphIndices = new int[length]
            float[] advanceX = new float[glyphList.size()]
            float[] advanceY = new float[glyphList.size()]
            float[] widths = new float[glyphList.size()]

            for (i = 0; i < glyphList.size(); i++) {
                glyphs[i] = (int) glyphList.get(i)
                charIndices[i] = glyphCharIndices.get(i)
                advanceX[i] = glyphAdvanceX
                advanceY[i] = i == 0 ? glyphAdvanceYfirst : 0
                widths[i] = glyphWidth(glyphList.get(i))
            }

            Arrays.fill(glyphIndices, - 1)
            for (i = glyphs.length - 1; i >= 0; i--) {
                glyphIndices[charIndices[i] - offset] = i
            }
            for (i = 1; i < length; i++) {
                if (glyphIndices[i] == -1) {
                    glyphIndices[i] = glyphIndices[i - 1]
                }
            }

            return new Shape(chars, offset, length,
                    glyphs, charIndices, glyphIndices, advanceX, advanceY, widths,
                    ascent, descent, linegap)
        }

        String piece(char[] chars, int limit, int index, int count) {
            if (index + count <= limit) {
                StringBuilder piece = new StringBuilder()
                for (int i = index; i < index + count; i++) {
                    piece.append(chars[i])
                }
                return piece.toString()
            } else {
                return null
            }
        }
    }

    class TestTextBreaker implements TextBreaker {
        @Override
        TextBreaker.BreakResult breakText(char[] chars, int begin, int end, int leftLimit, int rightLimit) {
            return new TextBreaker.BreakResult(end, false)
        }
    }

    class TestWidths implements Liner.CurrentWidthProvider {
        float[] widths
        int index = 0

        TestWidths(float... widths) {
            this.widths = widths
        }

        @Override
        float currentWidth() {
            return widths[index]
        }

        void nextWidth() {
            index++
            if (index >= widths.size()) {
                index = widths.size() - 1
            }
        }
    }

    class Lines implements ContentHandler {
        TestWidths widthProvider
        List<Content> list = new ArrayList<>()

        Lines(TestWidths widthProvider) {
            this.widthProvider = widthProvider
        }

        @Override
        void handleContent(Content content) {
            list.add(content)
            widthProvider.nextWidth()
        }

        def getAt(int index) {
            return list.get(index)
        }

        def size() {
            return list.size()
        }
    }
}

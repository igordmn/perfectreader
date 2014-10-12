package com.dmi.perfectreader.book.format.line;

import com.dmi.perfectreader.book.content.Content;
import com.dmi.perfectreader.book.content.ContentHandler;
import com.dmi.perfectreader.book.content.Size;
import com.dmi.perfectreader.book.content.Text;
import com.dmi.perfectreader.book.font.FontFace;
import com.dmi.perfectreader.book.format.shape.Shape;
import com.dmi.perfectreader.book.format.shape.Shaper;
import com.dmi.perfectreader.book.position.Position;
import com.dmi.perfectreader.book.position.Range;
import com.google.common.primitives.Chars;

import static com.dmi.perfectreader.book.content.Size.size;
import static com.dmi.perfectreader.book.format.shape.Shapes.firstCharIndex;
import static com.dmi.perfectreader.book.format.shape.Shapes.firstGlyphIndex;
import static com.dmi.perfectreader.book.format.shape.Shapes.glyphsHeight;
import static com.dmi.perfectreader.book.format.shape.Shapes.glyphsWidth;
import static com.dmi.perfectreader.book.format.shape.Shapes.isFirstGlyphOfChar;
import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.min;
import static java.lang.System.arraycopy;

public class ShapeLiner implements Liner {
    public final static int DEFAULT_SHAPE_BUFFER_SIZE = 64 * 1024;
    public final static int DEFAULT_MAX_WIDTH = 64 * 1024;
    // Сколько в конце буфера будет оставаться символов.
    // Они могут быть необходимы для определения переноса слова (для textBreaker'а).
    public final static int DEFAULT_REMAINING_CHAR_COUNT = 128;

    private final BreakConfig breakConfig;
    private final Shaper shaper;
    private final TextBreaker textBreaker;

    private final int shapeBufferSize;
    private final int maxWidth;
    private final int remainingCharCount;

    public ShapeLiner(BreakConfig breakConfig, Shaper shaper, TextBreaker textBreaker) {
        this(breakConfig, shaper, textBreaker,
                DEFAULT_SHAPE_BUFFER_SIZE, DEFAULT_MAX_WIDTH, DEFAULT_REMAINING_CHAR_COUNT);
    }

    public ShapeLiner(BreakConfig breakConfig, Shaper shaper, TextBreaker textBreaker,
                      int shapeBufferSize, int maxWidth, int remainingCharCount) {
        checkArgument(shapeBufferSize >= 10);
        checkArgument(maxWidth <= shapeBufferSize);
        checkArgument(remainingCharCount <= shapeBufferSize / 2);
        this.breakConfig = breakConfig;
        this.shaper = shaper;
        this.textBreaker = textBreaker;
        this.shapeBufferSize = shapeBufferSize;
        this.maxWidth = maxWidth;
        this.remainingCharCount = remainingCharCount;
    }

    @Override
    public Appender makeLines(final CurrentWidthProvider currentWidthProvider,
                              final ContentHandler contentHandler) {
        return new Appender() {
            private FontFace fontFace;
            private final char[] chars = new char[shapeBufferSize];
            private final char[] shapeChars = new char[shapeBufferSize];
            private final Position[] charPositions = new Position[shapeBufferSize];
            private int addedChars = 0;
            private Position lastPosition = null;

            @Override
            public void appendFontFace(Position position, FontFace fontFace) {
                if (this.fontFace != null) {
                    throw new UnsupportedOperationException("Different fontFaces in lines currently unsupported");
                }
                this.fontFace = fontFace;
            }

            @Override
            public void appendChar(Position position, char character) {
                checkArgument(addedChars == 0 || position.moreOrEquals(charPositions[addedChars - 1]));
                lastPosition = position;
                chars[addedChars] = character;
                shapeChars[addedChars] = isBlankChar(character) ? ' ' : character;
                charPositions[addedChars] = position;
                addedChars++;
                if (addedChars >= shapeBufferSize) {
                    shape(chars.length - remainingCharCount);
                }
            }

            @Override
            public void finish(Position position) {
                checkArgument(lastPosition == null || position.moreOrEquals(lastPosition));
                lastPosition = position;
                shape(addedChars);
            }

            private void shape(int limit) {
                Shape shape = shaper.shape(fontFace, shapeChars, 0, addedChars);
                LineBuffer lineBuffer = new LineBuffer();
                while (nextLine(shape, lineBuffer, limit)) {
                    handleLine(lineBuffer);
                }
                compactChars(lineBuffer);
            }

            private boolean nextLine(Shape shape, LineBuffer lineBuffer, int limit) {
                if (lineBuffer.beginIndex == -1) {
                    lineBuffer.beginIndex = 0;
                    lineBuffer.charBeginIndex = 0;
                } else {
                    lineBuffer.beginIndex = lineBuffer.endIndex;
                    lineBuffer.charBeginIndex = lineBuffer.charEndIndex;
                }

                if (lineBuffer.charBeginIndex >= limit) {
                    return false;
                }

                float desiredWidth = currentWidth();

                int outsideGlyphIndex = outsideGlyphIndex(shape, lineBuffer.beginIndex, desiredWidth);

                lineBuffer.width = Float.MAX_VALUE;
                int charEndIndex = outsideGlyphIndex < shape.size() ?
                        firstCharIndex(shape, outsideGlyphIndex) :
                        addedChars;

                if (charEndIndex > limit) {
                    return false;
                }

                lineBuffer.charEndIndex = charEndIndex;
                while (lineBuffer.width > desiredWidth && lineBuffer.charBeginIndex < charEndIndex) {
                    buildLine(lineBuffer, charEndIndex);
                    charEndIndex = min(charEndIndex, lineBuffer.charEndIndex) - 1;
                }
                lineBuffer.endIndex = lineBuffer.charEndIndex < addedChars ?
                        firstGlyphIndex(shape, lineBuffer.charEndIndex) :
                        shape.size();

                return true;
            }

            private float currentWidth() {
                float currentWidth = currentWidthProvider.currentWidth();
                checkArgument(currentWidth >= 0, "Width should >= 0");
                checkArgument(currentWidth <= maxWidth, "Should glyphsWidth less " + maxWidth);
                return currentWidth;
            }

            private int outsideGlyphIndex(Shape shape, int begin, float desiredWidth) {
                float[] advanceX = shape.advanceX();
                float x = 0;
                for (int i = begin; i < shape.size(); i++) {
                    if (isFirstGlyphOfChar(shape, i) && x > desiredWidth) {
                        return i;
                    }
                    x += advanceX[i];
                }
                return shape.size();
            }

            private void buildLine(LineBuffer lineBuffer, int charEndIndex) {
                lineBuffer.charEndIndex = blankEndIndex(chars, charEndIndex, addedChars);
                TextBreaker.BreakResult breakResult = textBreaker.breakText(chars, lineBuffer.charBeginIndex, lineBuffer.charEndIndex, 0, addedChars);
                lineBuffer.charEndIndex = breakResult.index();
                char[] lineShapeChars = lineShapeChars(lineBuffer, breakResult.wordBroken());
                lineBuffer.lineShape = shaper.shape(fontFace, lineShapeChars, 0, lineShapeChars.length);
                int lineCharsLength = lineBuffer.charEndIndex - lineBuffer.charBeginIndex;
                int blankBeginCharIndex =
                        blankBeginIndex(chars, lineBuffer.charBeginIndex, lineBuffer.charEndIndex) -
                                lineBuffer.charBeginIndex;
                int blankBeginGlyphIndex = blankBeginCharIndex < lineCharsLength ?
                        firstGlyphIndex(lineBuffer.lineShape, blankBeginCharIndex) :
                        lineBuffer.lineShape.size();
                lineBuffer.width = glyphsWidth(lineBuffer.lineShape, 0, blankBeginGlyphIndex);
                lineBuffer.height = glyphsHeight(lineBuffer.lineShape);
            }

            private int blankEndIndex(char[] chars, int beginIndex, int endIndex) {
                for (int i = beginIndex; i < endIndex; i++) {
                    boolean notBlank = !isBlankChar(chars[i]);
                    if (notBlank) {
                        return i;
                    }
                }
                return endIndex;
            }

            private int blankBeginIndex(char[] chars, int beginIndex, int endIndex) {
                int notBlankIndex = beginIndex - 1;
                for (int i = endIndex - 1; i >= beginIndex; i--) {
                    boolean notBlank = !isBlankChar(chars[i]);
                    if (notBlank) {
                        notBlankIndex = i;
                        break;
                    }
                }
                return notBlankIndex + 1;
            }

            private char[] lineShapeChars(LineBuffer lineBuffer, boolean wordBreaked) {
                int length = lineBuffer.charEndIndex - lineBuffer.charBeginIndex +
                        (wordBreaked ? 1 : 0);
                char[] lineChars = new char[length];
                arraycopy(shapeChars, lineBuffer.charBeginIndex, lineChars, 0,
                        lineBuffer.charEndIndex - lineBuffer.charBeginIndex);
                if (wordBreaked) {
                    lineChars[lineChars.length - 1] = breakConfig.hyphenChar();
                }
                return lineChars;
            }

            private void handleLine(LineBuffer lineBuffer) {
                Range range = range(lineBuffer);
                Text text = text(lineBuffer);
                Content content = new Content(range, text, text.size());
                contentHandler.handleContent(content);
            }

            private Range range(LineBuffer lineBuffer) {
                Position begin = charPositions[lineBuffer.charBeginIndex];
                Position end = lineBuffer.charEndIndex < addedChars ?
                        charPositions[lineBuffer.charEndIndex] : lastPosition;
                return Range.range(begin, end);
            }

            private Text text(LineBuffer lineBuffer) {
                Shape lineShape = lineBuffer.lineShape;

                int[] codepoints = lineShape.glyphs().clone();

                float[] coordinates = new float[2 * lineShape.size()];

                float[] advanceX = lineShape.advanceX();
                int k = 0;
                float x = 0;
                for (int i = 0; i < lineShape.size(); i++) {
                    coordinates[k] = x;
                    x += advanceX[i];
                    k += 2;
                }

                float[] advanceY = lineShape.advanceY();
                float y = lineShape.ascent();
                k = 1;
                for (int i = 0; i < lineShape.size(); i++) {
                    coordinates[k] = y;
                    y += advanceY[i];
                    k += 2;
                }

                Size size = size(lineBuffer.width, lineBuffer.height);

                return new Text(codepoints, coordinates, size);
            }

            private void compactChars(LineBuffer lineBuffer) {
                addedChars -= lineBuffer.charBeginIndex;
                arraycopy(chars, lineBuffer.charBeginIndex, chars, 0, addedChars);
                arraycopy(shapeChars, lineBuffer.charBeginIndex, shapeChars, 0, addedChars);
                arraycopy(charPositions, lineBuffer.charBeginIndex, charPositions, 0, addedChars);
            }
        };
    }

    private boolean isBlankChar(char ch) {
        return Chars.indexOf(breakConfig.blankChars(), ch) >= 0;
    }

    private static class LineBuffer {
        Shape lineShape;
        int beginIndex = -1;
        int endIndex = -1;
        int charBeginIndex = -1;
        int charEndIndex = -1;
        float width;
        float height;
    }
}

package com.dmi.perfectreader.book.format;

import com.dmi.perfectreader.book.content.Content;
import com.dmi.perfectreader.book.content.ContentBuilder;
import com.dmi.perfectreader.book.content.ContentHandler;
import com.dmi.perfectreader.book.font.FontFace;
import com.dmi.perfectreader.book.format.line.Liner;
import com.dmi.perfectreader.book.item.TextBreak;
import com.dmi.perfectreader.book.position.Position;

import java.util.LinkedList;
import java.util.Queue;

import static com.dmi.perfectreader.book.content.Size.size;
import static java.lang.Math.max;

public class LineFormatter implements Formatter {
    private final PageConfig pageConfig;
    private final FormatConfig formatConfig;
    private final Liner liner;

    public LineFormatter(PageConfig pageConfig, FormatConfig formatConfig, Liner liner) {
        this.pageConfig = pageConfig;
        this.formatConfig = formatConfig;
        this.liner = liner;
    }

    @Override
    public Appender format(final ContentHandler lineHandler) {
        return new Appender() {
            private Queue<Position> paragraphPositions = new LinkedList<>();

            private final float actualWidth = max(pageConfig.width() - pageConfig.paddingLeft() - pageConfig.paddingRight(), 0);
            private float currentWidth = actualWidth;
            private float currentOffsetX = 0;
            private float currentOffsetY = 0;

            private Liner.Appender linerAppender = liner.makeLines(
                    new Liner.CurrentWidthProvider() {
                        @Override
                        public float currentWidth() {
                            return currentWidth;
                        }
                    }, new ContentHandler() {
                        @Override
                        public void handleContent(Content line) {
                            Content formattedLine = new ContentBuilder()
                                    .appendFirst(line)
                                    .offset(currentOffsetX, currentOffsetY)
                                    .offset(pageConfig.paddingLeft(), 0)
                                    .size(size(pageConfig.width(), currentOffsetY + line.size().height()))
                                    .buildContent();

                            lineHandler.handleContent(formattedLine);

                            if (isParagraphBegin(line.range().end())) {
                                currentWidth = max(actualWidth - formatConfig.paragraphIndent(), 0);
                                currentOffsetX = formatConfig.paragraphIndent();
                                currentOffsetY = formatConfig.paragraphTopMargin();
                            } else {
                                currentWidth = actualWidth;
                                currentOffsetX = 0;
                                currentOffsetY = 0;
                            }
                        }

                        private boolean isParagraphBegin(Position position) {
                            Position paragraphPosition = paragraphPositions.peek();
                            if (paragraphPosition != null && position.moreOrEquals(paragraphPosition)) {
                                paragraphPositions.poll();
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });

            @Override
            public void appendFontFace(Position position, FontFace fontFace) {
                linerAppender.appendFontFace(position, fontFace);
            }

            @Override
            public void appendChar(Position position, char character) {
                linerAppender.appendChar(position, character);
            }

            @Override
            public void appendBreak(Position position, TextBreak textBreak) {
                linerAppender.appendChar(position, '\n');
                switch (textBreak) {
                    case PARAGRAPH:
                        paragraphPositions.add(position);
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }

            @Override
            public void finish(Position position) {
                linerAppender.finish(position);
            }
        };
    }
}

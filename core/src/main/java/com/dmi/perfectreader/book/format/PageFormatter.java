package com.dmi.perfectreader.book.format;

import com.dmi.perfectreader.book.content.Content;
import com.dmi.perfectreader.book.content.ContentBuilder;
import com.dmi.perfectreader.book.content.ContentHandler;
import com.dmi.perfectreader.book.font.FontFace;
import com.dmi.perfectreader.book.item.TextBreak;
import com.dmi.perfectreader.book.position.Position;

import static com.dmi.perfectreader.book.content.Size.size;

public class PageFormatter implements Formatter {
    private final PageConfig pageConfig;
    private final Formatter lineFormatter;

    public PageFormatter(PageConfig pageConfig, Formatter lineFormatter) {
        this.pageConfig = pageConfig;
        this.lineFormatter = lineFormatter;
    }

    @Override
    public Appender format(final ContentHandler pageHandler) {
        return new Appender() {
            private ContentBuilder pageBuilder = new ContentBuilder();

            private Formatter.Appender lineAppender = lineFormatter.format(
                    new ContentHandler() {
                        @Override
                        public void handleContent(Content line) {
                            float maxFactHeight = pageConfig.height() - pageConfig.paddingTop() - pageConfig.paddingBottom();
                            if (pageBuilder.size().height() == 0) {
                                pageBuilder.appendFirst(line);
                            } else if (line.size().height() + pageBuilder.size().height() <= maxFactHeight) {
                                pageBuilder.appendBelow(line);
                            } else {
                                buildPage();
                                pageBuilder = new ContentBuilder();
                                pageBuilder.appendFirst(line);
                            }
                        }
                    });

            @Override
            public void appendFontFace(Position position, FontFace fontFace) {
                lineAppender.appendFontFace(position, fontFace);
            }

            @Override
            public void appendChar(Position position, char character) {
                lineAppender.appendChar(position, character);
            }

            @Override
            public void appendBreak(Position position, TextBreak textBreak) {
                lineAppender.appendBreak(position, textBreak);
            }

            @Override
            public void finish(Position position) {
                lineAppender.finish(position);
                buildPage();
            }

            private void buildPage() {
                Content content = pageBuilder
                        .offset(0, pageConfig.paddingTop())
                        .size(size(pageConfig.width(), pageConfig.height()))
                        .buildContent();
                pageHandler.handleContent(content);
            }
        };
    }
}

package com.dmi.perfectreader.book.pageprovider;

import com.dmi.perfectreader.book.content.Content;
import com.dmi.perfectreader.book.content.ContentHandler;
import com.dmi.perfectreader.book.data.BookData;
import com.dmi.perfectreader.book.data.ItemReader;
import com.dmi.perfectreader.book.font.FontFace;
import com.dmi.perfectreader.book.format.Formatter;
import com.dmi.perfectreader.book.item.ItemHandler;
import com.dmi.perfectreader.book.item.TextBreak;
import com.dmi.perfectreader.book.item.TextType;
import com.dmi.perfectreader.book.position.Position;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.Nullable;

public class BuildPageProvider implements PageProvider {
    private final BookData bookData;
    private final Formatter pageFormatter;

    public BuildPageProvider(BookData bookData, Formatter pageFormatter) {
        this.bookData = bookData;
        this.pageFormatter = pageFormatter;
    }

    @Override
    public Iterator iterator(final Position position) throws IOException {
        return new Iterator() {
            private final FontFace fontFace = FontFace.DEFAULT;

            private final ItemReader influenceItemReader = bookData.influenceItemReader(position);
            private final ItemReader itemReader = bookData.itemReader(position);
            private final PageQueue pageQueue = new PageQueue();
            private final Formatter.Appender pagerAppender = pageFormatter.format(pageQueue);
            private final ItemAppender itemAppender = new ItemAppender();

            private boolean finished = false;

            @Nullable
            @Override
            public Content next() throws IOException {
                while (influenceItemReader.hasItems()) {
                    influenceItemReader.nextItem(itemAppender);
                }
                while (pageQueue.peek() == null && itemReader.hasItems()) {
                    itemReader.nextItem(itemAppender);
                }
                if (!itemReader.hasItems() && !finished) {
                    pagerAppender.finish(Position.END);
                    finished = true;
                }
                return pageQueue.poll();
            }

            @Override
            public void close() throws IOException {
                if (!finished) {
                    pagerAppender.finish(Position.END);
                    finished = true;
                }
            }

            class PageQueue implements ContentHandler {
                private final Queue<Content> pages = new LinkedList<>();

                public Content peek() {
                    return pages.peek();
                }

                public Content poll() {
                    return pages.poll();
                }

                @Override
                public void handleContent(Content page) {
                    pages.add(page);
                }
            }

            class ItemAppender implements ItemHandler {
                @Override
                public void handleChar(Position position, char character) {
                    pagerAppender.appendChar(position, character);
                }

                @Override
                public void handleBreak(Position position, TextBreak textBreak) {
                    pagerAppender.appendBreak(position, textBreak);
                }

                @Override
                public void handleTextType(TextType textType, Position position) {
                    switch (textType) {
                        case MAIN:
                            pagerAppender.appendFontFace(position, fontFace);
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }
                }
            }
        };
    }
}

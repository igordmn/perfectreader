package com.dmi.perfectreader.book.data;

import com.dmi.perfectreader.book.item.ItemHandler;
import com.dmi.perfectreader.book.item.TextBreak;
import com.dmi.perfectreader.book.item.TextType;
import com.dmi.perfectreader.book.position.Position;

import static com.dmi.perfectreader.book.position.Position.BEGIN;
import static com.dmi.perfectreader.book.position.Position.toPosition;
import static com.google.common.base.Preconditions.checkState;

class StringBookData implements BookData {
    private final String string;

    StringBookData(String string) {
        this.string = string;
    }

    @Override
    public ItemReader itemReader(final Position position) {
        return new ItemReader() {
            final int startIndex = (int) position.toLocalPosition(string.length());
            int index = startIndex;
            boolean textTypeNotReaded = true;

            @Override
            public boolean hasItems() {
                return index < string.length();
            }

            @Override
            public void nextItem(ItemHandler itemHandler) {
                checkState(hasItems());
                if (index == 0 && textTypeNotReaded) {
                    itemHandler.handleTextType(TextType.MAIN, position);
                    textTypeNotReaded = false;
                } else {
                    Position currentPosition = toPosition(index, string.length());
                    char character = string.charAt(index);

                    switch (character) {
                        case '\n':
                            itemHandler.handleBreak(currentPosition, TextBreak.PARAGRAPH);
                            break;
                        default:
                            itemHandler.handleChar(currentPosition, character);
                    }

                    index++;
                }
            }
        };
    }

    @Override
    public ItemReader influenceItemReader(final Position position) {
        return new ItemReader() {
            boolean textTypeNotRead = true;
            boolean paragraphNotRead = true;

            @Override
            public boolean hasItems() {
                return (textTypeNotRead || paragraphNotRead) && position.more(BEGIN);
            }

            @Override
            public void nextItem(ItemHandler itemHandler) {
                checkState(hasItems());
                if (textTypeNotRead) {
                    itemHandler.handleTextType(TextType.MAIN, BEGIN);
                    textTypeNotRead = false;
                } else if (paragraphNotRead) {
                    final int index = (int) position.toLocalPosition(string.length());
                    if (index - 1 >= 0 && string.charAt(index - 1) == '\n') {
                        itemHandler.handleBreak(toPosition(index - 1, string.length()), TextBreak.PARAGRAPH);
                    }
                    paragraphNotRead = false;
                }
            }
        };
    }
}

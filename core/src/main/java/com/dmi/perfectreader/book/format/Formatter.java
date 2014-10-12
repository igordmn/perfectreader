package com.dmi.perfectreader.book.format;

import com.dmi.perfectreader.book.content.ContentHandler;
import com.dmi.perfectreader.book.font.FontFace;
import com.dmi.perfectreader.book.item.TextBreak;
import com.dmi.perfectreader.book.position.Position;

public interface Formatter {
    Appender format(ContentHandler contentHandler);

    interface Appender {
        void appendFontFace(Position position, FontFace fontFace);

        void appendChar(Position position, char character);

        void appendBreak(Position position, TextBreak textBreak);

        void finish(Position position);
    }
}

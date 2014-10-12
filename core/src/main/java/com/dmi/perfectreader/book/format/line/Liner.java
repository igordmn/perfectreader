package com.dmi.perfectreader.book.format.line;

import com.dmi.perfectreader.book.content.ContentHandler;
import com.dmi.perfectreader.book.font.FontFace;
import com.dmi.perfectreader.book.position.Position;

public interface Liner {
    Appender makeLines(CurrentWidthProvider currentWidthProvider,
                       ContentHandler contentHandler);

    interface Appender {
        void appendFontFace(Position position, FontFace fontFace);

        void appendChar(Position position, char character);

        void finish(Position position);
    }

    interface CurrentWidthProvider {
        float currentWidth();
    }
}

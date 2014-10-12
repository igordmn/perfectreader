package com.dmi.perfectreader.book.item;

import com.dmi.perfectreader.book.position.Position;

public interface ItemHandler {
    void handleChar(Position position, char character);

    void handleBreak(Position position, TextBreak textBreak);

    void handleTextType(TextType textType, Position position);
}

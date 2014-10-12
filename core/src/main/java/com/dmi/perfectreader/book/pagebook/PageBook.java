package com.dmi.perfectreader.book.pagebook;

import com.dmi.perfectreader.book.position.Position;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface PageBook {
    void goPosition(Position position);

    void tryGoNext();

    void tryGoPreview();

    Position position();
}

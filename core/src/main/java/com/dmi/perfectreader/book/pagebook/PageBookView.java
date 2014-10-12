package com.dmi.perfectreader.book.pagebook;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface PageBookView {
    void moveNext(Pages pages);

    void movePreview(Pages pages);

    void setPages(Pages pages);
}

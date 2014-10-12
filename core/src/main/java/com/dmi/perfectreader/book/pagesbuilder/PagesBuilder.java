package com.dmi.perfectreader.book.pagesbuilder;

import com.dmi.perfectreader.book.pagebook.Pages;
import com.dmi.perfectreader.book.position.Position;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface PagesBuilder {
    void build(Position position, Pages pages) throws InterruptedException, IOException;
}

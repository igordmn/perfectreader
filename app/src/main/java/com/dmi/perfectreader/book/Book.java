package com.dmi.perfectreader.book;

import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.bookview.BookConfigurator;

public interface Book {
    BookLocation currentLocation();

    void goLocation(BookLocation location);

    BookLocation percentToLocation(double percent);

    double locationToPercent(BookLocation location);

    void goNextPage();

    void goPreviewPage();

    BookConfigurator configure();
}

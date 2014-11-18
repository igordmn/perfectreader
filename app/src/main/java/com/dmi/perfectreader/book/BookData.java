package com.dmi.perfectreader.book;

import java.util.Collections;
import java.util.List;

public class BookData {
    public static BookData EMPTY = new BookData(Collections.<String>emptyList());

    private final List<String> segmentUrls;

    public BookData(List<String> segmentUrls) {
        this.segmentUrls = segmentUrls;
    }

    int segmentCount() {
        return segmentUrls.size();
    }

    String segmentFile(int segmentIndex) {
        return segmentUrls.get(segmentIndex);
    }
}

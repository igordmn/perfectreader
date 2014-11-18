package com.dmi.perfectreader.book;

import java.util.List;

public class BookData {
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

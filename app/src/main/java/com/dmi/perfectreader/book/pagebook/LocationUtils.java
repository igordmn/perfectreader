package com.dmi.perfectreader.book.pagebook;

import com.dmi.util.lang.IntegerPercent;

import static java8.util.J8Arrays.stream;

class LocationUtils {
    public static SegmentLocation percentToSegmentLocation(int[] segmentSizes, int integerPercent) {
        int totalSize = stream(segmentSizes).sum();
        int position = IntegerPercent.multiply(integerPercent, totalSize);
        int segmentStart, segmentEnd = 0;
        for (int i = 0; i < segmentSizes.length; i++) {
            segmentStart = segmentEnd;
            int segmentSize = segmentSizes[i];
            segmentEnd += segmentSize;
            if (position >= segmentStart && position < segmentEnd) {
                int positionInSegment = position - segmentStart;
                return new SegmentLocation(i, (double) positionInSegment / segmentSize);
            }
        }
        return new SegmentLocation(segmentSizes.length - 1, 1.0);
    }

    public static int segmentLocationToPercent(int[] segmentSizes, SegmentLocation segmentLocation) {
        int totalSize = stream(segmentSizes).sum();
        int position = 0;
        for (int i = 0; i < segmentLocation.index; i++) {
            position += segmentSizes[i];
        }
        position += segmentSizes[segmentLocation.index] * segmentLocation.percent;
        return IntegerPercent.valuePercent(position, totalSize);
    }

    public static int percentToPage(int pageCount, double percent) {
        return (int) Math.min(Math.round(percent * pageCount), pageCount - 1);
    }

    public static double pageToPercent(int pageCount, int page) {
        return (double) page / pageCount;
    }

    public static class SegmentLocation {
        public final int index;
        public final double percent;

        public SegmentLocation(int index, double percent) {
            this.index = index;
            this.percent = percent;
        }
    }
}

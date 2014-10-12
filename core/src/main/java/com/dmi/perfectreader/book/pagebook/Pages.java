package com.dmi.perfectreader.book.pagebook;

import com.dmi.perfectreader.book.content.Content;
import com.dmi.perfectreader.book.position.Position;
import com.dmi.perfectreader.book.position.Range;
import com.dmi.perfectreader.util.collection.DuplexBuffer;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.abs;
import static java.lang.Math.min;

@ThreadSafe
public class Pages {
    private final int maxRelativeIndex;

    private final TreeMap<Position, Content> map = new TreeMap<>();
    private Position middlePosition = null;

    public Pages(int maxRelativeIndex) {
        this.maxRelativeIndex = maxRelativeIndex;
    }

    public synchronized void put(Content page) {
        if (isNotOverlappedPage(page)) {
            removeOverlappedPages(page);
            map.put(page.range().begin(), page);
        }
        removeFarPages();
    }

    public synchronized void clear() {
        map.clear();
    }

    public synchronized void setMiddle(@Nullable Position middlePosition) {
        this.middlePosition = middlePosition;
        removeFarPages();
    }

    private boolean isNotOverlappedPage(Content addingPage) {
        Map.Entry<Position, Content> floorEntry = map.floorEntry(addingPage.range().begin());
        return floorEntry == null || addingPage.range().end().more(floorEntry.getValue().range().end());
    }


    private boolean inPage(Content inPage, Content outPage) {
        Range inRange = inPage.range();
        Range outRange = outPage.range();
        return inRange.begin().moreOrEquals(outRange.begin()) &&
                inRange.end().lessOrEquals(outRange.end());
    }

    private void removeOverlappedPages(Content addingPage) {
        while (true) {
            Range pageRange = addingPage.range();
            Map.Entry<Position, Content> lowerEntry = map.lowerEntry(pageRange.end());
            if (lowerEntry != null && inPage(lowerEntry.getValue(), addingPage)) {
                map.remove(lowerEntry.getKey());
            } else {
                break;
            }
        }
    }

    private void removeFarPages() {
        if (middlePosition != null && pageExists(middlePosition)) {
            while (true) {
                Map.Entry<Position, Content> leftFar = relativeEntry(middlePosition, -maxRelativeIndex - 1);
                Map.Entry<Position, Content> rightFar = relativeEntry(middlePosition, maxRelativeIndex + 1);
                if (leftFar != null) {
                    map.remove(leftFar.getKey());
                }
                if (rightFar != null) {
                    map.remove(rightFar.getKey());
                }
                if (leftFar == null || rightFar == null) {
                    break;
                }
            }
        } else {
            map.clear();
        }
    }

    @Nullable
    public synchronized Content get(int relativeIndex) {
        checkArgument(abs(relativeIndex) <= maxRelativeIndex);
        if (middlePosition != null) {
            Map.Entry<Position, Content> entry = relativeEntry(middlePosition, relativeIndex);
            return entry != null ? entry.getValue() : null;
        } else {
            return null;
        }
    }

    public synchronized void get(DuplexBuffer<Content> pageBuffer) {
        int minMaxRelativeIndex = min(maxRelativeIndex, pageBuffer.maxRelativeIndex());
        for (int i = -minMaxRelativeIndex; i <= minMaxRelativeIndex; i++) {
            pageBuffer.set(i, get(i));
        }
    }

    @Nullable
    public synchronized Position middle() {
        return middlePosition;
    }

    public synchronized int maxRelativeIndex() {
        return maxRelativeIndex;
    }

    private Map.Entry<Position, Content> relativeEntry(Position position, int index) {
        Map.Entry<Position, Content> it = map.floorEntry(position);
        if (index >= 0) {
            for (int i = 0; i < index && it != null; i++) {
                it = map.higherEntry(it.getValue().range().begin());
            }
        } else {
            for (int i = 0; i < -index && it != null; i++) {
                it = map.lowerEntry(it.getValue().range().begin());
            }
        }
        return it;
    }

    private boolean pageExists(Position position) {
        Map.Entry<Position, Content> floorEntry = map.floorEntry(position);
        return floorEntry != null && floorEntry.getValue().range().end().more(position);
    }
}

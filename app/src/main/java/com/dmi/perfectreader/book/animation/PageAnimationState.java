package com.dmi.perfectreader.book.animation;

import com.carrotsearch.hppc.FloatArrayList;
import com.carrotsearch.hppc.IntArrayList;

public class PageAnimationState {
    private final IntArrayList relativeIndices = new IntArrayList();
    private final FloatArrayList xPositions = new FloatArrayList();
    private int minRelativeIndex = 0;
    private int maxRelativeIndex = 0;

    void clear() {
        relativeIndices.clear();
        xPositions.clear();
        minRelativeIndex = 0;
        maxRelativeIndex = 0;
    }

    void add(int relativeIndex, float xPosition) {
        relativeIndices.add(relativeIndex);
        xPositions.add(xPosition);
        if (relativeIndex < minRelativeIndex) {
            minRelativeIndex = relativeIndex;
        }
        if (relativeIndex > maxRelativeIndex) {
            maxRelativeIndex = relativeIndex;
        }
    }

    public int pageCount() {
        return relativeIndices.size();
    }

    public int pageRelativeIndex(int index) {
        return relativeIndices.get(index);
    }

    public float pagePositionX(int index) {
        return xPositions.get(index);
    }

    public int minRelativeIndex() {
        return minRelativeIndex;
    }

    public int maxRelativeIndex() {
        return maxRelativeIndex;
    }
}

package com.dmi.perfectreader.book.animation;

import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

public class PageAnimationState {
    private final TIntArrayList relativeIndices = new TIntArrayList();
    private final TFloatArrayList xPositions = new TFloatArrayList();
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

package com.dmi.perfectreader.book.animation;

import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

public class PageAnimationState {
    private final TIntArrayList relativeIndices = new TIntArrayList();
    private final TFloatArrayList xPositions = new TFloatArrayList();

    public void clear() {
        relativeIndices.clear();
        xPositions.clear();
    }

    public void add(int relativeIndex, float xPosition) {
        relativeIndices.add(relativeIndex);
        xPositions.add(xPosition);
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
}

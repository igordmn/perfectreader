package com.dmi.perfectreader.util.collection;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.abs;

public class DuplexBuffer<T> {
    private final int maxRelativeIndex;

    private final Object[] items;

    public DuplexBuffer(int maxRelativeIndex) {
        items = new Object[2 * maxRelativeIndex + 1];
        this.maxRelativeIndex = maxRelativeIndex;
    }

    public void set(int relativeIndex, T item) {
        checkArgument(abs(relativeIndex) <= maxRelativeIndex);
        items[arrayIndex(relativeIndex)] = item;
    }

    @SuppressWarnings("unchecked")
    public T get(int relativeIndex) {
        checkArgument(abs(relativeIndex) <= maxRelativeIndex);
        return (T) items[arrayIndex(relativeIndex)];
    }

    public void clear() {
        Arrays.fill(items, null);
    }

    public int maxRelativeIndex() {
        return maxRelativeIndex;
    }

    private int arrayIndex(int relativeIndex) {
        return maxRelativeIndex + relativeIndex;
    }
}

package com.dmi.util.collection;

import java.util.Arrays;

import static com.dmi.util.lang.MathExt.modPositive;
import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.abs;

public class DuplexBuffer<T> {
    private final int maxRelativeIndex;
    private final Object[] items;
    private int shift = 0;

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

    public void shiftLeft() {
        shift--;
    }

    public void shiftRight() {
        shift++;
    }

    public void clear() {
        Arrays.fill(items, null);
    }

    public int maxRelativeIndex() {
        return maxRelativeIndex;
    }

    private int arrayIndex(int relativeIndex) {
        return modPositive(maxRelativeIndex + relativeIndex - shift, items.length);
    }
}

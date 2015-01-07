package com.dmi.perfectreader.util.lang;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.addAll;

// todo оптимизировать по памяти (переделать на массивы или Set<Holder>. Это необходимо, т.к. в цикле постоянно вызвается acquire и release в PageAnimationView.RefreshService
public class Pool<T> implements Iterable<T> {
    private final Set<T> items;
    private final Set<T> availableItems;

    public Pool(T[] precreatedItems) {
        items = new HashSet<>(precreatedItems.length);
        availableItems = new HashSet<>(precreatedItems.length);
        addAll(items, precreatedItems);
        addAll(availableItems, precreatedItems);
    }

    public synchronized T acquire() {
        checkState(canAcquire());
        T item = availableItems.iterator().next();
        availableItems.remove(item);
        return item;
    }

    public synchronized void release(T item) {
        checkState(isUsed(item));
        availableItems.add(item);
    }

    public boolean canAcquire() {
        return availableItems.size() > 0;
    }

    public int available() {
        return availableItems.size();
    }

    public boolean isUsed(T item) {
        checkState(items.contains(item));
        return !availableItems.contains(item);
    }

    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }
}

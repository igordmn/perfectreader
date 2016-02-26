package com.dmi.util.cache;

import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.CharArrayList;
import com.carrotsearch.hppc.FloatArrayList;
import com.carrotsearch.hppc.IntArrayList;
import com.dmi.util.annotation.Reusable;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.NotThreadSafe;

import java8.util.function.Supplier;

@NotThreadSafe
public abstract class ReuseCache {
    public static <T> Reuser<T> reuser(Supplier<T> creator) {
        AtomicReference<WeakReference<T>> holder = new AtomicReference<>(new WeakReference<>(null));
        return () -> {
            WeakReference<T> weakRef = holder.get();
            T value = weakRef.get();
            if (value == null) {
                value = creator.get();
                holder.set(new WeakReference<>(value));
            }
            return value;
        };
    }

    public static StringBuilder reuseStringBuilder(Reuser<StringBuilder> reuser) {
        StringBuilder value = reuser.reuse();
        value.setLength(0);
        return value;
    }

    public static <T extends Collection<?>> T reuseCollection(Reuser<T> reuser) {
        T value = reuser.reuse();
        value.clear();
        return value;
    }

    public static IntArrayList reuseIntArrayList(Reuser<IntArrayList> reuser) {
        IntArrayList value = reuser.reuse();
        value.elementsCount = 0;
        return value;
    }

    public static ByteArrayList reuseByteArrayList(Reuser<ByteArrayList> reuser) {
        ByteArrayList value = reuser.reuse();
        value.elementsCount = 0;
        return value;
    }

    public static FloatArrayList reuseFloatArrayList(Reuser<FloatArrayList> reuser) {
        FloatArrayList value = reuser.reuse();
        value.elementsCount = 0;
        return value;
    }

    public static CharArrayList reuseCharArrayList(Reuser<CharArrayList> reuser) {
        CharArrayList value = reuser.reuse();
        value.elementsCount = 0;
        return value;
    }

    public static float[] reuseFloatArray(Reuser<AtomicReference<float[]>> reuser, int capacity) {
        AtomicReference<float[]> ref = reuser.reuse();
        float[] value = ref.get();
        if (value.length < capacity) {
            value = new float[capacity + capacity >> 1];
            ref.set(value);
        }
        return value;
    }

    public static boolean[] reuseBooleanArray(Reuser<AtomicReference<boolean[]>> reuser, int capacity) {
        AtomicReference<boolean[]> ref = reuser.reuse();
        boolean[] value = ref.get();
        if (value.length < capacity) {
            value = new boolean[capacity + capacity >> 1];
            ref.set(value);
        }
        return value;
    }

    public interface Reuser<T> {
        @Reusable
        T reuse();
    }
}

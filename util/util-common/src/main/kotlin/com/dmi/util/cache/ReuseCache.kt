package com.dmi.util.cache

import com.carrotsearch.hppc.ByteArrayList
import com.carrotsearch.hppc.CharArrayList
import com.carrotsearch.hppc.FloatArrayList
import com.carrotsearch.hppc.IntArrayList
import com.dmi.util.annotation.Reusable
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference

object ReuseCache {
    fun <T> reuser(create: () -> T): Reuser<T> {
        val holder = AtomicReference(WeakReference<T>(null))
        return object: Reuser<T> {
            override fun reuse(): T {
                val weakRef = holder.get()
                var value = weakRef.get()
                if (value == null) {
                    value = create()
                    holder.set(WeakReference(value))
                }
                return value
            }
        }
    }

    fun reuseStringBuilder(reuser: Reuser<StringBuilder>): StringBuilder {
        val value = reuser.reuse()
        value.setLength(0)
        return value
    }

    fun <T : MutableCollection<*>> reuseCollection(reuser: Reuser<T>): T {
        val value = reuser.reuse()
        value.clear()
        return value
    }

    fun reuseIntArrayList(reuser: Reuser<IntArrayList>): IntArrayList {
        val value = reuser.reuse()
        value.elementsCount = 0
        return value
    }

    fun reuseByteArrayList(reuser: Reuser<ByteArrayList>): ByteArrayList {
        val value = reuser.reuse()
        value.elementsCount = 0
        return value
    }

    fun reuseFloatArrayList(reuser: Reuser<FloatArrayList>): FloatArrayList {
        val value = reuser.reuse()
        value.elementsCount = 0
        return value
    }

    fun reuseCharArrayList(reuser: Reuser<CharArrayList>): CharArrayList {
        val value = reuser.reuse()
        value.elementsCount = 0
        return value
    }

    fun reuseFloatArray(reuser: Reuser<AtomicReference<FloatArray>>, capacity: Int): FloatArray {
        val ref = reuser.reuse()
        var value = ref.get()
        if (value.size < capacity) {
            value = FloatArray(capacity + capacity shr 1)
            ref.set(value)
        }
        return value
    }

    fun reuseBooleanArray(reuser: Reuser<AtomicReference<BooleanArray>>, capacity: Int): BooleanArray {
        val ref = reuser.reuse()
        var value = ref.get()
        if (value.size < capacity) {
            value = BooleanArray(capacity + capacity shr 1)
            ref.set(value)
        }
        return value
    }

    interface Reuser<T> {
        @Reusable
        fun reuse(): T
    }
}

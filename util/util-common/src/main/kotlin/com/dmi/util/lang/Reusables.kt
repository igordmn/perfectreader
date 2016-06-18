package com.dmi.util.lang

import com.carrotsearch.hppc.FloatArrayList
import com.carrotsearch.hppc.IntArrayList
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class ReusableValue<T>(private val create: () -> T) {
    private val holder = AtomicReference(WeakReference<T>(null))

    operator fun invoke(): T {
        val weakRef = holder.get()
        var value = weakRef.get()
        if (value == null) {
            value = create()
            holder.set(WeakReference(value))
        }
        return value
    }
}

class ReusableStringBuilder(capacity: Int = 16) {
    private val value = ReusableValue({ StringBuilder(capacity) })

    operator fun invoke(): StringBuilder {
        return value().apply {
            setLength(0)
        }
    }
}

class ReusableArrayList<T>(capacity: Int = 16) {
    private val value = ReusableValue({ ArrayList<T>(capacity) })

    operator fun invoke(): ArrayList<T> {
        return value().apply {
            clear()
        }
    }
}

class ReusableIntArrayList(capacity: Int = 16) {
    private val value = ReusableValue({ IntArrayList(capacity) })

    operator fun invoke(): IntArrayList {
        return value().apply {
            clear()
        }
    }
}

class ReusableFloatArrayList(capacity: Int = 16) {
    private val value = ReusableValue({ FloatArrayList(capacity) })

    operator fun invoke(): FloatArrayList {
        return value().apply {
            clear()
        }
    }
}

class ReusableFloatArray(capacity: Int = 16) {
    private val value = ReusableValue({ AtomicReference<FloatArray>(FloatArray(capacity)) })

    operator fun invoke(capacity: Int): FloatArray {
        val ref = value()
        var value = ref.get()
        if (value.size < capacity) {
            value = FloatArray(capacity + capacity shr 1)
            ref.set(value)
        }
        return value
    }
}

class ReusableBooleanArray(capacity: Int = 16) {
    private val value = ReusableValue({ AtomicReference<BooleanArray>(BooleanArray(capacity)) })

    operator fun invoke(capacity: Int): BooleanArray {
        val ref = value()
        var value = ref.get()
        if (value.size < capacity) {
            value = BooleanArray(capacity + capacity shr 1)
            ref.set(value)
        }
        return value
    }
}

class ReusableByteArray(capacity: Int = 16) {
    private val value = ReusableValue({ AtomicReference<ByteArray>(ByteArray(capacity)) })

    operator fun invoke(capacity: Int): ByteArray {
        val ref = value()
        var value = ref.get()
        if (value.size < capacity) {
            value = ByteArray(capacity + capacity shr 1)
            ref.set(value)
        }
        return value
    }
}
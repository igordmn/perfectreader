package com.dmi.util.collection

import com.dmi.util.scope.Disposable
import java.util.*
import kotlin.collections.LinkedHashSet

interface Pool<T> {
    fun acquire(): T
    fun release(obj: T)
}

fun <T: Disposable> ImmediatelyCreatePool(size: Int, create: () -> T) = ImmediatelyCreatePool(size, create, dispose = { it.dispose() })

class ImmediatelyCreatePool<T>(val size: Int, create: () -> T, private val dispose: (T) -> Unit = {}) : Pool<T>, Disposable {
    private val all = LinkedHashSet<T>()
    private val free = Stack<T>()

    override fun dispose() {
        all.forEach {
            dispose(it)
        }
    }

    init {
        repeat(size) {
            val element = create()
            all.add(element)
            free.add(element)
        }
    }

    override fun acquire(): T {
        return free.pop()
    }

    override fun release(obj: T) {
        require(all.contains(obj))
        free.add(obj)
    }
}
package com.dmi.util.collection

import com.dmi.util.lang.returnUnit
import java.util.*
import java.util.concurrent.atomic.AtomicReference

interface Pool<T> {
    fun acquire(): T
    fun release(obj: T)
}

class ImmediatelyCreatePool<T>(val size: Int, create: () -> T) : Pool<T> {
    private val free = Stack<T>()

    init {
        for (i in 0..size - 1) {
            free.add(create())
        }
    }

    fun hasFree() = free.size > 0
    override fun acquire() = require(hasFree()).run { free.pop() }
    override fun release(obj: T) = free.add(obj).returnUnit()
}

class SinglePool<T>(create: () -> T) : Pool<T> {
    private val value = AtomicReference(create())

    override fun acquire() = value.getAndSet(null).apply {
        require(this != null)
    }

    override fun release(obj: T) = value.set(obj)
}
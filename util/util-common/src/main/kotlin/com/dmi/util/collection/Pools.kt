package com.dmi.util.collection

import com.dmi.util.lang.returnUnit
import java.util.*
import java.util.concurrent.Semaphore

interface Pool<T> {
    fun acquire(): T
    fun release(obj: T)
}

class ImmediatelyCreatePool<T>(val size: Int, create: () -> T) : Pool<T> {
    private val free = Stack<T>()

    init {
        repeat(size) {
            free.add(create())
        }
    }

    fun hasFree() = free.size > 0
    override fun acquire() = require(hasFree()).run { free.pop() }
    override fun release(obj: T) = free.add(obj).returnUnit()
}

class SingleBlockingPool<T>(create: () -> T) : Pool<T> {
    private val value = create()
    private val semaphore = Semaphore(1)

    override fun acquire(): T {
        semaphore.acquire()
        return value
    }

    override fun release(obj: T) {
        require(obj === value)
        semaphore.release()
    }
}
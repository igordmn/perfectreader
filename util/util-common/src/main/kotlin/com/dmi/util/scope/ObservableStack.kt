package com.dmi.util.scope

import java.util.*

class ObservableStack<T : Any> {
    private val stack = LinkedList<T>()

    val afterPush = EmittableEvent()
    val afterPop = EmittableEvent()

    val top: T? get() = stack.peek()
    val size: Int get() = stack.size

    fun push(item: T) {
        stack.push(item)
        afterPush.emit()
    }

    fun pop(): T {
        val item = stack.pop()
        afterPop.emit()
        return item
    }

    operator fun get(index: Int) = stack[index]

    fun descendingIterator(): Iterator<T> = stack.descendingIterator()
}
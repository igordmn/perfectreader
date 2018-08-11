package com.dmi.util.collection

class LinkedCache<T> : Iterable<T> {
    private var first: Entry? = null
    private var last: Entry? = null

    fun add(item: T): Entry {
        return if (last == null) {
            val entry = Entry(item, null, null)
            this.first = entry
            this.last = entry
            entry
        } else {
            val entry = Entry(item, last, null)
            last!!.next = entry
            this.last = entry
            entry
        }
    }

    override fun iterator(): Iterator<T> = It()

    private inner class It : Iterator<T> {
        private var entry: Entry? = null
        override fun hasNext() = first != null && entry == null || entry != null && entry!!.next != null

        override fun next(): T {
            entry = if (entry == null) {
                first
            } else {
                entry!!.next
            }
            return entry!!.item
        }
    }

    inner class Entry(val item: T, private var previous: Entry?, internal var next: Entry?) {
        private var removed = false

        fun remove() {
            check(!removed)
            removed = true
            previous?.next = next
            next?.previous = previous
            if (previous == null)
                first = next
            if (next == null)
                last = previous
        }
    }
}
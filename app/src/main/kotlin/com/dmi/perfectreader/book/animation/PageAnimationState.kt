package com.dmi.perfectreader.book.animation

import com.carrotsearch.hppc.FloatArrayList
import com.carrotsearch.hppc.IntArrayList

class PageAnimationState {
    private val relativeIndices = IntArrayList()
    private val xPositions = FloatArrayList()
    private var minRelativeIndex = 0
    private var maxRelativeIndex = 0

    fun clear() {
        relativeIndices.clear()
        xPositions.clear()
        minRelativeIndex = 0
        maxRelativeIndex = 0
    }

    fun add(relativeIndex: Int, xPosition: Float) {
        relativeIndices.add(relativeIndex)
        xPositions.add(xPosition)
        if (relativeIndex < minRelativeIndex) {
            minRelativeIndex = relativeIndex
        }
        if (relativeIndex > maxRelativeIndex) {
            maxRelativeIndex = relativeIndex
        }
    }

    fun pageCount(): Int {
        return relativeIndices.size()
    }

    fun pageRelativeIndex(index: Int): Int {
        return relativeIndices.get(index)
    }

    fun pagePositionX(index: Int): Float {
        return xPositions.get(index)
    }

    fun minRelativeIndex(): Int {
        return minRelativeIndex
    }

    fun maxRelativeIndex(): Int {
        return maxRelativeIndex
    }
}
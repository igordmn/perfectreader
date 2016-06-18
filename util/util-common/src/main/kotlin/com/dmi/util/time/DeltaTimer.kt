package com.dmi.util.time

class DeltaTimer {
    private var lastTime = System.nanoTime()

    fun reset() {
        lastTime = System.nanoTime()
    }

    fun deltaSeconds() = deltaNanos() / 1.0E9F

    fun deltaNanos(): Long {
        val current = System.nanoTime()
        val delta = current - lastTime
        lastTime = current
        return delta
    }
}
package com.dmi.util.debug

import java.lang.System.nanoTime

fun benchmark(name: String = "", iterations: Int = 10, run: () -> Unit) {
    run()  // cold call

    val t1 = nanoTime()
    repeat(iterations) { run() }
    val t2 = nanoTime()
    val time = ((t2 - t1) / 1.0E6) / iterations

    if (name != "") {
        println("$name time: $time")
    } else {
        println("time: $time")
    }
}

fun <T> measureTime(name: String = "", run: () -> T): T {
    val t1 = nanoTime()
    val result = run()
    val t2 = nanoTime()
    val time = (t2 - t1) / 1.0E6

    if (name != "") {
        println("$name time: $time")
    } else {
        println("time: $time")
    }

    return result
}
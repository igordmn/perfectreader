package com.dmi.util.debug

import java.lang.System.nanoTime

inline fun benchmark(name: String = "", iterations: Int = 10, run: () -> Unit) {
    run()  // cold call

    val t1 = nanoTime()
    for(i in 1..iterations)
        run()
    val t2 = nanoTime()
    val time = ((t2 - t1) / 1.0E6) / iterations

    if (name != "") {
        println("$name time: $time")
    } else {
        println("time: $time")
    }
}

inline fun measureTime(name: String = "", run: () -> Unit) {
    val t1 = nanoTime()
    run()
    val t2 = nanoTime()
    val time = (t2 - t1) / 1.0E6

    if (name != "") {
        println("$name time: $time")
    } else {
        println("time: $time")
    }
}

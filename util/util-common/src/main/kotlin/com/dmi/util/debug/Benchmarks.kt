package com.dmi.util.debug

import com.dmi.util.log
import java.lang.System.nanoTime

fun benchmark(name: String = "", iterations: Int = 10, run: () -> Unit) {
    run()  // cold call

    val t1 = nanoTime()
    repeat(iterations) { run() }
    val t2 = nanoTime()
    val time = ((t2 - t1) / 1.0E6) / iterations

    if (name != "") {
        log.i("$name time: $time")
    } else {
        log.i("time: $time")
    }
}

fun <T> measureTime(name: String = "", run: () -> T): T {
    val t1 = nanoTime()
    val result = run()
    val t2 = nanoTime()
    val time = (t2 - t1) / 1.0E6

    if (name != "") {
        log.i("$name time: $time")
    } else {
        log.i("time: $time")
    }

    return result
}
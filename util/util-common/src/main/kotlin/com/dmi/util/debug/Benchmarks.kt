package com.dmi.util.debug

import java.lang.System.nanoTime


inline fun measureTime(name: String = "", run: () -> Unit) {
    val t1 = nanoTime()
    run()
    val t2 = nanoTime()
    val time = (t2 - t1) / 1E6

    if (name != "") {
        println("$name time: $time")
    } else {
        println("time: $time")
    }
}

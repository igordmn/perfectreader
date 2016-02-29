package com.dmi.util.debug

import java.lang.System.currentTimeMillis

inline fun measureTime(name: String = "", run: () -> Unit) {
    val t1 = currentTimeMillis()
    run()
    val t2 = currentTimeMillis()
    val time = t2 - t1

    if (name != "") {
        println("$name time: $time")
    } else {
        println("time: $time")
    }
}

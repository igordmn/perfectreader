package com.dmi.util.lang

fun unsupported(): Nothing = throw UnsupportedOperationException()
fun unsupported(value: Any): Nothing = throw UnsupportedOperationException("$value")

inline fun <reified E : Throwable, T> doWhileFail(action: () -> T): T {
    while (true) {
        try {
            return action()
        } catch (e: Throwable) {
            if (e.javaClass != E::class.java)
                throw e
        }
    }
}
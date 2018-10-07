package com.dmi.util.lang

fun unsupported(): Nothing = throw UnsupportedOperationException()
fun unsupported(value: Any): Nothing = throw UnsupportedOperationException("$value")
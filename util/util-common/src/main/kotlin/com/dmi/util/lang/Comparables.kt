package com.dmi.util.lang

fun <T: Comparable<T>> min(a: T, b: T) = if (a < b) a else b
fun <T: Comparable<T>> max(a: T, b: T) = if (a > b) a else b
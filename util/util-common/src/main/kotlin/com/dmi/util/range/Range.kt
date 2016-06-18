package com.dmi.util.range

interface Range<T : Comparable<T>> {
    val begin: T
    val end: T
}
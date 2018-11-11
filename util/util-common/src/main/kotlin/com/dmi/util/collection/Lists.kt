package com.dmi.util.collection

fun <T : Any> ArrayList<T>.removeLast(): T? = if (size > 0) removeAt(size - 1) else null
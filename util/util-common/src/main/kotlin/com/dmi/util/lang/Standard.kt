package com.dmi.util.lang

fun Any?.returnUnit() = Unit

fun <T : Any?> Any?.returnValue(value: T) = value
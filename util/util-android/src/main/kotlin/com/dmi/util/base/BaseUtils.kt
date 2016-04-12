package com.dmi.util.base

import android.os.Bundle
import java.io.Serializable

fun argumentsBundle(vararg arguments: Serializable?) = Bundle().apply {
    arguments.forEachIndexed { i, argument ->
        putSerializable("ARGUMENT$i", argument)
    }
}

@Suppress("UNCHECKED_CAST")
fun <A> Bundle.argumentAt(index: Int) = getSerializable("ARGUMENT$index") as A

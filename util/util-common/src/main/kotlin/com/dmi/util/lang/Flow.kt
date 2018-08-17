package com.dmi.util.lang

infix fun (() -> Unit).then(other: () -> Unit): () -> Unit {
    return {
        this()
        other()
    }
}
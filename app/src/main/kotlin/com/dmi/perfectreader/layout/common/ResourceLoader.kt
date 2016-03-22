package com.dmi.perfectreader.layout.common

import java.io.InputStream

interface ResourceLoader {
    fun load(src: String): InputStream
}

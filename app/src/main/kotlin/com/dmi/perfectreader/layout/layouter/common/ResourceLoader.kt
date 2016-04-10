package com.dmi.perfectreader.layout.layouter.common

import java.io.InputStream

interface ResourceLoader {
    fun load(src: String): InputStream
}

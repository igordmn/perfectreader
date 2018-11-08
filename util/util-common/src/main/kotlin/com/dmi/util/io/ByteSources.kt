package com.dmi.util.io

import com.google.common.io.ByteSource
import com.google.common.io.Files
import java.io.File
import java.io.InputStream

fun ByteSource(openStream: () -> InputStream) = object : ByteSource() {
    override fun openStream() = openStream()
}

fun File.asByteSource(): ByteSource = Files.asByteSource(this)
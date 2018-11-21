package com.dmi.perfectreader.book.parse

import android.net.Uri
import com.dmi.perfectreader.book.parse.format.TXTParser
import com.dmi.perfectreader.book.parse.format.fb2.FB2Parser
import com.dmi.util.lang.unsupported
import com.dmi.util.log.Log
import com.google.common.io.Files
import java.io.File

class BookParsers(log: Log) {
    private val charsetDetector = CharsetDetector(log)
    private val supportedExtensions = setOf("txt", "fb2")

    fun isSupported(uri: Uri) = uri.lastPathSegment!!.substringAfterLast(".") in supportedExtensions

    operator fun get(uri: Uri): BookParser {
        val extension = uri.path!!.substringAfterLast('.')
        val source = sourceFor(uri)
        val fileName = uri.pathSegments.last().substringBeforeLast(".")
        return when (extension) {
            "txt" -> TXTParser(charsetDetector, source, fileName)
            "fb2" -> FB2Parser(charsetDetector, source, fileName)
            else -> unsupported("Unsupported format $extension")
        }
    }

    private fun sourceFor(uri: Uri) = when (uri.scheme) {
        "file" -> Files.asByteSource(File(uri.path))
        else -> error("Unsupported scheme")
    }
}
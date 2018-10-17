package com.dmi.perfectreader.book.parse

import android.net.Uri
import com.dmi.perfectreader.book.parse.format.FB2ContentParser
import com.dmi.perfectreader.book.parse.format.TXTContentParser
import com.dmi.util.log.Log
import com.google.common.io.Files
import java.io.File

class BookContentParsers(log: Log, config: ParseConfig) {
    private val charsetDetector = CharsetDetector(log, config.defaultCharset)

    fun parserFor(uri: Uri): BookContentParser {
        val extension = uri.path!!.substringAfterLast('.')
        val source = sourceFor(uri)
        val fileName = uri.pathSegments.last()
        return when (extension) {
            "txt" -> TXTContentParser(charsetDetector, source, fileName)
            "fb2" -> FB2ContentParser(charsetDetector, source, fileName)
            else -> error("Unsupported format")
        }
    }

    private fun sourceFor(uri: Uri) = when (uri.scheme) {
        "file" -> Files.asByteSource(File(uri.path))
        else -> error("Unsupported scheme")
    }
}
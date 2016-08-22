package com.dmi.util.android.font

import com.dmi.util.log
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import java.util.*
import javax.xml.parsers.SAXParserFactory

fun androidSystemFontFiles(): Sequence<File> = try {
    parseFontFilesFromFontConfig(File("/system/fonts"), File("/etc/system_fonts.xml"))
} catch (e: Exception) {
    log.e(e, "Cannot read system fonts")
    emptySequence<File>()
}

fun parseFontFilesFromFontConfig(fontsFolder: File, fontConfigFile: File): Sequence<File> {
    val files = ArrayList<File>()

    val handler = object : DefaultHandler() {
        var isFamilySet = false
        var isFamily = false
        var isFileSet = false
        var isFile = false

        override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
            when (qName.toLowerCase()) {
                "familyset" -> isFamilySet = true
                "family" -> isFamily = true
                "fileset" -> isFileSet = true
                "file" -> isFile = true
            }
        }

        override fun endElement(uri: String, localName: String, qName: String) {
            when (qName.toLowerCase()) {
                "familyset" -> isFamilySet = false
                "family" -> isFamily = false
                "fileset" -> isFileSet = false
                "file" -> isFile = false
            }
        }

        override fun characters(chars: CharArray, start: Int, length: Int) {
            if (isFamilySet && isFamily && isFileSet && isFile) {
                val path = String(chars, start, length).trim()
                files.add(File(fontsFolder, path))
            }
        }
    }

    val saxParser = SAXParserFactory.newInstance().newSAXParser()
    saxParser.parse(fontConfigFile, handler)

    return files.asSequence()
}
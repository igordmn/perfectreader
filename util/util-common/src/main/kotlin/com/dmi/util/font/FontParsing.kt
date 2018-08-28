package com.dmi.util.font

import java.io.File
import java.io.RandomAccessFile
import java.lang.Math.min
import java.nio.charset.Charset

private val TRUETYPE_SFNT_VERSION = 0x00010000  // из двух SHORT значений 1 и 0 (т.е. версия 1.0)
private val CCF_SFNT_VERSION = 0x4F54544F  // int значение слова OTTO в ASCII кодировке
private val NAME_STR_INT = 0x6E616D65  // int значение слова name в ASCII кодировке
private val FAMILY_NAME_ID = 1
private val SUB_FAMILY_NAME_ID = 2
private val PLATFORM_UNICODE_ID = 0
private val PLATFORM_WINDOWS_ID = 3
private val ENCODING_WINDOWS_SYMBOL_ID = 0
private val ENCODING_WINDOWS_UNICODE_BMP_ID = 1
private val STRING_BUFFER = ByteArray(512)

private val WINDOWS_PLATFORM_ID = 3
private val MACINTOSH_PLATFORM_ID = 1
private val WINDOWS_ENGLISH_ID = 0x0409
private val MACINTOSH_ENGLISH_ID = 0

/**
 * See:
 * https://docs.microsoft.com/en-us/typography/opentype/spec/otff
 * https://docs.microsoft.com/ru-ru/typography/opentype/spec/name
 */
fun parseFontInfo(file: File): FontInfo {
    var familyName: String? = null
    var subFamilyName: String? = null

    RandomAccessFile(file, "r").use { raf ->
        val sfntVersion = raf.readInt()
        val numOfTables = raf.readShort()
        raf.readShort() // searchRange
        raf.readShort() // entrySelector
        raf.readShort() // rangeShift

        if (sfntVersion != TRUETYPE_SFNT_VERSION && sfntVersion != CCF_SFNT_VERSION)
            throw RuntimeException("Wrong font file version. Path: ${file.absolutePath}")
        if (numOfTables > 2000)
            throw RuntimeException("Too many tables. Path: ${file.absolutePath}")

        for (i in 0 until numOfTables) {
            val tableName = raf.readInt()
            raf.readInt() // checkSum
            val tableOffset = raf.readInt().toLong()
            raf.readInt() // length

            if (tableName == NAME_STR_INT) {
                raf.seek(tableOffset)
                raf.readShort() // format
                val recordCount = raf.readShort().toInt()
                val stringStorageOffset = raf.readShort().toLong()
                if (recordCount > 2000)
                    throw RuntimeException("Too many records. Path: ${file.absolutePath}")

                for (j in 0 until recordCount) {
                    val platformID = raf.readShort().toInt()
                    val encodingID = raf.readShort().toInt()
                    val languageID = raf.readShort().toInt()
                    val stringNameID = raf.readShort().toInt()
                    val stringLength = raf.readShort().toInt()
                    val stringOffset = raf.readShort().toLong()

                    val isLanguageValid = when (platformID) {
                        WINDOWS_PLATFORM_ID -> languageID == WINDOWS_ENGLISH_ID
                        MACINTOSH_PLATFORM_ID -> languageID == MACINTOSH_ENGLISH_ID
                        else -> true
                    }

                    if (stringLength > 0 && isLanguageValid) {
                        fun readValue(): String {
                            val stringTotalOffset = tableOffset + stringStorageOffset + stringOffset
                            val charset = chooseCharsetFor(platformID, encodingID)
                            return readString(raf, stringTotalOffset, stringLength, charset)
                        }

                        when (stringNameID) {
                            FAMILY_NAME_ID -> {
                                if (familyName == null)
                                    familyName = readValue()
                            }
                            SUB_FAMILY_NAME_ID -> {
                                if (subFamilyName == null)
                                    subFamilyName = readValue()
                            }
                        }
                    }

                    if (familyName != null && subFamilyName != null) {
                        break
                    }
                }

                break
            }
        }
    }

    return when {
        familyName != null && subFamilyName != null -> FontInfo(familyName!!, subFamilyName!!)
        else -> error("Font doesn't contain family name")
    }
}

private fun chooseCharsetFor(platformID: Int, encodingID: Int) = when {
    platformID == PLATFORM_UNICODE_ID -> Charsets.UTF_16
    platformID == PLATFORM_WINDOWS_ID && (encodingID == ENCODING_WINDOWS_SYMBOL_ID ||
                                          encodingID == ENCODING_WINDOWS_UNICODE_BMP_ID) -> Charsets.UTF_16
    else -> Charsets.ISO_8859_1
}

private fun readString(raf: RandomAccessFile, stringOffset: Long, stringLength: Int, charset: Charset): String {
    val clippedLength = min(STRING_BUFFER.size, stringLength)

    val oldPos = raf.filePointer
    raf.seek(stringOffset)
    raf.read(STRING_BUFFER, 0, clippedLength)
    raf.seek(oldPos)

    return String(STRING_BUFFER, 0, clippedLength, charset)
}

data class FontInfo(val familyName: String, val styleName: String)
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

/**
 * См. https://www.microsoft.com/typography/otspec/otff.htm и https://www.microsoft.com/typography/otspec/name.htm
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

        for (i in 0..numOfTables - 1) {
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

                for (j in 0..recordCount - 1) {
                    val platformID = raf.readShort().toInt()
                    val encodingID = raf.readShort().toInt()
                    raf.readShort() // languageID
                    val stringNameID = raf.readShort().toInt()
                    val stringLength = raf.readShort().toInt()
                    val stringOffset = raf.readShort().toLong()

                    if (stringLength > 0 && (stringNameID == FAMILY_NAME_ID || stringNameID == SUB_FAMILY_NAME_ID)) {
                        val stringTotalOffset = tableOffset + stringStorageOffset + stringOffset
                        val charset = chooseCharsetFor(platformID, encodingID)

                        when (stringNameID) {
                            FAMILY_NAME_ID -> {
                                familyName = readString(raf, stringTotalOffset, stringLength, charset)
                            }
                            SUB_FAMILY_NAME_ID -> {
                                subFamilyName = readString(raf, stringTotalOffset, stringLength, charset)
                            }
                        }

                        if (familyName != null && subFamilyName != null) {
                            break
                        }
                    }
                }

                break
            }
        }
    }

    if (familyName != null && subFamilyName != null) {
        return FontInfo(familyName!!, subFamilyName!!)
    } else {
        throw RuntimeException("Font doesn't contain family name")
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
package com.dmi.perfectreader.fragment.book.layout.obj

import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import java.lang.Math.round
import java.util.*
import java.util.Collections.emptyList

open class LayoutText(width: Float,
                      height: Float,
                      val text: CharSequence,
                      val locale: Locale,
                      val baseline: Float,
                      val charOffsets: FloatArray,
                      val style: ConfiguredFontStyle,
                      range: LocationRange
) : LayoutObject(width, height, emptyList<LayoutChild>(), range) {
    val charCount = text.length

    init {
        require(text.length > 0)
        require(text.length == charOffsets.size)
    }

    fun charOffset(index: Int): Float {
        require(index >=0 && index <= charCount)
        return if (index < charCount) charOffsets[index] else width
    }

    fun charLocation(index: Int): Location {
        require(index >=0 && index <= charCount)
        return range.sublocation(
                index.toDouble() / charCount
        )
    }

    fun charIndex(location: Location): Int {
        require(location >= range.begin && location <= range.end)
        return round(range.percentOf(location) * charCount).toInt()
    }

    override fun toString() = text.toString()
}
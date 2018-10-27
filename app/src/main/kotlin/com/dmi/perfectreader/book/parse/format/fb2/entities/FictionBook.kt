package com.dmi.perfectreader.book.parse.format.fb2.entities

import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.util.xml.ElementDesc
import com.dmi.util.xml.Text
import com.dmi.util.xml.parseDesc
import java.io.Reader

class FictionBook : ElementDesc() {
    val description: Description? by element("description", ::Description)
    val bodies: List<Body> by elements("body", ::Body)
}

fun parseFictionBook(reader: Reader): FictionBook = parseDesc(reader, "FictionBook", ::FictionBook)

fun Text.range() = LocationRange(Location(index.toDouble()), Location(index.toDouble() + data.length))
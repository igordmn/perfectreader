package com.dmi.perfectreader.book.parse.format.fb2.entities

import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.util.xml.ElementDesc
import com.dmi.util.xml.Text
import com.dmi.util.xml.parseDesc
import java.io.Reader

class Binary : ElementDesc() {
    val id: String? by attribute("id")
    val data: String by text()
}

class FictionBook : ElementDesc() {
    val description: Description? by element("description", ::Description)
    val bodies: List<Body> by elements("body", ::Body)
}

class FictionBookDescription : ElementDesc() {
    val description: Description? by element("description", ::Description)
}

class FictionBookBinaries : ElementDesc() {
    val binaries: List<Binary> by elements("binary", ::Binary)
}

fun parseFictionBook(reader: Reader): FictionBook = parseDesc(reader, "FictionBook", ::FictionBook)
fun parseFictionBookDescription(reader: Reader): Description? = parseDesc(reader, "FictionBook", ::FictionBookDescription).description
fun parseFictionBookBinaries(reader: Reader): List<Binary> = parseDesc(reader, "FictionBook", ::FictionBookBinaries).binaries

fun Text.range() = LocationRange(Location(index.toDouble()), Location(index.toDouble() + data.length))
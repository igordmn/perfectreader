package com.dmi.perfectreader.book.parse.format.fb2.entities

import com.dmi.util.xml.ElementDesc
import com.dmi.util.xml.ListDesc

// todo benchmark with this:
// ListDesc(nodes)
// companion object {
//     private val nodes = listOf("strong" to ::Strong, ...)
// }

open class Lines : ListDesc(
        "strong" to ::Strong,
        "emphasis" to ::Emphasis,
        "strikethrough" to ::Strikethrough,
        "sub" to ::Sub,
        "sup" to ::Sup,
        "code" to ::Code,
        "style" to ::Style,
        "a" to ::A,
        "image" to ::Image
) {
    val lang: String? by attribute("lang")
}

class Strong : Lines()
class Emphasis : Lines()
class Strikethrough : Lines()
class Sub : Lines()
class Sup : Lines()
class Code : Lines()
class Style : Lines()
class A : Lines()

class Image : ElementDesc() {
    val href: String? by attribute("href")
}

abstract class Box(vararg nodeByName: Pair<String, () -> ElementDesc>) : ListDesc(
        "epigraph" to ::Epigraph,
        "image" to ::Image,
        "annotation" to ::Annotation,
        "p" to ::P,
        "poem" to ::Poem,
        "title" to ::Title,
        "subtitle" to ::Subtitle,
        "empty-line" to ::EmptyLine,
        "text-author" to ::TextAuthor,
        "table" to ::Table,
        "cite" to ::Cite,
        *nodeByName
) {
    val lang: String? by attribute("lang")
}

class Poem : Box("stanza" to ::Stanza)
class Stanza : Box("v" to ::V)
class V : Lines()

class Title : Box()
class P : Lines()
class Subtitle : Lines()
class TextAuthor : Lines()
class EmptyLine : ElementDesc()
class Epigraph : Box()
class Annotation : Box()
class Cite : Box()
class Table : ElementDesc()

open class Section : Box("section" to ::Section)
class Body : Section()
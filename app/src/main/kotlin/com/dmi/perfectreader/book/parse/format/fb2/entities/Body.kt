package com.dmi.perfectreader.book.parse.format.fb2.entities

import com.dmi.util.xml.ElementDesc
import com.dmi.util.xml.ListDesc

open class Text : ListDesc(
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

class Strong : Text()
class Emphasis : Text()
class Strikethrough : Text()
class Sub : Text()
class Sup : Text()
class Code : Text()
class Style : Text()
class A : Text()

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
class V : Text()

class Title : Box()
class P : Text()
class Subtitle : Text()
class TextAuthor : Text()
class EmptyLine : ElementDesc()
class Epigraph : Box()
class Annotation : Box()
class Cite : Box()
class Table : ElementDesc()

open class Section : Box("section" to ::Section)
class Body : Section()
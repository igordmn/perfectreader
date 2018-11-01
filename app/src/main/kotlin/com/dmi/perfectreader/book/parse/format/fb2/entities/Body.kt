package com.dmi.perfectreader.book.parse.format.fb2.entities

import com.dmi.util.xml.ElementDesc
import com.dmi.util.xml.ListDesc

// todo benchmark with this:
// ListDesc(nodes)
// companion object {
//     private val nodes = listOf("strong" to ::Strong, ...)
// }

abstract class Inline : ListDesc(
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

class Strong : Inline()
class Emphasis : Inline()
class Strikethrough : Inline()
class Sub : Inline()
class Sup : Inline()
class Code : Inline()
class Style : Inline()
class A : Inline()

class Image : ElementDesc() {
    val href: String? by attribute("href")
}

abstract class Box(vararg nodeByName: Pair<String, () -> ElementDesc>) : ListDesc(
        "image" to ::Image,
        "epigraph" to ::Epigraph,
        "annotation" to ::Annotation,
        "p" to ::P,
        "poem" to ::Poem,
        "stanza" to ::Stanza,
        "v" to ::V,
        "title" to ::Title,
        "subtitle" to ::Subtitle,
        "cite" to ::Cite,
        "text-author" to ::TextAuthor,
        "empty-line" to ::EmptyLine,
        "table" to ::Table,
        *nodeByName
) {
    val lang: String? by attribute("lang")
}

abstract class Lines : Inline()

class Poem : Box()
class Stanza : Box()
class Title : Box()
class Epigraph : Box()
class Annotation : Box()
class Cite : Box()

class V : Lines()
class P : Lines()
class Subtitle : Lines()
class TextAuthor : Lines()

class EmptyLine : ElementDesc()
class Table : ElementDesc()

open class Section : Box("section" to ::Section) {
    // todo add support of multiple elements in ElementDesc with same name
    // now you cannot use class "ListDesc" or fun "nodes" with fun "element" if name is the same
    val title: Title? by lazy { find { it is Title } as Title? }
}

class Body : Section()
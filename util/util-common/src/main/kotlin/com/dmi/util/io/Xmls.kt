package com.dmi.util.io

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

fun parseDOM(stream: InputStream): Document {
    val factory = DocumentBuilderFactory.newInstance()
    factory.isCoalescing = true
    return factory.newDocumentBuilder().parse(stream)
}

fun Element.getChild(name: String): Node {
    val children = getElementsByTagName(name)
    require(children.length > 0) { "Node \"$nodeName\" does't contain node \"$name\""}
    return children.item(0)
}
package com.dmi.util.xml

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.InputStream
import java.io.Reader
import javax.xml.parsers.DocumentBuilderFactory

fun parseXML(stream: InputStream): Document {
    val factory = DocumentBuilderFactory.newInstance()
    factory.isCoalescing = true
    return factory.newDocumentBuilder().parse(stream)
}

fun parseXML(reader: Reader): Document {
    val factory = DocumentBuilderFactory.newInstance()
    factory.isCoalescing = true
    return factory.newDocumentBuilder().parse(InputSource(reader))
}

fun Element.getChild(name: String): Node {
    val children = getElementsByTagName(name)
    require(children.length > 0) { "Node \"$nodeName\" doesn't contain node \"$name\"" }
    return children.item(0)
}
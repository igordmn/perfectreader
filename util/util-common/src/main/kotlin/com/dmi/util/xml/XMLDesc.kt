package com.dmi.util.xml

import com.dmi.util.lang.ReadOnlyDelegate
import com.dmi.util.lang.initOnce
import com.dmi.util.lang.required
import kotlinx.io.Reader
import org.kxml2.io.KXmlParser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.FEATURE_PROCESS_NAMESPACES
import java.util.*
import kotlin.collections.ArrayList

abstract class XMLDesc {
    /**
     * index of xml node in whole xml tree
     */
    // todo replace index by char offset in xml file. maybe fork kxml or use antlr for xml parsing
    // char offset is more robust. it is preserving value across different implementation of xml parsing
    // so when we change xml parsing, update application, user saved book location will not change
    var index: Int by initOnce()
        internal set
}

class Text : XMLDesc() {
    var data: String by initOnce()
        internal set
}

abstract class ListDesc(vararg nodeByName: Pair<String, () -> ElementDesc>) : ElementDesc(), Iterable<XMLDesc> {
    private val children: List<XMLDesc> by nodes(*nodeByName)
    override fun iterator() = children.iterator()
}

abstract class ElementDesc : XMLDesc() {
    private var inits: Inits? = Inits()

    internal val isComplete get() = inits!!.isComplete

    internal fun addAttribute(name: String, value: String) = inits!!.addAttribute(name, value)
    internal fun addElement(name: String): ElementDesc? = inits!!.addElement(name)
    internal fun addText(): Text? = inits!!.addText()

    internal fun commit() {
        inits!!.commit()
        inits = null
    }

    protected fun attribute(name: String) = xmlDelegate<String?> {
        var value: String? = null

        onAttribute(name) {
            value = it
        }

        onValue {
            value
        }
    }

    protected fun <T : ElementDesc> element(name: String, desc: () -> T) = xmlDelegate<T?> {
        var value: T? = null

        onFirstElement(name) {
            value = desc()
            value!!
        }

        onValue {
            value
        }
    }

    protected fun <T : ElementDesc> elements(name: String, desc: () -> T) = xmlDelegate<List<T>> {
        val list = ArrayList<T>()

        onElement(name) {
            val value = desc()
            list.add(value)
            value
        }

        onValue {
            list
        }
    }

    protected fun element(name: String): ReadOnlyDelegate<Any?, String?> = xmlDelegate {
        class Element : ElementDesc() {
            val text: String by text()
        }

        val element by element(name, ::Element)

        onValue {
            element?.text
        }
    }

    /**
     * List of [Text] or [ElementDesc]
     */
    protected fun nodes(vararg createByName: Pair<String, () -> ElementDesc>): ReadOnlyDelegate<Any?, List<XMLDesc>> = xmlDelegate {
        val list = ArrayList<XMLDesc>()

        createByName.forEach {
            val itemName = it.first
            val itemDesc = it.second

            onElement(itemName) {
                val itemValue = itemDesc()
                list.add(itemValue)
                itemValue
            }
        }

        onText {
            val text = Text()
            list.add(text)
            text
        }

        onValue {
            list
        }
    }

    protected fun text(): ReadOnlyDelegate<Any?, String> = xmlDelegate {
        val descs = ArrayList<Text>()

        onText {
            val text = Text()
            descs.add(text)
            text
        }

        onValue {
            descs.joinToString("") { it.data }
        }
    }

    private fun <T> xmlDelegate(init: DelegateInit<T>.() -> Unit) = ReadOnlyDelegate<Any?, T> {
        var value: T by initOnce()

        val delegateInit = object : DelegateInit<T>(inits!!) {
            override fun onValue(createValue: () -> T) = inits.onCommit {
                value = createValue()
            }
        }
        delegateInit.init()

        com.dmi.util.lang.value { value }
    }

    private abstract class DelegateInit<T>(protected val inits: Inits) {
        fun onAttribute(name: String, action: (value: String) -> Unit) = inits.onAttribute(name, action)
        fun onFirstElement(name: String, action: () -> ElementDesc) = inits.onFirstElement(name, action)
        fun onElement(name: String, action: () -> ElementDesc) = inits.onElement(name, action)
        fun onText(action: () -> Text) = inits.onText(action)
        abstract fun onValue(createValue: () -> T)
    }

    private class Inits {
        private val attributeByName = HashMap<String, (value: String) -> Unit>()
        private val firstElementByName = HashMap<String, () -> ElementDesc>()
        private val elementByName = HashMap<String, () -> ElementDesc>()
        private var text: (() -> Text)? = null
        private val commits = ArrayList<() -> Unit>()

        internal val isComplete get() = attributeByName.isEmpty() && firstElementByName.isEmpty() && elementByName.isEmpty() && text == null

        fun addAttribute(name: String, value: String) {
            attributeByName[name]?.invoke(value)
            attributeByName.remove(name)
        }

        fun addElement(name: String): ElementDesc? {
            val element = firstElementByName[name]?.invoke() ?: elementByName[name]?.invoke()
            firstElementByName.remove(name)
            return element
        }

        fun addText(): Text? = text?.invoke()
        fun commit() = commits.forEach { it.invoke() }

        fun onAttribute(name: String, action: (value: String) -> Unit) {
            require(!attributeByName.contains(name))
            attributeByName[name] = action
        }

        fun onFirstElement(name: String, action: () -> ElementDesc) {
            require(!firstElementByName.contains(name) && !elementByName.contains(name))
            firstElementByName[name] = action
        }

        fun onElement(name: String, action: () -> ElementDesc) {
            require(!firstElementByName.contains(name) && !elementByName.contains(name))
            elementByName[name] = action
        }

        fun onText(action: () -> Text) {
            require(text == null)
            text = action
        }

        fun onCommit(action: () -> Unit) {
            commits += action
        }
    }
}

// todo maybe make suspend with yield? need benchmark
fun <T : ElementDesc> parseDesc(reader: Reader, name: String, desc: () -> T): T {
    val parser = KXmlParser()
    parser.setInput(reader)
    parser.setFeature(FEATURE_PROCESS_NAMESPACES, true)

    val document = object : ElementDesc() {
        val root: T by element(name, desc).required()
    }

    val descs = LinkedList<ElementDesc?>()
    descs.push(document)

    fun onStart(index: Int) {
        val child = descs.peek()?.addElement(parser.name)?.also {
            it.parseAttributes(parser)
            it.index = index
        }
        descs.push(child)
    }

    fun onEnd() = descs.pop()?.commit()

    fun onText(index: Int) = descs.peek()?.addText()?.also {
        it.data = parser.text
        it.index = index
    }

    fun isComplete() = descs.all { it == null || it.isComplete }

    var index = 0
    var type = parser.next()
    while (type != XmlPullParser.END_DOCUMENT && !isComplete()) {
        when (type) {
            XmlPullParser.START_TAG -> onStart(index)
            XmlPullParser.END_TAG -> onEnd()
            XmlPullParser.TEXT, XmlPullParser.CDSECT -> onText(index)
        }
        type = parser.next()
        index++
    }

    while(descs.size > 0)
        descs.pop()!!.commit()

    return document.root
}

fun ElementDesc.parseAttributes(parser: KXmlParser) {
    for (i in 0 until parser.attributeCount) {
        addAttribute(
                name = parser.getAttributeName(i),
                value = parser.getAttributeValue(i)
        )
    }
}
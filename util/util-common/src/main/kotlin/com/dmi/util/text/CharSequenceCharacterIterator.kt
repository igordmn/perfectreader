package com.dmi.util.text

import com.google.common.base.Preconditions.checkArgument
import java.text.CharacterIterator

class CharSequenceCharacterIterator(private val text: CharSequence) : CharacterIterator {
    private var index: Int = 0

    init {
        index = 0
    }

    override fun getBeginIndex(): Int {
        return 0
    }

    override fun getEndIndex(): Int {
        return text.length
    }

    override fun getIndex(): Int {
        return index
    }

    override fun current(): Char {
        return if (index < text.length) text[index] else CharacterIterator.DONE
    }

    override fun first(): Char {
        index = 0
        return current()
    }

    override fun last(): Char {
        index = if (text.length > 0) text.length - 1 else 0
        return current()
    }

    override fun next(): Char {
        return if (index < text.length - 1) text[++index] else CharacterIterator.DONE
    }

    override fun previous(): Char {
        return if (index > 0) text[--index] else CharacterIterator.DONE
    }

    override fun setIndex(index: Int): Char {
        checkArgument(index >= 0 || index < text.length)
        this.index = index
        return current()
    }

    override fun clone(): Any {
        val clone = CharSequenceCharacterIterator(text)
        clone.index = index
        return clone
    }
}

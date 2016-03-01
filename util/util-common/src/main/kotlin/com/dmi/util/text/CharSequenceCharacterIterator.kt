package com.dmi.util.text

import com.google.common.base.Preconditions.checkArgument
import java.text.CharacterIterator
import java.text.CharacterIterator.DONE

class CharSequenceCharacterIterator(private val text: CharSequence) : CharacterIterator {
    private var index = 0

    override fun getBeginIndex() = 0

    override fun getEndIndex() = text.length

    override fun getIndex() = index

    override fun current() = if (index < text.length) text[index] else DONE

    override fun first(): Char {
        index = 0
        return current()
    }

    override fun last(): Char {
        index = if (text.length > 0) text.length - 1 else 0
        return current()
    }

    override fun next() = if (index < text.length - 1) text[++index] else DONE

    override fun previous() = if (index > 0) text[--index] else DONE

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

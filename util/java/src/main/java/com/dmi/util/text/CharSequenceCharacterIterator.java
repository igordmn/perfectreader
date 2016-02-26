package com.dmi.util.text;

import java.text.CharacterIterator;

import static com.google.common.base.Preconditions.checkArgument;

public class CharSequenceCharacterIterator implements CharacterIterator {
    private final CharSequence text;

    private int index;

    public CharSequenceCharacterIterator(CharSequence text) {
        this.text = text;
        index = 0;
    }

    @Override
    public int getBeginIndex() {
        return 0;
    }

    @Override
    public int getEndIndex() {
        return text.length();
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public char current() {
        return index < text.length() ? text.charAt(index) : DONE;
    }

    @Override
    public char first() {
        index = 0;
        return current();
    }

    @Override
    public char last() {
        index = text.length() > 0 ? text.length() - 1 : 0;
        return current();
    }

    @Override
    public char next() {
        return index < text.length() - 1 ? text.charAt(++index) : DONE;
    }

    @Override
    public char previous() {
        return index > 0 ? text.charAt(--index) : DONE;
    }

    @Override
    public char setIndex(int index) {
        checkArgument(index >= 0 || index < text.length());
        this.index = index;
        return current();
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

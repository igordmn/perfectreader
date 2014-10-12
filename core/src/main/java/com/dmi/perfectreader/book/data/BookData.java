package com.dmi.perfectreader.book.data;

import com.dmi.perfectreader.book.position.Position;

import java.io.IOException;

/**
 * Основное правило - любой позиции должна всегда соответствовать единственная страница, независимо от того, как эта позиция была достигнута
 */
public interface BookData {
    ItemReader itemReader(Position position) throws IOException;

    /**
     * Читать элементы до position, которые влияют на элементы, находящиеся на и после position
     * (например, такими элементами могут быть символ параграфа \n, маркер изменения стиля, начало новой строки в таблице)
     */
    ItemReader influenceItemReader(Position position) throws IOException;
}

package com.dmi.perfectreader.facade;

public interface BookReaderFacade {
    default BookFacade book() { return null; }

    default void toggleMenu() {}

    default void exit() {}
}

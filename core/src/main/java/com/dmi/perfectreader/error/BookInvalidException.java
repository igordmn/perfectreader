package com.dmi.perfectreader.error;

import java.io.IOException;

public class BookInvalidException extends IOException {
    public BookInvalidException(Throwable cause) {
        super(cause);
    }
}

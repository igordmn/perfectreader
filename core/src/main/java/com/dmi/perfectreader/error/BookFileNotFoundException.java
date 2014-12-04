package com.dmi.perfectreader.error;

import java.io.File;
import java.io.FileNotFoundException;

public class BookFileNotFoundException extends FileNotFoundException {
    private File file;

    public BookFileNotFoundException(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}

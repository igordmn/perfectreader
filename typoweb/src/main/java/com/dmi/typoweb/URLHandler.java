package com.dmi.typoweb;

import java.io.IOException;
import java.io.InputStream;

public interface URLHandler {
    InputStream handleURL(String url) throws IOException, SecurityException;
}

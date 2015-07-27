package com.dmi.typoweb;

import java.io.IOException;
import java.io.InputStream;

public interface HyphenationPatternsLoader {
    InputStream loadPatterns(String language) throws IOException;
}

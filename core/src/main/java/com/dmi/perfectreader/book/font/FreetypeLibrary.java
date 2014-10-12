package com.dmi.perfectreader.book.font;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FreetypeLibrary {
    public final String fontsPath;
    public final long cFontsPath;
    public final long ftLibrary;
    public final long ftcManager;
    public final long ftcSBitCache;

    public final Object freetypeMutex = this;

    private final int hDpi;
    private final int vDpi;

    private final Map<FontFace, Long> ftcScalers = new HashMap<>();

    public FreetypeLibrary(File fontsPath, int hDpi, int vDpi) {
        this.fontsPath = fontsPath.getAbsolutePath();
        cFontsPath = nativeNewCText(this.fontsPath);
        ftLibrary = nativeInitFtLibrary();
        ftcManager = nativeInitFtcManager(ftLibrary, cFontsPath);
        ftcSBitCache = nativeInitSbitCache(ftcManager);
        this.hDpi = hDpi;
        this.vDpi = vDpi;
    }

    private static native long nativeInitFtLibrary();

    private static native void nativeDoneFtLibrary(long ftLibrary);

    private static native long nativeInitFtcManager(long ftLibrary, long fontsPath);

    private static native void nativeDoneFtcManager(long ftManager);

    private static native long nativeInitSbitCache(long ftcManager);

    private static native long nativeNewCText(String text);

    private static native void nativeDeleteCText(String text, long cText);

    private static native long nativeCreateScaler(String name, float width, float height, int hDpi, int vDpi);

    private static native void nativeDestroyScaler(long scaler);

    public long ftcScaler(FontFace fontFace) {
        Long ftcScaler = ftcScalers.get(fontFace);
        if (ftcScaler == null) {
            ftcScaler = nativeCreateScaler(fontFace.name.toLowerCase(), fontFace.size, fontFace.size, hDpi, vDpi);
            ftcScalers.put(fontFace, ftcScaler);
        }
        return ftcScaler;
    }

    public void destroy() {
        for (Long ftcScaler : ftcScalers.values()) {
            nativeDestroyScaler(ftcScaler);
        }
        nativeDoneFtcManager(ftcManager);
        nativeDoneFtLibrary(ftLibrary);
        nativeDeleteCText(fontsPath, cFontsPath);
    }
}

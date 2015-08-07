package com.dmi.typoweb;

import android.annotation.SuppressLint;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import timber.log.Timber;

class TypoWebLibrary {
    private static boolean init = false;

    private static final ComponentCallbacks2 componentCallbacks = new ComponentCallbacks2() {
        @Override
        public void onTrimMemory(int level) {
            if (level >= ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
                lowMemoryNotification(true);
            } else if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND ||
                       level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
                lowMemoryNotification(false);
            }
        }

        @Override
        public void onLowMemory() {
            lowMemoryNotification(true);
        }

        @Override
        public void onConfigurationChanged(Configuration configuration) {
        }
    };

    static {
        System.loadLibrary("typoweb");
    }

    public static void checkInit(final Context context, final String userAgent) {
        if (!init) {
            init = true;
            ApplicationContext.set(context.getApplicationContext());
            context.getApplicationContext().registerComponentCallbacks(componentCallbacks);
            nativeStartMainThread();
            mainThread().postTask(() -> {
                initICU(context);
                nativeInitBlink(userAgent);
            });
        }
    }

    @SuppressLint("NewApi")
    private static void initICU(Context context) {
        try {
            try (InputStream is = context.getAssets().open("icu/icudtl.dat")) {
                ByteBuffer icuData = nativeNewDirectByteBuffer(is.available());
                byte[] buf = new byte[8 * 1024];
                int length;
                while ((length = is.read(buf)) != -1) {
                    icuData.put(buf, 0, length);
                }
                nativeInitICU(icuData);
            }
        } catch (IOException e) {
            Timber.e(e, "ICU init error");
        }
    }

    public static WebThreadImpl mainThread() {
        return nativeMainThread();
    }

    private static void lowMemoryNotification(final boolean critical) {
        mainThread().postTask(() -> nativeLowMemoryNotification(critical));
    }

    public static void pause() {
        mainThread().postTask(TypoWebLibrary::nativePause);
    }

    public static void resume() {
        mainThread().postTask(TypoWebLibrary::nativeResume);
    }

    public static void setURLHandler(URLHandler urlHandler) {
        mainThread().postTask(() -> nativeSetURLHandler(urlHandler));
    }

    public static void setHangingPunctuationConfig(HangingPunctuationConfig config) {
        mainThread().postTask(() -> nativeSetHangingPunctuationConfig(config));
    }

    public static void setHyphenationPatternsLoader(HyphenationPatternsLoader patternsLoader) {
        mainThread().postTask(() -> nativeSetHyphenationPatternsLoader(patternsLoader));
    }

    private static native void nativeStartMainThread();
    private static native WebThreadImpl nativeMainThread();
    private static native void nativeInitBlink(String userAgent);
    private static native void nativeInitICU(ByteBuffer byteBuffer);
    private static native ByteBuffer nativeNewDirectByteBuffer(int size);
    private static native void nativeLowMemoryNotification(boolean critical);
    private static native void nativePause();
    private static native void nativeResume();
    private static native void nativeSetURLHandler(URLHandler urlHandler);
    private static native void nativeSetHangingPunctuationConfig(HangingPunctuationConfig config);
    private static native void nativeSetHyphenationPatternsLoader(HyphenationPatternsLoader patternsLoader);
}

package com.dmi.typoweb;

import android.annotation.SuppressLint;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

class TypoWebLibrary {
    private static final String LOG_TAG = TypoWebLibrary.class.getSimpleName();

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
            try (InputStream is = context.getResources().openRawResource(R.raw.icudtl)) {
                ByteBuffer icuData = nativeNewDirectByteBuffer(is.available());
                byte[] buf = new byte[8 * 1024];
                int length;
                while ((length = is.read(buf)) != -1) {
                    icuData.put(buf, 0, length);
                }
                nativeInitICU(icuData);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
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

    private static native void nativeStartMainThread();
    private static native WebThreadImpl nativeMainThread();
    private static native void nativeInitBlink(String userAgent);
    private static native void nativeInitICU(ByteBuffer byteBuffer);
    private static native ByteBuffer nativeNewDirectByteBuffer(int size);
    private static native void nativeLowMemoryNotification(boolean critical);
    private static native void nativePause();
    private static native void nativeResume();
}

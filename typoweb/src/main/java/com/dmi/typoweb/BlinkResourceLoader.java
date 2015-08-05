package com.dmi.typoweb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.dmi.util.natv.UsedByNative;
import com.google.common.io.ByteStreams;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;

@UsedByNative
class BlinkResourceLoader {
    private static final String LOG_TAG = BlinkResourceLoader.class.getSimpleName();
    private static final String RES_FOLDER = "webkit/res/";

    @SuppressLint("NewApi")
    @UsedByNative
    private static byte[] loadResource(String name) {
        Context context = ApplicationContext.get();
        try (InputStream is = context.getAssets().open(RES_FOLDER + name)) {
            return ByteStreams.toByteArray(is);
        } catch (FileNotFoundException e) {
            Log.w(LOG_TAG, format("Resource %s doesn't exist in application", name));
        } catch (IOException e) {
            Log.e(LOG_TAG, format("Error loading blink resource %s :\n%s", name, Log.getStackTraceString(e)));
        }
        return new byte[0];
    }
}

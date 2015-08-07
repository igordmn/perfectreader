package com.dmi.typoweb;

import android.annotation.SuppressLint;
import android.content.Context;

import com.dmi.util.natv.UsedByNative;
import com.google.common.io.ByteStreams;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

import static java.lang.String.format;

@UsedByNative
class BlinkResourceLoader {
    private static final String RES_FOLDER = "webkit/res/";

    @SuppressLint("NewApi")
    @UsedByNative
    private static byte[] loadResource(String name) {
        Context context = ApplicationContext.get();
        try (InputStream is = context.getAssets().open(RES_FOLDER + name)) {
            return ByteStreams.toByteArray(is);
        } catch (FileNotFoundException e) {
            Timber.w(format("Resource %s doesn't exist in application", name));
        } catch (IOException e) {
            Timber.e(e, "Error loading blink resource: %s", name);
        }
        return new byte[0];
    }
}

package com.dmi.typoweb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.dmi.util.natv.UsedByNative;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@UsedByNative
class BlinkResourceLoader {
    private static final String LOG_TAG = BlinkResourceLoader.class.getSimpleName();

    // WARNING! don't delete win, mac, etc resources. they are loading by blink
    private static final Map<String, Integer> fileNameToResource = new HashMap<String, Integer>() {{
        put("fullscreen.css", R.raw.css_fullscreen);
        put("html.css", R.raw.css_html);
        put("mathml.css", R.raw.css_mathml);
        put("mediaControls.css", R.raw.css_media_controls);
        put("mediaControlsAndroid.css", R.raw.css_media_controls_android);
        put("navigationTransitions.css", R.raw.css_navigation_transitions);
        put("quirks.css", R.raw.css_quirks);
        put("svg.css", R.raw.css_svg);
        put("themeChromium.css", R.raw.css_theme_chromium);
        put("themeChromiumAndroid.css", R.raw.css_theme_chromium_android);
        put("themeChromiumLinux.css", R.raw.css_theme_chromium_linux);
        put("themeChromiumSkia.css", R.raw.css_theme_chromium_skia);
        put("themeInputMultipleFields.css", R.raw.css_theme_input_multiple_fields);
        put("themeMac.css", R.raw.css_theme_mac);
        put("themeWin.css", R.raw.css_theme_win);
        put("themeWinQuirks.css", R.raw.css_theme_win_quirks);
        put("view-source.css", R.raw.css_view_source);
        put("viewportAndroid.css", R.raw.css_viewport_android);
        put("xhtmlmp.css", R.raw.css_xhtmlmp);
    }};

    @UsedByNative
    private static byte[] loadResource(String name) {
        Integer resId = fileNameToResource.get(name);
        if (resId != null) {
            try {
                return getResourceData(resId);
            } catch (IOException e) {
                Log.e(LOG_TAG, format("Error loading blink resource %s :\n%s", name, Log.getStackTraceString(e)));
            }
        } else {
            Log.w(LOG_TAG, format("Resource %s doesn't exist in application", name));
        }
        return new byte[0];
    }

    @SuppressLint("NewApi")
    private static byte[] getResourceData(Integer resId) throws IOException {
        Context context = ApplicationContext.get();
        try (InputStream is = context.getResources().openRawResource(resId)) {
            return ByteStreams.toByteArray(is);
        }
    }
}

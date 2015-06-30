package com.dmi.util;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceUtils {
    @SuppressLint("NewApi")
    public static String readTextRawResource(Resources resources,
                                             final int resourceId) {
        try (InputStream inputStream = resources.openRawResource(resourceId)) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                try (BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader)) {
                    String nextLine;
                    final StringBuilder body = new StringBuilder();

                    while ((nextLine = bufferedReader.readLine()) != null) {
                        body.append(nextLine);
                        body.append('\n');
                    }

                    return body.toString();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

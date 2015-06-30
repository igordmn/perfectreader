package com.dmi.typoweb;

import android.content.Context;

abstract class ApplicationContext {
    private static Context instance;

    public static void set(Context context) {
        instance = context != null ? context.getApplicationContext() : null;
    }

    public static Context get() {
        return instance;
    }
}

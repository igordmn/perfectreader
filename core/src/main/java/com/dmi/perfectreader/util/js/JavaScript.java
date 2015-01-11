package com.dmi.perfectreader.util.js;

import java.util.List;

public abstract class JavaScript {
    public static String jsArray(List<String> items) {
        return jsArray((String[]) items.toArray());
    }

    public static String jsArray(String[] items) {
        StringBuilder js = new StringBuilder();
        js.append("[\n");
        for (int i = 0; i < items.length; i++) {
            js.append("'");
            js.append(items[i]);
            js.append("'");
            if (i < items.length - 1) {
                js.append(",\n");
            }
        }
        js.append("]\n");
        return js.toString();
    }

    public static String jsValue(String value) {
        return "'" + value + "'";
    }

    public static String jsValue(int value) {
        return String.valueOf(value);
    }
}

package com.dmi.util.android.jni;

@UsedByNative
class JniUtils {
    @UsedByNative
    public static String getCurrentStackTrace() {
        StringBuilder s = new StringBuilder();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 3; i < stackTrace.length; i++) {
            if (s.length() > 0)
                s.append('\n');
            s.append("\tat ");
            s.append(stackTrace[i].toString());
        }
        return s.toString();
    }
}
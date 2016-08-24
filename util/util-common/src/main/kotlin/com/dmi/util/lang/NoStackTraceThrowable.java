package com.dmi.util.lang;

public class NoStackTraceThrowable extends Throwable {
    public NoStackTraceThrowable(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
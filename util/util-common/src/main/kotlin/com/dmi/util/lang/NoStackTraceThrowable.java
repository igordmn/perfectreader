package com.dmi.util.lang;

class NoStackTraceThrowable extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
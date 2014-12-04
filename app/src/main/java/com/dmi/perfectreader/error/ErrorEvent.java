package com.dmi.perfectreader.error;

public class ErrorEvent {
    private final Throwable throwable;

    public ErrorEvent(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}

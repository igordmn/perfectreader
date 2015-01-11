package com.dmi.perfectreader.command;

public class RunnableCommand implements Command {
    private final Runnable runnable;

    public RunnableCommand(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void perform() {
        runnable.run();
    }
}

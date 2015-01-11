package com.dmi.perfectreader.command;

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class Commands {
    private Command toggleMenuCommand;

    public void setToggleMenuCommand(Command toggleMenuCommand) {
        this.toggleMenuCommand = toggleMenuCommand;
    }

    public void toggleMenu() {
        toggleMenuCommand.perform();
    }
}

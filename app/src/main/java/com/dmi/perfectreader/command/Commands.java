package com.dmi.perfectreader.command;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class Commands {
    @Bean
    protected ToggleMenuCommand toggleMenuCommand;

    public void toggleMenu() {
        toggleMenuCommand.perform();
    }
}

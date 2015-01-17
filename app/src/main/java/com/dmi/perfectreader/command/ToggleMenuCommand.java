package com.dmi.perfectreader.command;

import com.dmi.perfectreader.main.ToggleMenuIntent;
import com.dmi.perfectreader.util.android.EventBus;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class ToggleMenuCommand implements Command {
    @Bean
    protected EventBus eventBus;

    @Override
    public void perform() {
        eventBus.post(new ToggleMenuIntent());
    }
}

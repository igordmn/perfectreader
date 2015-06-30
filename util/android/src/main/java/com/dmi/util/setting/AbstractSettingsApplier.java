package com.dmi.util.setting;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSettingsApplier {
    private final List<AbstractSettings.Setting<?>> settings = new ArrayList<>();

    public void startListen() {
        listen();
    }

    public void stopListen() {
        for (AbstractSettings.Setting<?> setting : settings) {
            setting.setListener(null);
        }
    }

    protected abstract void listen();

    protected <T> void listen(AbstractSettings.Setting<T> setting, SettingListener<T> listener) {
        setting.setListener(listener);
        settings.add(setting);
    }
}
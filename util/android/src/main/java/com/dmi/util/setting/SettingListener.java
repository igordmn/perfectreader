package com.dmi.util.setting;

public interface SettingListener<T> {
    void onValueSet(T value);
}

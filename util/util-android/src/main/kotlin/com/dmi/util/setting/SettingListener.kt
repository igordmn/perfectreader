package com.dmi.util.setting

interface SettingListener<T> {
    fun onValueSet(value: T)
}

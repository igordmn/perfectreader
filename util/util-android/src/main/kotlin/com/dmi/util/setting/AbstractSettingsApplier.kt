package com.dmi.util.setting

import java.util.*

abstract class AbstractSettingsApplier {
    private val settings = ArrayList<AbstractSettings.Setting<*>>()

    fun startListen() {
        listen()
    }

    fun stopListen() {
        for (setting in settings) {
            setting.removeListener()
        }
    }

    protected abstract fun listen()

    protected fun <T: Any> listen(setting: AbstractSettings.Setting<T>, listener: SettingListener<T>) {
        setting.setListener(listener)
        settings.add(setting)
    }
}
package com.dmi.perfectreader.settingschange

import kotlinx.serialization.Serializable

class SettingsChange(val back: () -> Unit, val state: SettingsChangeState)

@Serializable
class SettingsChangeState
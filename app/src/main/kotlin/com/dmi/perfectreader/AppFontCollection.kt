package com.dmi.perfectreader

import com.dmi.perfectreader.data.UserSettingKeys
import com.dmi.perfectreader.data.UserSettings

fun userFontsDirectory(protocols: AppProtocols, settings: UserSettings) =
        protocols.fileFor(settings[UserSettingKeys.System.fontsPath])
package com.dmi.perfectreader.settings

import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class OtherSettings(store: ValueStore) {
    var fontsPath by store.value("externalStorage://Fonts")

    var scaleByDpi by store.value(true)
    var scaleByDpiInteger by store.value(true)
    var scaleFixed by store.value(1F)
    var scaleIncFiltered by store.value(false)
    var scaleDecFiltered by store.value(true)

    var pageSymbolCount by store.value(1024)
    var pageSymbolCountIsAuto by store.value(true)

    var defaultLanguageIsSystem by store.value(true)
    var defaultLanguage by store.value("")
    var ignoreDeclaredLanguage by store.value(false)

    val selectWords: Boolean by store.value(true)
}
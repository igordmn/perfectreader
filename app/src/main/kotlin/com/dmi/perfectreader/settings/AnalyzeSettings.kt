package com.dmi.perfectreader.settings

import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class AnalyzeSettings(store: ValueStore) {
    var defaultCharsetIsAuto by store.value(true)
    var defaultCharset by store.value("")
    var ignoreDeclaredCharset by store.value(false)
    var defaultLanguageIsSystem by store.value(true)
    var defaultLanguage by store.value("")
    var ignoreDeclaredLanguage by store.value(false)
}
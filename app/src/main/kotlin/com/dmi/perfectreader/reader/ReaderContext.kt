package com.dmi.perfectreader.reader

import com.dmi.perfectreader.MainContext
import com.dmi.perfectreader.selection.TextActions

class ReaderContext(val main: MainContext, activity: ReaderActivity) {
    val textActions = TextActions(activity)
}
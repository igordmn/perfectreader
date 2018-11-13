package com.dmi.perfectreader.ui.reader

import com.dmi.perfectreader.MainContext
import com.dmi.perfectreader.ui.selection.TextActions

class ReaderContext(val main: MainContext, activity: ReaderActivity) {
    val textActions = TextActions(activity)
}
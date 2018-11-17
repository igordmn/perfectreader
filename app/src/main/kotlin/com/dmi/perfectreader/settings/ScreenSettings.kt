package com.dmi.perfectreader.settings

import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class ScreenSettings(store: ValueStore) {
    var animationPath by store.value("assets:///resources/animations/0001.xml")
    var timeout by store.value(-1L)
    var brightnessIsSystem by store.value(true)
    var brightnessValue by store.value(1.0F)
    var orientation: ScreenOrientation by store.value(ScreenOrientation.SYSTEM)
    
    var footerEnabled by store.value(true)
    var footerTextSizePercent by store.value(0.8F)
    var footerHeightEm by store.value(1.4F)
    var footerPaddingBottomPercent by store.value(-0.2F)
    var footerPageNumber by store.value(true)
    var footerNumberOfPages by store.value(true)
    var footerChapter by store.value(true)
}

val ScreenSettings.brightness get() = if (brightnessIsSystem) ScreenBrightness.System else ScreenBrightness.Manual(brightnessValue)

var ScreenSettings.brightnessValueAndEnable: Float
    get() = brightnessValue
    set(value) {
        brightnessValue = value
        brightnessIsSystem = false
    }

sealed class ScreenBrightness {
    object System : ScreenBrightness()
    class Manual(val value: Float): ScreenBrightness()
}

enum class ScreenOrientation {
    SYSTEM,
    PORTRAIT,
    LANDSCAPE
}

var ScreenSettings.footerElements: ScreenFooterElements
    get() = ScreenFooterElements(footerPageNumber, footerNumberOfPages, footerChapter)
    set(value) {
        footerPageNumber = value.pageNumber
        footerNumberOfPages = value.numberOfPages
        footerChapter = value.chapter
        footerEnabled = value.pageNumber || value.numberOfPages || value.chapter
    }

var ScreenSettings.footerElementsArray: BooleanArray
    get() = footerElements.toArray()
    set(value) {
        footerElements = value.toElements()
    }

class ScreenFooterElements(val pageNumber: Boolean, val numberOfPages: Boolean, val chapter: Boolean)
fun BooleanArray.toElements() = ScreenFooterElements(this[0], this[1], this[2])
fun ScreenFooterElements.toArray() = booleanArrayOf(pageNumber, numberOfPages, chapter)
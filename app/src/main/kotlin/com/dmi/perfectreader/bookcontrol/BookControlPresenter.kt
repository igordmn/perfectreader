package com.dmi.perfectreader.bookcontrol

import com.dmi.perfectreader.book.BookPresenter
import com.dmi.perfectreader.bookreader.BookReaderPresenter
import com.dmi.perfectreader.setting.AppSettings
import com.dmi.util.Units.dipToPx
import com.dmi.util.base.BasePresenter
import com.dmi.util.log.Log
import java.lang.Math.abs
import java.lang.Math.sqrt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookControlPresenter : BasePresenter() {

    @Inject
    protected lateinit var appSettings: AppSettings
    @Inject
    protected lateinit var bookReaderPresenter: BookReaderPresenter
    @Inject
    protected lateinit var bookPresenter: BookPresenter

    private var touchDownX = 0F
    private var touchDownY = 0F
    private var oldApplySlideActionTouchY = 0F
    private var nowIsSlideByLeftSide = false

    fun onTouchDown(touchInfo: TouchInfo) {
        touchDownX = touchInfo.x
        touchDownY = touchInfo.y
        oldApplySlideActionTouchY = touchDownY
        nowIsSlideByLeftSide = false
    }

    fun onTouchMove(touchInfo: TouchInfo) {
        val onLeftSide = touchDownX <= LEFT_SIDE_WIDTH_FOR_SLIDE
        val isTouchedFar = abs(touchInfo.y - touchDownY) >= TOUCH_SENSITIVITY
        if (onLeftSide && isTouchedFar) {
            nowIsSlideByLeftSide = true
        }

        if (nowIsSlideByLeftSide) {
            if (abs(oldApplySlideActionTouchY - touchInfo.y) >= SLIDE_SENSITIVITY) {
                val count = ((touchInfo.y - oldApplySlideActionTouchY) / SLIDE_SENSITIVITY).toInt()
                var fontSize = appSettings.format.fontSizePercents.get() as Int
                fontSize = Math.max(FONT_SIZE_MIN, Math.min(FONT_SIZE_MAX, fontSize + count * FONT_SIZE_DELTA))
                appSettings.format.fontSizePercents.set(fontSize)
                oldApplySlideActionTouchY = touchInfo.y
            }
        }
    }

    fun onTouchUp(touchInfo: TouchInfo) {
        if (!nowIsSlideByLeftSide) {
            val x = touchInfo.x
            val y = touchInfo.y
            val touchDiameter = touchInfo.touchDiameter
            val touchOffsetX = x - touchDownX
            val touchOffsetY = y - touchDownY
            val touchOffset = sqrt((touchOffsetX * touchOffsetX + touchOffsetY * touchOffsetY).toDouble()).toFloat()
            val isTap = touchOffset <= TOUCH_SENSITIVITY

            if (isTap) {
                bookPresenter.tap(x, y, touchDiameter, object: BookPresenter.TapHandler {
                    override fun handleTap() {
                        val xPart = x / touchInfo.width
                        val yPart = y / touchInfo.height
                        val configuration = appSettings.control.tapZones.shortTaps.configuration.get() as TapZoneConfiguration
                        val tapZone = configuration.getAt(xPart, yPart)
                        val action = appSettings.control.tapZones.shortTaps.action(tapZone).get() as Action
                        performAction(action)
                    }
                })
            } else {
                val swipeLeft = touchOffsetX <= -TOUCH_SENSITIVITY
                val swipeRight = touchOffsetX >= TOUCH_SENSITIVITY
                if (swipeLeft) {
                    bookPresenter.goNextPage()
                } else if (swipeRight) {
                    bookPresenter.goPreviewPage()
                }
            }
        }
    }

    fun onKeyDown(hardKey: HardKey) {
        if (hardKey != HardKey.UNKNOWN) {
            val action = appSettings.control.hardKeys.shortPress.action(hardKey).get() as Action
            performAction(action)
        }
    }

    private fun performAction(action: Action) {
        when (action) {
            Action.NONE -> {
            }
            Action.TOGGLE_MENU -> bookReaderPresenter.toggleMenu()
            Action.EXIT -> bookReaderPresenter.exit()
            Action.GO_NEXT_PAGE -> bookPresenter.goNextPage()
            Action.GO_PREVIEW_PAGE -> bookPresenter.goPreviewPage()
            Action.SELECT_TEXT -> Log.w("select text not implemented")
            else -> throw UnsupportedOperationException()
        }
    }

    companion object {
        private val TOUCH_SENSITIVITY = dipToPx(8f)
        private val LEFT_SIDE_WIDTH_FOR_SLIDE = dipToPx(40f)
        private val SLIDE_SENSITIVITY = dipToPx(20f)

        private val FONT_SIZE_MAX = 800
        private val FONT_SIZE_MIN = 20
        private val FONT_SIZE_DELTA = 10
    }
}
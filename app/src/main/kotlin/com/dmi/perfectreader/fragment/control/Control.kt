package com.dmi.perfectreader.fragment.control

import com.dmi.perfectreader.fragment.book.Book
import com.dmi.perfectreader.fragment.book.content.plainText
import com.dmi.perfectreader.fragment.book.selection.selectionInitialRange
import com.dmi.perfectreader.fragment.control.entity.Action
import com.dmi.perfectreader.fragment.control.entity.ControlMode
import com.dmi.perfectreader.fragment.control.entity.HardKey
import com.dmi.perfectreader.fragment.control.entity.TouchInfo
import com.dmi.perfectreader.fragment.reader.Reader
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.graphic.SizeF
import com.dmi.util.setting.Settings
import rx.lang.kotlin.PublishSubject
import com.dmi.perfectreader.data.UserSettingKeys.Control as ControlKeys
import com.dmi.perfectreader.data.UserSettingKeys.Format as FormatKeys

class Control(
        private val userSettings: Settings,
        private val book: Book,
        private val reader: Reader,
        private val closeApp: () -> Unit,
        private val copyPlainText: (String) -> Unit,
        dip2px: (Float) -> Float
) : BaseViewModel() {
    private val TOUCH_SENSITIVITY = dip2px(8F)
    private val LEFT_SIDE_WIDTH_FOR_SLIDE = dip2px(40F)
    private val SLIDE_SENSITIVITY = dip2px(20F)

    private val FONT_SIZE_MAX = 4F
    private val FONT_SIZE_MIN = 0.2F
    private val FONT_SIZE_DELTA = 0.05F

    private lateinit var size: SizeF

    private var mode: ControlMode by saveState(ControlMode.NORMAL)
    private var touchDownX = 0F
    private var touchDownY = 0F
    private var oldApplySlideActionTouchY = 0F
    private var nowIsSlideByLeftSide = false

    val onSelectionCopiedToClipboard = PublishSubject<Unit>()

    fun resize(size: SizeF) = run { this.size = size }

    fun onTouchDown(touchInfo: TouchInfo) {
        touchDownX = touchInfo.x
        touchDownY = touchInfo.y
        if (mode == ControlMode.NORMAL) {
            oldApplySlideActionTouchY = touchDownY
            nowIsSlideByLeftSide = false
        }
    }

    fun onTouchMove(touchInfo: TouchInfo) {
        if (mode == ControlMode.NORMAL) {
            val y = touchInfo.y
            val onLeftSide = touchDownX <= LEFT_SIDE_WIDTH_FOR_SLIDE
            val isTouchedFar = Math.abs(y - touchDownY) >= TOUCH_SENSITIVITY
            if (onLeftSide && isTouchedFar) {
                nowIsSlideByLeftSide = true
            }

            if (nowIsSlideByLeftSide) {
                if (Math.abs(oldApplySlideActionTouchY - y) >= SLIDE_SENSITIVITY) {
                    val count = ((y - oldApplySlideActionTouchY) / SLIDE_SENSITIVITY).toInt()
                    changeFontSize(count)
                    oldApplySlideActionTouchY = y
                }
            }
        }
    }

    private fun changeFontSize(count: Int) {
        val fontSize = userSettings[FormatKeys.fontSizeMultiplier]
        val newFontSize = Math.max(FONT_SIZE_MIN, Math.min(FONT_SIZE_MAX, fontSize + count * FONT_SIZE_DELTA))
        if (newFontSize != fontSize) {
            userSettings[FormatKeys.fontSizeMultiplier] = newFontSize
            book.reformat()
        }
    }

    fun onTouchUp(touchInfo: TouchInfo) {
        val x = touchInfo.x
        val y = touchInfo.y
        if (mode == ControlMode.NORMAL) {
            val radius = touchInfo.radius
            if (!nowIsSlideByLeftSide) {
                val touchOffsetX = x - touchDownX
                val touchOffsetY = y - touchDownY
                val touchOffset = Math.sqrt((touchOffsetX * touchOffsetX + touchOffsetY * touchOffsetY).toDouble()).toFloat()
                val isTap = touchOffset <= TOUCH_SENSITIVITY

                if (isTap) {
//                    val xPart = x / size.width
//                    val yPart = y / size.height
//                    val configuration = userSettings[ControlKeys.TapZones.ShortTaps.configuration]
//                    val tapZone = configuration.getAt(xPart, yPart)
//                    val action = userSettings[ControlKeys.TapZones.ShortTaps.Actions[tapZone]]
//                    performCoordinatedAction(action, x, y, radius)
                    startSelection(x, y)
                } else {
                    val swipeLeft = touchOffsetX <= -TOUCH_SENSITIVITY
                    val swipeRight = touchOffsetX >= TOUCH_SENSITIVITY
                    if (swipeLeft) {
                        book.goNextPage()
                    } else if (swipeRight) {
                        book.goPreviousPage()
                    }
                }
            }
        } else {
            val touchOffsetX = x - touchDownX
            val touchOffsetY = y - touchDownY
            val touchOffset = Math.sqrt((touchOffsetX * touchOffsetX + touchOffsetY * touchOffsetY).toDouble()).toFloat()
            val isTap = touchOffset <= TOUCH_SENSITIVITY
            if (isTap) {
                cancelSelection()
            }else {
                val swipeLeft = touchOffsetX <= -TOUCH_SENSITIVITY
                val swipeRight = touchOffsetX >= TOUCH_SENSITIVITY
                if (swipeLeft) {
                    book.goNextPage()
                } else if (swipeRight) {
                    book.goPreviousPage()
                }
            }
        }
    }

    fun onKeyDown(hardKey: HardKey) {
        val action = userSettings[ControlKeys.HardKeys.ShortPress.Actions[hardKey]]
        performAction(action)
    }

    private fun performCoordinatedAction(action: Action, x: Float, y: Float, radius: Float) = when (action) {
        Action.SELECT_TEXT -> startSelection(x, y)
        else -> performAction(action)
    }

    private fun performAction(action: Action) = when (action) {
        Action.NONE -> Unit
        Action.TOGGLE_MENU -> reader.toggleMenu()
        Action.EXIT_APP -> closeApp()
        Action.GO_NEXT_PAGE -> book.goNextPage()
        Action.GO_PREVIOUS_PAGE -> book.goPreviousPage()
        else -> throw IllegalStateException("Action didn't handle")
    }

    private fun startSelection(x: Float, y: Float) {
        val currentPage = book.pageAt(0)
        if (currentPage != null) {
            val selectionRange = selectionInitialRange(book.content, currentPage, x, y)
            if (selectionRange != null) {
                book.selectionRange =  selectionRange
                reader.toggleSelection()
                mode = ControlMode.SELECTION
            }
        }
    }

    private fun cancelSelection() {
        copySelectionText()
        reader.toggleSelection()
        book.selectionRange = null
        mode = ControlMode.NORMAL

    }

    private fun copySelectionText() {
        val selectionRange = book.selectionRange
        if (selectionRange != null) {
            val plainText = book.content.plainText(selectionRange)
            copyPlainText(plainText)
            onSelectionCopiedToClipboard.onNext(Unit)
        }
    }
}
package com.dmi.perfectreader.bookcontrol;

import com.dmi.perfectreader.book.BookPresenter;
import com.dmi.perfectreader.bookreader.BookReaderPresenter;
import com.dmi.perfectreader.setting.AppSettings;
import com.dmi.util.base.BasePresenter;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

import static com.dmi.util.Units.dipToPx;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

@Singleton
public class BookControlPresenter extends BasePresenter {
    private static final float TOUCH_SENSITIVITY = dipToPx(8);
    private static final float LEFT_SIDE_WIDTH_FOR_SLIDE = dipToPx(40);
    private static final float SLIDE_SENSITIVITY = dipToPx(20);

    private static final int FONT_SIZE_MAX = 800;
    private static final int FONT_SIZE_MIN = 20;
    private static final int FONT_SIZE_DELTA = 10;

    @Inject
    protected AppSettings appSettings;
    @Inject
    protected BookReaderPresenter bookReaderPresenter;
    @Inject
    protected BookPresenter bookPresenter;

    private float touchDownX;
    private float touchDownY;
    private float oldApplySlideActionTouchY;
    private boolean nowIsSlideByLeftSide = false;

    public void onTouchDown(TouchInfo touchInfo) {
        touchDownX = touchInfo.x;
        touchDownY = touchInfo.y;
        oldApplySlideActionTouchY = touchDownY;
        nowIsSlideByLeftSide = false;
    }

    public void onTouchMove(TouchInfo touchInfo) {
        boolean onLeftSide = touchDownX <= LEFT_SIDE_WIDTH_FOR_SLIDE;
        boolean isTouchedFar = abs(touchInfo.y - touchDownY) >= TOUCH_SENSITIVITY;
        if (onLeftSide && isTouchedFar) {
            nowIsSlideByLeftSide = true;
        }

        if (nowIsSlideByLeftSide) {
            if (abs(oldApplySlideActionTouchY - touchInfo.y) >= SLIDE_SENSITIVITY) {
                int count = (int) ((touchInfo.y - oldApplySlideActionTouchY) / SLIDE_SENSITIVITY);
                int fontSize = appSettings.format.fontSizePercents.get();
                fontSize = max(FONT_SIZE_MIN, min(FONT_SIZE_MAX, fontSize + count * FONT_SIZE_DELTA));
                appSettings.format.fontSizePercents.set(fontSize);
                oldApplySlideActionTouchY = touchInfo.y;
            }
        }
    }

    public void onTouchUp(TouchInfo touchInfo) {
        if (!nowIsSlideByLeftSide) {
            float x = touchInfo.x;
            float y = touchInfo.y;
            float touchDiameter = touchInfo.touchDiameter;
            float touchOffsetX = x - touchDownX;
            float touchOffsetY = y - touchDownY;
            float touchOffset = (float) sqrt(touchOffsetX * touchOffsetX + touchOffsetY * touchOffsetY);
            boolean isTap = touchOffset <= TOUCH_SENSITIVITY;

            if (isTap) {
                bookPresenter.tap(x, y, touchDiameter, () -> {
                    float xPart = x / touchInfo.width;
                    float yPart = y / touchInfo.height;
                    TapZoneConfiguration configuration = appSettings.control.tapZones.shortTaps.configuration.get();
                    TapZone tapZone = configuration.getAt(xPart, yPart);
                    Action action = appSettings.control.tapZones.shortTaps.action(tapZone).get();
                    performAction(action);
                });
            } else {
                boolean swipeLeft = touchOffsetX <= -TOUCH_SENSITIVITY;
                boolean swipeRight = touchOffsetX >= TOUCH_SENSITIVITY;
                if (swipeLeft) {
                    bookPresenter.goNextPage();
                } else if (swipeRight) {
                    bookPresenter.goPreviewPage();
                }
            }
        }
    }

    public void onKeyDown(HardKey hardKey) {
        if (hardKey != HardKey.UNKNOWN) {
            Action action = appSettings.control.hardKeys.shortPress.action(hardKey).get();
            performAction(action);
        }
    }

    private void performAction(Action action) {
        switch (action) {
            case NONE:
                break;
            case TOGGLE_MENU:
                bookReaderPresenter.toggleMenu();
                break;
            case EXIT:
                bookReaderPresenter.exit();
                break;
            case GO_NEXT_PAGE:
                bookPresenter.goNextPage();
                break;
            case GO_PREVIEW_PAGE:
                bookPresenter.goPreviewPage();
                break;
            case SELECT_TEXT:
                Timber.w("select text not implemented");
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}

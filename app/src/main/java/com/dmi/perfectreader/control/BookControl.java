package com.dmi.perfectreader.control;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.dmi.perfectreader.book.Book;
import com.dmi.perfectreader.main.GoBackIntent;
import com.dmi.perfectreader.main.ToggleMenuIntent;
import com.dmi.perfectreader.setting.Settings;
import com.dmi.perfectreader.util.android.EventBus;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import static com.dmi.perfectreader.util.android.Units.dipToPx;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

@EBean(scope = EBean.Scope.Singleton)
public class BookControl implements KeyEvent.Callback, View.OnTouchListener {
    private static final float TOUCH_SENSITIVITY = dipToPx(8);
    private static final float LEFT_SIDE_WIDTH_FOR_SLIDE = dipToPx(40);
    private static final float SLIDE_SENSITIVITY = dipToPx(20);

    private static final int FONT_SIZE_MAX = 800;
    private static final int FONT_SIZE_MIN = 20;
    private static final int FONT_SIZE_DELTA = 10;

    @Bean
    protected EventBus eventBus;
    @Bean
    protected Settings settings;
    private Book book;

    private int width;
    private int height;
    private float touchDownX;
    private float touchDownY;
    private float oldApplySlideActionTouchY;
    private boolean nowIsSlideByLeftSide = false;

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        width = view.getWidth();
        height = view.getHeight();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                break;
        }
        return true;
    }

    private void onTouchDown(MotionEvent motionEvent) {
        touchDownX = motionEvent.getX();
        touchDownY = motionEvent.getY();
        oldApplySlideActionTouchY = touchDownY;
        nowIsSlideByLeftSide = false;
    }

    private void onTouchMove(MotionEvent motionEvent) {
        boolean onLeftSide = touchDownX <= LEFT_SIDE_WIDTH_FOR_SLIDE;
        boolean isTouchedFar = abs(motionEvent.getY() - touchDownY) >= TOUCH_SENSITIVITY;
        if (onLeftSide && isTouchedFar) {
            nowIsSlideByLeftSide = true;
        }

        if (nowIsSlideByLeftSide) {
            if (abs(oldApplySlideActionTouchY - motionEvent.getY()) >= SLIDE_SENSITIVITY) {
                int count = (int) ((motionEvent.getY() - oldApplySlideActionTouchY) / SLIDE_SENSITIVITY);
                int fontSize = settings.format.fontSize.get();
                fontSize = max(FONT_SIZE_MIN, min(FONT_SIZE_MAX, fontSize + count * FONT_SIZE_DELTA));
                settings.format.fontSize.set(fontSize);
                book.configure().setFontSize(fontSize).commit();
                oldApplySlideActionTouchY = motionEvent.getY();
            }
        }
    }

    private void onTouchUp(MotionEvent motionEvent) {
        if (!nowIsSlideByLeftSide) {
            float touchOffsetX = motionEvent.getX() - touchDownX;
            float touchOffsetY = motionEvent.getY() - touchDownY;
            float touchOffset = (float) sqrt(touchOffsetX * touchOffsetX + touchOffsetY * touchOffsetY);
            boolean isTap = touchOffset <= TOUCH_SENSITIVITY;

            if (isTap) {
                float xPart = motionEvent.getX() / width;
                float yPart = motionEvent.getY() / height;
                TapZoneConfiguration configuration = settings.control.tapZones.shortTaps.configuration.get();
                TapZone tapZone = configuration.getAt(xPart, yPart);
                Action action = settings.control.tapZones.shortTaps.action(tapZone).get();
                performAction(action);
            } else {
                boolean swipeLeft = touchOffsetX <= -TOUCH_SENSITIVITY;
                boolean swipeRight = touchOffsetX >= TOUCH_SENSITIVITY;
                if (swipeLeft) {
                    book.goNextPage();
                } else if (swipeRight) {
                    book.goPreviewPage();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        HardKey hardKey = HardKey.fromKeyCode(keyCode);
        if (hardKey != HardKey.UNKNOWN) {
            Action action = settings.control.hardKeys.shortPress.action(hardKey).get();
            performAction(action);
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }

    private void performAction(Action action) {
        switch (action) {
            case NONE:
                break;
            case TOGGLE_MENU:
                eventBus.post(new ToggleMenuIntent());
                break;
            case GO_BACK:
                eventBus.post(new GoBackIntent());
                break;
            case GO_NEXT_PAGE:
                book.goNextPage();
                break;
            case GO_PREVIEW_PAGE:
                book.goPreviewPage();
                break;
            case SELECT_TEXT:
                book.selectText();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}

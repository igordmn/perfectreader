package com.dmi.perfectreader.control;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.facade.BookReaderFacade;
import com.dmi.perfectreader.setting.AppSettings;
import com.dmi.util.FragmentExt;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import static com.dmi.util.Units.dipToPx;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

@EFragment(R.layout.fragment_bookcontrol)
public class BookControlFragment extends FragmentExt implements View.OnTouchListener {
    private static String LOG_TAG = BookControlFragment.class.getName();

    private static final float TOUCH_SENSITIVITY = dipToPx(8);
    private static final float LEFT_SIDE_WIDTH_FOR_SLIDE = dipToPx(40);
    private static final float SLIDE_SENSITIVITY = dipToPx(20);

    private static final int FONT_SIZE_MAX = 800;
    private static final int FONT_SIZE_MIN = 20;
    private static final int FONT_SIZE_DELTA = 10;

    @ViewById
    protected FrameLayout spaceView;
    
    @Bean
    protected AppSettings appSettings;
    private BookReaderFacade bookReader;

    private int width;
    private int height;
    private float touchDownX;
    private float touchDownY;
    private float oldApplySlideActionTouchY;
    private boolean nowIsSlideByLeftSide = false;

    @AfterInject
    protected void init() {
        setRetainInstance(true);
    }

    @AfterViews
    protected void initViews() {
        spaceView.setOnTouchListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        bookReader = getClient(BookReaderFacade.class);
    }

    @Override
    public void onDetach() {
        bookReader = null;
        super.onDetach();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        width = spaceView.getWidth();
        height = spaceView.getHeight();
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
                int fontSize = appSettings.format.fontSizePercents.get();
                fontSize = max(FONT_SIZE_MIN, min(FONT_SIZE_MAX, fontSize + count * FONT_SIZE_DELTA));
                appSettings.format.fontSizePercents.set(fontSize);
                oldApplySlideActionTouchY = motionEvent.getY();
            }
        }
    }

    private void onTouchUp(MotionEvent motionEvent) {
        if (!nowIsSlideByLeftSide) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            float touchDiameter = motionEvent.getTouchMajor();
            float touchOffsetX = x - touchDownX;
            float touchOffsetY = y - touchDownY;
            float touchOffset = (float) sqrt(touchOffsetX * touchOffsetX + touchOffsetY * touchOffsetY);
            boolean isTap = touchOffset <= TOUCH_SENSITIVITY;

            if (isTap) {
                bookReader.book().tap(x, y, touchDiameter, () -> {
                    float xPart = x / width;
                    float yPart = y / height;
                    TapZoneConfiguration configuration = appSettings.control.tapZones.shortTaps.configuration.get();
                    TapZone tapZone = configuration.getAt(xPart, yPart);
                    Action action = appSettings.control.tapZones.shortTaps.action(tapZone).get();
                    performAction(action);
                });
            } else {
                boolean swipeLeft = touchOffsetX <= -TOUCH_SENSITIVITY;
                boolean swipeRight = touchOffsetX >= TOUCH_SENSITIVITY;
                if (swipeLeft) {
                    bookReader.book().goNextPage();
                } else if (swipeRight) {
                    bookReader.book().goPreviewPage();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        HardKey hardKey = HardKey.fromKeyCode(keyCode);
        if (hardKey != HardKey.UNKNOWN) {
            Action action = appSettings.control.hardKeys.shortPress.action(hardKey).get();
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
                bookReader.toggleMenu();
                break;
            case EXIT:
                bookReader.exit();
                break;
            case GO_NEXT_PAGE:
                bookReader.book().goNextPage();
                break;
            case GO_PREVIEW_PAGE:
                bookReader.book().goPreviewPage();
                break;
            case SELECT_TEXT:
                Log.w(LOG_TAG, "select text not implemented");
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}

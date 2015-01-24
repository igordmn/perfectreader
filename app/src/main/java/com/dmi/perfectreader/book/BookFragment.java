package com.dmi.perfectreader.book;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.asset.AssetPaths;
import com.dmi.perfectreader.book.animation.SlidePageAnimation;
import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.bookview.PageBookBox;
import com.dmi.perfectreader.command.Commands;
import com.dmi.perfectreader.error.ErrorEvent;
import com.dmi.perfectreader.setting.Settings;
import com.dmi.perfectreader.userdata.UserData;
import com.dmi.perfectreader.util.android.EventBus;
import com.dmi.perfectreader.util.android.FragmentExt;
import com.dmi.perfectreader.util.lang.IntegerPercent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;

import static com.dmi.perfectreader.util.android.Units.dipToPx;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

@EFragment(R.layout.fragment_book)
public class BookFragment extends FragmentExt {
    private final static float TIME_FOR_ONE_SLIDE_IN_SECONDS = 0.4F;

    @FragmentArg
    protected File bookFile;
    @ViewById
    protected PageBookBox bookBox;
    @Bean
    protected AssetPaths assetPaths;
    @Bean
    protected EventBus eventBus;
    @Bean
    protected Commands commands;
    @Bean
    protected UserData userData;
    @Bean
    protected Settings settings;

    private static final int FONT_SIZE_MAX = 800;
    private static final int FONT_SIZE_MIN = 20;
    private static final int FONT_SIZE_DELTA = 10;

    @AfterViews
    protected void initViews() {
        bookBox.setPageAnimation(new SlidePageAnimation(TIME_FOR_ONE_SLIDE_IN_SECONDS));
        bookBox.setOnTouchListener(new OnTouchListenerImpl());
        bookBox.setOnLocationChangeListener(new OnLocationChangeListenerImpl());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadBook();
    }

    @Background
    protected void loadBook() {
        try {
            bookBox.load(bookFile);
        } catch (IOException e) {
            eventBus.postOnMainThread(new ErrorEvent(e));
        }

        bookBox.configure()
                .setTextAlign(settings.format.TEXT_ALIGN.get())
                .setFontSize(settings.format.FONT_SIZE.get())
                .setLineHeight(settings.format.LINE_HEIGHT.get())
                .commit();

        BookLocation loadedLocation = userData.loadBookLocation(bookFile);
        goLoadedLocation(loadedLocation);
    }

    @UiThread
    protected void goLoadedLocation(BookLocation loadedLocation) {
        if (loadedLocation != null) {
            bookBox.goLocation(loadedLocation);
        } else {
            bookBox.goLocation(new BookLocation(0, IntegerPercent.ZERO));
        }
    }

    public BookLocation currentLocation() {
        return bookBox.currentLocation();
    }

    public void goLocation(BookLocation location) {
        bookBox.goLocation(location);
    }

    public BookLocation percentToLocation(double percent) {
        return bookBox.percentToLocation(percent);
    }

    public double locationToPercent(BookLocation location) {
        return bookBox.locationToPercent(location);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (bookBox.canGoNextPage()) {
                bookBox.goNextPage();
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (bookBox.canGoPreviewPage()) {
                bookBox.goPreviewPage();
            }
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        return true;
    }

    private class OnTouchListenerImpl implements View.OnTouchListener {
        private final float TOUCH_SENSITIVITY = dipToPx(8);
        private final float LEFT_SIDE_WIDTH_FOR_SLIDE = dipToPx(40);
        private final float SLIDE_SENSITIVITY = dipToPx(20);
        private float touchDownX;
        private float touchDownY;
        private float oldApplySlideActionTouchY;
        private boolean nowIsSlideByLeftSide = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                onTouchDown(event);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                onTouchMove(event);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                onTouchUp(event);
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
                    int fontSize = settings.format.FONT_SIZE.get();
                    fontSize = max(FONT_SIZE_MIN, min(FONT_SIZE_MAX, fontSize + count * FONT_SIZE_DELTA));
                    settings.format.FONT_SIZE.set(fontSize);
                    bookBox.configure().setFontSize(fontSize).commit();
                    oldApplySlideActionTouchY = motionEvent.getY();
                }
            }
        }

        private void onTouchUp(MotionEvent motionEvent) {
            if (!nowIsSlideByLeftSide) {
                int thirdOfScreen = bookBox.getWidth() / 3;
                float touchOffsetX = motionEvent.getX() - touchDownX;
                float touchOffsetY = motionEvent.getY() - touchDownY;
                float touchOffset = (float) sqrt(touchOffsetX * touchOffsetX + touchOffsetY * touchOffsetY);
                boolean swipeLeft = touchOffsetX <= -TOUCH_SENSITIVITY;
                boolean swipeRight = touchOffsetX >= TOUCH_SENSITIVITY;
                boolean touchLeftZone = motionEvent.getX() < thirdOfScreen &&
                                        !swipeLeft && touchOffset <= TOUCH_SENSITIVITY;
                boolean touchRightZone = motionEvent.getX() > 2 * thirdOfScreen &&
                                         !swipeRight && touchOffset <= TOUCH_SENSITIVITY;
                boolean touchCenterZone =
                        motionEvent.getX() >= thirdOfScreen && motionEvent.getX() <= 2 * thirdOfScreen &&
                        !swipeRight && touchOffset <= TOUCH_SENSITIVITY;

                if (touchRightZone || swipeLeft) {
                    if (bookBox.canGoNextPage()) {
                        bookBox.goNextPage();
                    }
                } else if (touchLeftZone || swipeRight) {
                    if (bookBox.canGoPreviewPage()) {
                        bookBox.goPreviewPage();
                    }
                } else if (touchCenterZone) {
                    commands.toggleMenu();
                }
            }
        }
    }

    private class OnLocationChangeListenerImpl implements PageBookBox.OnLocationChangeListener {
        @Override
        public void onBookLocationChange() {
            userData.saveBookLocation(bookFile, bookBox.currentLocation());
        }
    }
}

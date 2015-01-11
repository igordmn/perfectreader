package com.dmi.perfectreader.book;

import android.app.Fragment;
import android.os.AsyncTask;
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
import com.dmi.perfectreader.main.EventBus;
import com.dmi.perfectreader.util.lang.IntegerPercent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;

import static com.dmi.perfectreader.util.android.Units.dipToPx;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

@EFragment(R.layout.fragment_book)
public class BookFragment extends Fragment {
    private final static float TIME_FOR_ONE_SLIDE_IN_SECONDS = 0.4F;

    @FragmentArg
    protected File bookFile;
    @ViewById
    protected PageBookBox pageBookBox;
    @Bean
    protected AssetPaths assetPaths;
    @Bean
    protected EventBus eventBus;
    @Bean
    protected Commands commands;

    private static final int FONT_SIZE_MAX = 800;
    private static final int FONT_SIZE_MIN = 20;
    private static final int FONT_SIZE_DELTA = 10;
    private int fontSize = 200;

    @AfterViews
    protected void initViews() {
        pageBookBox.setPageAnimation(new SlidePageAnimation(TIME_FOR_ONE_SLIDE_IN_SECONDS));
        pageBookBox.setOnTouchListener(new BookBoxOnTouchListener());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadBook();
    }

    private void loadBook() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    pageBookBox.load(bookFile);
                } catch (IOException e) {
                    eventBus.post(new ErrorEvent(e));
                }

                pageBookBox.configure().setFontSize(fontSize).commit();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pageBookBox.goLocation(new BookLocation(0, IntegerPercent.ZERO));
                    }
                });

                return null;
            }
        }.execute();
    }

    public BookLocation currentLocation() {
        return pageBookBox.currentLocation();
    }

    public void goLocation(BookLocation location) {
        pageBookBox.goLocation(location);
    }

    public BookLocation percentToLocation(double percent) {
        return pageBookBox.percentToLocation(percent);
    }

    public double locationToPercent(BookLocation location) {
        return pageBookBox.locationToPercent(location);
    }

    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (pageBookBox.canGoNextPage()) {
                pageBookBox.goNextPage();
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (pageBookBox.canGoPreviewPage()) {
                pageBookBox.goPreviewPage();
            }
        }
        return true;
    }

    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        return true;
    }

    private class BookBoxOnTouchListener implements View.OnTouchListener {
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
            boolean onLeftSide = motionEvent.getX() <= LEFT_SIDE_WIDTH_FOR_SLIDE;
            boolean isTouchedFar = abs(motionEvent.getY() - touchDownY) >= TOUCH_SENSITIVITY;
            if (onLeftSide && isTouchedFar) {
                nowIsSlideByLeftSide = true;
            }

            if (nowIsSlideByLeftSide) {
                if (abs(oldApplySlideActionTouchY - motionEvent.getY()) >= SLIDE_SENSITIVITY) {
                    int count = (int) ((motionEvent.getY() - oldApplySlideActionTouchY) / SLIDE_SENSITIVITY);
                    fontSize = max(FONT_SIZE_MIN, min(FONT_SIZE_MAX, fontSize + count * FONT_SIZE_DELTA));
                    pageBookBox.configure().setFontSize(fontSize).commit();
                    oldApplySlideActionTouchY = motionEvent.getY();
                }
            }
        }

        private void onTouchUp(MotionEvent motionEvent) {
            if (!nowIsSlideByLeftSide) {
                int thirdOfScreen = pageBookBox.getWidth() / 3;
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
                    if (pageBookBox.canGoNextPage()) {
                        pageBookBox.goNextPage();
                    }
                } else if (touchLeftZone || swipeRight) {
                    if (pageBookBox.canGoPreviewPage()) {
                        pageBookBox.goPreviewPage();
                    }
                } else if (touchCenterZone) {
                    commands.toggleMenu();
                }
            }
        }
    }
}

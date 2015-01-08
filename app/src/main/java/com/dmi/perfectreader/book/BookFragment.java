package com.dmi.perfectreader.book;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.asset.AssetPaths;
import com.dmi.perfectreader.book.animation.SlidePageAnimation;
import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.bookview.PageBookBox;
import com.dmi.perfectreader.error.ErrorEvent;
import com.dmi.perfectreader.main.EventBus;
import com.dmi.perfectreader.util.android.Units;
import com.dmi.perfectreader.util.cache.BookResourceCache;
import com.dmi.perfectreader.util.lang.IntegerPercent;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;

@EFragment(R.layout.fragment_book)
public class BookFragment extends Fragment {
    private final static float TIME_FOR_ONE_SLID_IN_SECONDS = 0.4F;
    private final static float TOUCH_SENSITIVITY = 8;

    @FragmentArg
    protected File bookFile;
    @ViewById
    protected PageBookBox pageBookBox;
    @Bean
    protected AssetPaths assetPaths;
    @Bean
    protected BookResourceCache bookResourceCache;
    @Bean
    protected EventBus eventBus;
    private BookStorage bookStorage;

    private View.OnClickListener onClickListener;

    private float touchSensitivityInPixels;

    private float touchDownX;
    private float touchDownY;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @AfterInject
    protected void init() {
        bookStorage = new BookStorage(bookResourceCache);
    }

    @AfterViews
    protected void initViews() {
        pageBookBox.setPageAnimation(new SlidePageAnimation(TIME_FOR_ONE_SLID_IN_SECONDS));
        pageBookBox.setBookStorage(bookStorage);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        touchSensitivityInPixels = new Units(getActivity()).dipToPx(TOUCH_SENSITIVITY);

        loadBook();
    }

    private void loadBook() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    bookStorage.load(bookFile);
                    pageBookBox.init();
                    pageBookBox.configure().setFontSize(200).commit();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pageBookBox.goLocation(new BookLocation(0, IntegerPercent.ZERO));
                        }
                    });
                } catch (IOException e) {
                    eventBus.post(new ErrorEvent(e));
                }

                return null;
            }
        }.execute();
    }

    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (pageBookBox.canGoNextPage()) {
                pageBookBox.goNextPage();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (pageBookBox.canGoPreviewPage()) {
                pageBookBox.goPreviewPage();
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP;
    }
/*
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onTouchDown(event);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            onTouchUp(event);
            v.performClick();
            return true;
        }
        return false;
    }

    private void onTouchDown(MotionEvent motionEvent) {
        touchDownX = motionEvent.getX();
        touchDownY = motionEvent.getY();
    }

    private void onTouchUp(MotionEvent motionEvent) {
        int thirdOfScreen = pageBookBox.getWidth() / 3;
        float touchOffsetX = motionEvent.getX() - touchDownX;
        float touchOffsetY = motionEvent.getY() - touchDownY;
        float touchOffset = (float) sqrt(touchOffsetX * touchOffsetX + touchOffsetY * touchOffsetY);
        boolean swipeLeft = touchOffsetX <= -touchSensitivityInPixels;
        boolean swipeRight = touchOffsetX >= touchSensitivityInPixels;
        boolean touchLeftZone = motionEvent.getX() < thirdOfScreen &&
                                !swipeLeft && touchOffset <= touchSensitivityInPixels;
        boolean touchRightZone = motionEvent.getX() > 2 * thirdOfScreen &&
                                 !swipeRight && touchOffset <= touchSensitivityInPixels;
        boolean touchCenterZone = motionEvent.getX() >= thirdOfScreen && motionEvent.getX() <= 2 * thirdOfScreen &&
                                  !swipeRight && touchOffset <= touchSensitivityInPixels;

        if (touchRightZone || swipeLeft) {
            if (pageBookBox.canGoNextPage()) {
                pageBookBox.goNextPage();
            }
        } else if (touchLeftZone || swipeRight) {
            if (pageBookBox.canGoPreviewPage()) {
                pageBookBox.goPreviewPage();
            }
        } else if (touchCenterZone) {
            onClickListener.onClick(pageBookBox);
        }
    }*/
}

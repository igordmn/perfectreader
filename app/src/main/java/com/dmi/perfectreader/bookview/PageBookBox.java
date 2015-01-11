package com.dmi.perfectreader.bookview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.dmi.perfectreader.book.animation.SlidePageAnimation;
import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.util.android.MainThreads;

import java.io.File;
import java.io.IOException;

import static com.dmi.perfectreader.util.android.Units.dipToPx;

public class PageBookBox extends FrameLayout {
    public static final float TOUCH_TO_START_MOVE_SENSITIVITY = dipToPx(8);

    private PageAnimationView pageAnimationView;
    private PageBookView pageBookView;
    private OnLocationChangeListener onLocationChangeListener;
    private OnTouchListener onTouchListener;

    private MotionEvent touchDownEvent;
    private boolean nowSelect = false;
    private boolean touchOnAllowedElement = false;
    private boolean customTouchStarted = false;

    public PageBookBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        ShowAnimationListener showAnimationListener = new ShowAnimationListener();
        pageAnimationView = new PageAnimationView(context);
        pageAnimationView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        pageAnimationView.setListener(showAnimationListener);
        pageBookView = PageBookView_.build(context);
        pageBookView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        pageBookView.setPageAnimationView(pageAnimationView);
        pageBookView.setListener(showAnimationListener);
        addView(pageAnimationView);
        addView(pageBookView);
    }

    public void setPageAnimation(SlidePageAnimation pageAnimation) {
        pageAnimationView.setPageAnimation(pageAnimation);
    }

    public void load(File bookFile) throws IOException {
        pageBookView.load(bookFile);
    }

    public PageBookView.BookConfigurator configure() {
        return pageBookView.configure();
    }

    public BookLocation percentToLocation(double percent) {
        return pageBookView.percentToLocation(percent);
    }

    public double locationToPercent(BookLocation location) {
        return pageBookView.locationToPercent(location);
    }

    public BookLocation currentLocation() {
        return pageBookView.currentLocation();
    }

    public boolean canGoNextPage() {
        return pageBookView.canGoNextPage();
    }

    public boolean canGoPreviewPage() {
        return pageBookView.canGoPreviewPage();
    }

    public void goLocation(BookLocation location) {
        pageBookView.goLocation(location);
    }

    public void goNextPage() {
        pageBookView.goNextPage();
    }

    public void goPreviewPage() {
        pageBookView.goPreviewPage();
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setOnLocationChangeListener(OnLocationChangeListener onLocationChangeListener) {
        this.onLocationChangeListener = onLocationChangeListener;
    }

    // todo onTouchStartAllowedElement может вызваться уже после ACTION_UP на тормозящих девайсах
    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            touchDownEvent = MotionEvent.obtain(event);
            touchOnAllowedElement = false;
            customTouchStarted = false;

            boolean animationVisible = pageBookView.getVisibility() == View.INVISIBLE;
            if (animationVisible) {
                customTouchStarted = true;
            }
        }

        if (action == MotionEvent.ACTION_MOVE &&
            touchDistance(touchDownEvent, event) >= TOUCH_TO_START_MOVE_SENSITIVITY ||
            action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_CANCEL)
        {
            boolean defaultTouchNotStarted = touchOnAllowedElement && !nowSelect;
            if (defaultTouchNotStarted && !customTouchStarted) {
                pageBookView.preventDefaultTouch();
                customTouchStarted = true;
                onTouchListener.onTouch(this, touchDownEvent);
            }
        }

        if (customTouchStarted) {
            onTouchListener.onTouch(this, event);
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            touchDownEvent.recycle();
        }

        super.dispatchTouchEvent(event);

        return true;
    }

    private static double touchDistance(MotionEvent event1, MotionEvent event2) {
        float dX = event1.getX() - event2.getX();
        float dY = event1.getY() - event2.getY();
        return Math.sqrt(dX * dX + dY * dY);
    }

    private class ShowAnimationListener implements PageAnimationView.Listener, PageBookView.Listener  {
        private int hideAnimationDelay = 100;

        private final Runnable hideAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                pageBookView.setVisibility(View.VISIBLE);
            }
        };
        private final Runnable showAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                pageBookView.setVisibility(View.INVISIBLE);
            }
        };

        private volatile boolean isAnimating = false;
        private volatile boolean isLoading = false;
        private volatile boolean isInvalidatedAfterLoad = false;

        @Override
        public void onStartAnimation() {
            MainThreads.removeCallbacks(hideAnimationRunnable);
            MainThreads.post(showAnimationRunnable);
            isAnimating = true;
        }

        @Override
        public void onEndAnimation() {
            isAnimating = false;
            hideAnimationIfPossible();
        }

        @Override
        public void beforeLoad() {
            isLoading = true;
            isInvalidatedAfterLoad = false;
        }

        @Override
        public void afterLoad() {
            isLoading = false;
            hideAnimationIfPossible();
        }

        @Override
        public void afterInvalidate() {
            if (!isLoading) {
                isInvalidatedAfterLoad = true;
            }
            hideAnimationIfPossible();
        }

        private void hideAnimationIfPossible() {
            if (!isAnimating && !isLoading && isInvalidatedAfterLoad) {
                MainThreads.postDelayed(hideAnimationRunnable, hideAnimationDelay);
            }
        }

        @Override
        public void onTouchStartAllowedElement() {
            touchOnAllowedElement = true;
        }

        @Override
        public void onSelectStart() {
            nowSelect = true;
        }

        @Override
        public void onSelectEnd() {
            nowSelect = false;
        }

        @Override
        public void onLocationChange() {
            onLocationChangeListener.onBookLocationChange();
        }
    }

    public static interface OnLocationChangeListener {
        void onBookLocationChange();
    }
}

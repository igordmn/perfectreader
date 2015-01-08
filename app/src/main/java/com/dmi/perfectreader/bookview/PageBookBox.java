package com.dmi.perfectreader.bookview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.dmi.perfectreader.book.BookStorage;
import com.dmi.perfectreader.book.animation.SlidePageAnimation;
import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.util.android.MainThreads;

public class PageBookBox extends FrameLayout {
    private PageAnimationView pageAnimationView;
    private PageBookView pageBookView;

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

    public void setBookStorage(BookStorage bookStorage) {
        pageBookView.setBookStorage(bookStorage);
    }

    public void init() {
        pageBookView.init();
    }

    public PageBookView.BookConfigurator configure() {
        return pageBookView.configure();
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
    }
}

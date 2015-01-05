package com.dmi.perfectreader.bookview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.dmi.perfectreader.book.BookStorage;
import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.util.lang.IntegerPercent;

import org.androidannotations.annotations.EViewGroup;

import static com.dmi.perfectreader.util.lang.IntegerPercent.toDouble;
import static com.dmi.perfectreader.util.lang.IntegerPercent.valuePercent;
import static com.google.common.base.Preconditions.checkState;

@EViewGroup
public class PageBookView extends FrameLayout implements PagesDrawer {
    private PageAnimationView pageAnimationView;
    private BookStorage bookStorage;

    private final PageBookWebView webView;
    private final Object loadMutex = new Object();

    private BookLocation currentLocation = null;
    private int segmentCount = 0;
    private int currentPage = 0;
    private int pageCount = 0;

    public PageBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        webView = new PageBookWebView_(context);
        webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        webView.setListener(new WebViewListener());
        addView(webView);
    }

    public void setPageAnimationView(PageAnimationView pageAnimationView) {
        checkState(this.pageAnimationView == null || pageAnimationView == this.pageAnimationView);
        this.pageAnimationView = pageAnimationView;
        pageAnimationView.setPagesDrawer(this);
    }

    public void setBookStorage(BookStorage bookStorage) {
        this.bookStorage = bookStorage;
        webView.setBookStorage(bookStorage);
    }

    public void init() {
        webView.init();
        segmentCount = bookStorage.segmentUrls().size();
    }

    public void goLocation(BookLocation location) {
        currentLocation = location;
        webView.goLocation(location.segmentIndex(), location.percent());
       // pageAnimationView.moveLocation(currentLocation);
    }

    public boolean canGoNextPage() {
        return webView.isLoaded() && (
                currentLocation != null && currentLocation.segmentIndex() < segmentCount - 1 ||
                currentPage < pageCount - 1
        );
    }

    public boolean canGoPreviewPage() {
        return webView.isLoaded() && (
                currentLocation != null && currentLocation.segmentIndex() > 0 ||
                currentPage > 0
        );
    }

    public void goNextPage() {
        synchronized (loadMutex) {
            if (canGoNextPage()) {
                if (currentPage < pageCount - 1) {
                    currentPage++;
                    int percent = valuePercent(currentPage, pageCount);
                    currentLocation = new BookLocation(currentLocation.segmentIndex(), percent);
                    webView.goLocation(currentLocation.segmentIndex(), percent);
                } else {
                    int segmentIndex = currentLocation.segmentIndex() + 1;
                    currentLocation = new BookLocation(segmentIndex, IntegerPercent.ZERO);
                    webView.goLocation(segmentIndex, IntegerPercent.ZERO);
                }

                //pageAnimationView.moveNext(currentLocation);
            }
        }
    }

    public void goPreviewPage() {
        synchronized (loadMutex) {
            if (canGoPreviewPage()) {
                if (currentPage > 0) {
                    currentPage--;
                    int percent = valuePercent(currentPage, pageCount);
                    currentLocation = new BookLocation(currentLocation.segmentIndex(), percent);
                    webView.goLocation(currentLocation.segmentIndex(), percent);
                } else {
                    int segmentIndex = currentLocation.segmentIndex() - 1;
                    currentLocation = new BookLocation(segmentIndex, IntegerPercent.HUNDRED);
                    webView.goLocation(segmentIndex, IntegerPercent.HUNDRED);
                }

                //pageAnimationView.movePreview(currentLocation);
            }
        }
    }

    public BookLocation currentLocation() {
        return currentLocation;
    }

    @Override
    public void drawPage(BookLocation location, int relativeIndex, Canvas canvas) {
    }

    public PageBookWebView.BookConfigurator configure() {
        return webView.configure();
    }

    private static int percentToPage(int percent, int totalWidth, int screenWidth) {
        int pageCount = screenWidth > 0 ? Math.round(totalWidth / screenWidth) : 0;
        return screenWidth > 0 ?
                Math.min((int) Math.round(toDouble(percent) * totalWidth / screenWidth),
                        pageCount - 1) :
                0;
    }

    private class WebViewListener implements PageBookWebView.Listener {
        @Override
        public void onInvalidate() {
            //pageAnimationView.postRefresh();
        }

        @Override
        public void afterLoad(int totalWidth, int screenWidth) {
            synchronized (loadMutex) {
                pageCount = screenWidth > 0 ? Math.round(totalWidth / screenWidth) : 0;
                currentPage = percentToPage(currentLocation.percent(), totalWidth, screenWidth);
            }
        }
    }
}

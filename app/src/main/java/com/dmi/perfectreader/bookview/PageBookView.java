package com.dmi.perfectreader.bookview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.dmi.perfectreader.book.BookStorage;
import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.book.config.TextAlign;
import com.dmi.perfectreader.error.ErrorEvent;
import com.dmi.perfectreader.main.EventBus;
import com.dmi.perfectreader.util.concurrent.Waiter;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.dmi.perfectreader.util.android.MainThreads.runOnMainThread;
import static com.dmi.perfectreader.util.js.JavaScript.jsArray;
import static com.dmi.perfectreader.util.js.JavaScript.jsValue;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.net.URLConnection.guessContentTypeFromName;

@EViewGroup
public class PageBookView extends FrameLayout implements PagesDrawer {
    private static final String LOG_TAG = PageBookView.class.getSimpleName();

    private PageAnimationView pageAnimationView;
    private Listener listener;
    @Bean
    protected EventBus eventBus;
    @Bean
    protected BookStorage bookStorage;

    private final MyWebView webView;

    private Waiter htmlPageReady = new Waiter();
    private Waiter jsReady = new Waiter();

    private BookLocation currentLocation = null;
    private boolean canGoNextPage = false;
    private boolean canGoPreviewPage = false;
    private final BatchDrawImpl batchDraw = new BatchDrawImpl();

    public PageBookView(Context context) {
        super(context);
        webView = createWebView(context);
        webView.setLongClickable(false);
        webView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        addView(webView);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private MyWebView createWebView(Context context) {
        final MyWebView webView = new MyWebView(context);
        webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setInitialScale(100);
        webView.addJavascriptInterface(new JavaBridge(), "javaBridge");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.v(LOG_TAG, "onPageStarted. url: " + url);
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                Log.v(LOG_TAG, "onPageFinished. url: " + url);
                htmlPageReady.request();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(LOG_TAG,
                        format("onReceivedError. errorCode: {%s}, description: {%s}, failingUrl: {%s}",
                                errorCode, description, failingUrl));
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                Log.v(LOG_TAG, "loading " + url);
                if (bookStorage.isBookResource(url)) {
                    return bookStorageResponse(url);
                } else {
                    return super.shouldInterceptRequest(view, url);
                }
            }

            private WebResourceResponse bookStorageResponse(String url) {
                try {
                    return new WebResourceResponse(
                            guessContentTypeFromName(url),
                            null,
                            bookStorage.readResource(url));
                } catch (IOException e) {
                    eventBus.postOnMainThread(new ErrorEvent(e));
                    return null;
                }
            }
        });
        return webView;
    }

    public void setPageAnimationView(PageAnimationView pageAnimationView) {
        checkState(this.pageAnimationView == null || pageAnimationView == this.pageAnimationView);
        this.pageAnimationView = pageAnimationView;
        pageAnimationView.setPagesDrawer(this);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void load(File bookFile) throws IOException {
        bookStorage.load(bookFile);
        loadUrl("file:///android_asset/pageBook/pageBook.html");
        htmlPageReady.waitRequest();
        execJs(
                "reader.setCallback(javaBridge);\n" +
                format("reader.setSegmentUrls(%s);\n", jsArray(bookStorage.segmentUrls())) +
                format("javaBridge.jsReady()")
        );
        jsReady.waitRequest();
    }

    // todo вызывая постоянно эту команду, можно заDDOSить и все затормозит. нужно сделать проверку, выполнился ли прошлый вызов. если нет, то отложить.
    public BookConfigurator configure() {
        return new BookConfigurator();
    }

    public BookLocation percentToLocation(double percent) {
        return bookStorage.percentToLocation(percent);
    }

    public double locationToPercent(BookLocation location) {
        return bookStorage.locationToPercent(location);
    }

    public boolean canGoNextPage() {
        return canGoNextPage && pageAnimationView.canMoveNext();
    }

    public boolean canGoPreviewPage() {
        return canGoPreviewPage && pageAnimationView.canMovePreview();
    }

    public void goLocation(BookLocation location) {
        checkArgument(location.segmentIndex() >= 0 && location.segmentIndex() < bookStorage.segmentUrls().length);
        resetCanGoPages();
        execJs(format("reader.goLocation({segmentIndex: %s, percent: %s})", location.segmentIndex(), location.percent()));
    }

    public void goNextPage() {
        resetCanGoPages();
        execJs("reader.goNextPage()");
    }

    public void goPreviewPage() {
        resetCanGoPages();
        execJs("reader.goPreviewPage()");
    }

    public BookLocation currentLocation() {
        return currentLocation;
    }

    @Override
    public void drawPage(int relativeIndex, Canvas canvas) {
        checkArgument(abs(relativeIndex) <= 1);
        canvas.save();
        int width = getWidth();
        canvas.translate(-(width + relativeIndex * width), 0);
        webView.draw(canvas);
        canvas.restore();
    }

    @Override
    public BatchDraw batchDraw() {
        batchDraw.acquire();
        return batchDraw;
    }

    public void preventDefaultTouch() {
        webView.preventDefaultTouch();
    }

    private void execJs(String js) {
        loadUrl("javascript:" + js);
    }

    private void loadUrl(final String url) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        });
    }

    private void resetCanGoPages() {
        canGoNextPage = false;
        canGoPreviewPage = false;
    }

    private void updateStateByJsReader() {
        execJs("javaBridge.setCurrentLocation(reader.currentLocation.segmentIndex, reader.currentLocation.percent);\n" +
               "javaBridge.setCanGoPages(reader.canGoNextPage(), reader.canGoPreviewPage());\n");
    }

    public class BookConfigurator {
        private final StringBuilder fullJs = new StringBuilder();

        public BookConfigurator setPagePadding(int pageTopPaddingInPixels,
                                               int pageRightPaddingInPixels,
                                               int pageBottomPaddingInPixels,
                                               int pageLeftPaddingInPixels) {
            appendJs(
                    format("reader.setPagePadding(%s, %s, %s, %s);",
                            jsValue(pageTopPaddingInPixels),
                            jsValue(pageRightPaddingInPixels),
                            jsValue(pageBottomPaddingInPixels),
                            jsValue(pageLeftPaddingInPixels)
                    )
            );
            return this;
        }

        public BookConfigurator setTextAlign(TextAlign textAlign) {
            appendJs(
                    format("reader.setTextAlign(%s);",
                            jsValue(textAlign.cssValue())
                    )
            );
            return this;
        }

        public BookConfigurator setFontSize(int fontSizeInPercents) {
            appendJs(
                    format("reader.setFontSize(%s);",
                            jsValue(fontSizeInPercents)
                    )
            );
            return this;
        }

        public BookConfigurator setLineHeight(int lineHeightInPercents) {
            appendJs(
                    format("reader.setLineHeight(%s);",
                            jsValue(lineHeightInPercents)
                    )
            );
            return this;
        }

        public void commit() {
            resetCanGoPages();
            execJs(fullJs.toString());
        }

        private void appendJs(String js) {
            fullJs.append(js).append('\n');
        }
    }

    private class JavaBridge {
        @JavascriptInterface
        public void jsReady() {
            jsReady.request();
        }

        @JavascriptInterface
        public void setCurrentLocation(int segmentIndex, int percent) {
            if (currentLocation == null ||
                currentLocation.segmentIndex() != segmentIndex ||
                currentLocation.percent() != percent)
            {
                currentLocation = new BookLocation(segmentIndex, percent);
                listener.onLocationChange();
            }
        }

        @JavascriptInterface
        public void setCanGoPages(boolean canGoNextPage, boolean canGoPreviewPage) {
            PageBookView.this.canGoNextPage = canGoNextPage;
            PageBookView.this.canGoPreviewPage = canGoPreviewPage;
        }

        @JavascriptInterface
        public void beforeLoad() {
            listener.beforeLoad();
            batchDraw.beforeLoad();
        }

        @JavascriptInterface
        public void afterLoad() {
            batchDraw.afterLoad();
            updateStateByJsReader();
            listener.afterLoad();
        }

        @JavascriptInterface
        public void beforeGoLocation() {
            pageAnimationView.reset();
        }

        @JavascriptInterface
        public void beforeGoNextPage() {
            pageAnimationView.moveNext();
        }

        @JavascriptInterface
        public void beforeGoPreviewPage() {
            pageAnimationView.movePreview();
        }

        @JavascriptInterface
        public void onTouchStartAllowedElement() {
            listener.onTouchStartAllowedElement();
        }
    }

    private class MyWebView extends WebView {
        private boolean preventDefaultTouch = false;
        private long downTime;
        private boolean isDown = false;

        public MyWebView(Context context) {
            super(context);
        }

        @Override
        public void invalidate(@NonNull Rect dirty) {
            super.invalidate(dirty);
            notifyOnInvalidate();
        }

        @Override
        public void invalidate(int l, int t, int r, int b) {
            super.invalidate(l, t, r, b);
            notifyOnInvalidate();
        }

        @Override
        public void invalidate() {
            super.invalidate();
            notifyOnInvalidate();
        }

        private void notifyOnInvalidate() {
            batchDraw.afterInvalidate();
            pageAnimationView.postRefresh();
            listener.afterInvalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                isDown = true;
                downTime = event.getDownTime();
                preventDefaultTouch = false;
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                isDown = false;
                preventDefaultTouch = false;
            }

            if (!preventDefaultTouch) {
                super.onTouchEvent(event);
            }

            return true;
        }

        public void preventDefaultTouch() {
            if (isDown) {
                super.onTouchEvent(MotionEvent.obtain(
                        downTime,
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_CANCEL, 0, 0, 0));
                preventDefaultTouch = true;
            }
        }

        @Override
        public boolean performLongClick() {
            return !preventDefaultTouch && super.performLongClick();
        }

        @Override
        public ActionMode startActionMode(final ActionMode.Callback callback) {
            listener.onSelectStart();
            return super.startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return callback.onCreateActionMode(mode, menu);
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return callback.onPrepareActionMode(mode, menu);
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return callback.onActionItemClicked(mode, item);
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    callback.onDestroyActionMode(mode);
                    listener.onSelectEnd();
                }
            });
        }
    }

    private class BatchDrawImpl implements BatchDraw {
        private final Lock lock = new ReentrantLock();
        private volatile int allPagesLoadedCount = 0;
        private volatile boolean jsViewDrawn = true;
        private boolean correctlyDrawn = false;

        public void beforeLoad() {
            allPagesLoadedCount++;
            jsViewDrawn = false;
            correctlyDrawn = false;
        }

        public void afterLoad() {
            allPagesLoadedCount--;
        }

        public void afterInvalidate() {
            if (allPagesLoadedCount == 0) {
                jsViewDrawn = true;
            }
        }

        public void acquire() {
            lock.lock();
            correctlyDrawn = jsViewDrawn;
        }

        @Override
        public boolean endDraw() {
            lock.unlock();
            return correctlyDrawn;
        }
    }

    public static interface Listener {
        void beforeLoad();

        void afterLoad();

        void afterInvalidate();

        void onTouchStartAllowedElement();

        void onSelectStart();

        void onSelectEnd();

        void onLocationChange();
    }
}

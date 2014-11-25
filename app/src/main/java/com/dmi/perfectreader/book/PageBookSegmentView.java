package com.dmi.perfectreader.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.dmi.perfectreader.util.lang.LongPercent;

import java.util.concurrent.atomic.AtomicInteger;

import static com.dmi.perfectreader.main.MainConstants.LOG_TAG;
import static com.dmi.perfectreader.util.lang.LongPercent.valuePercent;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.String.format;

// todo отрефакторить loadId
public class PageBookSegmentView extends FrameLayout {
    private final WebView webView;

    private OnInvalidateListener onInvalidateListener;

    private boolean isLoaded = false;
    private String loadingUrl;
    private final AtomicInteger loadId = new AtomicInteger();

    private int pageCount = 0;
    private int totalWidth = 0;
    private int screenWidth = 0;
    private LongPercent currentPercent = LongPercent.ZERO;

    public PageBookSegmentView(Context context) {
        super(context);
        webView = createWebView(context);
        webView.setVisibility(INVISIBLE);
        setBackgroundColor(Color.WHITE);
        addView(webView);
    }

    public void setOnInvalidateListener(OnInvalidateListener onInvalidateListener) {
        this.onInvalidateListener = onInvalidateListener;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private WebView createWebView(Context context) {
        final WebView webView = new MyWebView(context);
        webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, String url) {
                if (url.equals(loadingUrl)) {
                    String script =
                            format("__javaBridge.setScreenWidth(%s, document.body.clientWidth);\n", loadId.get()) +
                            format("__javaBridge.setTotalWidth(%s, document.body.scrollWidth);\n", loadId.get()) +
                            format("__javaBridge.finishLoad(%s);", loadId.get());
                    webView.loadUrl("javascript:" + script);
                }
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(LOG_TAG,
                        format("WebView error. errorCode: {%s}, description: {%s}, failingUrl: {%s}",
                                errorCode, description, failingUrl));
            }


        });
        webView.addJavascriptInterface(new JavaBridge(), "__javaBridge");
        webView.setInitialScale(100);
        return webView;
    }

    public void loadUrl(String url) {
        loadId.incrementAndGet();
        isLoaded = false;
        webView.setVisibility(INVISIBLE);
        webView.stopLoading();
        if (url != null) {
            loadingUrl = url;
            webView.loadUrl(url);
        } else {
            loadBlank();
            loadingUrl = null;
        }
    }

    @SuppressWarnings("deprecation")
    private void loadBlank() {
        if (Build.VERSION.SDK_INT < 18) {
            webView.clearView();
        } else {
            webView.loadUrl("about:blank");
        }
    }

    public void goPercent(LongPercent percent) {
        currentPercent = percent;
        if (isLoaded) {
            scrollToCurrentViewport();
        }
    }

    private void scrollToCurrentViewport() {
        webView.scrollTo(currentPage() * screenWidth, 0);
    }

    public boolean canGoNextPage() {
        return isLoaded() && !isLastPage() ;
    }

    public boolean canGoPreviewPage() {
        return isLoaded() && !isFirstPage();
    }

    public void goNextPage() {
        checkState(canGoNextPage());
        currentPercent = pageToPercent(currentPage() + 1);
        scrollToCurrentViewport();
    }

    public void goPreviewPage() {
        checkState(canGoPreviewPage());
        currentPercent = pageToPercent(currentPage() - 1);
        scrollToCurrentViewport();
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public int currentPage() {
        checkState(isLoaded());
        return currentPercent.equals(LongPercent.HUNDRED) ?
                pageCount - 1 :
                (int) round(currentPercent.multiply(totalWidth) / screenWidth);
    }

    public LongPercent currentPercent() {
        return currentPercent;
    }

    public int pageCount() {
        checkState(isLoaded());
        return pageCount;
    }

    public LongPercent pageToPercent(int page) {
        checkState(isLoaded());
        return valuePercent(page, pageCount);
    }

    public int percentToPage(LongPercent percent) {
        checkState(isLoaded());
        return min((int) round(percent.multiply(totalWidth) / screenWidth), pageCount - 1);
    }

    public boolean isLastPage() {
        checkState(isLoaded());
        return currentPage() == pageCount - 1;
    }

    public boolean isFirstPage() {
        checkState(isLoaded());
        return currentPage() == 0;
    }

    public void drawPage(int pageIndex, Canvas canvas) {
        if (isLoaded() && pageIndex >= 0 && pageIndex < pageCount) {
            canvas.save();
            canvas.translate(-pageIndex * screenWidth, 0);
            webView.draw(canvas);
            canvas.restore();
        } else {
            canvas.drawColor(Color.WHITE);
        }
    }

    public void setFontSize(String size) {
        webView.loadUrl(format("javascript: setFontSize(\"%s\")", size));
    }

    public void setLineHeight(float height) {
        throw new UnsupportedOperationException();
    }

    private void notifyOnInvalidate() {
        if (isLoaded && onInvalidateListener != null) {
            onInvalidateListener.onInvalidate();
        }
    }

    private class JavaBridge {
        @JavascriptInterface
        public void setScreenWidth(int loadId, int screenWidth) {
            PageBookSegmentView.this.screenWidth = screenWidth;
        }

        @JavascriptInterface
        public void setTotalWidth(int loadId, int totalWidth) {
            PageBookSegmentView.this.totalWidth = totalWidth;
            pageCount = round(totalWidth / screenWidth);
        }

        @JavascriptInterface
        public void finishLoad(final int loadId) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (PageBookSegmentView.this.loadId.get() == loadId) {
                        webView.setVisibility(VISIBLE);
                        isLoaded = true;
                        scrollToCurrentViewport();
                        notifyOnInvalidate();
                    }
                }
            });
        }
    }

    private class MyWebView extends WebView {
        public MyWebView(Context context) {
            super(context);
        }

        @Override
        public ViewParent invalidateChildInParent(int[] location, @NonNull Rect dirty) {
            return super.invalidateChildInParent(location, dirty);
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
    }

    public static interface OnInvalidateListener {
        void onInvalidate();
    }
}

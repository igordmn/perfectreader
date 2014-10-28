package com.dmi.perfectreader.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.dmi.perfectreader.util.lang.LongPercent;

import static com.dmi.perfectreader.util.lang.LongPercent.valuePercent;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.round;

public class PageBookSegmentView extends FrameLayout {
    private final WebView webView;

    private boolean isLoaded = false;

    private int pageCount = 0;
    private int totalWidth = 0;
    private int screenWidth = 0;
    private LongPercent currentPercent = LongPercent.ZERO;

    public PageBookSegmentView(Context context) {
        super(context);
        webView = createWebView(context);
        addView(webView);
    }

    public PageBookSegmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        webView = createWebView(context);
        addView(webView);
    }

    public PageBookSegmentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        webView = createWebView(context);
        addView(webView);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private WebView createWebView(Context context) {
        final WebView webView = new WebView(context);
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
                String script =
                        "__javaBridge.setScreenWidth(document.body.clientWidth);\n" +
                        "__javaBridge.setTotalWidth(document.body.scrollWidth);\n" +
                        "__javaBridge.finishLoad();";
                webView.loadUrl("javascript:" + script);
            }
        });
        webView.addJavascriptInterface(new JavaBridge(), "__javaBridge");
        webView.setInitialScale(100);
        return webView;
    }

    public void loadUrl(String url) {
        isLoaded = false;
        webView.stopLoading();
        loadBlank();
        if (url != null) {
            webView.loadUrl(url);
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

    public boolean isLastPage() {
        checkState(isLoaded());
        return currentPage() == pageCount - 1;
    }

    public boolean isFirstPage() {
        checkState(isLoaded());
        return currentPage() == 0;
    }

    public void drawCurrentPage(Canvas canvas) {
        canvas.save();
        canvas.translate(-currentPage() * screenWidth, 0);
        if (isLoaded()) {
            webView.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        canvas.restore();
    }

    public void drawNextPage(Canvas canvas) {
        checkState(isLoaded());
        canvas.save();
        canvas.translate(-(currentPage() + 1) * screenWidth, 0);
        if (isLoaded()) {
            webView.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        canvas.restore();
    }

    public void drawPreviewPage(Canvas canvas) {
        checkState(isLoaded());
        canvas.save();
        canvas.translate(-(currentPage() - 1) * screenWidth, 0);
        if (isLoaded()) {
            webView.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        canvas.restore();
    }

    public void setFontSize(float size) {
        checkState(isLoaded());
        throw new UnsupportedOperationException();
    }

    public void setLineHeight(float height) {
        checkState(isLoaded());
        throw new UnsupportedOperationException();
    }

    private LongPercent pageToPercent(int page) {
        return valuePercent(page, pageCount);
    }

    private class JavaBridge {
        @JavascriptInterface
        public void setScreenWidth(int screenWidth) {
            PageBookSegmentView.this.screenWidth = screenWidth;
        }

        @JavascriptInterface
        public void setTotalWidth(int totalWidth) {
            PageBookSegmentView.this.totalWidth = totalWidth;
            pageCount = (int) round(totalWidth / screenWidth);
        }

        @JavascriptInterface
        public void finishLoad() {
            isLoaded =  true;
            scrollToCurrentViewport();
        }
    }
}

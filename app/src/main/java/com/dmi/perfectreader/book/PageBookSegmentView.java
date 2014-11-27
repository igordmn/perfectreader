package com.dmi.perfectreader.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.util.lang.LongPercent;
import com.dmi.perfectreader.util.opengl.ResourceUtils;
import com.google.common.base.Charsets;

import java.io.ByteArrayInputStream;

import static com.dmi.perfectreader.util.lang.LongPercent.valuePercent;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.String.format;

// todo проверить, нужен ли loadId
public class PageBookSegmentView extends FrameLayout {
    private static final String LOG_TAG = BookFragment.class.getSimpleName();

    private final WebView webView;

    private OnInvalidateListener onInvalidateListener;

    private boolean isLoaded = false;

    private int pageCount = 0;
    private int totalWidth = 0;
    private int screenWidth = 0;
    private LongPercent currentPercent = LongPercent.ZERO;

    private String fontSize = "100%";

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
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setInitialScale(100);
        webView.addJavascriptInterface(new JavaBridge(), "__javaBridge");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i(LOG_TAG, "onPageStarted. url: " + url);
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                Log.i(LOG_TAG, "onPageFinished. url: " + url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(LOG_TAG,
                        format("onReceivedError. errorCode: {%s}, description: {%s}, failingUrl: {%s}",
                                errorCode, description, failingUrl));
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                Log.i(LOG_TAG, "loading " + url);
                boolean isInitScript = url.startsWith("javabridge://initscript");
                if (isInitScript) {
                    return scriptResourceResponse(initScript());
                } else {
                    return super.shouldInterceptRequest(view, url);
                }
            }

            private WebResourceResponse scriptResourceResponse(String script) {
                ByteArrayInputStream is = new ByteArrayInputStream(script.getBytes(Charsets.UTF_8));
                return new WebResourceResponse("text/javascript", "UTF-8", is);
            }

            private String initScript() {
                String varScript = varDeclaration("__javaFontSize", fontSize);
                String mainScript = ResourceUtils.readTextRawResource(getResources(), R.raw.js_booksegment_init);
                return varScript + mainScript;
            }

            private String varDeclaration(String name, String value) {
                return  format("var %s = \"%s\";\n", name, value);
            }
        });
        return webView;
    }

    public void loadUrl(String url) {
        Log.i(LOG_TAG, "loadUrl. url: " + url);
        isLoaded = false;
        webView.setVisibility(INVISIBLE);
        webView.stopLoading();
        if (url != null) {
            webView.loadUrl(url);
        } else {
            loadBlank();
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
        return screenWidth > 0 ? min((int) round(percent.multiply(totalWidth) / screenWidth), pageCount - 1) : 0;
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

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
        webView.loadUrl(format("javascript: setFontSize(\"%s\")", fontSize));
    }

    public void setLineHeight(float lineHeight) {
        throw new UnsupportedOperationException();
    }

    private void notifyOnInvalidate() {
        if (isLoaded && onInvalidateListener != null) {
            onInvalidateListener.onInvalidate();
        }
    }

    private class JavaBridge {
        @JavascriptInterface
        public void setScreenWidth(int screenWidth) {
            PageBookSegmentView.this.screenWidth = screenWidth;
            if (isLoaded) {
                scrollToCurrentViewport();
            }
        }

        @JavascriptInterface
        public void setTotalWidth(int totalWidth) {
            PageBookSegmentView.this.totalWidth = totalWidth;
            pageCount = screenWidth > 0 ? round(totalWidth / screenWidth) : 0;
            if (isLoaded) {
                scrollToCurrentViewport();
            }
        }

        @JavascriptInterface
        public void finishLoad() {
            Log.i(LOG_TAG, "finishLoad");
            post(new Runnable() {
                @Override
                public void run() {
                    webView.setVisibility(VISIBLE);
                    isLoaded = true;
                    scrollToCurrentViewport();
                    notifyOnInvalidate();
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

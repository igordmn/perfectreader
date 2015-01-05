package com.dmi.perfectreader.bookview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.dmi.perfectreader.book.BookStorage;
import com.dmi.perfectreader.book.config.TextAlign;
import com.dmi.perfectreader.error.ErrorEvent;
import com.dmi.perfectreader.main.EventBus;
import com.dmi.perfectreader.util.concurrent.Waiter;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.dmi.perfectreader.util.android.MainThreads.runOnMainThread;
import static com.dmi.perfectreader.util.js.JavaScript.jsArray;
import static com.dmi.perfectreader.util.js.JavaScript.jsValue;
import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.net.URLConnection.guessContentTypeFromName;

@EViewGroup
class PageBookWebView extends FrameLayout {
    private static final String LOG_TAG = PageBookWebView.class.getSimpleName();

    private Listener listener;
    private BookStorage bookStorage;
    @Bean
    protected EventBus eventBus;

    private final MyWebView webView;

    private Waiter htmlPageReady = new Waiter();
    private Waiter jsReady = new Waiter();

    private final AtomicBoolean isLoaded = new AtomicBoolean(false);

    public PageBookWebView(Context context) {
        super(context);
        webView = createWebView(context);
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
                    eventBus.post(new ErrorEvent(e));
                    return null;
                }
            }
        });

        return webView;
    }

    public void setBookStorage(BookStorage bookStorage) {
        this.bookStorage = bookStorage;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Инициализация должна запускаться вне UI потока, т.к. она происходит небыстро
     * Должна вызываться после того, как загрузится bookStorage
     */
    public void init() {
        loadUrl("file:///android_asset/pageBook/pageBook.html");
        htmlPageReady.waitRequest();
        String setCallback = "reader.setCallback(javaBridge);\n";
        String setSegmentUrls = format("reader.setSegmentUrls(%s);\n", jsArray(bookStorage.segmentUrls()));
        String callJsReady = format("javaBridge.jsReady()");
        execJs(setCallback + setSegmentUrls + callJsReady);
        jsReady.waitRequest();
    }

    public BookConfigurator configure() {
        return new BookConfigurator();
    }

    public void goLocation(int segmentIndex, int xOffset) {
        checkArgument(segmentIndex >= 0 && segmentIndex < bookStorage.segmentUrls().size());
        isLoaded.set(false);
        execJs(format("reader.goLocation({segmentIndex: %s, percent: %s})", segmentIndex, xOffset));
    }

    public void drawPage(float offset, Canvas canvas) {
    }

    public void drawNextSegmentPage(Canvas canvas) {
    }

    public void drawPreviewSegmentPage(Canvas canvas) {
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

    public boolean isLoaded() {
        return isLoaded.get();
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

        public BookConfigurator setLineHeight(float lineHeight) {
            throw new UnsupportedOperationException();
        }

        public void commit() {
            isLoaded.set(false);
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
        public void beforeLoad() {
        }

        @JavascriptInterface
        public void afterLoad(int totalWidth, int screenWidth) {
            isLoaded.set(true);
            listener.afterLoad(totalWidth, screenWidth);
        }
    }

    private class MyWebView extends WebView {
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
            if (listener != null) {
                listener.onInvalidate();
            }
        }
    }

    public static interface Listener {
        void onInvalidate();

        void afterLoad(int totalWidth, int screenWidth);
    }
}

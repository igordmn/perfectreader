package com.dmi.perfectreader.book;

import android.content.Context;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.config.TextAlign;
import com.dmi.perfectreader.bookstorage.BookStorage;
import com.dmi.typoweb.HangingPunctuationConfig;
import com.dmi.typoweb.JavascriptInterface;
import com.dmi.typoweb.RenderContext;
import com.dmi.typoweb.TypoWeb;

import static com.dmi.perfectreader.book.LocationUtils.pageToPercent;
import static com.dmi.perfectreader.book.LocationUtils.percentToPage;
import static com.dmi.util.js.JavaScript.jsArray;
import static com.dmi.util.js.JavaScript.jsValue;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

public class WebPageBook implements PageBook, TypoWeb.Client {
    private final Client client;

    private TypoWeb typoWeb;
    private RenderContext renderContext;

    private final Settings settings = new Settings();
    private BookStorage bookStorage;
    private final CurrentLocation currentLocation = new CurrentLocation();
    private final LoadingState canDrawState = new LoadingState();

    public WebPageBook(Client client, Context context) {
        this.client = client;

        typoWeb = new TypoWeb(this, context, context.getString(R.string.app_name));
        typoWeb.setURLHandler((url) -> {
            if (url.startsWith("assets://pageBook/")) {
                return context.getAssets().open(url.substring("assets://".length()));
            } else if (bookStorage != null) {
                return bookStorage.readURL(url);
            } else {
                throw new SecurityException();
            }
        });
        typoWeb.setHangingPunctuationConfig(hangingPunctuationConfig());
        typoWeb.setHyphenationPatternsLoader(new TexHyphenationPatternsLoader(context));
        typoWeb.addJavascriptInterface("__javaBridge", new JavaBridge());
        typoWeb.loadUrl("assets://pageBook/pageBook.html");
        typoWeb.execJavaScript("reader.setClient(__javaBridge);");
    }

    private HangingPunctuationConfig hangingPunctuationConfig() {
        return HangingPunctuationConfig.builder()

            // скобки
            .startChar('(', 0.5F)
            .startChar('[', 0.5F)
            .startChar('{', 0.5F)
            .endChar(')', 0.50F)
            .endChar(']', 0.50F)
            .endChar('}', 0.50F)
            .endChar('>', 0.50F)

            // кавычки
            .startChar('"', 0.5F)
            .startChar('\'', 0.5F)
            .startChar('«', 0.5F)
            .startChar('»', 0.5F)
            .startChar('„', 0.5F)
            .startChar('“', 0.5F)
            .startChar('‘', 0.5F)
            .startChar('‚', 0.5F)
            .startChar('‹',  0.5F)
            .startChar('‚',  0.5F)
            .endChar('"', 0.50F)
            .endChar('\'', 0.50F)
            .endChar('»', 0.50F)
            .endChar('«', 0.50F)
            .endChar('“', 0.50F)
            .endChar('”', 0.50F)
            .endChar('’', 0.50F)
            .endChar('‘',  0.50F)
            .endChar('›',  0.50F)

            // знаки препинания
            .endChar(',', 0.50F)
            .endChar('.', 0.50F)
            .endChar('…', 0.50F)
            .endChar(':', 0.50F)
            .endChar(';', 0.50F)
            .endChar('!', 0.50F)
            .endChar('‼', 0.50F)
            .endChar('?', 0.50F)
            .endChar('،',  0.50F)
            .endChar('۔',  0.50F)
            .endChar('、',  0.25F)
            .endChar('。',  0.25F)
            .endChar('，',  0.25F)
            .endChar('．',  0.25F)
            .endChar('﹐',  0.25F)
            .endChar('﹑',  0.25F)
            .endChar('﹒',  0.5F)
            .endChar('｡',  0.5F)
            .endChar('､',  0.5F)

            // тире, дефисы, мягкий перенос
            .endChar('\u2010', 0.50F)
            .endChar('\u2011', 0.50F)
            .endChar('\u2012', 0.25F)
            .endChar('\u2013', 0.25F)
            .endChar('\u2014', 0.25F)
            .endChar('\u00AD', 0.50F)
            .endChar('\u002D', 0.50F)
            .build();
    }

    public void destroy() {
        typoWeb.destroy();
        typoWeb = null;
    }

    public void load(BookStorage bookStorage) {
        checkState(this.bookStorage == null, "Cannot load twice");
        checkArgument(bookStorage.getSegmentSizes().length == bookStorage.getSegmentURLs().length);
        this.bookStorage = bookStorage;
        currentLocation.setSegmentSizes(bookStorage.getSegmentSizes());
        typoWeb.execJavaScript(format(
                "reader.load(%s);",
                jsArray(bookStorage.getSegmentURLs())
        ));
    }

    public void pause() {
        typoWeb.pause();
    }

    public void resume() {
        typoWeb.resume();
    }

    public int currentPercent() {
        return currentLocation.totalPercent();
    }

    @Override
    public CanGoResult canGoPage(int offset) {
        checkState(bookStorage != null);
        return currentLocation.canGoPage(offset);
    }

    @Override
    public boolean glCanDraw() {
        return canDrawState.canDraw();
    }

    public Settings settings() {
        return settings;
    }

    public void tap(float x, float y, float tapDiameter) {
        typoWeb.tap(x, y, x, y, tapDiameter, System.nanoTime() / 1E6F);
    }

    @Override
    public void goPercent(int integerPercent) {
        currentLocation.goPercent(integerPercent);
        client.afterLocationChanged();
        goCurrentLocation();
    }

    @Override
    public void goNextPage() {
        currentLocation.goNextPage();
        client.afterLocationChanged();
        goCurrentLocation();
    }

    @Override
    public void goPreviewPage() {
        currentLocation.goPreviewPage();
        client.afterLocationChanged();
        goCurrentLocation();
    }

    private void goCurrentLocation() {
        canDrawState.beforePageGo();
        typoWeb.execJavaScript(format(
                "reader.goLocation(%s, %s);" +
                "__javaBridge.afterPageGo();",
                currentLocation.segmentIndex(), currentLocation.segmentPercent()
        ));
    }

    @Override
    public void glInit() {
        if (renderContext == null) {
            renderContext = new RenderContext();
        }
    }

    @Override
    public void glFreeResources() {
        if (renderContext != null) {
            renderContext.destroy();
            renderContext = null;
        }
    }

    @Override
    public void glSetSize(int width, int height) {
        canDrawState.beforeResize();
        typoWeb.setSize(width, height);
        typoWeb.execJavaScript(
                "reader.configure({" +
                "    pageWidth: innerWidth," +
                "    pageHeight: innerHeight" +
                "});" +
                "__javaBridge.afterResize();"
        );
    }

    @Override
    public void glDraw() {
        typoWeb.draw(renderContext);
    }

    @Override
    public void afterAnimate() {
        canDrawState.afterAnimate();
        client.afterAnimate();
    }

    private class JavaBridge {
        @JavascriptInterface
        public void notifyPageCounts(Integer previewSegment, Integer currentSegment, Integer nextSegment) {
            currentLocation.setPageCounts(previewSegment, currentSegment, nextSegment);
        }

        @JavascriptInterface
        public void afterPageGo() {
            canDrawState.afterPageGo();
        }

        @JavascriptInterface
        public void afterResize() {
            canDrawState.afterResize();
        }

        @JavascriptInterface
        public int percentToPage(int pageCount, double percent) {
            return LocationUtils.percentToPage(pageCount, percent);
        }

        @JavascriptInterface
        public void handleTap() {
            client.handleTap();
        }
    }

    private static class LoadingState {
        private int scheduledGoes = 0;
        private int scheduledResizes = 0;
        private boolean canDraw = true;

        public synchronized void beforeResize() {
            canDraw = false;
            scheduledResizes++;
            checkState(scheduledResizes >= 0);
        }

        public synchronized void afterResize() {
            scheduledResizes--;
            checkState(scheduledResizes >= 0);
        }

        public synchronized void beforePageGo() {
            canDraw = false;
            scheduledGoes++;
            checkState(scheduledGoes >= 0);
        }

        public synchronized void afterPageGo() {
            scheduledGoes--;
            checkState(scheduledGoes >= 0);
        }

        public synchronized void afterAnimate() {
            if (scheduledGoes == 0 && scheduledResizes == 0) {
                canDraw = true;
            }
        }

        public synchronized boolean canDraw() {
            return canDraw;
        }
    }

    public interface Client {
        default void afterAnimate() {}

        default void afterLocationChanged() {}

        default void handleTap() {}
    }

    public class Settings {
        public void setPaddingTop(int value) {
            configure("paddingTop", value);
        }

        public void setPaddingRight(int value) {
            configure("paddingRight", value);
        }

        public void setPaddingBottom(int value) {
            configure("paddingBottom", value);
        }

        public void setPaddingLeft(int value) {
            configure("paddingLeft", value);
        }

        public void setTextAlign(TextAlign value) {
            configure("textAlign", value.cssValue());
        }

        public void setFontSizePercents(int value) {
            configure("fontSizePercents", value);
        }

        public void setLineHeightPercents(int value) {
            configure("lineHeightPercents", value);
        }

        public void setHangingPunctuation(boolean value) {
            configure("hangingPunctuation", value);
        }

        public void setHyphenation(boolean value) {
            configure("hyphenation", value);
        }

        private void configure(String settingName, int settingValue) {
            currentLocation.resetPageCounts();
            typoWeb.execJavaScript(
                    format("reader.configure({%s: %s})", settingName, jsValue(settingValue))
            );
        }

        private void configure(String settingName, String settingValue) {
            currentLocation.resetPageCounts();
            typoWeb.execJavaScript(
                    format("reader.configure({%s: %s})", settingName, jsValue(settingValue))
            );
        }

        private void configure(String settingName, boolean settingValue) {
            currentLocation.resetPageCounts();
            typoWeb.execJavaScript(
                    format("reader.configure({%s: %s})", settingName, jsValue(settingValue))
            );
        }
    }
}

class CurrentLocation {
    private int[] segmentSizes;
    private int segmentCount;

    private Integer previewSegmentPageCount;
    private Integer currentSegmentPageCount;
    private Integer nextSegmentPageCount;

    private int totalPercent;
    private int segmentIndex;
    private double segmentPercent;
    private Integer segmentPage;
    private Integer segmentPages;

    public synchronized void setSegmentSizes(int[] segmentSizes) {
        this.segmentSizes = segmentSizes;
        this.segmentCount = segmentSizes.length;
        totalPercentToSegmentLocation();
    }

    public synchronized void setPageCounts(Integer previewSegment, Integer currentSegment, Integer nextSegment) {
        this.previewSegmentPageCount = previewSegment;
        this.currentSegmentPageCount = currentSegment;
        this.nextSegmentPageCount = nextSegment;
        segmentPages = currentSegmentPageCount;
        segmentPage = segmentPages != null ? percentToPage(segmentPages, segmentPercent) : null;
    }

    public synchronized void resetPageCounts() {
        previewSegmentPageCount = null;
        currentSegmentPageCount = null;
        nextSegmentPageCount = null;
    }

    public synchronized void goPercent(int integerPercent) {
        resetPageCounts();
        totalPercent = integerPercent;
        if (segmentSizes != null) {
            totalPercentToSegmentLocation();
        }
    }

    public synchronized void goNextPage() {
        if (canGoPage(1) == PageBook.CanGoResult.CAN) {
            if (segmentPage < segmentPages - 1) {
                segmentPage++;
            } else {
                previewSegmentPageCount = currentSegmentPageCount;
                currentSegmentPageCount = nextSegmentPageCount;
                nextSegmentPageCount = null;
                segmentIndex++;
                segmentPage = 0;
                segmentPages = currentSegmentPageCount;
            }
            segmentPercent = pageToPercent(segmentPages, segmentPage);
            segmentLocationToTotalPercent();
        }
    }

    public synchronized void goPreviewPage() {
        if (canGoPage(-1) == PageBook.CanGoResult.CAN) {
            if (segmentPage > 0) {
                segmentPage--;
            } else {
                nextSegmentPageCount = currentSegmentPageCount;
                currentSegmentPageCount = previewSegmentPageCount;
                previewSegmentPageCount = null;
                segmentIndex--;
                segmentPage = currentSegmentPageCount - 1;
                segmentPages = currentSegmentPageCount;
            }
            segmentPercent = pageToPercent(segmentPages, segmentPage);
            segmentLocationToTotalPercent();
        }
    }

    public synchronized int totalPercent() {
        return totalPercent;
    }

    public synchronized double segmentIndex() {
        return segmentIndex;
    }

    public synchronized double segmentPercent() {
        return segmentPercent;
    }

    public synchronized PageBook.CanGoResult canGoPage(int offset) {
        if (offset == 0) {
            return PageBook.CanGoResult.CAN;
        } else if (segmentPage == null) {
            return PageBook.CanGoResult.CANNOT;
        } else if (offset > 0) {
            int targetPage = segmentPage + offset;
            int loadLimit = segmentPage;  // индекс первой незагруженной страницы относительно начала текущего сегмента
            if (currentSegmentPageCount != null) {
                if (nextSegmentPageCount == null) {
                    loadLimit = currentSegmentPageCount;
                } else {
                    loadLimit = currentSegmentPageCount + nextSegmentPageCount;
                }
            }

            int endLimit = Integer.MAX_VALUE;  // индекс конца книги относительно начала текущего сегмента
            if (currentSegmentPageCount != null) {
                if (segmentIndex == segmentCount - 1) {
                    endLimit = currentSegmentPageCount;
                } else if (segmentIndex == segmentCount - 2 && nextSegmentPageCount != null) {
                    endLimit = currentSegmentPageCount + nextSegmentPageCount;
                }
            }

            if (targetPage >= endLimit) {
                return PageBook.CanGoResult.CANNOT;
            } else if (targetPage >= loadLimit) {
                return PageBook.CanGoResult.UNKNOWN;
            } else {
                return PageBook.CanGoResult.CAN;
            }
        } else if (offset < 0) {
            int targetPage = segmentPage + offset;
            int loadLimit = segmentPage;
            if (currentSegmentPageCount != null) {
                if (previewSegmentPageCount == null) {
                    loadLimit = -1;
                } else {
                    loadLimit = -1 - previewSegmentPageCount;
                }
            }

            int endLimit = Integer.MIN_VALUE;
            if (currentSegmentPageCount != null) {
                if (segmentIndex == 0) {
                    endLimit = -1;
                } else if (segmentIndex == 1 && previewSegmentPageCount != null) {
                    endLimit = -1 - previewSegmentPageCount;
                }
            }

            if (targetPage <= endLimit) {
                return PageBook.CanGoResult.CANNOT;
            } else if (targetPage <= loadLimit) {
                return PageBook.CanGoResult.UNKNOWN;
            } else {
                return PageBook.CanGoResult.CAN;
            }
        }
        throw new IllegalStateException();
    }

    private void totalPercentToSegmentLocation() {
        LocationUtils.SegmentLocation segmentLocation = LocationUtils.percentToSegmentLocation(segmentSizes, totalPercent);
        segmentIndex = segmentLocation.index;
        segmentPercent = segmentLocation.percent;
    }

    private void segmentLocationToTotalPercent() {
        LocationUtils.SegmentLocation segmentLocation = new LocationUtils.SegmentLocation(segmentIndex, segmentPercent);
        totalPercent = LocationUtils.segmentLocationToPercent(segmentSizes, segmentLocation);
    }
}


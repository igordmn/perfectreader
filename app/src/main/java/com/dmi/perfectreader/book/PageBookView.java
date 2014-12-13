package com.dmi.perfectreader.book;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.util.lang.LongPercent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class PageBookView extends FrameLayout implements PagesDrawer {
    private static final BookLocation PRECREATED_CURRENT_LOCATION = new BookLocation(0, LongPercent.ZERO);

    private PageAnimationView pageAnimationView;

    private PageBookSegmentView currentSegment;
    private PageBookSegmentView nextSegment;
    private PageBookSegmentView previewSegment;

    private BookStorage bookStorage;

    private BookLocation currentLocation = null;

    public PageBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        currentSegment = PageBookSegmentView_.build(context);
        nextSegment = PageBookSegmentView_.build(context);
        previewSegment = PageBookSegmentView_.build(context);
        currentSegment.setOnInvalidateListener(new SegmentOnInvalidateListener(currentSegment));
        nextSegment.setOnInvalidateListener(new SegmentOnInvalidateListener(nextSegment));
        previewSegment.setOnInvalidateListener(new SegmentOnInvalidateListener(previewSegment));
        currentSegment.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        nextSegment.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        previewSegment.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        reinitSegments();
        addView(currentSegment);
        addView(nextSegment);
        addView(previewSegment);
    }

    public void setPageAnimationView(PageAnimationView pageAnimationView) {
        checkState(this.pageAnimationView == null || pageAnimationView == this.pageAnimationView);
        this.pageAnimationView = pageAnimationView;
        pageAnimationView.setPagesDrawer(this);
    }

    public void setBookStorage(BookStorage bookStorage) {
        this.bookStorage = bookStorage;
        currentSegment.setBookStorage(bookStorage);
        nextSegment.setBookStorage(bookStorage);
        previewSegment.setBookStorage(bookStorage);
    }

    public void goLocation(BookLocation location) {
        if (currentLocation == null) {
            currentLocation = PRECREATED_CURRENT_LOCATION;
        }
        currentLocation.setSegmentIndex(location.segmentIndex());
        currentLocation.setPercent(location.percent());
        String segmentUrl = bookStorage.segmentUrl(location.segmentIndex());
        currentSegment.loadUrl(segmentUrl);
        if (location.segmentIndex() < bookStorage.segmentCount() - 1) {
            segmentUrl = bookStorage.segmentUrl(location.segmentIndex() + 1);
            nextSegment.loadUrl(segmentUrl);
        } else {
            nextSegment.loadUrl(null);
        }
        if (location.segmentIndex() > 0) {
            segmentUrl = bookStorage.segmentUrl(location.segmentIndex() - 1);
            previewSegment.loadUrl(segmentUrl);
        } else {
            previewSegment.loadUrl(null);
        }
        pageAnimationView.moveLocation(currentLocation);
    }

    public boolean canGoNextPage() {
        return currentLocation != null &&
               currentLocation.segmentIndex() < bookStorage.segmentCount() - 1 && currentSegment.isLoaded() ||
               currentSegment.canGoNextPage();
    }

    public boolean canGoPreviewPage() {
        return currentLocation != null &&
               currentLocation.segmentIndex() > 0 && currentSegment.isLoaded() ||
               currentSegment.canGoPreviewPage();
    }

    public void goNextPage() {
        checkArgument(canGoNextPage());
        if (currentSegment.isLastPage()) {
            PageBookSegmentView newNextSegment = previewSegment;
            previewSegment = currentSegment;
            currentSegment = nextSegment;
            nextSegment = newNextSegment;
            currentLocation.setSegmentIndex(currentLocation.segmentIndex() + 1);
            if (currentLocation.segmentIndex() < bookStorage.segmentCount() - 1) {
                String segmentUrl = bookStorage.segmentUrl(currentLocation.segmentIndex() + 1);
                nextSegment.loadUrl(segmentUrl);
            } else {
                nextSegment.loadUrl(null);
            }
            reinitSegments();
        } else {
            currentSegment.goNextPage();
        }
        currentLocation.setPercent(currentSegment.currentPercent());
        pageAnimationView.moveNext(currentLocation);
    }

    public void goPreviewPage() {
        checkArgument(canGoPreviewPage());
        if (currentSegment.isFirstPage()) {
            PageBookSegmentView newPreviewSegment = nextSegment;
            nextSegment = currentSegment;
            currentSegment = previewSegment;
            previewSegment = newPreviewSegment;
            currentLocation.setSegmentIndex(currentLocation.segmentIndex() - 1);
            if (currentLocation.segmentIndex() > 0) {
                String segmentUrl = bookStorage.segmentUrl(currentLocation.segmentIndex() - 1);
                previewSegment.loadUrl(segmentUrl);
            } else {
                previewSegment.loadUrl(null);
            }
            reinitSegments();
        } else {
            currentSegment.goPreviewPage();
        }
        currentLocation.setPercent(currentSegment.currentPercent());
        pageAnimationView.movePreview(currentLocation);
    }

    private void reinitSegments() {
        nextSegment.goPercent(LongPercent.ZERO);
        previewSegment.goPercent(LongPercent.HUNDRED);
        currentSegment.setVisibility(VISIBLE);
        previewSegment.setVisibility(INVISIBLE);
        nextSegment.setVisibility(INVISIBLE);
    }

    @Override
    public void drawPage(BookLocation location, int relativeIndex, Canvas canvas) {
        if (location.segmentIndex() == currentLocation.segmentIndex()) {
            drawPage(location, relativeIndex, canvas, previewSegment, currentSegment, nextSegment);
        } else if (location.segmentIndex() == currentLocation.segmentIndex() + 1) {
            drawPage(location, relativeIndex, canvas, currentSegment, nextSegment, null);
        } else if (location.segmentIndex() == currentLocation.segmentIndex() - 1) {
            drawPage(location, relativeIndex, canvas, null, previewSegment, currentSegment);
        } else {
            canvas.drawColor(Color.WHITE);
        }
    }

    private static void drawPage(BookLocation location, int relativeIndex, Canvas canvas,
                                 PageBookSegmentView previewSegment,
                                 PageBookSegmentView currentSegment,
                                 PageBookSegmentView nextSegment) {
        if (currentSegment != null && currentSegment.isLoaded()) {
            int pageIndex = currentSegment.percentToPage(location.percent()) + relativeIndex;
            if (pageIndex >= 0 && pageIndex < currentSegment.pageCount()) {
                currentSegment.drawPage(pageIndex, canvas);
            } else if (pageIndex < 0) {
                if (previewSegment != null && previewSegment.isLoaded()) {
                    previewSegment.drawPage(previewSegment.pageCount() + pageIndex, canvas);
                } else {
                    canvas.drawColor(Color.WHITE);
                }
            } else if (pageIndex >= currentSegment.pageCount()) {
                if (nextSegment != null && nextSegment.isLoaded()) {
                    nextSegment.drawPage(pageIndex - currentSegment.pageCount(), canvas);
                } else {
                    canvas.drawColor(Color.WHITE);
                }
            }
        } else {
            canvas.drawColor(Color.WHITE);
        }
    }

    public void setFontSize(int fontSizeInPercents) {
        currentSegment.setFontSize(fontSizeInPercents);
        nextSegment.setFontSize(fontSizeInPercents);
        previewSegment.setFontSize(fontSizeInPercents);
    }

    public void setPagePadding(int topPaddingInPixels,
                               int rightPaddingInPixels,
                               int bottomPaddingInPixels,
                               int leftPaddingInPixels) {
        currentSegment.setPagePadding(topPaddingInPixels, rightPaddingInPixels, bottomPaddingInPixels, leftPaddingInPixels);
        nextSegment.setPagePadding(topPaddingInPixels, rightPaddingInPixels, bottomPaddingInPixels, leftPaddingInPixels);
        previewSegment.setPagePadding(topPaddingInPixels, rightPaddingInPixels, bottomPaddingInPixels, leftPaddingInPixels);
    }

    public void setLineHeight(float height) {
        currentSegment.setLineHeight(height);
        nextSegment.setLineHeight(height);
        previewSegment.setLineHeight(height);
    }

    private class SegmentOnInvalidateListener implements PageBookSegmentView.OnInvalidateListener {
        private PageBookSegmentView segment;

        private SegmentOnInvalidateListener(PageBookSegmentView segment) {
            this.segment = segment;
        }

        @Override
        public void onInvalidate() {
            if (segment == currentSegment ||
                (segment == nextSegment && currentSegment.isLoaded() && currentSegment.isLastPage()) ||
                (segment == previewSegment && currentSegment.isLoaded() && currentSegment.isFirstPage())) {
                pageAnimationView.postRefresh();
            }
        }
    }
}

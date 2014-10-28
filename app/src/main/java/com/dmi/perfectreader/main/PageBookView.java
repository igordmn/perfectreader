package com.dmi.perfectreader.main;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.dmi.perfectreader.util.lang.LongPercent;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class PageBookView extends FrameLayout {
    private PageBookSegmentView currentSegment;
    private PageBookSegmentView nextSegment;
    private PageBookSegmentView previewSegment;

    private final List<String> segmentUrls = new ArrayList<>();

    private BookLocation currentLocation = null;

    public PageBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        currentSegment = new PageBookSegmentView(context);
        nextSegment = new PageBookSegmentView(context);
        previewSegment = new PageBookSegmentView(context);
        currentSegment.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        nextSegment.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        previewSegment.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        reinitSegments();
        addView(currentSegment);
        addView(nextSegment);
        addView(previewSegment);
    }

    public void addSegmentUrl(String segmentUrl) {
        segmentUrls.add(segmentUrl);
    }

    public void goLocation(BookLocation location) {
        this.currentLocation = new BookLocation(location.segmentIndex(), location.percent());
        String segmentUrl = segmentUrls.get(location.segmentIndex());
        currentSegment.loadUrl(segmentUrl);
        if (location.segmentIndex() < segmentUrls.size() - 1) {
            segmentUrl = segmentUrls.get(location.segmentIndex() + 1);
            nextSegment.loadUrl(segmentUrl);
        } else {
            nextSegment.loadUrl(null);
        }
        if (location.segmentIndex() > 0) {
            segmentUrl = segmentUrls.get(location.segmentIndex() - 1);
            previewSegment.loadUrl(segmentUrl);
        } else {
            previewSegment.loadUrl(null);
        }
    }

    public boolean isCurrentPageLoaded() {
        return currentSegment.isLoaded();
    }

    public boolean canGoNextPage() {
        return currentLocation.segmentIndex() < segmentUrls.size() - 1 && currentSegment.isLoaded() ||
               currentSegment.canGoNextPage();
    }

    public boolean canGoPreviewPage() {
        return currentLocation.segmentIndex() > 0 && currentSegment.isLoaded() ||
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
            if (currentLocation.segmentIndex() < segmentUrls.size() - 1) {
                String segmentUrl = segmentUrls.get(currentLocation.segmentIndex() + 1);
                nextSegment.loadUrl(segmentUrl);
            } else {
                nextSegment.loadUrl(null);
            }
            reinitSegments();
        } else {
            currentSegment.goNextPage();
        }
        currentLocation.setPercent(currentSegment.currentPercent());
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
                String segmentUrl = segmentUrls.get(currentLocation.segmentIndex() - 1);
                previewSegment.loadUrl(segmentUrl);
            } else {
                previewSegment.loadUrl(null);
            }
            reinitSegments();
        } else {
            currentSegment.goPreviewPage();
        }
        currentLocation.setPercent(currentSegment.currentPercent());
    }

    private void reinitSegments() {
        nextSegment.goPercent(LongPercent.ZERO);
        previewSegment.goPercent(LongPercent.HUNDRED);
        currentSegment.setVisibility(VISIBLE);
        previewSegment.setVisibility(INVISIBLE);
        nextSegment.setVisibility(INVISIBLE);
    }

    public void drawCurrentPage(Canvas canvas) {
        currentSegment.drawCurrentPage(canvas);
    }

    public void drawNextPage(Canvas canvas) {
        if (currentSegment.isLastPage()) {
            currentSegment.drawNextPage(canvas);
        } else {
            nextSegment.drawCurrentPage(canvas);
        }
    }

    public void drawPreviewPage(Canvas canvas) {
        if (currentSegment.isFirstPage()) {
            previewSegment.drawPreviewPage(canvas);
        } else {
            currentSegment.drawCurrentPage(canvas);
        }
    }

    public void setFontSize(float size) {
        currentSegment.setFontSize(size);
        nextSegment.setFontSize(size);
        previewSegment.setFontSize(size);
    }

    public void setLineHeight(float height) {
        currentSegment.setLineHeight(height);
        nextSegment.setLineHeight(height);
        previewSegment.setLineHeight(height);
    }
}

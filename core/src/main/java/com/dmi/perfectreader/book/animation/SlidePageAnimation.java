package com.dmi.perfectreader.book.animation;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

public class SlidePageAnimation implements PageAnimation {
    private float pageWidth = 100;
    private float timeForOnePageInSeconds = 1;

    private float destinationPageOffset = 0;

    SlidePageAnimation() {
    }

    public SlidePageAnimation(float timeForOnePageInSeconds) {
        this.timeForOnePageInSeconds = timeForOnePageInSeconds;
    }

    // t = sqrt(d)/t0
    static float computeTimeByDistance(float distanceInPages, float timeForOnePage) {
        if (distanceInPages > 0) {
            return (float) sqrt(distanceInPages) * timeForOnePage;
        } else {
            return 0;
        }
    }

    // d = (t*t0)^2
    static float computeDistanceByTime(float time, float timeForOnePage) {
        if (time > 0) {
            float a = time / timeForOnePage;
            return a * a;
        } else {
            return 0;
        }
    }

    @Override
    public void setPageWidth(float pageWidth) {
        this.pageWidth = pageWidth;
    }

    public void setTimeForOnePageInSeconds(float timeInSeconds) {
        this.timeForOnePageInSeconds = timeInSeconds;
    }

    @Override
    public boolean isPagesMoving() {
        return destinationPageOffset != 0;
    }

    float destinationPageOffset() {
        return destinationPageOffset;
    }

    void setDestinationPageOffset(float offset) {
        destinationPageOffset = offset;
    }

    @Override
    public void reset() {
        destinationPageOffset = 0;
    }

    @Override
    public void moveNext() {
        destinationPageOffset += pageWidth;
    }

    @Override
    public void movePreview() {
        destinationPageOffset -= pageWidth;
    }

    @Override
    public void update(float dt) {
        if (destinationPageOffset > 0) {
            float timeForStop = computeTimeByDistance(destinationPageOffset / pageWidth, timeForOnePageInSeconds);
            destinationPageOffset = computeDistanceByTime(timeForStop - dt, timeForOnePageInSeconds) * pageWidth;
            if (destinationPageOffset <= 0) {
                reset();
            }
        } else if (destinationPageOffset < 0) {
            float timeForStop = computeTimeByDistance(-destinationPageOffset / pageWidth, timeForOnePageInSeconds);
            destinationPageOffset = -computeDistanceByTime(timeForStop - dt, timeForOnePageInSeconds) * pageWidth;
            if (destinationPageOffset >= 0) {
                reset();
            }
        }
    }

    @Override
    public void drawPages(PageDrawer pageDrawer, float screenWidth) {
        float distanceInPages = destinationPageOffset / pageWidth;

        int firstRelativeIndex = (int) floor(-distanceInPages);
        float firstDrawingPageX = (distanceInPages - (float) ceil(distanceInPages)) * pageWidth;

        int relativeIndex = firstRelativeIndex;
        for (float pageX = firstDrawingPageX; pageX < screenWidth; pageX += pageWidth) {
            pageDrawer.drawPage(relativeIndex, pageX);
            relativeIndex++;
        }
    }
}

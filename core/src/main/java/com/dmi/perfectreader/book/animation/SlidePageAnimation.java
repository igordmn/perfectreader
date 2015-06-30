package com.dmi.perfectreader.book.animation;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class SlidePageAnimation implements PageAnimation {
    private float pageWidth = 100;
    private float timeForOneSlideInSeconds = 1;

    private PageAnimationState state = new PageAnimationState();
    private float distance = 0;
    private float velocity = 0;

    public SlidePageAnimation(float timeForOneSlideInSeconds) {
        checkArgument(timeForOneSlideInSeconds >= 0);
        this.timeForOneSlideInSeconds = timeForOneSlideInSeconds;
    }

    @Override
    public void setPageWidth(float pageWidth) {
        this.pageWidth = pageWidth;
    }

    @Override
    public boolean isAnimate() {
        return distance != 0;
    }

    @Override
    public void reset() {
        distance = 0;
        velocity = 0;
    }

    @Override
    public void moveNext() {
        distance += pageWidth;
        velocity = timeForOneSlideInSeconds > 0 ? abs(distance) / timeForOneSlideInSeconds : 1_000_000;
    }

    @Override
    public void movePreview() {
        distance -= pageWidth;
        velocity = timeForOneSlideInSeconds > 0 ? abs(distance) / timeForOneSlideInSeconds : 1_000_000;
    }

    @Override
    public void update(float dt) {
        if (distance != 0) {
            float oldDistance = distance;

            if (distance > 0) {
                distance -= velocity * dt;
            } else {
                distance += velocity * dt;
            }

            if (oldDistance > 0 && distance <= 0 || oldDistance < 0 && distance >= 0) {
                reset();
            }
        }
        updateState();
    }

    private void updateState() {
        state.clear();

        float distanceInPages = distance / pageWidth;

        int firstRelativeIndex = (int) floor(-distanceInPages);
        float firstDrawingPageX = (distanceInPages - (float) ceil(distanceInPages)) * pageWidth;

        int relativeIndex = firstRelativeIndex;
        for (float pageX = firstDrawingPageX; pageX < pageWidth; pageX += pageWidth) {
            state.add(relativeIndex, pageX);
            relativeIndex++;
        }
    }

    @Override
    public PageAnimationState state() {
        return state;
    }
}

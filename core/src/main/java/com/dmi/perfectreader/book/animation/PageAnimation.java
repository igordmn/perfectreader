package com.dmi.perfectreader.book.animation;

public interface PageAnimation {
    void setPageWidth(float pageWidth);

    boolean isAnimate();

    void reset();

    void moveNext();

    void movePreview();

    void update(float dt);

    PageAnimationState state();
}

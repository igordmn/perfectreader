package com.dmi.perfectreader.menu;

import com.dmi.perfectreader.book.BookPresenter;
import com.dmi.util.base.BasePresenter;
import com.dmi.util.lang.MathExt;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MenuPresenter extends BasePresenter {
    @Inject
    protected BookPresenter bookPresenter;
    @Inject
    protected MenuFragment view;

    public void showSettings() {
        view.close();
    }

    public void goPosition(int position, int maxPosition) {
        bookPresenter.goPercent(MathExt.clamp(0.0, 1.0, position / maxPosition));
    }

    public void requestCurrentPercent(int maxPosition) {
        view.setPosition(bookPresenter.currentPercent() * maxPosition);
    }
}

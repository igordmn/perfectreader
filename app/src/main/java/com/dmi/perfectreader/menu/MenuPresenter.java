package com.dmi.perfectreader.menu;

import com.dmi.perfectreader.book.BookPresenter;
import com.dmi.util.base.BasePresenter;
import com.dmi.util.lang.IntegerPercent;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.dmi.util.lang.IntegerPercent.valuePercent;

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
        bookPresenter.goPercent(valuePercent(position, maxPosition));
    }

    public void requestCurrentPosition(int maxPosition) {
        view.setPosition(IntegerPercent.multiply(bookPresenter.currentPercent(), maxPosition));
    }
}

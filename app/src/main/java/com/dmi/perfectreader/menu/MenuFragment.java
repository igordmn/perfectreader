package com.dmi.perfectreader.menu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.BookFragment;
import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.util.android.ExtFragment;
import com.gc.materialdesign.views.Slider;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;

import static com.dmi.perfectreader.util.android.Units.dipToPx;

@EFragment(R.layout.fragment_menu)
public class MenuFragment extends ExtFragment implements KeyEvent.Callback {
    private static final int SEEK_BAR_RESOLUTION = 1024;

    @ViewById
    protected Toolbar toolbar;
    @ViewById
    protected TextView currentChapterText;
    @ViewById
    protected TextView currentPageText;
    @ViewById
    protected ProgressWheel bottomProgressBar;
    @ViewById
    protected Slider locationSlider;

    @AfterViews
    protected void initViews() {
        initTopBar();
        initBottomBar();
    }

    private void initTopBar() {
        ViewCompat.setElevation(toolbar, dipToPx(2));
        toolbar.setTitle("Alice's Adventures in Wonderland");
        toolbar.setSubtitle("Lewis Carroll");
        toolbar.inflateMenu(R.menu.book);
        currentChapterText.setText("X — Alice's evidence");
        currentPageText.setText("302 / 2031");
    }

    private void initBottomBar() {
        final BookFragment bookFragment = bookFragment();
        locationSlider.setMax(SEEK_BAR_RESOLUTION);
        locationSlider.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                double percent = (double) value / SEEK_BAR_RESOLUTION;
                bookFragment.goLocation(bookFragment.percentToLocation(percent));
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkLocationAvailable();
    }

    @Override
    public void onDestroy() {
        BackgroundExecutor.cancelAll("checkLocationAvailable", true);
        super.onDestroy();
    }

    private void checkLocationAvailable() {
        final BookFragment bookFragment = bookFragment();
        BookLocation currentLocation = bookFragment.currentLocation();
        if (currentLocation != null) {
            onLocationAvailable(bookFragment, currentLocation);
        } else {
            bottomProgressBar.setVisibility(View.VISIBLE);
            startCheckLocationAvailable();
        }
    }

    @Background(id = "checkLocationAvailable", delay = 100)
    protected void startCheckLocationAvailable() {
        final BookFragment bookFragment = bookFragment();
        BookLocation currentLocation = bookFragment.currentLocation();
        if (currentLocation != null) {
            onLocationAvailable(bookFragment, currentLocation);
        } else {
            checkLocationAvailable();
        }
    }

    @UiThread
    protected void onLocationAvailable(BookFragment bookFragment, BookLocation currentLocation) {
        double percent = bookFragment.locationToPercent(currentLocation);
        locationSlider.setValue((int) (SEEK_BAR_RESOLUTION * percent));
        bottomProgressBar.setVisibility(View.INVISIBLE);
        locationSlider.setVisibility(View.VISIBLE);
    }

    @Click(R.id.middleSpace)
    protected void onMiddleSpaceClick() {
        closeMenu();
    }

    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeMenu();
            return true;
        }
        return false;
    }

    private void closeMenu() {
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .remove(MenuFragment.this)
                .commit();
    }

    private BookFragment bookFragment() {
        return (BookFragment) getFragmentManager().findFragmentByTag(BookFragment.class.getName());
    }
}

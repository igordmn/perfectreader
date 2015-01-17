package com.dmi.perfectreader.menu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.BookFragment;
import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.util.android.ExtFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;

@EFragment(R.layout.fragment_menu)
public class MenuFragment extends ExtFragment implements KeyEvent.Callback {
    private static final int SEEK_BAR_RESOLUTION = 1024;

    @ViewById
    protected ProgressBar downBarProgressBar;
    @ViewById
    protected SeekBar seekBar;

    @AfterViews
    protected void initViews() {
        initSeekBar();
    }

    private void initSeekBar() {
        final BookFragment bookFragment = bookFragment();
        seekBar.setMax(SEEK_BAR_RESOLUTION);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double percent = (double) seekBar.getProgress() / SEEK_BAR_RESOLUTION;
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
            downBarProgressBar.setVisibility(View.VISIBLE);
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
        downBarProgressBar.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.VISIBLE);
        double percent = bookFragment.locationToPercent(currentLocation);
        seekBar.setProgress((int) (SEEK_BAR_RESOLUTION * percent));
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
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    private BookFragment bookFragment() {
        return (BookFragment) getFragmentManager().findFragmentByTag(BookFragment.class.getName());
    }
}

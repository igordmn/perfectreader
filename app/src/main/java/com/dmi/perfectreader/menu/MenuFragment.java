package com.dmi.perfectreader.menu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.BookFragment;
import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.setting.SettingsFragment;
import com.dmi.perfectreader.setting.SettingsFragment_;
import com.dmi.perfectreader.util.android.ExtFragment;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
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
    protected ProgressWheel locationProgressBar;
    @ViewById
    protected DiscreteSeekBar locationSlider;

    @AfterViews
    protected void initViews() {
        initTopBar();
        initBottomBar();
    }

    private void initTopBar() {
        ViewCompat.setElevation(toolbar, dipToPx(2));
        toolbar.setTitle("Alice's Adventures in Wonderland");
        toolbar.setSubtitle("Lewis Carroll");
        MenuItem menuItem = toolbar.getMenu().add(R.string.bookMenuSettings);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setIcon(R.drawable.ic_settings_white_24dp);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                closeFragment();
                showSettings();
                return true;
            }
        });
        currentChapterText.setText("X — Alice's evidence");
        currentPageText.setText("302 / 2031");
    }

    private void initBottomBar() {
        final BookFragment bookFragment = bookFragment();
        locationSlider.setMax(SEEK_BAR_RESOLUTION);
        locationSlider.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    double percent = (double) progress / SEEK_BAR_RESOLUTION;
                    bookFragment.goLocation(bookFragment.percentToLocation(percent));
                }
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
            locationProgressBar.setVisibility(View.VISIBLE);
            locationSlider.setVisibility(View.INVISIBLE);
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
        locationSlider.setProgress((int) (SEEK_BAR_RESOLUTION * percent));
        locationProgressBar.setVisibility(View.INVISIBLE);
        locationSlider.setVisibility(View.VISIBLE);
    }

    @Click(R.id.middleSpace)
    protected void onMiddleSpaceClick() {
        closeFragment();
    }

    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeFragment();
            return true;
        }
        return false;
    }

    private void closeFragment() {
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .remove(this)
                .commit();
    }

    private void showSettings() {
        SettingsFragment fragment = SettingsFragment_.builder().build();
        String tag = SettingsFragment.class.getName();
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .add(R.id.mainContainer, fragment, tag)
                .commit();
    }

    private BookFragment bookFragment() {
        return (BookFragment) getFragmentManager().findFragmentByTag(BookFragment.class.getName());
    }
}

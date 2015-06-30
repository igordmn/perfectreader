package com.dmi.perfectreader.menu;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.facade.BookReaderFacade;
import com.dmi.util.FragmentExt;
import com.dmi.util.lang.IntegerPercent;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;

import static com.dmi.util.Units.dipToPx;
import static com.dmi.util.lang.IntegerPercent.valuePercent;

// todo добавить currentLocationChangeListener в BookFacade и использовать его здесь
@EFragment(R.layout.fragment_menu)
public class MenuFragment extends FragmentExt implements KeyEvent.Callback {
    private static final int SEEK_BAR_RESOLUTION = 1024;

    @ViewById
    protected Toolbar toolbar;
    @ViewById
    protected TextView currentChapterText;
    @ViewById
    protected TextView currentPageText;
    @ViewById
    protected DiscreteSeekBar locationSlider;

    private BookReaderFacade bookReader;

    @AfterViews
    protected void initViews() {
        initTopBar();
        initBottomBar();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        bookReader = getClient(BookReaderFacade.class);
    }

    @Override
    public void onDetach() {
        bookReader = null;
        super.onDetach();
    }

    private void initTopBar() {
        ViewCompat.setElevation(toolbar, dipToPx(2));
        toolbar.setTitle("Alice's Adventures in Wonderland");
        toolbar.setSubtitle("Lewis Carroll");
        MenuItem menuItem = toolbar.getMenu().add(R.string.bookMenuSettings);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setIcon(R.drawable.ic_settings_white_24dp);
        menuItem.setOnMenuItemClickListener(item -> {
            closeFragment();
            showSettings();
            return true;
        });
        currentChapterText.setText("X — Alice's evidence");
        currentPageText.setText("302 / 2031");
    }

    private void initBottomBar() {
        locationSlider.setMax(SEEK_BAR_RESOLUTION);
        locationSlider.setOnProgressChangeListener((discreteSeekBar, progress, fromUser) -> {
            if (fromUser) {
                bookReader.book().goPercent(valuePercent(progress, SEEK_BAR_RESOLUTION));
            }
        });
        locationSlider.setProgress(IntegerPercent.multiply(bookReader.book().currentPercent(), SEEK_BAR_RESOLUTION));
    }

    @Override
    public void onDestroy() {
        BackgroundExecutor.cancelAll("checkLocationAvailable", true);
        super.onDestroy();
    }

    @Click(R.id.middleSpace)
    protected void onMiddleSpaceClick() {
        closeFragment();
    }

    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            closeFragment();
        }
        return true;
    }

    private void closeFragment() {
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .remove(this)
                .commit();
    }

    private void showSettings() {
    }
}

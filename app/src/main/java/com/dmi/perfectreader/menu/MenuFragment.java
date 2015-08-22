package com.dmi.perfectreader.menu;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.BookPresenter;
import com.dmi.perfectreader.bookreader.BookReaderFragment;
import com.dmi.util.base.BaseFragment;
import com.dmi.util.layout.HasLayout;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import javax.inject.Inject;

import butterknife.Bind;
import dagger.ObjectGraph;
import dagger.Provides;

import static com.dmi.util.Units.dipToPx;

@HasLayout(R.layout.fragment_menu)
public class MenuFragment extends BaseFragment { // implements KeyEvent.Callback {
    private static final int SEEK_BAR_MAX_PROGRESS = 1024;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.currentChapterText)
    protected TextView currentChapterText;
    @Bind(R.id.currentPageText)
    protected TextView currentPageText;
    @Bind(R.id.locationSlider)
    protected DiscreteSeekBar locationSlider;
    @Bind(R.id.middleSpace)
    protected FrameLayout middleSpace;

    @Inject
    protected MenuPresenter presenter;

    @Override
    protected ObjectGraph createObjectGraph(ObjectGraph parentGraph) {
        return parentGraph.plus(new Module());
    }

    @Override
    public MenuPresenter presenter() {
        return presenter;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);
        initTopBar();
        initBottomBar();
        initMiddleSpace();
    }

    private void initTopBar() {
        ViewCompat.setElevation(toolbar, dipToPx(2));
        toolbar.setTitle("Alice's Adventures in Wonderland");
        toolbar.setSubtitle("Lewis Carroll");
        MenuItem menuItem = toolbar.getMenu().add(R.string.bookMenuSettings);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setIcon(R.drawable.ic_settings_white_24dp);
        menuItem.setOnMenuItemClickListener(item -> {
            presenter.showSettings();
            return true;
        });
        currentChapterText.setText("X — Alice's evidence");
        currentPageText.setText("302 / 2031");
    }

    private void initBottomBar() {
        locationSlider.setMax(SEEK_BAR_MAX_PROGRESS);
        locationSlider.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    presenter.goPosition(progress, SEEK_BAR_MAX_PROGRESS);
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {
            }
        });
        presenter.requestCurrentPosition(SEEK_BAR_MAX_PROGRESS);
    }

    private void initMiddleSpace() {
        middleSpace.setOnClickListener(view -> close());
    }

    public void setPosition(int position) {
        locationSlider.setProgress(position);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            close();
        }
        return true;
    }

    public void close() {
        BookReaderFragment bookReaderFragment = (BookReaderFragment) getParent();
        bookReaderFragment.toggleMenu();
    }

    @dagger.Module(addsTo = BookReaderFragment.Module.class, injects = {
            MenuFragment.class,
            MenuPresenter.class,
    })
    public class Module {
        @Provides
        public MenuFragment view() {
            return MenuFragment.this;
        }

        @Provides
        public BookPresenter bookPresenter() {
            BookReaderFragment bookReaderFragment = parentFragment();
            return bookReaderFragment.book().presenter();
        }
    }
}

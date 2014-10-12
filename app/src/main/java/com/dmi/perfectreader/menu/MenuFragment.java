package com.dmi.perfectreader.menu;

import android.app.Fragment;
import android.widget.SeekBar;

import com.dmi.perfectreader.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_menu)
public class MenuFragment extends Fragment {
    private static final int SEEK_BAR_RESOLUTION = 1024;

    @ViewById
    protected SeekBar seekBar;

    private MenuActions menuActions;

    @AfterViews
    protected void initViews() {
        seekBar.setMax(SEEK_BAR_RESOLUTION);
        seekBar.setProgress((int) (SEEK_BAR_RESOLUTION * menuActions.getPercent()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                menuActions.goPercent((double) seekBar.getProgress() / SEEK_BAR_RESOLUTION);
            }
        });
    }

    public void setMenuActions(MenuActions menuActions) {
        this.menuActions = menuActions;
    }
}

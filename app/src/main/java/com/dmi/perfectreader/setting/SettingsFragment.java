package com.dmi.perfectreader.setting;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.dmi.perfectreader.R;
import com.dmi.perfectreader.widget.ButtonFlatExt;
import com.dmi.perfectreader.widget.NonSwipeableViewPager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.util.ArrayList;

@EFragment(R.layout.fragment_settings)
public class SettingsFragment extends Fragment {
    @ViewById
    protected PagerSlidingTabStrip tabs;
    @ViewById
    protected NonSwipeableViewPager pager;

    @AfterViews
    protected void initViews() {
        final ArrayList<Setting> mainSettings = inflateSettings();
        pager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return SettingsFragment_.SubsettingsFragment_.builder()
                        .settings(mainSettings.get(position).subsettings)
                        .build();
            }

            @Override
            public int getCount() {
                return mainSettings.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mainSettings.get(position).title;
            }
        });
        tabs.setViewPager(pager);
    }

    private ArrayList<Setting> inflateSettings() {
        ArrayList<Setting> settings = new ArrayList<>();
        MenuInflater menuInflater = getActivity().getMenuInflater();
        MenuBuilder menu = new MenuBuilder(getActivity());
        menuInflater.inflate(R.menu.settings, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            String title = item.getTitle().toString();
            SubMenu subMenu = item.getSubMenu();
            Setting setting = new Setting(title);
            if (subMenu != null) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subItem = subMenu.getItem(j);
                    String subTitle = subItem.getTitle().toString();
                    setting.subsettings.add(new Setting(subTitle));
                }
            }
            settings.add(setting);
        }
        return settings;
    }

    @Click(R.id.middleSpace)
    protected void onMiddleSpaceClick() {
        closeFragment();
    }

    private void closeFragment() {
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .remove(this)
                .commit();
    }

    @EFragment(R.layout.fragment_settings_subsettings)
    public static class SubsettingsFragment extends Fragment {
        @FragmentArg
        protected ArrayList<Setting> settings;

        @ViewById
        protected LinearLayout subsettingsLayout;

        @AfterViews
        protected void initViews() {
            for (Setting setting : settings) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                ButtonFlatExt button = (ButtonFlatExt)
                        layoutInflater.inflate(R.layout.fragment_settings_subsettings_button, subsettingsLayout, false);
                button.setText(setting.title);
                subsettingsLayout.addView(button);
            }
        }
    }

    protected static class Setting implements Serializable {
        public final String title;
        public final ArrayList<Setting> subsettings = new ArrayList<>();

        private Setting(String title) {
            this.title = title;
        }
    }
}

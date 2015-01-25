package com.dmi.perfectreader.util.android;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;

import java.util.List;
import java.util.ListIterator;

public class ActionBarActivityExt extends ActionBarActivity {
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        ListIterator<Fragment> iterator = fragments.listIterator(fragments.size());
        while (iterator.hasPrevious()) {
            Fragment fragment = iterator.previous();
            if (fragment instanceof FragmentExt) {
                FragmentExt fragmentExt = (FragmentExt) fragment;
                boolean handled = fragmentExt.onKeyDown(keyCode, event);
                if (handled) {
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        ListIterator<Fragment> iterator = fragments.listIterator(fragments.size());
        while (iterator.hasPrevious()) {
            Fragment fragment = iterator.previous();
            if (fragment instanceof FragmentExt) {
                FragmentExt fragmentExt = (FragmentExt) fragment;
                boolean handled = fragmentExt.onKeyUp(keyCode, event);
                if (handled) {
                    return true;
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        ListIterator<Fragment> iterator = fragments.listIterator(fragments.size());
        while (iterator.hasPrevious()) {
            Fragment fragment = iterator.previous();
            if (fragment instanceof FragmentExt) {
                FragmentExt fragmentExt = (FragmentExt) fragment;
                boolean handled = fragmentExt.onKeyLongPress(keyCode, event);
                if (handled) {
                    return true;
                }
            }
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        ListIterator<Fragment> iterator = fragments.listIterator(fragments.size());
        while (iterator.hasPrevious()) {
            Fragment fragment = iterator.previous();
            if (fragment instanceof FragmentExt) {
                FragmentExt fragmentExt = (FragmentExt) fragment;
                boolean handled = fragmentExt.onKeyMultiple(keyCode, repeatCount, event);
                if (handled) {
                    return true;
                }
            }
        }
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }
}

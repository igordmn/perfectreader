package com.dmi.util;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

public class FragmentExt extends Fragment implements KeyEvent.Callback {
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    protected final <T> T getClient(Class<T> parentClass) {
        Fragment parentFragment = getParentFragment();
        FragmentActivity parentActivity = getActivity();
        if (parentClass.isInstance(parentFragment)) {
            return (T) parentFragment;
        } else if (parentClass.isInstance(parentActivity)) {
            return (T) parentActivity;
        }
        return null;
    }
}

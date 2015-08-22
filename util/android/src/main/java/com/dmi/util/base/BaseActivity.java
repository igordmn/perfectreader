package com.dmi.util.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.dmi.util.layout.HasLayout;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentAppCompatActivity;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.key.LayoutKey;

import static com.dmi.util.base.BaseUtils.handleAttachedFragments;

public class BaseActivity extends SimpleFragmentAppCompatActivity {
    private final int layoutId;

    protected BaseActivity() {
        HasLayout hasLayout = getClass().getAnnotation(HasLayout.class);
        this.layoutId = hasLayout != null ? hasLayout.value() : 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (layoutId > 0) {
            setContentView(layoutId);
        }
    }

    protected void inject(Object... modules) {
        BaseApplication baseApplication = (BaseApplication) getApplication();
        baseApplication.objectGraph().plus(modules).inject(this);
    }

    protected <T extends SimpleFragment> T findChild(int containerId) {
        return getSimpleFragmentManager().find(LayoutKey.of(containerId));
    }

    protected <T extends SimpleFragment> T addChild(Class<T> fragmentClass, int containerId) {
        return getSimpleFragmentManager().add(SimpleFragmentIntent.of(fragmentClass), LayoutKey.of(containerId));
    }

    protected <T extends SimpleFragment> T addChild(SimpleFragmentIntent<T> intent, int containerId) {
        return getSimpleFragmentManager().add(intent, LayoutKey.of(containerId));
    }

    protected <T extends SimpleFragment> T findOrAddChild(Class<T> fragmentClass, int containerId) {
        return getSimpleFragmentManager().findOrAdd(SimpleFragmentIntent.of(fragmentClass), LayoutKey.of(containerId));
    }

    protected <T extends SimpleFragment> T findOrAddChild(SimpleFragmentIntent<T> intent, int containerId) {
        return getSimpleFragmentManager().findOrAdd(intent, LayoutKey.of(containerId));
    }

    protected void removeChild(int containerId) {
        getSimpleFragmentManager().remove(getSimpleFragmentManager().find(LayoutKey.of(containerId)));
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        return handleAttachedFragments(this, fragment -> fragment.onKeyDown(keyCode, event)) ||
               super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        return handleAttachedFragments(this, fragment -> fragment.onKeyUp(keyCode, event)) ||
               super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return handleAttachedFragments(this, fragment -> fragment.onKeyLongPress(keyCode, event)) ||
               super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return handleAttachedFragments(this, fragment -> fragment.onKeyMultiple(keyCode, repeatCount, event)) ||
               super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (SimpleFragment fragment : getSimpleFragmentManager().getFragments()) {
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).resume();
            }
        }
    }

    @Override
    protected void onPause() {
        for (SimpleFragment fragment : getSimpleFragmentManager().getFragments()) {
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).pause();
            }
        }
        super.onPause();
    }
}

package com.dmi.util.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmi.util.layout.HasLayout;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import icepick.Icepick;
import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.key.LayoutKey;

import static com.dmi.util.base.BaseUtils.handleAttachedFragments;

public abstract class BaseFragment extends SimpleFragment implements KeyEvent.Callback {
    private final int layoutId;

    private ObjectGraph objectGraph;

    private boolean pause = true;

    protected BaseFragment() {
        HasLayout hasLayout = getClass().getAnnotation(HasLayout.class);
        this.layoutId = hasLayout != null ? hasLayout.value() : 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
        Icepick.restoreInstanceState(this, state);
        objectGraph = createObjectGraph(parentObjectGraph());
        if (objectGraph != null) {
            objectGraph.inject(this);
        }
        if (presenter() != null) {
            presenter().onCreate();
        }
    }

    @Override
    public void onDestroy() {
        if (presenter() != null) {
            presenter().onDestroy();
        }
        super.onDestroy();
    }

    protected ObjectGraph createObjectGraph(ObjectGraph parentGraph) {
        return null;
    }

    protected ObjectGraph objectGraph() {
        return objectGraph;
    }

    protected ObjectGraph parentObjectGraph() {
        BaseFragment parentFragment = (BaseFragment) getParentFragment();
        BaseApplication application = (BaseApplication) getActivity().getApplication();
        return parentFragment != null ? parentFragment.objectGraph() : application.objectGraph();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutId > 0 ? layoutInflater.inflate(layoutId, viewGroup, false) : null;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        ButterKnife.bind(this, view);
        resume();
    }

    @Override
    public void onViewDestroyed(@NonNull View view) {
        pause();
        super.onViewDestroyed(view);
    }

    @Override
    public void onSave(@NonNull Bundle state) {
        Icepick.saveInstanceState(this, state);
    }

    protected BasePresenter presenter() {
        return null;
    }

    @SuppressWarnings("unchecked")
    protected <T> T parentFragment() {
        return (T) getParentFragment();
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

    public void onResume() {
    }

    public void onPause() {
    }

    void resume() {
        if (pause) {
            onResume();
            pause = false;
        }
    }

    void pause() {
        if (!pause) {
            onPause();
            pause = true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        return handleAttachedFragments(this, fragment -> fragment.onKeyDown(keyCode, event));
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        return handleAttachedFragments(this, fragment -> fragment.onKeyUp(keyCode, event));
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return handleAttachedFragments(this, fragment -> fragment.onKeyLongPress(keyCode, event));
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return handleAttachedFragments(this, fragment -> fragment.onKeyMultiple(keyCode, repeatCount, event));
    }
}

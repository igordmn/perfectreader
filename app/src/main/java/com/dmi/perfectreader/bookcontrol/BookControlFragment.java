package com.dmi.perfectreader.bookcontrol;

import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.BookPresenter;
import com.dmi.perfectreader.bookreader.BookReaderFragment;
import com.dmi.util.base.BaseFragment;
import com.dmi.util.layout.HasLayout;

import javax.inject.Inject;

import butterknife.Bind;
import dagger.ObjectGraph;
import dagger.Provides;

@HasLayout(R.layout.fragment_bookcontrol)
public class BookControlFragment extends BaseFragment implements View.OnTouchListener {
    @Bind(R.id.spaceView)
    protected FrameLayout spaceView;

    @Inject
    protected BookControlPresenter presenter;

    private final TouchInfo touchInfo = new TouchInfo();

    @Override
    protected ObjectGraph createObjectGraph(ObjectGraph parentGraph) {
        return parentGraph.plus(new Module());
    }

    @Override
    public BookControlPresenter presenter() {
        return presenter;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);
        spaceView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        touchInfo.width = spaceView.getWidth();
        touchInfo.height = spaceView.getHeight();
        touchInfo.x = event.getX();
        touchInfo.y = event.getY();
        touchInfo.touchDiameter = event.getTouchMajor();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                presenter.onTouchDown(touchInfo);
                break;
            case MotionEvent.ACTION_MOVE:
                presenter.onTouchMove(touchInfo);
                break;
            case MotionEvent.ACTION_UP:
                presenter.onTouchUp(touchInfo);
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        presenter().onKeyDown(HardKey.fromKeyCode(keyCode));
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        return true;
    }

    @dagger.Module(addsTo = BookReaderFragment.Module.class, injects = {
            BookControlFragment.class,
            BookControlPresenter.class,
    })
    public class Module {
        @Provides
        public BookControlFragment view() {
            return BookControlFragment.this;
        }

        @Provides
        public BookPresenter bookPresenter() {
            BookReaderFragment bookReaderFragment = parentFragment();
            return bookReaderFragment.book().presenter();
        }
    }
}

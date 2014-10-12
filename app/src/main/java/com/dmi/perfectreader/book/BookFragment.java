package com.dmi.perfectreader.book;

import android.app.Fragment;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.asset.AssetPaths;
import com.dmi.perfectreader.book.animation.SlidePageAnimation;
import com.dmi.perfectreader.book.font.FreetypeLibrary;
import com.dmi.perfectreader.book.format.FormatConfig;
import com.dmi.perfectreader.book.format.PageConfig;
import com.dmi.perfectreader.book.module.PageBookModule;
import com.dmi.perfectreader.book.pagebook.PageBook;
import com.dmi.perfectreader.book.position.Position;
import com.dmi.perfectreader.util.android.Units;
import com.dmi.perfectreader.util.opengl.DeltaTimeRendererAdapter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.File;

@EFragment(R.layout.fragment_book)
public class BookFragment extends Fragment implements View.OnTouchListener {
    private final static float TIME_FOR_ONE_PAGE_IN_SECONDS = 1;
    private final static float TOUCH_SENSITIVITY = 8;
    private final static int MAX_RELATIVE_INDEX = 3;

    @FragmentArg
    protected File bookFile;
    @FragmentArg
    protected Position bookPosition;
    @ViewById
    protected GLSurfaceView glSurfaceView;
    @Bean
    protected AssetPaths assetPaths;

    private View.OnClickListener onClickListener;

    private float touchSensitivityInPixels;
    private AndroidPageBookView bookView;
    private PageBook pageBook;
    private FreetypeLibrary freetypeLibrary;

    private float touchDownX;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void goPosition(Position position) {
        pageBook.goPosition(position);
    }

    public Position position() {
        return pageBook.position();
    }

    @AfterInject
    protected void init() {
        Units u = new Units(getActivity());
        freetypeLibrary = new FreetypeLibrary(assetPaths.fontsPath(), u.displayDpi(), u.displayDpi());
    }

    @AfterViews
    protected void initViews() {
        bookView = new AndroidPageBookView(getActivity(), freetypeLibrary,
                new SlidePageAnimation(TIME_FOR_ONE_PAGE_IN_SECONDS), MAX_RELATIVE_INDEX);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new DeltaTimeRendererAdapter(glSurfaceView, bookView));
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        glSurfaceView.setOnTouchListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        touchSensitivityInPixels = new Units(getActivity()).dipToPx(TOUCH_SENSITIVITY);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Units u = new Units(getActivity());
        FormatConfig formatConfig = new FormatConfig(u.dipToPx(10), u.dipToPx(4));
        PageConfig pageConfig = new PageConfig(width, height, u.dipToPx(8), u.dipToPx(8), u.dipToPx(8), u.dipToPx(8));

        PageBookModule pageBookModule = new PageBookModule()
                .bookFile(bookFile)
                .formatConfig(formatConfig)
                .pageConfig(pageConfig)
                .freetypeLibrary(freetypeLibrary)
                .maxRelativeIndex(MAX_RELATIVE_INDEX)
                .bookView(bookView)
                .build();
        pageBook = pageBookModule.pageBook();

        pageBook.goPosition(bookPosition);
    }

    @Override
    public void onDestroy() {
        freetypeLibrary.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onTouchDown(event);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            onTouchUp(event);
            v.performClick();
            return true;
        }
        return false;
    }

    private void onTouchDown(MotionEvent motionEvent) {
        touchDownX = motionEvent.getX();
    }

    private void onTouchUp(MotionEvent motionEvent) {
        float touchOffset = motionEvent.getX() - touchDownX;
        boolean touchLeft = touchOffset <= -touchSensitivityInPixels;
        boolean touchRight = touchOffset >= touchSensitivityInPixels;

        if (touchLeft) {
            pageBook.tryGoNext();
        } else if (touchRight) {
            pageBook.tryGoPreview();
        } else {
            onClickListener.onClick(glSurfaceView);
        }
    }
}

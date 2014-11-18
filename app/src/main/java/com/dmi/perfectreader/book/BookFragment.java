package com.dmi.perfectreader.book;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.asset.AssetPaths;
import com.dmi.perfectreader.book.animation.SlidePageAnimation;
import com.dmi.perfectreader.book.position.Position;
import com.dmi.perfectreader.util.android.Units;
import com.dmi.perfectreader.util.lang.LongPercent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_book)
public class BookFragment extends Fragment {
    private final static float TIME_FOR_ONE_PAGE_IN_SECONDS = 1;
    private final static float TOUCH_SENSITIVITY = 8;

    @FragmentArg
    protected File bookFile;
    @FragmentArg
    protected Position bookPosition;
    @ViewById
    protected PageBookView pageBookView;
    @ViewById
    protected PageAnimationView pageAnimationView;
    @Bean
    protected AssetPaths assetPaths;

    private View.OnClickListener onClickListener;

    private float touchSensitivityInPixels;

    private float touchDownX;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @AfterViews
    protected void initViews() {
        pageAnimationView.setPageAnimation(new SlidePageAnimation(TIME_FOR_ONE_PAGE_IN_SECONDS));
        pageBookView.setPageAnimationView(pageAnimationView);
    }

    public void goPosition(Position position) {
    }

    public Position position() {
        return Position.BEGIN;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        touchSensitivityInPixels = new Units(getActivity()).dipToPx(TOUCH_SENSITIVITY);

        List<String> files = new ArrayList<>();
        files.add("file:///android_asset/testBook/content_1m.html");
        files.add("file:///android_asset/testBook/content_2m.html");
        files.add("file:///android_asset/testBook/content_3m.html");
        files.add("file:///android_asset/testBook/content_4m.html");
        pageBookView.setBookData(new BookData(files));
        pageBookView.goLocation(new BookLocation(0, LongPercent.ZERO));
    }

    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (pageBookView.canGoPreviewPage()) {
                pageBookView.goPreviewPage();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (pageBookView.canGoNextPage()) {
                pageBookView.goNextPage();
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP;
    }
}

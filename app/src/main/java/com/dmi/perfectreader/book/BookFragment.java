package com.dmi.perfectreader.book;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
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
import org.readium.sdk.android.Container;
import org.readium.sdk.android.EPub3;
import org.readium.sdk.android.Package;
import org.readium.sdk.android.SpineItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.dmi.perfectreader.util.zip.ZipUtls.unzipFiles;

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

        loadBook();
    }

    private void loadBook() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                File bookDir = unzipBook(getActivity(), bookFile);
                final List<String> files = getBookFiles(bookDir, bookFile);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pageBookView.setBookData(new BookData(files));
                        pageBookView.goLocation(new BookLocation(0, LongPercent.ZERO));
                    }
                });
                return null;
            }
        }.execute();
    }

    private static File unzipBook(Context context, File bookFile) {
        File cacheDir = new File(context.getExternalCacheDir(), "book");
        File bookCacheDir = new File(cacheDir, "defaultBook");
        deleteDirectory(bookCacheDir);
        try {
            unzipFiles(bookFile, bookCacheDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bookCacheDir;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return(directory.delete());
    }

    private static List<String> getBookFiles(File bookDir, File bookFile) {
        List<String> files = new ArrayList<>();

        Container container = EPub3.openBook(bookFile.getAbsolutePath());
        try {
            Package pack = container.getDefaultPackage();
            File baseDir = new File(bookDir, pack.getBasePath());
            for (SpineItem spineItem : pack.getSpineItems()) {
                File file = new File(baseDir, spineItem.getHref());
                files.add("file://" + file.getAbsolutePath());
            }
        } finally {
            EPub3.closeBook(container);
        }
        return files;
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

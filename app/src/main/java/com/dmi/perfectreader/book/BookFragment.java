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
import com.dmi.perfectreader.html.HtmlBookTransformer;
import com.dmi.perfectreader.util.android.Units;
import com.dmi.perfectreader.util.lang.LongPercent;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
                List<File> segmentFiles = getSegmentFiles(bookFile);
                File bookDir = unzipBook(getActivity(), bookFile, segmentFiles);
                final List<String> segmentUrls = toUrls(bookDir, segmentFiles);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pageBookView.setBookData(new BookData(segmentUrls));
                        pageBookView.goLocation(new BookLocation(0, LongPercent.ZERO));
                    }
                });

                return null;
            }
        }.execute();
    }

    private File unzipBook(Context context, File bookFile, List<File> segmentFiles) {
        File cacheDir = new File(context.getExternalCacheDir(), "book");
        File bookCacheDir = new File(cacheDir, "defaultBook");
        deleteDirectory(bookCacheDir);
        try {
            unzipFiles(bookFile, bookCacheDir, segmentFiles);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bookCacheDir;
    }

    private void unzipFiles(File zipFile, File outputFolder, List<File> segmentFiles) throws IOException {
        Set<File> segmentFilesSet = new HashSet<>(segmentFiles);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
                String localFileName = entry.getName();
                File localFile = new File(localFileName);
                File newFile = new File(outputFolder, localFileName);

                Files.createParentDirs(newFile);

                if (segmentFilesSet.contains(localFile)) {
                    try (OutputStream fos = new FileOutputStream(newFile)) {
                        new HtmlBookTransformer().transform(zis, fos);
                    }
                } else {
                    try (OutputStream fos = new FileOutputStream(newFile)) {
                        ByteStreams.copy(zis, fos);
                    }
                }
            }
        }
    }

    private static List<String> toUrls(File rootDirectory, List<File> localFiles) {
        List<String> urls = new ArrayList<>();
        for (File localFile : localFiles) {
            urls.add("file://" + new File(rootDirectory, localFile.getAbsolutePath()).getAbsolutePath());
        }
        return urls;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }

    private static List<File> getSegmentFiles(File bookFile) {
        List<File> files = new ArrayList<>();
        Container container = EPub3.openBook(bookFile.getAbsolutePath());
        try {
            Package pack = container.getDefaultPackage();
            File baseDir = new File(pack.getBasePath());
            for (SpineItem spineItem : pack.getSpineItems()) {
                files.add(new File(baseDir, spineItem.getHref()));
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

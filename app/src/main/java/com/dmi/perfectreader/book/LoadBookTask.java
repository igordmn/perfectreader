package com.dmi.perfectreader.book;

import android.app.Activity;
import android.os.AsyncTask;

import com.dmi.perfectreader.epub.EpubBookExtractor;
import com.dmi.perfectreader.html.HtmlBookTransformer;
import com.dmi.perfectreader.util.lang.LongPercent;

import java.io.File;
import java.util.List;

class LoadBookTask extends AsyncTask<Void, Void, Void> {
    private Activity activity;
    private File bookFile;
    private PageBookView pageBookView;
    private BookStorage bookStorage;

    LoadBookTask(Activity activity, File bookFile, PageBookView pageBookView, BookStorage bookStorage) {
        this.activity = activity;
        this.bookFile = bookFile;
        this.pageBookView = pageBookView;
        this.bookStorage = bookStorage;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            File bookCacheDir = bookCacheDir();
            bookStorage.setStorageFolder(bookCacheDir);
            HtmlBookTransformer htmlBookTransformer = new HtmlBookTransformer();
            htmlBookTransformer.setInitScriptUrlInjection("javabridge://initscript");
            EpubBookExtractor epubBookExtractor = new EpubBookExtractor(htmlBookTransformer);
            final List<String> segmentUrls = epubBookExtractor.extract(bookFile, bookCacheDir);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pageBookView.setBookData(new BookData(segmentUrls));
                    pageBookView.goLocation(new BookLocation(0, LongPercent.ZERO));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private File bookCacheDir() {
        File cacheDir = new File(activity.getExternalCacheDir(), "book");
        return new File(cacheDir, "defaultBook");
    }
}

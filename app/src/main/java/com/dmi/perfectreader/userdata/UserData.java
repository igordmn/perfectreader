package com.dmi.perfectreader.userdata;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.db.Databases;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.File;

@EBean(scope = EBean.Scope.Singleton)
public class UserData {
    @Bean
    protected Databases databases;

    public File loadLastBookFile() {
        SQLiteDatabase db = databases.userData().getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT path FROM lastBook", null)) {
            if (cursor.moveToFirst()) {
                String path = cursor.getString(0);
                return new File(path);
            } else {
                return null;
            }
        }
    }

    @Background
    public void saveLastBookFile(File bookFile) {
        SQLiteDatabase db = databases.userData().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("path", bookFile.getAbsolutePath());
        db.insertWithOnConflict("lastBook", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public BookLocation loadBookLocation(File bookFile) {
        SQLiteDatabase db = databases.userData().getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT segmentIndex, percent FROM bookLocation WHERE path = ?", new String[]{bookFile.getAbsolutePath()})) {
            if (cursor.moveToFirst()) {
                int segmentIndex = cursor.getInt(0);
                int percent = cursor.getInt(1);
                return new BookLocation(segmentIndex, percent);
            } else {
                return null;
            }
        }
    }

    @Background
    public void saveBookLocation(File bookFile, BookLocation location) {
        SQLiteDatabase db = databases.userData().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("path", bookFile.getAbsolutePath());
        values.put("segmentIndex", location.segmentIndex());
        values.put("percent", location.percent());
        db.insertWithOnConflict("bookLocation", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
}

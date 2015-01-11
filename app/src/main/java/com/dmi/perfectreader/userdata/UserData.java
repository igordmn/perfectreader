package com.dmi.perfectreader.userdata;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.db.Databases;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.File;

@EBean(scope = EBean.Scope.Singleton)
public class UserData {
    @Bean
    protected Databases databases;

    public BookLocation loadBookLocation(File bookFile) {
        SQLiteDatabase cacheDB = databases.userData().getReadableDatabase();
        try (Cursor cursor = cacheDB.rawQuery("SELECT segmentIndex, percent FROM bookLocation WHERE path = ?", new String[]{bookFile.getAbsolutePath()})) {
            if (cursor.moveToFirst()) {
                int segmentIndex = cursor.getInt(0);
                int percent = cursor.getInt(1);
                return new BookLocation(segmentIndex, percent);
            } else {
                return null;
            }
        }
    }

    public void saveBookLocation(File bookFile, BookLocation location) {
        SQLiteDatabase cacheDB = databases.userData().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("path", bookFile.getAbsolutePath());
        values.put("segmentIndex", location.segmentIndex());
        values.put("percent", location.percent());
        cacheDB.insertWithOnConflict("bookLocation", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
}

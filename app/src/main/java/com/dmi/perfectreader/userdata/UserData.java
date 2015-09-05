package com.dmi.perfectreader.userdata;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class UserData {
    @Inject
    @Named("userDatabase")
    protected SQLiteDatabase userDatabase;

    @SuppressLint("NewApi")
    public File loadLastBookFile() {
        try (Cursor cursor = userDatabase.rawQuery("SELECT path FROM lastBook where id = 1", null)) {
            if (cursor.moveToFirst()) {
                String path = cursor.getString(0);
                return new File(path);
            } else {
                return null;
            }
        }
    }

    public void saveLastBookFile(File bookFile) {
        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("path", bookFile.getAbsolutePath());
        userDatabase.insertWithOnConflict("lastBook", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @SuppressLint("NewApi")
    public Integer loadBookLocation(File bookFile) {
        try (Cursor cursor = userDatabase.rawQuery("SELECT integerPercent FROM bookLocation WHERE path = ?", new String[]{bookFile.getAbsolutePath()})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            } else {
                return null;
            }
        }
    }

    public void saveBookLocation(File bookFile, int integerPercent) {
        ContentValues values = new ContentValues();
        values.put("path", bookFile.getAbsolutePath());
        values.put("integerPercent", integerPercent);
        userDatabase.insertWithOnConflict("bookLocation", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
}

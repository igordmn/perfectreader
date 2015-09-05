package com.dmi.perfectreader.userdata;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dmi.perfectreader.db.Databases;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserData {
    @Inject
    protected Databases databases;

    @SuppressLint("NewApi")
    public File loadLastBookFile() {
        SQLiteDatabase db = databases.user();
        try (Cursor cursor = db.rawQuery("SELECT path FROM lastBook where id = 1", null)) {
            if (cursor.moveToFirst()) {
                String path = cursor.getString(0);
                return new File(path);
            } else {
                return null;
            }
        }
    }

    public void saveLastBookFile(File bookFile) {
        SQLiteDatabase db = databases.user();
        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("path", bookFile.getAbsolutePath());
        db.insertWithOnConflict("lastBook", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @SuppressLint("NewApi")
    public Integer loadBookLocation(File bookFile) {
        SQLiteDatabase db = databases.user();
        try (Cursor cursor = db.rawQuery("SELECT integerPercent FROM bookLocation WHERE path = ?", new String[]{bookFile.getAbsolutePath()})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            } else {
                return null;
            }
        }
    }

    public void saveBookLocation(File bookFile, int integerPercent) {
        SQLiteDatabase db = databases.user();
        ContentValues values = new ContentValues();
        values.put("path", bookFile.getAbsolutePath());
        values.put("integerPercent", integerPercent);
        db.insertWithOnConflict("bookLocation", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
}

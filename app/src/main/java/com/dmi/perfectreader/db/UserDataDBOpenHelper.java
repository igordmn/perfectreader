package com.dmi.perfectreader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDataDBOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    public UserDataDBOpenHelper(Context context) {
        super(context, "userData", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bookLocation (path TEXT PRIMARY KEY NOT NULL,\n" +
                   "                           segmentIndex INTEGER NOT NULL,\n" +
                   "                           percent INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

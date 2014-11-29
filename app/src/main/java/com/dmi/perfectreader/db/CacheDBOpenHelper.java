package com.dmi.perfectreader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// todo найти библиотеку для миграции с помощью sql скриптов
public class CacheDBOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    public CacheDBOpenHelper(Context context) {
        super(context, "cache", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // todo использовать механизм миграции
        db.execSQL("CREATE TABLE bookResource (key TEXT PRIMARY KEY NOT NULL,\n" +
                   "                           uuid TEXT NOT NULL,\n" +
                   "                           size INTEGER NOT NULL,\n" +
                   "                           lastAccess INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

package com.dmi.perfectreader.cache;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dmi.perfectreader.db.Databases;
import com.dmi.util.cache.DiskDataCache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

// todo не использовать БД для хранения ключей. очистку кэша производить по файлам, находящимся в папках. напрямую читая размер и время последнего доступа
@Singleton
public class BookResourceCache extends DiskDataCache {
    public static final String CACHE_FOLDER = "bookResource";
    private static final int MAX_SIZE = 32 * 1024 * 1024; // 32 MB

    @Inject
    protected Databases databases;

    @Inject
    public BookResourceCache(@Named("applicationContext") Context context) {
        super(MAX_SIZE);
        setCachePath(new File(context.getCacheDir(), CACHE_FOLDER));
    }

    @SuppressLint("NewApi")
    @Override
    protected String fetchUUID(String key) {
        SQLiteDatabase cacheDB = databases.cache().getReadableDatabase();
        try (Cursor cursor = cacheDB.rawQuery("SELECT uuid FROM bookResource WHERE key = ?", new String[]{key})) {
            return cursor.moveToFirst() ? cursor.getString(0) : null;
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected List<SizeEntry> sizeEntriesOrderByLastAccess() {
        ArrayList<SizeEntry> sizeEntries = new ArrayList<>();
        SQLiteDatabase cacheDB = databases.cache().getReadableDatabase();
        try (Cursor cursor = cacheDB.rawQuery("SELECT uuid, size FROM bookResource ORDER BY lastAccess DESC", null)) {
            if (cursor.moveToFirst()) {
                do {
                    String uuid = cursor.getString(0);
                    long size = cursor.getLong(1);
                    sizeEntries.add(new SizeEntry(uuid, size));
                } while (cursor.moveToNext());
            }
        }
        return sizeEntries;
    }

    @Override
    protected void deleteRecordsFromIndexOrderByLastAccess(int index) {
        SQLiteDatabase cacheDB = databases.cache().getWritableDatabase();
        cacheDB.execSQL("DELETE FROM bookResource WHERE key IN (SELECT key FROM bookResource WHERE rowid > ? ORDER BY lastAccess DESC)",
                new Object[]{ (long) index });
    }

    @Override
    protected void insertRecord(String key, String uuid, long size, long lastAccess) {
        SQLiteDatabase cacheDB = databases.cache().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("uuid", uuid);
        values.put("size", size);
        values.put("lastAccess", lastAccess);
        cacheDB.insertOrThrow("bookResource", null, values);
    }

    @Override
    protected void updateRecord(String key, String uuid, long size, long lastAccess) {
        SQLiteDatabase cacheDB = databases.cache().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uuid", uuid);
        values.put("size", size);
        values.put("lastAccess", lastAccess);
        cacheDB.update("bookResource", values, "key = ?", new String[]{key});
    }
}

package com.dmi.util.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.sort;

public abstract class DatabaseUpgrades {
    public static void upgradeDatabase(Context context, SQLiteDatabase database, String scriptsAssetsPath) throws DowngradeException {
        database.beginTransaction();
        try {
            NewScripts newScripts = getNewScripts(context, database.getVersion(), scriptsAssetsPath);
            if (newScripts.version < database.getVersion()) {
                throw new DowngradeException(
                        format("Database cannot be downgraded. Database version: %s, scripts version: %s",
                               database.getVersion(),
                               newScripts.version)
                );
            }
            applyScripts(context, database, newScripts.paths);
            database.setVersion(newScripts.version);
            database.setTransactionSuccessful();
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Upgrade database error", e);
        } finally {
            database.endTransaction();
        }
    }

    private static NewScripts getNewScripts(Context context, int currentVersion, String scriptsAssetsPath) throws IOException {
        NewScripts newScripts = new NewScripts();
        String[] scripts = context.getAssets().list(scriptsAssetsPath);
        sort(scripts);
        for (String script : scripts) {
            if (script.endsWith(".sql")) {
                String scriptName = script.substring(0, script.indexOf('.'));
                int scriptVersion = Integer.parseInt(scriptName);
                if (scriptVersion > currentVersion) {
                    newScripts.paths.add(scriptsAssetsPath + '/' + script);
                }
                newScripts.version = scriptVersion;
            }
        }
        return newScripts;
    }

    private static void applyScripts(Context context, SQLiteDatabase database, List<String> scriptPaths) throws IOException {
        for (String scriptPath : scriptPaths) {
            for (String sqlCommand : readSQLCommands(context, scriptPath)) {
                database.execSQL(sqlCommand);
            }
        }
    }

    @SuppressLint("NewApi")
    private static List<String> readSQLCommands(Context context, String scriptPath) throws IOException {
        try (InputStream is = context.getAssets().open(scriptPath)) {
            return CharStreams.readLines(new InputStreamReader(is, Charsets.UTF_8));
        }
    }

    private static class NewScripts {
        public final List<String> paths = new ArrayList<>();
        public int version;
    }

    public static class DowngradeException extends Exception {
        public DowngradeException(String detailMessage) {
            super(detailMessage);
        }
    }
}

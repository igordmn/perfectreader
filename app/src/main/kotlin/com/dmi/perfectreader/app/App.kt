package com.dmi.perfectreader.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.dmi.perfectreader.BuildConfig
import com.dmi.perfectreader.bookstorage.EPUBBookStorage
import com.dmi.perfectreader.cache.BookResourceCache
import com.dmi.perfectreader.db.AppDatabases
import com.dmi.perfectreader.setting.AppSettings
import com.dmi.perfectreader.userdata.UserData
import com.dmi.util.Units
import com.dmi.util.base.BaseApplication
import com.dmi.util.log.DebugLog
import com.dmi.util.log.Log
import com.dmi.util.log.ReleaseLog
import com.dmi.util.log.TimberLog
import dagger.ObjectGraph
import dagger.Provides
import javax.inject.Inject
import javax.inject.Named

class App : BaseApplication() {
    @Inject
    protected lateinit var databases: AppDatabases

    override fun createObjectGraph(): ObjectGraph {
        return ObjectGraph.create()
    }

    override fun onCreate() {
        super.onCreate()
        initLogging()
        initDatabases()
        initUnits()
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Log.init(
                    DebugLog(TimberLog())
            )
        } else {
            Log.init(
                    ReleaseLog(TimberLog())
            )
        }
    }

    private fun initDatabases() {
//        databases.init()
    }

    private fun initUnits() {
        Units.init(this)
    }

    @dagger.Module(library = true, injects = arrayOf(App::class, AppSettings::class, AppDatabases::class, EPUBBookStorage::class, BookResourceCache::class, UserData::class))
    inner class Module {
        @Provides
        @Named("applicationContext")
        fun context(): Context {
            return this@App
        }

        @Provides
        @Named("userDatabase")
        fun userDatabase(databases: AppDatabases): SQLiteDatabase {
            return databases.user()
        }
    }
}

package com.dmi.perfectreader.app

import android.app.Application
import android.content.Context

class App : Application() {
    lateinit var objects: AppObjects
        private set

    override fun onCreate() {
        super.onCreate()
        initAndroidPlatform(this)
        objects = AppObjects(this)
    }
}

val Context.app: App get() = applicationContext as App
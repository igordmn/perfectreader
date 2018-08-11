package com.dmi.perfectreader

import android.app.Application
import android.content.Context

class App : Application() {
    lateinit var objects: AppObjects
        private set

    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("utilAndroid")
        initAndroidPlatform(this)
        objects = AppObjects(this)
    }
}

val Context.app: App get() = applicationContext as App
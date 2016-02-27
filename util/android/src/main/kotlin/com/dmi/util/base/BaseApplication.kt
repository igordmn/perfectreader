package com.dmi.util.base

import android.app.Application

import dagger.ObjectGraph

open class BaseApplication : Application() {
    private lateinit var objectGraph: ObjectGraph

    @SuppressWarnings("unchecked")
    override fun onCreate() {
        super.onCreate()
//        objectGraph = createObjectGraph()
//        objectGraph.inject(this)
    }

    protected open fun createObjectGraph(): ObjectGraph {
        return ObjectGraph.create()
    }

    fun objectGraph(): ObjectGraph {
        return objectGraph
    }
}

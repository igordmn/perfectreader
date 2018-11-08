package com.dmi.util.android.view

import android.content.Context

class ViewBuild(val context: Context) {
    private var lastId = 0

    fun generateId(): Int = lastId++
}
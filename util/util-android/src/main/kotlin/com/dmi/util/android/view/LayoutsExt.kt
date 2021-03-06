package com.dmi.util.android.view

import android.content.Context
import android.view.KeyEvent
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.coordinatorlayout.widget.CoordinatorLayout
import me.dkzwm.widget.srl.MaterialSmoothRefreshLayout

// todo maybe get rid of key interception

interface KeyInterceptable {
    fun onInterceptKey(event: KeyEvent): Boolean
}

class LinearLayoutExt(context: Context) : LinearLayoutCompat(context), KeyInterceptable {
    private val onInterceptKeys = ArrayList<(event: KeyEvent) -> Boolean>()

    override fun onInterceptKey(event: KeyEvent): Boolean = onInterceptKeys.any { it(event) }

    fun onInterceptKey(action: (KeyEvent) -> Boolean) {
        onInterceptKeys.add(action)
    }

    fun onInterceptKeyDown(keyCode: Int, action: (KeyEvent) -> Boolean) = onInterceptKey {
        if (it.action == KeyEvent.ACTION_DOWN && it.keyCode == keyCode) action(it) else false
    }
}

class FrameLayoutExt(context: Context) : FrameLayout(context), KeyInterceptable {
    private val onInterceptKeys = ArrayList<(event: KeyEvent) -> Boolean>()

    override fun onInterceptKey(event: KeyEvent): Boolean = onInterceptKeys.any { it(event) }

    fun onInterceptKey(action: (KeyEvent) -> Boolean) {
        onInterceptKeys.add(action)
    }

    fun onInterceptKeyDown(keyCode: Int, action: (KeyEvent) -> Boolean) = onInterceptKey {
        if (it.action == KeyEvent.ACTION_DOWN && it.keyCode == keyCode) action(it) else false
    }
}

class CoordinatorLayoutExt(context: Context) : CoordinatorLayout(context), KeyInterceptable {
    private val onInterceptKeys = ArrayList<(event: KeyEvent) -> Boolean>()

    override fun onInterceptKey(event: KeyEvent): Boolean = onInterceptKeys.any { it(event) }

    fun onInterceptKey(action: (KeyEvent) -> Boolean) {
        onInterceptKeys.add(action)
    }

    fun onInterceptKeyDown(keyCode: Int, action: (KeyEvent) -> Boolean) = onInterceptKey {
        if (it.action == KeyEvent.ACTION_DOWN && it.keyCode == keyCode) action(it) else false
    }
}

class MaterialSmoothRefreshLayoutExt(context: Context) : MaterialSmoothRefreshLayout(context), KeyInterceptable {
    private val onInterceptKeys = ArrayList<(event: KeyEvent) -> Boolean>()

    override fun onInterceptKey(event: KeyEvent): Boolean = onInterceptKeys.any { it(event) }

    fun onInterceptKey(action: (KeyEvent) -> Boolean) {
        onInterceptKeys.add(action)
    }

    fun onInterceptKeyDown(keyCode: Int, action: (KeyEvent) -> Boolean) = onInterceptKey {
        if (it.action == KeyEvent.ACTION_DOWN && it.keyCode == keyCode) action(it) else false
    }
}
package com.dmi.util.base

import android.os.Bundle
import android.view.KeyEvent
import com.dmi.util.base.BaseUtils.handleAttachedFragments
import com.dmi.util.layout.HasLayout
import me.tatarka.simplefragment.SimpleFragment
import me.tatarka.simplefragment.SimpleFragmentAppCompatActivity
import me.tatarka.simplefragment.SimpleFragmentIntent
import me.tatarka.simplefragment.key.LayoutKey

open class BaseActivity protected constructor() : SimpleFragmentAppCompatActivity() {
    private val layoutId: Int

    init {
        val hasLayout = javaClass.getAnnotation(HasLayout::class.java)
        this.layoutId = if (hasLayout != null) hasLayout.value else 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (layoutId > 0) {
            setContentView(layoutId)
        }
    }

    protected fun inject(vararg modules: Any) {
//        val baseApplication = application as BaseApplication
//        baseApplication.objectGraph().plus(*modules).inject(this)
    }

    protected fun <T : SimpleFragment?> findChild(containerId: Int): T {
        return simpleFragmentManager.find<T>(LayoutKey.of(containerId))
    }

    protected fun <T : SimpleFragment> addChild(fragmentClass: Class<T>, containerId: Int): T {
        return simpleFragmentManager.add(SimpleFragmentIntent.of(fragmentClass), LayoutKey.of(containerId))
    }

    protected fun <T : SimpleFragment> addChild(intent: SimpleFragmentIntent<T>, containerId: Int): T {
        return simpleFragmentManager.add(intent, LayoutKey.of(containerId))
    }

    protected fun <T : SimpleFragment> findOrAddChild(fragmentClass: Class<T>, containerId: Int): T {
        return simpleFragmentManager.findOrAdd(SimpleFragmentIntent.of(fragmentClass), LayoutKey.of(containerId))
    }

    protected fun <T : SimpleFragment> findOrAddChild(intent: SimpleFragmentIntent<T>, containerId: Int): T {
        return simpleFragmentManager.findOrAdd(intent, LayoutKey.of(containerId))
    }

    protected fun removeChild(containerId: Int) {
        simpleFragmentManager.remove(simpleFragmentManager.find<SimpleFragment>(LayoutKey.of(containerId)))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return handleAttachedFragments(this, { it.onKeyDown(keyCode, event) }) || super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return handleAttachedFragments(this, { it.onKeyUp(keyCode, event) }) || super.onKeyUp(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        return handleAttachedFragments(this, { it.onKeyLongPress(keyCode, event) }) || super.onKeyLongPress(keyCode, event)
    }

    override fun onKeyMultiple(keyCode: Int, repeatCount: Int, event: KeyEvent): Boolean {
        return handleAttachedFragments(this, { it.onKeyMultiple(keyCode, repeatCount, event) }) || super.onKeyMultiple(keyCode, repeatCount, event)
    }

    override fun onResume() {
        super.onResume()
        for (fragment in simpleFragmentManager.fragments) {
            if (fragment is BaseFragment) {
                fragment.resume()
            }
        }
    }

    override fun onPause() {
        for (fragment in simpleFragmentManager.fragments) {
            if (fragment is BaseFragment) {
                fragment.pause()
            }
        }
        super.onPause()
    }
}

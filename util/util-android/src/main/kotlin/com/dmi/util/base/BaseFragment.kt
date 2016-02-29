package com.dmi.util.base

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.dmi.util.base.BaseUtils.handleAttachedFragments
import com.dmi.util.layout.HasLayout
import dagger.ObjectGraph
import icepick.Icepick
import me.tatarka.simplefragment.SimpleFragment
import me.tatarka.simplefragment.SimpleFragmentIntent
import me.tatarka.simplefragment.key.LayoutKey

abstract class BaseFragment protected constructor() : SimpleFragment(), KeyEvent.Callback {
    private val layoutId: Int

    private lateinit var objectGraph: ObjectGraph

    private var pause = true

    init {
        val hasLayout = javaClass.getAnnotation(HasLayout::class.java)
        this.layoutId = if (hasLayout != null) hasLayout.value else 0
    }

    @SuppressWarnings("unchecked")
    override fun onCreate(context: Context?, state: Bundle?) {
        Icepick.restoreInstanceState(this, state)
//        objectGraph = createObjectGraph(parentObjectGraph())
//        objectGraph.inject(this)
        if (presenter() != null) {
            presenter()!!.onCreate()
        }
    }

    override fun onDestroy() {
        if (presenter() != null) {
            presenter()!!.onDestroy()
        }
        super.onDestroy()
    }

    protected open fun createObjectGraph(parentGraph: ObjectGraph): ObjectGraph {
        return ObjectGraph.create()
    }

    protected fun objectGraph(): ObjectGraph {
        return objectGraph
    }

    protected fun parentObjectGraph(): ObjectGraph {
        val parentFragment = parentFragment as BaseFragment?
        return if (parentFragment != null) {
            parentFragment.objectGraph()
        } else {
            val application = getActivity<BaseActivity>().application as BaseApplication
            application.objectGraph()
        }
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View? {
        return if (layoutId > 0) layoutInflater.inflate(layoutId, viewGroup, false) else null
    }

    override fun onViewCreated(view: View) {
        ButterKnife.bind(this, view)
        resume()
    }

    override fun onViewDestroyed(view: View) {
        pause()
        super.onViewDestroyed(view)
    }

    override fun onSave(state: Bundle) {
        Icepick.saveInstanceState(this, state)
    }

    protected open fun presenter(): BasePresenter? {
        return null
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T> parentFragment(): T {
        return parentFragment as T
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

    open fun onResume() {
    }

    open fun onPause() {
    }

    internal fun resume() {
        if (pause) {
            onResume()
            pause = false
        }
    }

    internal fun pause() {
        if (!pause) {
            onPause()
            pause = true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return handleAttachedFragments(this, { it.onKeyDown(keyCode, event) })
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return handleAttachedFragments(this, { it.onKeyUp(keyCode, event) })
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        return handleAttachedFragments(this, { it.onKeyLongPress(keyCode, event) })
    }

    override fun onKeyMultiple(keyCode: Int, repeatCount: Int, event: KeyEvent): Boolean {
        return handleAttachedFragments(this, { it.onKeyMultiple(keyCode, repeatCount, event) })
    }
}

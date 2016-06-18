package com.dmi.perfectreader.fragment.menu

import android.content.Context
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import com.dmi.perfectreader.R
import com.dmi.util.base.BaseView
import com.dmi.util.base.find
import com.dmi.util.ext.onClick
import com.dmi.util.widget.onKeyDown
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.jetbrains.anko.onClick
import java.lang.Math.round

class MenuView(
        context: Context,
        private val model: Menu
) : BaseView(context, R.layout.fragment_menu) {
    private val toolbar = find<Toolbar>(R.id.toolbar)
    private val currentChapterText = find<TextView>(R.id.currentChapterText)
    private val currentPageText = find<TextView>(R.id.currentPageText)
    private val locationSlider = find<DiscreteSeekBar>(R.id.locationSlider)
    private val middleSpace = find<FrameLayout>(R.id.middleSpace)

    init {
        initTopBar()
        initBottomBar()
        initMiddleSpace()
    }

    private fun initTopBar() {
        toolbar.title = "Alice's Adventures in Wonderland"
        toolbar.subtitle = "Lewis Carroll"
        toolbar.menu.add(R.string.bookMenuSettings).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setIcon(R.drawable.ic_settings_white_24dp)
            onClick {
                model.showSettings()
            }
        }
        currentChapterText.text = "X — Alice's evidence"
        currentPageText.text = "302 / 2031"
        widget.onKeyDown { keyCode, keyEvent ->
            onKeyDown(keyCode)
        }
    }

    private fun onKeyDown(keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            model.close()
            return true
        } else {
            return false
        }
    }

    private fun initBottomBar() {
        subscribe(model.percentObservable) {
            locationSlider.progress = round(it * locationSlider.max).toInt()
        }
        locationSlider.numericTransformer = object: DiscreteSeekBar.NumericTransformer() {
            override fun transform(value: Int): Int {
                val percent = value.toFloat() / locationSlider.max
                return round(percent * 100)
            }
        }
        locationSlider.setOnProgressChangeListener(object: DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    model.goPercent(progress.toDouble() / locationSlider.max)
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) = Unit
        })
    }

    private fun initMiddleSpace() {
        middleSpace.onClick { model.close() }
    }
}
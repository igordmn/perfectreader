package com.dmi.perfectreader.fragment.menu

import android.content.Context
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.dmi.perfectreader.R
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.base.color
import com.dmi.util.android.base.drawable
import com.dmi.util.android.base.find
import com.dmi.util.android.ext.onClick
import com.dmi.util.android.widget.addHintOnLongClick
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

    private val searchButton = find<ImageButton>(R.id.searchButton)
    private val switchThemeButton = find<ImageButton>(R.id.switchThemeButton)
    private val autoScrollButton = find<ImageButton>(R.id.autoScrollButton)
    private val textToSpeechButton = find<ImageButton>(R.id.textToSpeechButton)
    private val addBookmarkButton = find<ImageButton>(R.id.addBookmarkButton)

    init {
        initTopBar()
        initBottomBar()
        initMiddleSpace()
    }

    private fun initTopBar() {
        DrawableCompat.setTint(toolbar.navigationIcon!!, color(R.color.icon_dark))

        toolbar.title = "Alice's Adventures in Wonderland"
        toolbar.subtitle = "Lewis Carroll"
        toolbar.menu.add(R.string.bookMenuSettings).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            icon = drawable(R.drawable.ic_settings, color(R.color.icon_dark))
            onClick {
                model.showSettings()
            }
        }
        currentChapterText.text = "X — Alice's evidence"
        currentPageText.text = "302 / 2031"
    }

    private fun initBottomBar() {
        DrawableCompat.setTint(searchButton.drawable, color(R.color.icon_dark))
        DrawableCompat.setTint(switchThemeButton.drawable, color(R.color.icon_dark))
        DrawableCompat.setTint(autoScrollButton.drawable, color(R.color.icon_dark))
        DrawableCompat.setTint(textToSpeechButton.drawable, color(R.color.icon_dark))
        DrawableCompat.setTint(addBookmarkButton.drawable, color(R.color.icon_dark))

        addHintOnLongClick(searchButton)
        addHintOnLongClick(switchThemeButton)
        addHintOnLongClick(autoScrollButton)
        addHintOnLongClick(textToSpeechButton)
        addHintOnLongClick(addBookmarkButton)

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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            model.close()
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }
}
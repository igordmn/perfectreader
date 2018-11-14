package com.dmi.perfectreader.ui.settings.place.screen

import android.graphics.PorterDuff
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.util.android.view.*
import org.jetbrains.anko.*

fun Places.screenBrightness(model: SettingsUI) = place {
    val settings = context.main.settings
    DialogView(context) {
        AlertDialog.Builder(context)
                .setView(LinearLayoutCompat(context).apply {
                    padding = dip(8)
                    orientation = LinearLayoutCompat.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL

                    child(params(wrapContent, wrapContent), ImageView(context).apply {
                        backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId
                        padding = dip(4)

                        autorun {
                            image = if (settings.screen.brightnessIsSystem) {
                                drawable(com.dmi.perfectreader.R.drawable.ic_brightness_system, color(com.dmi.perfectreader.R.color.onBackground))
                            } else {
                                drawable(com.dmi.perfectreader.R.drawable.ic_brightness_manual, color(com.dmi.perfectreader.R.color.onBackground))
                            }
                        }

                        onClick {
                            settings.screen.brightnessIsSystem = !settings.screen.brightnessIsSystem
                        }
                    })

                    child(params(matchParent, wrapContent), object : SeekBar(context) {
                        init {
                            progressDrawable.setColorFilter(color(com.dmi.perfectreader.R.color.secondary), PorterDuff.Mode.MULTIPLY)
                            max = 1000
                            progress = (settings.screen.brightnessValue * max).toInt()

                            autorun {
                                isEnabled = !settings.screen.brightnessIsSystem
                            }

                            setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                                    if (fromUser)
                                        settings.screen.brightnessValue = progress.toFloat() / max
                                }

                                override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
                                override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
                            })
                        }

                        override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                            if (event.actionMasked == MotionEvent.ACTION_DOWN && settings.screen.brightnessIsSystem) {
                                settings.screen.brightnessIsSystem = false
                                isEnabled = true
                            }
                            return super.dispatchTouchEvent(event)
                        }
                    })

                    onAttachStateChangeListener {
                        onViewDetachedFromWindow {
                            model.applyScreenBrightness = false
                        }
                        onViewAttachedToWindow {
                            model.applyScreenBrightness = true
                        }
                    }
                })
                .setOnDismissListener {
                    model.popup = null
                }
                .create()
                .apply {
                    window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                }
    }
}
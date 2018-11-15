package com.dmi.perfectreader.ui.settings.place.control

import android.app.Dialog
import android.text.TextUtils
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.viewpager.widget.ViewPager
import com.dmi.perfectreader.R
import com.dmi.perfectreader.settings.ControlSettings
import com.dmi.perfectreader.ui.action.ActionID
import com.dmi.perfectreader.ui.action.actionDescription
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.common.*
import com.dmi.util.action.TouchZone
import com.dmi.util.action.TouchZoneConfiguration
import com.dmi.util.action.zone
import com.dmi.util.android.view.*
import org.jetbrains.anko.*
import kotlin.reflect.KMutableProperty0


// todo triangle zones

//TouchZoneConfiguration.SINGLE,
//TouchZoneConfiguration.FOUR,
//TouchZoneConfiguration.NINE,
//TouchZoneConfiguration.SIXTEEN_FIXED,
//TouchZoneConfiguration.THREE_ROWS_TWO_COLUMNS,
//TouchZoneConfiguration.TWO_ROWS_THREE_COLUMNS,
//TouchZoneConfiguration.TWO_ROWS,
//TouchZoneConfiguration.THREE_ROWS,
//TouchZoneConfiguration.THREE_ROWS_FIXED,
//TouchZoneConfiguration.FOUR_ROWS_FIXED,
//TouchZoneConfiguration.TWO_COLUMNS,
//TouchZoneConfiguration.THREE_COLUMNS,
//TouchZoneConfiguration.THREE_COLUMNS_FIXED,
//TouchZoneConfiguration.FOUR_COLUMNS_FIXED,
//TouchZoneConfiguration.TRIANGLE_SIDES,
//TouchZoneConfiguration.TRIANGLE_SIDES_CENTER

fun Places.control(model: SettingsUI, settings: ControlSettings) = place {
    val tapConfigurations = listOf(
            TouchZoneConfiguration.SINGLE,
            TouchZoneConfiguration.FOUR,
            TouchZoneConfiguration.NINE,
            TouchZoneConfiguration.THREE_ROWS_TWO_COLUMNS,
            TouchZoneConfiguration.TWO_ROWS_THREE_COLUMNS,
            TouchZoneConfiguration.SIXTEEN_FIXED
    )

    val pinchConfigurations = listOf(
            TouchZoneConfiguration.SINGLE,
            TouchZoneConfiguration.FOUR
    )

    val horizontalScrollConfigurations = listOf(
            TouchZoneConfiguration.SINGLE,
            TouchZoneConfiguration.FOUR,
            TouchZoneConfiguration.THREE_ROWS,
            TouchZoneConfiguration.THREE_ROWS_FIXED,
            TouchZoneConfiguration.FOUR_ROWS_FIXED
    )

    val verticalScrollConfigurations = listOf(
            TouchZoneConfiguration.SINGLE,
            TouchZoneConfiguration.TWO_COLUMNS,
            TouchZoneConfiguration.THREE_COLUMNS,
            TouchZoneConfiguration.THREE_COLUMNS_FIXED,
            TouchZoneConfiguration.FOUR_COLUMNS_FIXED
    )

    val oneFinger = place {
        val singleTaps = controlDialog(model) {
            controlTap(
                    it, tapConfigurations, settings.touches.singleTaps::configuration,
                    settings.touches.singleTaps::property, R.string.settingsUIControlSingleTaps
            )
        }

        val longTaps = controlDialog(model) {
            controlTap(
                    it, tapConfigurations, settings.touches.longTaps::configuration,
                    settings.touches.longTaps::property, R.string.settingsUIControlLongTaps
            )
        }

        val doubleTaps = controlDialog(model) {
            controlDoubleTap(
                    it, tapConfigurations, settings.touches.doubleTaps::configuration,
                    settings.touches.doubleTaps::property, settings.touches::doubleTapEnabled, R.string.settingsUIControlDoubleTaps
            )
        }

//        val horizontalScrolls = controlDialog(model) {
//            controlDoubleTap(
//                    it, tapConfigurations, settings.touches.doubleTaps::configuration,
//                    settings.touches.doubleTaps::property, settings.touches::doubleTapEnabled, R.string.settingsUIControlDoubleTaps
//            )
//        }
//        val verticalScrolls = controlDialog(model)

        details(
                model, R.string.settingsUIControlOneFinger,
                verticalScroll(
                        popupSetting(model, emptyPreview(), singleTaps, R.string.settingsUIControlSingleTaps),
                        popupSetting(model, emptyPreview(), longTaps, R.string.settingsUIControlLongTaps),
                        popupSetting(model, emptyPreview(), doubleTaps, R.string.settingsUIControlDoubleTaps)
//                        popupSetting(model, emptyPreview(), horizontalScrolls, R.string.settingsUIControlHorizontalScrolls),
//                        popupSetting(model, emptyPreview(), verticalScrolls, R.string.settingsUIControlVerticalScrolls)
                )
        )
    }

    val twoFingers = place {
        //        val pinch = controlDialog(model)
        val singleTaps = controlDialog(model) {
            controlTap(
                    it, tapConfigurations, settings.touches.twoFingersSingleTaps::configuration,
                    settings.touches.twoFingersSingleTaps::property, R.string.settingsUIControlSingleTaps
            )
        }

        val longTaps = controlDialog(model) {
            controlTap(
                    it, tapConfigurations, settings.touches.twoFingersLongTaps::configuration,
                    settings.touches.twoFingersLongTaps::property, R.string.settingsUIControlLongTaps
            )
        }

        val doubleTaps = controlDialog(model) {
            controlDoubleTap(
                    it, tapConfigurations, settings.touches.twoFingersDoubleTaps::configuration,
                    settings.touches.twoFingersDoubleTaps::property, settings.touches::doubleTapEnabled, R.string.settingsUIControlDoubleTaps
            )
        }
//        val horizontalScrolls = controlDialog(model)
//        val verticalScrolls = controlDialog(model)

        details(
                model, R.string.settingsUIControlTwoFingers,
                verticalScroll(
//                        popupSetting(model, emptyPreview(), pinch, R.string.settingsUIControlPinch),
                        popupSetting(model, emptyPreview(), singleTaps, R.string.settingsUIControlSingleTaps),
                        popupSetting(model, emptyPreview(), longTaps, R.string.settingsUIControlLongTaps),
                        popupSetting(model, emptyPreview(), doubleTaps, R.string.settingsUIControlDoubleTaps)
//                        popupSetting(model, emptyPreview(), horizontalScrolls, R.string.settingsUIControlHorizontalScrolls),
//                        popupSetting(model, emptyPreview(), verticalScrolls, R.string.settingsUIControlVerticalScrolls)
                )
        )
    }

    verticalScroll(
            detailsSetting(model, emptyPreview(), oneFinger, R.string.settingsUIControlOneFinger),
            detailsSetting(model, emptyPreview(), twoFingers, R.string.settingsUIControlTwoFingers)
    )
}

private fun Places.controlDialog(
        model: SettingsUI,
        view: (Dialog) -> View
) = dialog {
    Dialog(context, R.style.fullScreenDialog).apply {
        val dialog = this
        setContentView(RelativeLayout(context).apply {
            child(params(matchParent, matchParent), view(dialog))
        })
        setOnDismissListener {
            model.popup = null
        }
    }
}

private fun ViewBuild.controlTap(
        dialog: Dialog,
        configurations: List<TouchZoneConfiguration>,
        configurationProperty: KMutableProperty0<TouchZoneConfiguration>,
        getActionProperty: (zone: TouchZone) -> KMutableProperty0<ActionID>,
        @StringRes titleRes: Int
): LinearLayoutCompat = LinearLayoutCompat(context).apply {
    orientation = LinearLayoutCompat.VERTICAL

    child(params(matchParent, wrapContent, weight = 0F), controlToolbar(titleRes, dialog))
    child(params(matchParent, matchParent, weight = 1F), controlContent(configurations, configurationProperty, getActionProperty))
}

private fun ViewBuild.controlDoubleTap(
        dialog: Dialog,
        configurations: List<TouchZoneConfiguration>,
        configurationProperty: KMutableProperty0<TouchZoneConfiguration>,
        getActionProperty: (zone: TouchZone) -> KMutableProperty0<ActionID>,
        doubleTapEnabledProperty: KMutableProperty0<Boolean>,
        @StringRes titleRes: Int
): LinearLayoutCompat = LinearLayoutCompat(context).apply {
    orientation = LinearLayoutCompat.VERTICAL

    child(params(matchParent, wrapContent, weight = 0F), controlToolbar(titleRes, dialog).apply {
        setPadding(0, 0, dip(16), 0)
        menu.add(R.string.settingsUIControlDoubleTapsEnable).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            actionView = SwitchCompat(context).apply {
                isChecked = doubleTapEnabledProperty.get()
                onClick {
                    doubleTapEnabledProperty.set(isChecked)
                }
            }
        }

    })

    child(params(matchParent, matchParent, weight = 1F), FrameLayout(context).apply {
        child(params(matchParent, matchParent), controlContent(configurations, configurationProperty, getActionProperty).apply {
            autorun {
                isVisible = doubleTapEnabledProperty.get()
            }
        })
        child(params(wrapContent, wrapContent, gravity = Gravity.CENTER), LinearLayoutCompat(context).apply {
            padding = dip(16)
            orientation = LinearLayoutCompat.VERTICAL

            child(params(matchParent, wrapContent), TextView(context).apply {
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
                textColor = color(R.color.onBackground)
                textResource = R.string.settingsUIControlDoubleTapsIsDisabled
                gravity = Gravity.CENTER_HORIZONTAL
            })

            child(params(matchParent, wrapContent), TextView(context).apply {
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
                textColor = color(R.color.onBackground).withOpacity(0.60)
                textResource = R.string.settingsUIControlDoubleTapsWarning
                gravity = Gravity.CENTER_HORIZONTAL
            })

            autorun {
                isVisible = !doubleTapEnabledProperty.get()
            }
        })
    })
}

private fun ViewBuild.controlToolbar(@StringRes titleRes: Int, dialog: Dialog) = Toolbar(context).apply {
    setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
    backgroundColor = color(android.R.color.transparent)
    navigationIcon = drawable(R.drawable.ic_arrow_back)
    title = string(titleRes)
    setNavigationOnClickListener {
        dialog.dismiss()
    }
}

private fun ViewBuild.controlContent(
        configurations: List<TouchZoneConfiguration>,
        configurationProperty: KMutableProperty0<TouchZoneConfiguration>,
        getActionProperty: (zone: TouchZone) -> KMutableProperty0<ActionID>
): View {
    fun LinearLayoutCompat.horizontalDivider() {
        child(params(matchParent, dip(1), weight = 0F), View(context).apply {
            backgroundColor = color(attr(android.R.attr.colorControlHighlight).resourceId)
        })
    }

    fun LinearLayoutCompat.verticalDivider() {
        child(params(dip(1), matchParent, weight = 0F), View(context).apply {
            backgroundColor = color(attr(android.R.attr.colorControlHighlight).resourceId)
        })
    }

    fun LinearLayoutCompat.vparams(height: Int) = if (height < 0) {
        params(matchParent, matchParent, weight = 1F)
    } else {
        params(matchParent, dip(height), weight = 0F)
    }

    fun LinearLayoutCompat.hparams(width: Int) = if (width < 0) {
        params(matchParent, matchParent, weight = 1F)
    } else {
        params(dip(width), matchParent, weight = 0F)
    }

    class MenuBuild(val menu: Menu) {
        val nameToSub = HashMap<String, MenuBuild>()

        fun sub(name: String) = nameToSub.getOrPut(name) {
            MenuBuild(menu.addSubMenu(name))
        }

        fun add(name: String) = menu.add(name)
    }

    fun actionsMenu(menu: Menu, onClick: (ActionID) -> Unit) {
        val build = MenuBuild(menu)

        for (actionID in ActionID.values()) {
            val desc = actionDescription(context, actionID)
            val item = when {
                desc.second != null && desc.third != null -> build.sub(desc.first).sub(desc.second).add(desc.third)
                desc.second != null -> build.sub(desc.first).add(desc.second)
                else -> build.add(desc.first)
            }
            item.onClick {
                onClick(actionID)
            }
        }
    }

    fun cell(zone: TouchZone, isSmall: Boolean) = FrameLayout(context).apply {
        val actionProperty = getActionProperty(zone)

        val menuAnchor = child(params(0, 0, gravity = Gravity.CENTER), View(context).apply {
            visibility = View.INVISIBLE
        })

        child(params(matchParent, matchParent), TextView(context).apply {
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
            textColor = color(R.color.onBackground)
            textSize = spFloat(8F)
            gravity = Gravity.CENTER
            padding = dip(4)
            backgroundResource = attr(android.R.attr.selectableItemBackground).resourceId
            ellipsize = TextUtils.TruncateAt.END

            autorun {
                val actionID = actionProperty.get()
                val desc = actionDescription(context, actionID).toString()
                text = when {
                    actionID == ActionID.NONE -> ""
                    isSmall -> "⋯"
                    else -> desc
                }
                TooltipCompat.setTooltipText(this, desc)
            }

            onClick {
                val popup = PopupMenu(context, menuAnchor, Gravity.CENTER)
                actionsMenu(popup.menu, onClick = { actionId ->
                    actionProperty.set(actionId)
                })
                popup.show()
            }
        })
    }

    fun ViewBuild.taps(configuration: TouchZoneConfiguration): View = LinearLayoutCompat(context).apply {
        val shapes = configuration.shapes as TouchZoneConfiguration.Shapes.Table
        orientation = LinearLayoutCompat.VERTICAL
        setPadding(dip(16), 0, dip(16), 0)

        horizontalDivider()
        for (vertical in shapes.verticals) {
            child(vparams(vertical.size), LinearLayoutCompat(context).apply {
                orientation = LinearLayoutCompat.HORIZONTAL
                verticalDivider()
                for (horizontal in shapes.horizontals) {
                    val zone = zone(horizontal.zone, vertical.zone)
                    val isSmall = vertical.size in 0..32 || horizontal.size in 0..32
                    child(hparams(horizontal.size), cell(zone, isSmall))
                    verticalDivider()
                }
            })
            horizontalDivider()
        }
    }

    fun viewPager() = ViewPager(context).apply {
        val views = configurations.map { configuration ->
            val view: ViewBuild.() -> View = { taps(configuration) }
            view
        }

        val configuration = configurationProperty.get()

        adapter = ViewPagerAdapter(views)
        currentItem = configurations.indexOf(configuration)
        addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) = Unit
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

            override fun onPageSelected(position: Int) {
                configurationProperty.set(configurations[position])
            }
        })
    }

    return FrameLayout(context).apply {
        setPadding(0, 0, 0, dip(16))
        child(params(matchParent, matchParent), viewPager())
    }
}
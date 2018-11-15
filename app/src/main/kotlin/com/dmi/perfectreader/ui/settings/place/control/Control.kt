package com.dmi.perfectreader.ui.settings.place.control

import android.app.Dialog
import android.text.TextUtils
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.TooltipCompat
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
    val oneFinger = place {
        val tapConfigurations = listOf(
                TouchZoneConfiguration.SINGLE,
                TouchZoneConfiguration.FOUR,
                TouchZoneConfiguration.NINE,
                TouchZoneConfiguration.THREE_ROWS_TWO_COLUMNS,
                TouchZoneConfiguration.TWO_ROWS_THREE_COLUMNS,
                TouchZoneConfiguration.SIXTEEN_FIXED
        )

        val singleTaps = controlDialog(
                model, tapConfigurations,
                settings.touches.singleTaps::configuration, settings.touches.singleTaps::property,
                R.string.settingsUIControlSingleTaps
        )

        val longTaps = controlDialog(
                model, tapConfigurations,
                settings.touches.singleTaps::configuration, settings.touches.singleTaps::property,
                R.string.settingsUIControlSingleTaps
        )

        val doubleTaps = controlDialog(
                model, tapConfigurations,
                settings.touches.singleTaps::configuration, settings.touches.singleTaps::property,
                R.string.settingsUIControlSingleTaps
        )

//        val longTaps = controlDialog(model)
//        val doubleTaps = controlDialog(model)
//        val horizontalScrolls = controlDialog(model)
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
//        val singleTaps = controlDialog(model)
//        val longTaps = controlDialog(model)
//        val doubleTaps = controlDialog(model)
//        val horizontalScrolls = controlDialog(model)
//        val verticalScrolls = controlDialog(model)

        details(
                model, R.string.settingsUIControlTwoFingers,
                verticalScroll(
//                        detailsSetting(model, emptyPreview(), pinch, R.string.settingsUIControlPinch),
//                        detailsSetting(model, emptyPreview(), singleTaps, R.string.settingsUIControlSingleTaps),
//                        detailsSetting(model, emptyPreview(), longTaps, R.string.settingsUIControlLongTaps),
//                        detailsSetting(model, emptyPreview(), doubleTaps, R.string.settingsUIControlDoubleTaps),
//                        detailsSetting(model, emptyPreview(), horizontalScrolls, R.string.settingsUIControlHorizontalScrolls),
//                        detailsSetting(model, emptyPreview(), verticalScrolls, R.string.settingsUIControlVerticalScrolls)
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
        configurations: List<TouchZoneConfiguration>,
        configurationProperty: KMutableProperty0<TouchZoneConfiguration>,
        getActionProperty: (zone: TouchZone) -> KMutableProperty0<ActionID>,
        @StringRes titleRes: Int
) = dialog {
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
        addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) = Unit
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)  = Unit

            override fun onPageSelected(position: Int) {
                configurationProperty.set(configurations[position])
            }
        })
    }

    fun view(dialog: Dialog): LinearLayoutCompat = LinearLayoutCompat(context).apply {
        orientation = LinearLayoutCompat.VERTICAL

        child(params(matchParent, wrapContent, weight = 0F), Toolbar(context).apply {
            setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
            backgroundColor = color(android.R.color.transparent)
            navigationIcon = drawable(R.drawable.ic_arrow_back)
            title = string(titleRes)
            setNavigationOnClickListener {
                dialog.dismiss()
            }
        })
        child(params(matchParent, matchParent, weight = 1F), FrameLayout(context).apply {
            setPadding(0, 0, 0, dip(16))
            child(params(matchParent, matchParent), viewPager())
        })
    }

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
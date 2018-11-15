package com.dmi.perfectreader.ui.settings.place.control

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.text.TextUtils
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.viewpager.widget.ViewPager
import com.dmi.perfectreader.R
import com.dmi.perfectreader.ui.action.ActionID
import com.dmi.perfectreader.ui.action.actionDescription
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.util.action.TouchZone
import com.dmi.util.action.TouchZoneConfiguration
import com.dmi.util.action.zone
import com.dmi.util.android.view.*
import org.jetbrains.anko.*
import kotlin.reflect.KMutableProperty0

// todo refactoring

fun Places.controlDialog(
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

fun ViewBuild.controlTaps(
        @StringRes titleRes: Int,
        dialog: Dialog,
        configurations: List<TouchZoneConfiguration>,
        configurationProperty: KMutableProperty0<TouchZoneConfiguration>,
        getActionProperty: (zone: TouchZone) -> KMutableProperty0<ActionID>
): LinearLayoutCompat = LinearLayoutCompat(context).apply {
    orientation = LinearLayoutCompat.VERTICAL

    child(params(matchParent, wrapContent, weight = 0F), controlToolbar(titleRes, dialog))
    child(params(matchParent, matchParent, weight = 1F), controlContent(configurations, configurationProperty, getActionProperty))
}

fun ViewBuild.controlDoubleTaps(
        @StringRes titleRes: Int,
        dialog: Dialog,
        configurations: List<TouchZoneConfiguration>,
        configurationProperty: KMutableProperty0<TouchZoneConfiguration>,
        getActionProperty: (zone: TouchZone) -> KMutableProperty0<ActionID>,
        doubleTapEnabledProperty: KMutableProperty0<Boolean>
): LinearLayoutCompat = LinearLayoutCompat(context).apply {
    orientation = LinearLayoutCompat.VERTICAL

    fun switch() = SwitchCompat(context).apply {
        isChecked = doubleTapEnabledProperty.get()
        onClick {
            doubleTapEnabledProperty.set(isChecked)
        }
    }

    child(params(matchParent, wrapContent, weight = 0F), controlToolbar(titleRes, dialog).apply {
        setPadding(0, 0, dip(16), 0)
        menu.add(R.string.settingsUIControlDoubleTapsEnable).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            actionView = switch()
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

@Suppress("UNUSED_PARAMETER")
fun ViewBuild.controlPinchTaps(
        @StringRes titleRes: Int,
        dialog: Dialog,
        actionProperty1: KMutableProperty0<ActionID>,
        actionProperty2: KMutableProperty0<ActionID>,
        @DrawableRes icon1: Int,
        @DrawableRes icon2: Int,
        layoutOrientation: Int
): View {
    val configurationProperty = object {
        var delegate: TouchZoneConfiguration
            get() = TouchZoneConfiguration.SINGLE
            set(value) = Unit
    }

    fun getActionProperty1(zone: TouchZone) = actionProperty1
    fun getActionProperty2(zone: TouchZone) = actionProperty2

    return controlDirectionTaps(
            titleRes, dialog, listOf(TouchZoneConfiguration.SINGLE),
            configurationProperty::delegate, configurationProperty::delegate,
            ::getActionProperty1, ::getActionProperty2,
            icon1, icon2, layoutOrientation
    )
}

fun ViewBuild.controlDirectionTaps(
        @StringRes titleRes: Int,
        dialog: Dialog,
        configurations: List<TouchZoneConfiguration>,
        configurationProperty1: KMutableProperty0<TouchZoneConfiguration>,
        configurationProperty2: KMutableProperty0<TouchZoneConfiguration>,
        getActionProperty1: (zone: TouchZone) -> KMutableProperty0<ActionID>,
        getActionProperty2: (zone: TouchZone) -> KMutableProperty0<ActionID>,
        @DrawableRes icon1: Int,
        @DrawableRes icon2: Int,
        layoutOrientation: Int
): LinearLayoutCompat = LinearLayoutCompat(context).apply {
    val configurationProperty = object {
        var delegate: TouchZoneConfiguration
            get() = configurationProperty1.get()
            set(value) {
                configurationProperty1.set(value)
                configurationProperty2.set(value)
            }
    }

    fun cell(
            zone: TouchZone,
            isSmallVertical: Boolean,
            isSmallHorizontal: Boolean
    ): View = doubleCell(
            getActionProperty1(zone), getActionProperty2(zone),
            icon1, icon2,
            layoutOrientation,
            isSmallVertical,
            isSmallHorizontal
    )

    orientation = LinearLayoutCompat.VERTICAL

    child(params(matchParent, wrapContent, weight = 0F), controlToolbar(titleRes, dialog))
    child(params(matchParent, matchParent, weight = 1F), controlContent(configurations, configurationProperty::delegate, ::cell))
}

fun ViewBuild.controlToolbar(@StringRes titleRes: Int, dialog: Dialog) = Toolbar(context).apply {
    setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
    backgroundColor = color(android.R.color.transparent)
    navigationIcon = drawable(R.drawable.ic_arrow_left)
    title = string(titleRes)
    setNavigationOnClickListener {
        dialog.dismiss()
    }
}

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
    private val nameToSub = HashMap<String, MenuBuild>()

    fun sub(name: String) = nameToSub.getOrPut(name) {
        MenuBuild(menu.addSubMenu(name))
    }

    fun add(name: String): MenuItem = menu.add(name)
}

fun ViewBuild.actionsMenu(menu: Menu, onClick: (ActionID) -> Unit) {
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

fun ViewBuild.cell(
        property: KMutableProperty0<ActionID>,
        isSmallVertical: Boolean,
        isSmallHorizontal: Boolean,
        @DrawableRes icon: Int? = null
) = FrameLayout(context).apply {
    val menuAnchor = child(params(1, 1), View(context).apply {
        backgroundColor = Color.TRANSPARENT
    })

    backgroundResource = attr(android.R.attr.selectableItemBackground).resourceId

    val textView = TextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
        textColor = color(R.color.onBackground)
        textSize = spFloat(8F)
        gravity = Gravity.CENTER
        padding = dip(4)
        ellipsize = TextUtils.TruncateAt.END
    }

    @SuppressLint("RtlHardcoded")
    when {
        icon != null && !isSmallVertical -> child(params(matchParent, matchParent), LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.VERTICAL

            child(params(matchParent, matchParent, weight = 0.5F), FrameLayout(context).apply {
                child(params(dip(24), dip(24), gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL), ImageView(context).apply {
                    image = drawable(icon, color(R.color.onBackground).withOpacity(0.10))
                })
            })

            child(params(matchParent, matchParent, weight = 0.5F), FrameLayout(context).apply {
                child(params(wrapContent, wrapContent, gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL), textView)
            })
        })

        icon != null && isSmallVertical -> child(params(matchParent, matchParent), LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.HORIZONTAL

            child(params(matchParent, matchParent, weight = 0.5F), FrameLayout(context).apply {
                child(params(dip(24), dip(24), gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL), ImageView(context).apply {
                    image = drawable(icon, color(R.color.onBackground).withOpacity(0.10))
                })
            })

            child(params(matchParent, matchParent, weight = 0.5F), FrameLayout(context).apply {
                child(params(wrapContent, wrapContent, gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL), textView)
            })
        })

        else -> child(params(wrapContent, wrapContent, gravity = Gravity.CENTER), textView)
    }

    autorun {
        val actionID = property.get()
        val desc = actionDescription(context, actionID).toString()
        textView.text = when {
            actionID == ActionID.NONE -> ""
            isSmallVertical || isSmallHorizontal -> "⋯"
            else -> desc
        }
        TooltipCompat.setTooltipText(this, desc)
    }

    var lastX = 0F
    var lastY = 0F

    onTouch { _, event ->
        lastX = event.x
        lastY = event.y
        false
    }

    onClick {
        menuAnchor.x = lastX
        menuAnchor.y = lastY
        val popup = PopupMenu(context, menuAnchor)
        actionsMenu(popup.menu, onClick = { actionId ->
            property.set(actionId)
        })
        popup.show()
    }
}

fun ViewBuild.doubleCell(
        property1: KMutableProperty0<ActionID>,
        property2: KMutableProperty0<ActionID>,
        @DrawableRes icon1: Int,
        @DrawableRes icon2: Int,
        layoutOrientation: Int,
        isSmallVertical: Boolean,
        isSmallHorizontal: Boolean
) = FrameLayout(context).apply {
    child(params(matchParent, matchParent), LinearLayoutCompat(context).apply {
        orientation = layoutOrientation
        child(params(matchParent, matchParent, weight = 0.5F), cell(property1, isSmallVertical, isSmallHorizontal, icon1))
        child(params(matchParent, matchParent, weight = 0.5F), cell(property2, isSmallVertical, isSmallHorizontal, icon2))
    })
}

fun ViewBuild.controlContent(
        configurations: List<TouchZoneConfiguration>,
        configurationProperty: KMutableProperty0<TouchZoneConfiguration>,
        getActionProperty: (zone: TouchZone) -> KMutableProperty0<ActionID>
) = controlContent(configurations, configurationProperty) { zone, isSmallVertical, isSmallHorizontal ->
    cell(getActionProperty(zone), isSmallVertical, isSmallHorizontal)
}

fun ViewBuild.controlContent(
        configurations: List<TouchZoneConfiguration>,
        configurationProperty: KMutableProperty0<TouchZoneConfiguration>,
        getCell: (zone: TouchZone, isSmallVertical: Boolean, isSmallHorizontal: Boolean) -> View
): View {
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
                    child(hparams(horizontal.size), getCell(zone, vertical.size in 0..32, horizontal.size in 0..32))
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
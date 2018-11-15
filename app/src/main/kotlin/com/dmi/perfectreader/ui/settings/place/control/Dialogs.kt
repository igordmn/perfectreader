package com.dmi.perfectreader.ui.settings.place.control

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onTouch
import kotlin.reflect.KMutableProperty0
import com.dmi.util.action.TouchZoneConfiguration.Shapes.Zone as ShapeZone

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

fun ViewBuild.control(toolBar: View, content: View) = LinearLayoutCompat(context).apply {
    orientation = LinearLayoutCompat.VERTICAL
    child(params(matchParent, wrapContent, weight = 0F), toolBar)
    child(params(matchParent, matchParent, weight = 1F), content)
}

fun ViewBuild.controlDoubleTaps(
        taps: View,
        enabledProperty: KMutableProperty0<Boolean>
) = FrameLayout(context).apply {
    child(params(matchParent, matchParent), taps.apply {
        autorun {
            isVisible = enabledProperty.get()
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
            isVisible = !enabledProperty.get()
        }
    })
}

fun ViewBuild.controlDoubleTapsToolbar(
        @StringRes titleRes: Int,
        dialog: Dialog,
        enabledProperty: KMutableProperty0<Boolean>
) = controlToolbar(titleRes, dialog).apply {
    setPadding(0, 0, dip(16), 0)
    val item = menu.add(R.string.settingsUIControlDoubleTapsEnable)
    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    item.actionView = SwitchCompat(context).apply {
        isChecked = enabledProperty.get()
        onClick {
            enabledProperty.set(isChecked)
        }
    }
}

@Suppress("UNUSED_PARAMETER")
fun ViewBuild.controlPinchTaps(
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
            listOf(TouchZoneConfiguration.SINGLE),
            configurationProperty::delegate, configurationProperty::delegate,
            ::getActionProperty1, ::getActionProperty2,
            icon1, icon2, layoutOrientation
    )
}

fun ViewBuild.controlDirectionTaps(
        configurations: List<TouchZoneConfiguration>,
        configurationProperty1: KMutableProperty0<TouchZoneConfiguration>,
        configurationProperty2: KMutableProperty0<TouchZoneConfiguration>,
        getActionProperty1: (zone: TouchZone) -> KMutableProperty0<ActionID>,
        getActionProperty2: (zone: TouchZone) -> KMutableProperty0<ActionID>,
        @DrawableRes icon1: Int,
        @DrawableRes icon2: Int,
        layoutOrientation: Int
): View {
    val configurationProperty = object {
        var delegate: TouchZoneConfiguration
            get() = configurationProperty1.get()
            set(value) {
                configurationProperty1.set(value)
                configurationProperty2.set(value)
            }
    }

    fun cell(zone: TouchZone) = doubleCell(
            getActionProperty1(zone), getActionProperty2(zone),
            icon1, icon2,
            layoutOrientation
    )

    return controlTaps(configurations, configurationProperty::delegate, getCell = ::cell)
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

class MenuBuild(val menu: Menu) {
    private val nameToSub = HashMap<String, MenuBuild>()

    fun sub(name: String) = nameToSub.getOrPut(name) {
        MenuBuild(menu.addSubMenu(name))
    }

    fun add(name: String): MenuItem = menu.add(name)
}

fun ViewBuild.cell(property: KMutableProperty0<ActionID>, @DrawableRes icon: Int? = null) = FrameLayout(context).apply {
    backgroundResource = attr(android.R.attr.selectableItemBackground).resourceId

    val isSmallHorizontal = layoutParams.width >= 0 && layoutParams.width <= dip(32)
    val isSmallVertical = layoutParams.height >= 0 && layoutParams.height <= dip(32)

    val textView = TextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
        textColor = color(R.color.onBackground)
        textSize = spFloat(8F)
        gravity = Gravity.CENTER
        padding = dip(4)
        ellipsize = TextUtils.TruncateAt.END
    }

    if (icon != null) {
        child(
                params(matchParent, matchParent),
                twoInCenter(
                        if (isSmallVertical) LinearLayoutCompat.HORIZONTAL else LinearLayoutCompat.VERTICAL,
                        ImageView(context).apply {
                            image = drawable(icon, color(R.color.onBackground).withOpacity(0.10))
                        },
                        textView
                )
        )
    } else {
        child(params(wrapContent, wrapContent, gravity = Gravity.CENTER), textView)
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

    attachMenu {
        actionsMenu(menu, onClick = { actionId ->
            property.set(actionId)
        })
    }
}

fun FrameLayout.attachMenu(configure: PopupMenu.() -> Unit) {
    var lastX = 0F
    var lastY = 0F
    val menuAnchor = child(params(1, 1), View(context).apply {
        backgroundColor = Color.TRANSPARENT
    })

    onTouch(returnValue = false) { _, event ->
        lastX = event.x
        lastY = event.y
    }

    onClick {
        menuAnchor.x = lastX
        menuAnchor.y = lastY
        val popup = PopupMenu(context, menuAnchor)
        configure(popup)
        popup.show()
    }
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

@SuppressLint("RtlHardcoded")
private fun ViewBuild.twoInCenter(orientation: Int, view1: View, view2: View) = LinearLayoutCompat(context).apply {
    val gravity1 = if (orientation == LinearLayoutCompat.VERTICAL) Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL else Gravity.RIGHT or Gravity.CENTER_VERTICAL
    val gravity2 = if (orientation == LinearLayoutCompat.VERTICAL) Gravity.TOP or Gravity.CENTER_HORIZONTAL else Gravity.LEFT or Gravity.CENTER_VERTICAL

    this.orientation = orientation

    child(params(matchParent, matchParent, weight = 0.5F), FrameLayout(context).apply {
        child(params(dip(24), dip(24), gravity = gravity1), view1)
    })

    child(params(matchParent, matchParent, weight = 0.5F), FrameLayout(context).apply {
        child(params(wrapContent, wrapContent, gravity = gravity2), view2)
    })
}

fun ViewBuild.doubleCell(
        property1: KMutableProperty0<ActionID>,
        property2: KMutableProperty0<ActionID>,
        @DrawableRes icon1: Int,
        @DrawableRes icon2: Int,
        layoutOrientation: Int
) = FrameLayout(context).apply {
    child(params(matchParent, matchParent), LinearLayoutCompat(context).apply {
        orientation = layoutOrientation
        child(params(matchParent, matchParent, weight = 0.5F), cell(property1, icon1))
        child(params(matchParent, matchParent, weight = 0.5F), cell(property2, icon2))
    })
}

@JvmName("controlContent1")
fun ViewBuild.controlTaps(
        configurations: List<TouchZoneConfiguration>,
        configurationProperty: KMutableProperty0<TouchZoneConfiguration>,
        getActionProperty: (zone: TouchZone) -> KMutableProperty0<ActionID>
) = controlTaps(configurations, configurationProperty, getCell = {
    cell(getActionProperty(it))
})

@JvmName("controlContent2")
fun ViewBuild.controlTaps(
        configurations: List<TouchZoneConfiguration>,
        configurationProperty: KMutableProperty0<TouchZoneConfiguration>,
        getCell: (zone: TouchZone) -> View
): View {
    fun LinearLayoutCompat.tapColumn(horizontal: ShapeZone, vertical: ShapeZone) {
        val zone = zone(horizontal.zone, vertical.zone)
        child(hparams(horizontal.size), getCell(zone))
    }

    fun LinearLayoutCompat.tapRow(vertical: ShapeZone, horizontals: List<ShapeZone>) {
        child(vparams(vertical.size), LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.HORIZONTAL
            verticalDivider()
            for (horizontal in horizontals) {
                tapColumn(horizontal, vertical)
                verticalDivider()
            }
        })
    }

    fun ViewBuild.taps(configuration: TouchZoneConfiguration): View = LinearLayoutCompat(context).apply {
        val shapes = configuration.shapes as TouchZoneConfiguration.Shapes.Table
        orientation = LinearLayoutCompat.VERTICAL
        setPadding(dip(16), 0, dip(16), 0)

        horizontalDivider()
        for (vertical in shapes.verticals) {
            tapRow(vertical, shapes.horizontals)
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
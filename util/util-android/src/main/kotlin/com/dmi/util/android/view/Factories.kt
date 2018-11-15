package com.dmi.util.android.view

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.jaredrummler.android.colorpicker.ColorPickerView
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar

class ViewBuild(val context: Context) {
    private var lastId = 0

    fun generateId(): Int = lastId++
}

fun LinearLayoutCompat.params(
        width: Int,
        height: Int,
        gravity: Int = -1,
        weight: Float = 0F,
        leftMargin: Int = 0,
        topMargin: Int = 0,
        rightMargin: Int = 0,
        bottomMargin: Int = 0
) = LinearLayoutCompat.LayoutParams(width, height, weight).apply {
    this.gravity = gravity
    this.leftMargin = leftMargin
    this.topMargin = topMargin
    this.rightMargin = rightMargin
    this.bottomMargin = bottomMargin
}

fun FrameLayout.params(
        width: Int,
        height: Int,
        gravity: Int = -1,
        leftMargin: Int = 0,
        topMargin: Int = 0,
        rightMargin: Int = 0,
        bottomMargin: Int = 0
) = FrameLayout.LayoutParams(width, height, gravity).apply {
    this.leftMargin = leftMargin
    this.topMargin = topMargin
    this.rightMargin = rightMargin
    this.bottomMargin = bottomMargin
}

fun ConstraintLayout.params(
        width: Int,
        height: Int,
        leftMargin: Int = 0,
        topMargin: Int = 0,
        rightMargin: Int = 0,
        bottomMargin: Int = 0
) = ConstraintLayout.LayoutParams(width, height).apply {
    this.leftMargin = leftMargin
    this.topMargin = topMargin
    this.rightMargin = rightMargin
    this.bottomMargin = bottomMargin
}

fun CoordinatorLayout.params(
        width: Int,
        height: Int,
        gravity: Int = Gravity.NO_GRAVITY,
        behavior: CoordinatorLayout.Behavior<*>? = null
) = CoordinatorLayout.LayoutParams(width, height).apply {
    this.gravity = gravity
    this.behavior = behavior
}

fun AppBarLayout.params(
        width: Int,
        height: Int,
        scrollFlags: Int = 1
) = AppBarLayout.LayoutParams(width, height).apply {
    this.scrollFlags = scrollFlags
}

fun CollapsingToolbarLayout.params(
        width: Int,
        height: Int,
        mode: Int = 0,
        parallaxMultiplier: Float = 0F
) = CollapsingToolbarLayout.LayoutParams(width, height).apply {
    this.collapseMode = mode
    this.parallaxMultiplier = parallaxMultiplier
}

fun ViewGroup.params(
        width: Int,
        height: Int
) = ViewGroup.LayoutParams(width, height)

fun <T : View> ViewGroup.child(
        params: ViewGroup.LayoutParams,
        view: T
): T = view.apply {
    layoutParams = params
    addView(this)
}

fun ViewBuild.HorizontalLayout(build: LinearLayoutCompat.() -> Unit): LinearLayoutCompat {
    val layout = LinearLayoutCompat(context)
    layout.orientation = LinearLayoutCompat.HORIZONTAL
    layout.build()
    return layout
}

fun ViewBuild.VerticalLayout(build: LinearLayoutCompat.() -> Unit): LinearLayoutCompat {
    val layout = LinearLayoutCompat(context)
    layout.orientation = LinearLayoutCompat.VERTICAL
    layout.build()
    return layout
}

fun ViewBuild.VerticalLayoutExt(build: LinearLayoutExt.() -> Unit): LinearLayoutExt {
    val layout = LinearLayoutExt(context)
    layout.orientation = LinearLayoutCompat.VERTICAL
    layout.build()
    return layout
}

fun ViewBuild.ScrollView(build: ScrollView.() -> Unit) = ScrollView(context).apply(build)
fun ViewBuild.AppCompatImageButton(build: AppCompatImageButton.() -> Unit) = AppCompatImageButton(context).apply(build)
fun ViewBuild.NestedScrollView(build: NestedScrollView.() -> Unit) = NestedScrollView(context).apply(build)
fun ViewBuild.CheckableImageButton(build: CheckableImageButton.() -> Unit) = CheckableImageButton(context).apply(build)
fun ViewBuild.TabLayout(build: TabLayout.() -> Unit) = TabLayout(context).apply(build)
fun ViewBuild.MaterialCardView(build: MaterialCardView.() -> Unit) = MaterialCardView(context).apply(build)
fun ViewBuild.SearchView(build: SearchView.() -> Unit) = SearchView(context).apply(build)
fun ViewBuild.SwipeRefreshLayout(build: SwipeRefreshLayout.() -> Unit) = SwipeRefreshLayout(context).apply(build)
fun ViewBuild.ColorPickerView(build: ColorPickerView.() -> Unit) = ColorPickerView(context).apply(build)
fun ViewBuild.LinearLayoutExt(build: LinearLayoutExt.() -> Unit) = LinearLayoutExt(context).apply(build)
fun ViewBuild.FrameLayoutExt(build: FrameLayoutExt.() -> Unit) = FrameLayoutExt(context).apply(build)
fun ViewBuild.CoordinatorLayoutExt(build: CoordinatorLayoutExt.() -> Unit) = CoordinatorLayoutExt(context).apply(build)
fun ViewBuild.LinearLayoutCompat(build: LinearLayoutCompat.() -> Unit) = LinearLayoutCompat(context).apply(build)
fun ViewBuild.ProgressBar(build: ProgressBar.() -> Unit) = ProgressBar(context).apply(build)
fun ViewBuild.ViewPager(build: ViewPager.() -> Unit) = ViewPager(context).apply(build)
fun ViewBuild.DiscreteSeekBar(build: DiscreteSeekBar.() -> Unit) = DiscreteSeekBar(context).apply(build)
fun ViewBuild.Button(build: Button.() -> Unit) = Button(context).apply(build)
fun ViewBuild.CheckBox(build: CheckBox.() -> Unit) = CheckBox(context).apply(build)
fun ViewBuild.RecyclerView(build: RecyclerView.() -> Unit) = RecyclerView(context).apply(build)
fun ViewBuild.ViewPagerSaveable(build: ViewPagerSaveable.() -> Unit) = ViewPagerSaveable(context).apply(build)
fun ViewBuild.SwitchCompat(build: SwitchCompat.() -> Unit) = SwitchCompat(context).apply(build)
fun ViewBuild.ConstraintLayout(build: ConstraintLayout.() -> Unit) = ConstraintLayout(context).apply(build)
fun ViewBuild.CoordinatorLayout(build: CoordinatorLayout.() -> Unit) = CoordinatorLayout(context).apply(build)
fun ViewBuild.AppBarLayout(build: AppBarLayout.() -> Unit) = AppBarLayout(context).apply(build)
fun ViewBuild.CollapsingToolbarLayout(build: CollapsingToolbarLayout.() -> Unit) = CollapsingToolbarLayout(context).apply(build)
fun ViewBuild.RelativeLayout(build: RelativeLayout.() -> Unit) = RelativeLayout(context).apply(build)
fun ViewBuild.FrameLayout(build: FrameLayout.() -> Unit) = FrameLayout(context).apply(build)
fun ViewBuild.TextView(build: TextView.() -> Unit) = TextView(context).apply(build)
fun ViewBuild.ImageView(build: ImageView.() -> Unit) = ImageView(context).apply(build)
fun ViewBuild.EditText(build: EditText.() -> Unit) = EditText(context).apply(build)
fun ViewBuild.EditNumber(build: EditNumber.() -> Unit) = EditNumber(context).apply(build)
fun ViewBuild.Toolbar(build: Toolbar.() -> Unit) = Toolbar(context).apply(build)
fun ViewBuild.View(build: View.() -> Unit) = View(context).apply(build)
package com.dmi.perfectreader.menu

import com.dmi.perfectreader.book.BookPresenter
import com.dmi.util.base.BasePresenter
import com.dmi.util.lang.clamp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuPresenter : BasePresenter() {
    @Inject
    protected lateinit var bookPresenter: BookPresenter
    @Inject
    protected lateinit var view: MenuFragment

    fun showSettings() {
        view.close()
    }

    fun goPosition(position: Int, maxPosition: Int) {
        bookPresenter.goPercent(clamp(0.0, 1.0, (position / maxPosition).toDouble()))
    }

    fun requestCurrentPercent(maxPosition: Int) {
        view.setPosition((bookPresenter.currentPercent() * maxPosition).toInt())
    }
}
